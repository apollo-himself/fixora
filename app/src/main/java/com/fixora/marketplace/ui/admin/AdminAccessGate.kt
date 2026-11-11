package com.fixora.marketplace.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fixora.marketplace.data.auth.AdminAccess
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminAccessGate(onNavigateBack: () -> Unit, onSignIn: () -> Unit, content: @Composable () -> Unit) {
    val user by FixoraRepository.currentUser.collectAsState()
    val auth = remember { FirebaseAuth.getInstance() }
    var authenticatedUser by remember { mutableStateOf(auth.currentUser) }
    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { authenticatedUser = it.currentUser }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }
    val identity = authenticatedUser
    var hasAdminClaim by remember(identity?.uid) { mutableStateOf(false) }
    LaunchedEffect(identity?.uid) {
        hasAdminClaim = false
        identity?.getIdToken(false)
            ?.addOnSuccessListener { token -> hasAdminClaim = token.claims["admin"] == true }
    }
    if (AdminAccess.canOpen(user.id, identity?.uid, identity?.isEmailVerified == true, hasAdminClaim)) {
        content()
    } else {
        Scaffold(topBar = { FixoraTopBar("Admin access", canNavigateBack = true, onNavigateBack = onNavigateBack) }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Sign in with your admin Google account", style = MaterialTheme.typography.headlineSmall)
                Text("Use an approved account to open the admin portal.")
                Button(onClick = onSignIn) { Text("Go to sign-in") }
            }
        }
    }
}
