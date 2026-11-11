package com.fixora.marketplace.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

/** Observable backend status, shared by all screens. A queued change is not a confirmed save. */
object CloudStore {
    val db get() = FirebaseFirestore.getInstance()
    val gson = GsonBuilder().serializeNulls().create()
    val error = MutableStateFlow<String?>(null)
    val pendingWrites = MutableStateFlow(0)
    fun report(message: String) { error.value = message }
    fun map(value: Any): Map<String, Any?> = gson.fromJson(gson.toJson(value),
        object : TypeToken<Map<String, Any?>>() {}.type)
    fun <T> decode(data: Map<String, Any?>, type: Class<T>): T = gson.fromJson(gson.toJson(data), type)
}

/**
 * StateFlow adapter for the existing UI. Writes only changed fields and checks their
 * previous values in a transaction, so stale devices cannot overwrite a newer edit.
 * Pending overlays survive listener updates; rejected writes roll back to server data.
 */
class CloudList<T : Any>(
    private val collection: String,
    private val type: Class<T>,
    private val id: (T) -> String
) {
    private val state = MutableStateFlow<List<T>>(emptyList())
    private val listeners = mutableListOf<ListenerRegistration>()
    private val snapshots = mutableMapOf<Int, List<T>>()
    private val overlays = mutableMapOf<String, T>()
    private val revisions = mutableMapOf<String, Int>()
    private val locks = mutableMapOf<String, Mutex>()
    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var generation = 0
    private var activeUid: String? = null
    private var pending = 0
    private var ready = false

    fun asStateFlow() = state.asStateFlow()
    var value: List<T>
        get() = state.value
        set(next) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null || !user.isEmailVerified || activeUid != user.uid || !ready) {
                CloudStore.report("Please sign in, verify your email, and wait for data to load before saving.")
                return
            }
            val before = state.value.associateBy(id)
            // Deletion is deliberately explicit; removing an item from a filtered UI is not a delete.
            val changes = next.filter { before[id(it)] != it }
            changes.forEach { item ->
                val key = id(item)
                val old = before[key]?.let(::encode)
                val data = encode(item)
                val patch = data.filter { (field, v) -> old == null || old[field] != v }
                if (patch.isEmpty()) return@forEach
                val revision = (revisions[key] ?: 0) + 1
                revisions[key] = revision
                overlays[key] = item
                publish()
                val epoch = generation
                val uid = user.uid
                pending++
                CloudStore.pendingWrites.value++
                val lock = locks.getOrPut(key) { Mutex() }
                scope.launch {
                    try {
                        lock.withLock {
                            check(FirebaseAuth.getInstance().currentUser?.uid == uid) { "Your session changed. Please try again." }
                            val ref = CloudStore.db.collection(collection).document(key)
                            CloudStore.db.runTransaction { transaction ->
                                val current = transaction.get(ref)
                                if (old == null) {
                                    check(!current.exists()) { "This record already exists. Refresh and try again." }
                                    transaction.set(ref, data)
                                } else {
                                    check(current.exists()) { "This record no longer exists." }
                                    val remote = current.data.orEmpty()
                                    check(patch.keys.all { field -> equivalent(remote[field], old[field]) }) {
                                        "This record was changed on another device. Please review it and retry."
                                    }
                                    transaction.update(ref, patch)
                                }
                                null
                            }.await()
                            // Read back the committed object before removing its optimistic overlay.
                            val saved = ref.get(Source.SERVER).await()
                            if (epoch == generation && saved.exists()) {
                                val decoded = CloudStore.decode(saved.data!!, type)
                                snapshots[-1] = snapshots[-1].orEmpty().filterNot { id(it) == key } + decoded
                            }
                        }
                    } catch (e: CancellationException) { throw e
                    } catch (e: Exception) {
                        if (epoch == generation) CloudStore.report("Could not save " + collection.replace('_', ' ') + ": " + (e.localizedMessage ?: "Try again."))
                    } finally {
                        if (epoch == generation) {
                            pending--
                            CloudStore.pendingWrites.value = (CloudStore.pendingWrites.value - 1).coerceAtLeast(0)
                            if (revisions[key] == revision) overlays.remove(key)
                            publish()
                        }
                    }
                }
            }
        }

    private fun equivalent(a: Any?, b: Any?): Boolean =
        CloudStore.gson.toJsonTree(a) == CloudStore.gson.toJsonTree(b)

    private fun encode(item: T): Map<String, Any?> {
        val data = CloudStore.map(item).toMutableMap()
        if (collection == "provider_profiles") {
            data.remove("tradeLicenseNumber")
            data.remove("emiratesIdNumber")
        }
        return data
    }

    fun start(queries: List<Query>) {
        stop()
        activeUid = FirebaseAuth.getInstance().currentUser?.uid
        val epoch = generation
        val loaded = mutableSetOf<Int>()
        queries.forEachIndexed { index, query ->
            listeners += query.addSnapshotListener { snapshot, exception ->
                if (epoch != generation) return@addSnapshotListener
                if (exception != null) {
                    ready = false
                    CloudStore.report("Could not load " + collection.replace('_', ' ') + ": " + exception.localizedMessage)
                } else if (snapshot != null) {
                    try {
                        val items = snapshot.documents.map { CloudStore.decode(it.data!!, type) }
                        // Fresh snapshots replace any read-back copy for documents in this query.
                        val seen = items.map(id).toSet() + snapshots[index].orEmpty().map(id)
                        snapshots[-1] = snapshots[-1].orEmpty().filterNot { id(it) in seen }
                        snapshots[index] = items
                        loaded += index
                        ready = loaded.size == queries.size
                        publish()
                    } catch (e: Exception) {
                        ready = false
                        CloudStore.report("Invalid " + collection + " data: " + e.localizedMessage)
                    }
                }
            }
        }
    }

    private fun publish() {
        val merged = linkedMapOf<String, T>()
        snapshots.filterKeys { it >= 0 }.values.flatten().forEach { merged[id(it)] = it }
        snapshots[-1].orEmpty().forEach { merged[id(it)] = it }
        merged.putAll(overlays)
        state.value = merged.values.toList()
    }

    fun stop() {
        generation++
        listeners.forEach { it.remove() }
        listeners.clear()
        scope.cancel()
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        CloudStore.pendingWrites.value = (CloudStore.pendingWrites.value - pending).coerceAtLeast(0)
        pending = 0
        ready = false
        activeUid = null
        snapshots.clear()
        overlays.clear()
        revisions.clear()
        locks.clear()
        state.value = emptyList()
    }
}
