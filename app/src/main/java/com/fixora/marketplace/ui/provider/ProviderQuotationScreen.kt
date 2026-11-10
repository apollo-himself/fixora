package com.fixora.marketplace.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun ProviderQuotationScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    onQuotationSubmitted: () -> Unit
) {
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val bookings by FixoraRepository.bookings.collectAsState()
    val booking = bookings.firstOrNull { it.id == bookingId }

    var laborCostText by remember { mutableStateOf("100") }
    var partsCostText by remember { mutableStateOf("80") }
    var notesText by remember { mutableStateOf("Replaced AC capacitor and leak-tested copper joint.") }

    val labor = laborCostText.toDoubleOrNull() ?: 0.0
    val parts = partsCostText.toDoubleOrNull() ?: 0.0
    val total = labor + parts

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Create Quotation",
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
                colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Itemize Additional Work", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Customer must review and approve this quotation before you carry out the additional work.", style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = laborCostText,
                        onValueChange = { laborCostText = it },
                        label = { Text("Extra Labor Cost (PKR)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = partsCostText,
                        onValueChange = { partsCostText = it },
                        label = { Text("Replacement Parts / Materials (PKR)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Explanation for Customer") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Quotation Added", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "PKR ${total.toInt()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FixoraPrimary)
                    }
                }
            }

            Button(
                onClick = {
                    FixoraRepository.submitQuotation(
                        bookingId = bookingId,
                        providerId = currentUser.id,
                        providerName = currentUser.name,
                        laborCostAed = labor,
                        partsCostAed = parts,
                        notes = notesText
                    )
                    onQuotationSubmitted()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
            ) {
                Text("Submit Quotation to Customer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

