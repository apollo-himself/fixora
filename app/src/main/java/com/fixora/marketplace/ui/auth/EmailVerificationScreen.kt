package com.fixora.marketplace.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.auth.AuthResult
import com.fixora.marketplace.data.auth.FirebaseAuthService
import com.fixora.marketplace.ui.components.FixoraBrandMark
import com.fixora.marketplace.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun EmailVerificationScreen(
    email: String,
    onVerified: () -> Unit,
    onSignOut: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var isCheckingVerification by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<Pair<Boolean, String>?>(null) } // (isError, message)

    // Automatically send verification email once when screen is first shown
    LaunchedEffect(email) {
        isResending = true
        when (val result = FirebaseAuthService.sendVerificationEmail(email)) {
            is AuthResult.Success -> statusMessage = false to "Verification link sent. Check your inbox and spam folder."
            is AuthResult.Error -> statusMessage = true to result.message
        }
        isResending = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FixoraPageBrush),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dark navy header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FixoraDarkNavy)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FixoraBrandMark(Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Verify Your Email",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "One last step before you access Fixora",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Email icon + address card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FixoraSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, FixoraPrimary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(FixoraPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = FixoraPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Email to verify:",
                        style = MaterialTheme.typography.labelSmall,
                        color = FixoraTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = FixoraTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Instructions
        Text(
            text = "Check your inbox (and spam folder) for a\nverification link, then tap the button below.",
            style = MaterialTheme.typography.bodyMedium,
            color = FixoraTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Status message
        statusMessage?.let { (isError, msg) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isError) FixoraEmergency.copy(alpha = 0.1f) else FixoraPrimary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = msg,
                    color = if (isError) FixoraEmergency else FixoraPrimary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(14.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // "I've Verified" button
        Button(
            onClick = {
                scope.launch {
                    isCheckingVerification = true
                    statusMessage = null
                    val result = FirebaseAuthService.checkEmailVerified(email)
                    isCheckingVerification = false
                    when (result) {
                        is AuthResult.Success -> {
                            if (result.data) {
                                onVerified()
                            } else {
                                statusMessage = true to "Email not verified yet. Please click the link in your inbox."
                            }
                        }
                        is AuthResult.Error -> {
                            statusMessage = true to result.message
                        }
                    }
                }
            },
            enabled = !isCheckingVerification && !isResending,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FixoraAction),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
        ) {
            if (isCheckingVerification) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Checking...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            } else {
                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("I've Verified My Email", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Resend button
        OutlinedButton(
            onClick = {
                scope.launch {
                    isResending = true
                    statusMessage = null
                    val result = FirebaseAuthService.sendVerificationEmail(email)
                    isResending = false
                    when (result) {
                        is AuthResult.Success -> {
                            statusMessage = false to "Verification email sent! Check your inbox."
                        }
                        is AuthResult.Error -> {
                            statusMessage = true to result.message
                        }
                    }
                }
            },
            enabled = !isCheckingVerification && !isResending,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FixoraPrimary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoraPrimary)
        ) {
            if (isResending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = FixoraPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sending...", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            } else {
                Text("Resend Verification Email", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign out / change email
        TextButton(
            onClick = onSignOut,
            enabled = !isCheckingVerification && !isResending
        ) {
            Text(
                text = "Use a different email / Sign out",
                style = MaterialTheme.typography.bodySmall,
                color = FixoraTextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
