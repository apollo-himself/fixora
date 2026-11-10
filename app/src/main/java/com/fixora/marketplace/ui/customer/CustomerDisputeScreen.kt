package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun CustomerDisputeScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    onDisputeSubmitted: () -> Unit
) {
    var reason by remember { mutableStateOf("Poor Quality of Work / Leaking Still Present") }
    var details by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "File a Dispute",
                subtitle = "Booking #$bookingId",
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
                colors = CardDefaults.cardColors(containerColor = FixoraEmergencyLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoraEmergency.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReportProblem, contentDescription = null, tint = FixoraEmergency, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Fixora Fair Resolution Guarantee: Our operations team will review your complaint, hold technician payout, and issue a full refund if standards are not met.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FixoraTextPrimary
                    )
                }
            }

            Text(text = "Reason for Complaint", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Text(text = "Explain What Happened", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                placeholder = { Text("Provide specific details about the issue or technician conduct...") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = {
                    isSubmitting = true
                    FixoraRepository.raiseDispute(bookingId, reason, details)
                    isSubmitting = false
                    onDisputeSubmitted()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FixoraEmergency)
            ) {
                Text("Submit Dispute to Fixora Admin", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
