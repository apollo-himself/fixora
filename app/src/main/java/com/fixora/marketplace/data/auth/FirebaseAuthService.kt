package com.fixora.marketplace.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

object FirebaseAuthService {
    private val auth get() = FirebaseAuth.getInstance()
    private val lastSent = mutableMapOf<String, Long>()

    private suspend fun <T> attempt(block: suspend () -> T): AuthResult<T> = try {
        AuthResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AuthResult.Error(e.localizedMessage ?: "Unable to connect. Please try again.")
    }

    suspend fun signIn(email: String, password: String): AuthResult<FirebaseUser> = attempt {
        requireNotNull(auth.signInWithEmailAndPassword(email.trim(), password).await().user)
            .also { it.reload().await() }
    }

    suspend fun signUp(email: String, password: String): AuthResult<FirebaseUser> = attempt {
        requireNotNull(auth.createUserWithEmailAndPassword(email.trim(), password).await().user)
    }

    suspend fun sendPasswordReset(email: String): AuthResult<Unit> = attempt {
        auth.sendPasswordResetEmail(email.trim()).await()
        Unit
    }

    suspend fun sendVerificationEmail(email: String): AuthResult<Unit> = attempt {
        val user = requireNotNull(auth.currentUser) { "Please sign in again." }
        require(user.email.equals(email, ignoreCase = true)) { "Please sign in with this email address." }
        val now = System.currentTimeMillis()
        require(now - (lastSent[user.uid] ?: 0L) >= 30_000L) {
            "Please wait 30 seconds before requesting another verification email."
        }
        // Firebase's hosted action handler verifies the link in the browser.
        // No Dynamic Links or custom deep-link domain is required.
        user.sendEmailVerification().await()
        lastSent[user.uid] = now
        Unit
    }

    suspend fun checkEmailVerified(email: String): AuthResult<Boolean> = attempt {
        val user = requireNotNull(auth.currentUser) { "Please sign in again." }
        require(user.email.equals(email, ignoreCase = true)) { "Please sign in with this email address." }
        user.reload().await()
        if (user.isEmailVerified) user.getIdToken(true).await()
        user.isEmailVerified
    }
}
