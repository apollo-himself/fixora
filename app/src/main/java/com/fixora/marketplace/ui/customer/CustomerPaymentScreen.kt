package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.PaymentMethod
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun CustomerPaymentScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: (String) -> Unit
) {
    val bookings by FixoraRepository.bookings.collectAsState()
    val booking = bookings.firstOrNull { it.id == bookingId }

    var selectedMethod by remember { mutableStateOf(PaymentMethod.CREDIT_DEBIT_CARD) }
    var isProcessing by remember { mutableStateOf(false) }
    var showReviewModal by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableStateOf(5.0) }
    var reviewComment by remember { mutableStateOf("Great service, prompt and knowledgeable!") }

    if (booking == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Booking not found")
        }
        return
    }

    if (showReviewModal) {
        AlertDialog(
            onDismissRequest = { onPaymentSuccess(booking.id) },
            title = {
                Text(
                    text = "Rate Your Experience",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "How was the service provided by ${booking.providerName ?: "Technician"}?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FixoraTextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { reviewRating = star.toDouble() }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$star stars",
                                    tint = if (star <= reviewRating) Color(0xFFF59E0B) else FixoraBorder,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Write feedback") },
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        booking.providerId?.let { pId ->
                            FixoraRepository.submitReview(
                                bookingId = booking.id,
                                providerId = pId,
                                rating = reviewRating,
                                comment = reviewComment,
                                tags = listOf("Punctual", "Quality Work")
                            )
                        }
                        showReviewModal = false
                        onPaymentSuccess(booking.id)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                ) {
                    Text("Submit Review", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showReviewModal = false
                    onPaymentSuccess(booking.id)
                }) {
                    Text("Skip for Now")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Payment & Receipt",
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
                    Button(
                        onClick = {
                            isProcessing = true
                            val success = FixoraRepository.settleBookingPayment(booking.id, selectedMethod)
                            isProcessing = false
                            if (success) {
                                showReviewModal = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !isProcessing,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "Authorize & Pay PKR ${booking.agreedFinalPriceAed.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
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
            // Amount Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraDarkNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Amount Due (PKR)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PKR ${booking.agreedFinalPriceAed.toInt()}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Includes Provincial Sales Tax (PRA/SRB) & Guarantee",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Select Payment Method
            item {
                Text(text = "Choose Payment Method", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // JazzCash / Easypaisa
                    PaymentMethodRow(
                        title = "JazzCash / Easypaisa",
                        subtitle = "Instant mobile wallet & QR payment",
                        icon = Icons.Default.AccountBalanceWallet,
                        isSelected = selectedMethod == PaymentMethod.APPLE_GOOGLE_PAY,
                        onClick = { selectedMethod = PaymentMethod.APPLE_GOOGLE_PAY }
                    )
                    // Debit / Credit / Raast
                    PaymentMethodRow(
                        title = "Debit card / Raast ID",
                        subtitle = "Instant 1Link / Raast interbank settlement",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedMethod == PaymentMethod.CREDIT_DEBIT_CARD,
                        onClick = { selectedMethod = PaymentMethod.CREDIT_DEBIT_CARD }
                    )
                    // Cash on Completion
                    PaymentMethodRow(
                        title = "Cash on Completion (PKR Cash)",
                        subtitle = "Pay the professional in cash after the service",
                        icon = Icons.Default.Payments,
                        isSelected = selectedMethod == PaymentMethod.CASH_ON_COMPLETION,
                        onClick = { selectedMethod = PaymentMethod.CASH_ON_COMPLETION }
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentMethodRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) FixoraPrimaryLight else FixoraSurface,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, FixoraPrimary) else androidx.compose.foundation.BorderStroke(1.dp, FixoraBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) FixoraPrimary else FixoraTextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
            }
        }
    }
}
