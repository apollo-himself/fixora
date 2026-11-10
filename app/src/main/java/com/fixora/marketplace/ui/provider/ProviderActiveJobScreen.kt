package com.fixora.marketplace.ui.provider

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.BookingStatus
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.components.StatusBadge
import com.fixora.marketplace.ui.theme.*

@Composable
fun ProviderActiveJobScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    onCreateQuotation: (String) -> Unit
) {
    val context = LocalContext.current
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
                title = "Job Progress Controls",
                subtitle = "Booking #${booking.id}",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
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
            // Customer Info Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = booking.customerName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            StatusBadge(status = booking.status)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Phone: ${booking.customerPhone}", style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)
                        Text(text = "Property: ${booking.propertyType.name} (${booking.urgency.name})", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = FixoraDivider)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = booking.address.fullAddressDisplay, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val geoUri = Uri.parse("geo:${booking.address.latitude},${booking.address.longitude}?q=${Uri.encode(booking.address.fullAddressDisplay)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                                    context.startActivity(Intent.createChooser(mapIntent, "Open Navigation"))
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Navigate Map")
                            }
                            Button(
                                onClick = { onOpenChat(booking.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Chat Customer")
                            }
                        }
                    }
                }
            }

            // Issue Description
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Customer Reported Issue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = booking.issueDescription, style = MaterialTheme.typography.bodyLarge, color = FixoraTextSecondary)
                    }
                }
            }

            // Progression Action Step
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraDarkNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Next Progression Step",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        when (booking.status) {
                            BookingStatus.PROVIDER_ACCEPTED, BookingStatus.CONFIRMED -> {
                                Text(
                                    text = "Ready to travel to customer address?",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        FixoraRepository.updateBookingStatus(
                                            booking.id,
                                            BookingStatus.EN_ROUTE,
                                            "Technician en route to ${booking.address.area}"
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                                ) {
                                    Text("Start Travel (En Route)", fontWeight = FontWeight.Bold)
                                }
                            }
                            BookingStatus.EN_ROUTE -> {
                                Text(
                                    text = "Have you arrived at the building/villa?",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        FixoraRepository.updateBookingStatus(
                                            booking.id,
                                            BookingStatus.ARRIVED,
                                            "Technician arrived at building security"
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                                ) {
                                    Text("Mark Arrived on Site", fontWeight = FontWeight.Bold)
                                }
                            }
                            BookingStatus.ARRIVED -> {
                                Text(
                                    text = "Meeting customer and starting diagnostic?",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        FixoraRepository.updateBookingStatus(
                                            booking.id,
                                            BookingStatus.IN_PROGRESS,
                                            "Diagnostic & repair work started"
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                                ) {
                                    Text("Start Work (In Progress)", fontWeight = FontWeight.Bold)
                                }
                            }
                            BookingStatus.IN_PROGRESS, BookingStatus.AWAITING_CUSTOMER_APPROVAL -> {
                                Text(
                                    text = "Job In Progress",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Need parts or extra labor? Submit a quotation for customer approval. Once finished, complete the job.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Dual Completion Sign-off",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                color = if (booking.isCompletedByProvider) FixoraSuccess.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        if (booking.isCompletedByProvider) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                        contentDescription = null,
                                                        tint = if (booking.isCompletedByProvider) FixoraSuccess else Color.White.copy(alpha = 0.7f),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        if (booking.isCompletedByProvider) "Pro: Done ✓" else "Pro: Pending",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                            Surface(
                                                color = if (booking.isCompletedByCustomer) FixoraSuccess.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        if (booking.isCompletedByCustomer) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                        contentDescription = null,
                                                        tint = if (booking.isCompletedByCustomer) FixoraSuccess else Color.White.copy(alpha = 0.7f),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        if (booking.isCompletedByCustomer) "Client: Done ✓" else "Client: Pending",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { onCreateQuotation(booking.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.RequestQuote, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Submit Parts/Labor Quotation")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        FixoraRepository.providerConfirmJobCompleted(booking.id)
                                    },
                                    enabled = !booking.isCompletedByProvider,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FixoraSuccess,
                                        disabledContainerColor = FixoraSuccess.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (booking.isCompletedByProvider) "You Signed Off: Job Completed ✓" else "Mark Job as Done (Sign-Off)",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            BookingStatus.COMPLETED -> {
                                Text(
                                    text = "Job Marked Complete",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = FixoraSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Customer has been notified to inspect and authorize payment.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            BookingStatus.PAYMENT_SETTLED -> {
                                Text(
                                    text = "Payment Settled! PKR ${booking.providerEarningsAed} credited.",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = FixoraSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            else -> {
                                Text(text = "Status: ${booking.status.name}", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
