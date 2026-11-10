package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.components.WarrantyCertificateCard
import com.fixora.marketplace.ui.theme.*

@Composable
fun JobCompletionCustomerScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    onProceedToPayment: (String) -> Unit,
    onRaiseDispute: (String) -> Unit
) {
    val bookings by FixoraRepository.bookings.collectAsState()
    val booking = bookings.firstOrNull { it.id == bookingId }

    if (booking == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Booking not found")
        }
        return
    }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Job Completion Review",
                subtitle = "Booking #${booking.id}",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            Surface(
                color = FixoraSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!booking.isCompletedByCustomer) {
                        Button(
                            onClick = {
                                FixoraRepository.customerConfirmJobCompleted(booking.id)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FixoraSuccess)
                        ) {
                            Icon(Icons.Default.TaskAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("I Confirm: Job is Done!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    } else {
                        Surface(
                            color = FixoraSuccessLight,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FixoraSuccess, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("You Confirmed Job Completion ✓", fontWeight = FontWeight.Bold, color = FixoraSuccess)
                            }
                        }
                    }

                    Button(
                        onClick = { onProceedToPayment(booking.id) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                    ) {
                        Text("Proceed to Settlement (PKR ${booking.agreedFinalPriceAed})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { onRaiseDispute(booking.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Work Incomplete? File a Dispute", color = FixoraEmergency, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FixoraPageBrush),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Dual Completion Sign-off Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FixoraTextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (booking.isCompletedByProvider) FixoraSuccessLight else FixoraSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (booking.isCompletedByProvider) FixoraSuccess.copy(alpha = 0.5f) else FixoraDivider
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        if (booking.isCompletedByProvider) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = if (booking.isCompletedByProvider) FixoraSuccess else FixoraTextMuted,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        "Technician",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        if (booking.isCompletedByProvider) "Signed Off ✓" else "Pending...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (booking.isCompletedByProvider) FixoraSuccess else FixoraTextMuted
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (booking.isCompletedByCustomer) FixoraSuccessLight else FixoraSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (booking.isCompletedByCustomer) FixoraSuccess.copy(alpha = 0.5f) else FixoraDivider
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        if (booking.isCompletedByCustomer) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                                        contentDescription = null,
                                        tint = if (booking.isCompletedByCustomer) FixoraSuccess else FixoraTextMuted,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        "Customer (You)",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        if (booking.isCompletedByCustomer) "Signed Off ✓" else "Action Needed",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (booking.isCompletedByCustomer) FixoraSuccess else FixoraEmergency
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Before & After Proof Photo Simulation
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Job Verification Photos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Before Photo Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FixoraSurfaceVariant,
                                modifier = Modifier.weight(1f).height(120.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = FixoraTextMuted, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Before Work", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Text("Verified Upload", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                                }
                            }
                            // After Photo Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FixoraSuccessLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, FixoraSuccess.copy(alpha = 0.4f)),
                                modifier = Modifier.weight(1f).height(120.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FixoraSuccess, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("After Work", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FixoraSuccess)
                                    Text("Fixed & Tested", style = MaterialTheme.typography.labelSmall, color = FixoraSuccess)
                                }
                            }
                        }
                    }
                }
            }

            // 7-Day Warranty Certificate Preview
            item {
                WarrantyCertificateCard(
                    bookingId = booking.id,
                    serviceTitle = booking.serviceTitle,
                    providerName = booking.providerName ?: "Certified Pro",
                    warrantyDays = 7,
                    completedTimestamp = System.currentTimeMillis()
                )
            }

            // Invoice Overview
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Final Itemized Invoice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = booking.serviceTitle, color = FixoraTextSecondary)
                            Text(text = "PKR ${booking.basePriceAed.toInt()}")
                        }
                        if (booking.partsLaborQuotationAed != null && booking.partsLaborQuotationAed > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Approved Parts & Extra Labor", color = FixoraTextSecondary)
                                Text(text = "PKR ${booking.partsLaborQuotationAed.toInt()}")
                            }
                        }
                        if (booking.emergencySurgeFeeAed > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Emergency 24/7 Surge Fee", color = FixoraEmergency)
                                Text(text = "PKR ${booking.emergencySurgeFeeAed.toInt()}", color = FixoraEmergency)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Sales Tax/GST (5%)", color = FixoraTextSecondary)
                            Text(text = "PKR ${booking.vatAed}")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = FixoraDivider)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total Amount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(text = "PKR ${booking.agreedFinalPriceAed}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FixoraPrimary)
                        }
                    }
                }
            }
        }
    }
}

