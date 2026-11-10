package com.fixora.marketplace.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.VerificationStatus
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun ProviderVerificationScreen(
    onNavigateBack: () -> Unit
) {
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val providers by FixoraRepository.providers.collectAsState()

    val myProfile = providers.firstOrNull { it.userId == currentUser.id }
        ?: providers.firstOrNull { it.id == "PROV-01" } ?: providers.first()

    var tradeLicense by remember { mutableStateOf(myProfile.tradeLicenseNumber ?: "DED-DXB-98442") }
    var cnicNumber by remember { mutableStateOf(myProfile.emiratesIdNumber ?: "784-1988-1234567-1") }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Document Verification",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FixoraPageBrush)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (myProfile.documentStatus == VerificationStatus.APPROVED) FixoraSuccessLight else FixoraWarningLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (myProfile.documentStatus == VerificationStatus.APPROVED) FixoraSuccess else FixoraWarning)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (myProfile.documentStatus == VerificationStatus.APPROVED) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = if (myProfile.documentStatus == VerificationStatus.APPROVED) FixoraSuccess else FixoraWarning,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (myProfile.documentStatus == VerificationStatus.APPROVED) "Verified Freelance Professional" else "Verification Pending Review",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (myProfile.documentStatus == VerificationStatus.APPROVED) FixoraSuccess else FixoraWarning
                        )
                        Text(
                            text = if (myProfile.documentStatus == VerificationStatus.APPROVED) "Your CNIC and permit have been approved by Fixora Operations." else "Fixora Operations is reviewing your submitted credentials.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Submitted credentials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = cnicNumber,
                        onValueChange = { cnicNumber = it },
                        label = { Text("CNIC (784-XXXX-XXXXXXX-X)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tradeLicense,
                        onValueChange = { tradeLicense = it },
                        label = { Text("DED / Freelance Permit Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            FixoraRepository.submitProviderDocuments(myProfile.id, tradeLicense, cnicNumber)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                    ) {
                        Text("Update & Submit for Verification", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


