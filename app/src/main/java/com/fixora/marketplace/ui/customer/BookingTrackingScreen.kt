package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
import com.fixora.marketplace.data.model.BookingStatus
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.components.SimulatedLiveTrackingMap
import com.fixora.marketplace.ui.components.StarRatingBar
import com.fixora.marketplace.ui.components.StatusBadge
import com.fixora.marketplace.ui.components.WarrantyCertificateCard
import com.fixora.marketplace.ui.theme.*

@Composable
fun BookingTrackingScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    onViewCompletion: (String) -> Unit,
    onPayNow: (String) -> Unit
) {
    val bookings by FixoraRepository.bookings.collectAsState()
    val quotations by FixoraRepository.quotations.collectAsState()

    val booking = bookings.firstOrNull { it.id == bookingId }
    val pendingQuote = quotations.firstOrNull { it.bookingId == bookingId && !it.isApprovedByCustomer }

    var showCancelDialog by remember { mutableStateOf(false) }

    if (booking == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Booking not found")
        }
        return
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Booking?") },
            text = { Text("Are you sure you want to cancel this booking? Cancellations within 1 hour may incur a call-out fee.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        FixoraRepository.cancelBooking(booking.id, "Customer requested cancellation")
                        showCancelDialog = false
                    }
                ) {
                    Text("Confirm Cancel", color = FixoraEmergency, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Booking")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Job Tracking",
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
                    when (booking.status) {
                        BookingStatus.COMPLETED -> {
                            Button(
                                onClick = { onViewCompletion(booking.id) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FixoraSuccess)
                            ) {
                                Text("Review & Confirm Completion", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        BookingStatus.PAYMENT_SETTLED -> {
                            Surface(
                                color = FixoraSuccessLight,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FixoraSuccess)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Job Completed & Paid in Full", fontWeight = FontWeight.Bold, color = FixoraSuccess)
                                }
                            }
                        }
                        else -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                if (booking.status != BookingStatus.PAYMENT_SETTLED) {
                                    Button(
                                        onClick = { onPayNow(booking.id) },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                                    ) {
                                        Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Pay PKR ${booking.agreedFinalPriceAed}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onOpenChat(booking.id) },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Chat Pro")
                                    }

                                    if (booking.status == BookingStatus.REQUESTED || booking.status == BookingStatus.MATCHING || booking.status == BookingStatus.PROVIDER_ACCEPTED) {
                                        OutlinedButton(
                                            onClick = { showCancelDialog = true },
                                            modifier = Modifier.weight(1f).height(48.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoraEmergency)
                                        ) {
                                            Text("Cancel")
                                        }
                                    }
                                }
                            }
                        }
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
            // Status Card
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
                            Text(
                                text = booking.serviceTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(status = booking.status)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Scheduled: ${booking.scheduledDate} (${booking.scheduledTimeSlot})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FixoraTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${booking.address.buildingOrVilla}, ${booking.address.area}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FixoraTextSecondary
                        )
                    }
                }
            }

            // Simulated Live Map & Technician En-Route Tracking
            if (booking.providerName != null && booking.status != BookingStatus.CUSTOMER_CANCELLED && booking.status != BookingStatus.PROVIDER_CANCELLED) {
                item {
                    SimulatedLiveTrackingMap(
                        providerName = booking.providerName,
                        destinationArea = booking.address.area,
                        status = booking.status
                    )
                }
            }

            // Dual Completion Sign-off Card (Show when job is in progress, awaiting approval, or completed)
            if (booking.status == BookingStatus.IN_PROGRESS || booking.status == BookingStatus.AWAITING_CUSTOMER_APPROVAL || booking.status == BookingStatus.COMPLETED) {
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
                                Text(
                                    text = "Completion Sign-off",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoraTextPrimary
                                )
                                Text(
                                    text = if (booking.isCompletedByCustomer && booking.isCompletedByProvider) "Both Signed Off ✓" else "Requires Both Parties",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (booking.isCompletedByCustomer && booking.isCompletedByProvider) FixoraSuccess else FixoraWarning,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            if (booking.isCompletedByProvider) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = if (booking.isCompletedByProvider) FixoraSuccess else FixoraTextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Technician",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Text(
                                            if (booking.isCompletedByProvider) "Signed Off ✓" else "Pending...",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (booking.isCompletedByProvider) FixoraSuccess else FixoraTextMuted,
                                            fontSize = 11.sp
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
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            if (booking.isCompletedByCustomer) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                                            contentDescription = null,
                                            tint = if (booking.isCompletedByCustomer) FixoraSuccess else FixoraTextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "You (Customer)",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Text(
                                            if (booking.isCompletedByCustomer) "Signed Off ✓" else "Not Signed",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (booking.isCompletedByCustomer) FixoraSuccess else FixoraTextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            if (!booking.isCompletedByCustomer) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        FixoraRepository.customerConfirmJobCompleted(booking.id)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FixoraSuccess)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("I Confirm Job is Done", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Digital 7-Day Guarantee Warranty Certificate (When Completed / Settled)
            if (booking.status == BookingStatus.COMPLETED || booking.status == BookingStatus.PAYMENT_SETTLED) {
                item {
                    WarrantyCertificateCard(
                        bookingId = booking.id,
                        serviceTitle = booking.serviceTitle,
                        providerName = booking.providerName ?: "Certified Pro",
                        warrantyDays = 7,
                        completedTimestamp = booking.updatedAt
                    )
                }
            }

            // Pending Quotation Banner if submitted by provider
            if (pendingQuote != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraWarningLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FixoraWarning.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RequestQuote, contentDescription = null, tint = FixoraWarning)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Quotation Approval Required",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoraWarning
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Technician notes: ${pendingQuote.notes}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Labor: PKR ${pendingQuote.laborCostAed.toInt()} | Parts: PKR ${pendingQuote.partsCostAed.toInt()} | Total: PKR ${pendingQuote.totalAed.toInt()}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = FixoraTextPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { FixoraRepository.approveQuotation(pendingQuote.id) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FixoraWarning)
                            ) {
                                Text("Approve Quotation (PKR ${pendingQuote.totalAed.toInt()})", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Assigned Provider Card (if assigned)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Assigned Freelance Technician", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (booking.providerName != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(FixoraDarkNavy),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = booking.providerName.take(1),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = booking.providerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(16.dp))
                                    }
                                    Text(text = booking.providerPhone ?: "+92 50 ...", style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)
                                }
                                booking.providerRating?.let {
                                    StarRatingBar(rating = it)
                                }
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp, color = FixoraPrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Matching available certified providers...", style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)
                            }
                        }
                    }
                }
            }

            // Work Proof Photos (When Available)
            if (booking.beforePhotoUrl != null || booking.afterPhotoUrl != null) {
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Job Verification Photos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Surface(
                                    color = FixoraSuccessLight,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Verified by Pro", color = FixoraSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Before Photo Box
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = FixoraSurfaceVariant,
                                    modifier = Modifier.weight(1f).height(110.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.Image, contentDescription = null, tint = FixoraTextMuted, modifier = Modifier.size(28.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Before Work", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        Text("Initial Diagnostic", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted, fontSize = 10.sp)
                                    }
                                }
                                // After Photo Box
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = FixoraSuccessLight,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoraSuccess.copy(alpha = 0.4f)),
                                    modifier = Modifier.weight(1f).height(110.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FixoraSuccess, modifier = Modifier.size(28.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("After Work", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = FixoraSuccess)
                                        Text("Repaired & Tested", style = MaterialTheme.typography.labelSmall, color = FixoraSuccess, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Timeline Steps
            item {
                Text(text = "Job Lifecycle Timeline", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            items(booking.statusHistory) { event ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(FixoraPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = event.status.name.replace('_', ' '),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (event.note != null) {
                            Text(text = event.note, style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

