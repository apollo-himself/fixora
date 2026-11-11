package com.fixora.marketplace.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.BookingStatus
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun ProviderEarningsScreen(
    onNavigateBack: () -> Unit
) {
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val providers by FixoraRepository.providers.collectAsState()
    val bookings by FixoraRepository.bookings.collectAsState()
    val ledger by FixoraRepository.ledger.collectAsState()

    val myProfile = providers.firstOrNull { it.userId == currentUser.id }
        ?: run { Text("Your provider profile is loading or unavailable."); return }

    val myCompletedBookings = bookings.filter {
        it.providerId == myProfile.id && it.status == BookingStatus.PAYMENT_SETTLED
    }

    val grossVolume = myCompletedBookings.sumOf { it.agreedFinalPriceAed }
    val netEarnings = myCompletedBookings.sumOf { it.providerEarningsAed }
    val commissionPaid = myCompletedBookings.sumOf { it.platformCommissionAed }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Earnings & Payouts",
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
            // Net Earnings Card
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
                            text = "Net Available Balance (PKR)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PKR ${netEarnings.toInt()}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = FixoraSuccess.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Weekly Transfer to Bank Account / EasyPaisa / JazzCash",
                                color = FixoraSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Summary Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Marketplace Payout Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total Gross Bookings", color = FixoraTextSecondary)
                            Text(text = "PKR ${grossVolume.toInt()}", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Fixora Platform Commission (15%)", color = FixoraTextSecondary)
                            Text(text = "-PKR ${commissionPaid.toInt()}", color = FixoraEmergency, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Provider Net Earnings (85%)", fontWeight = FontWeight.Bold)
                            Text(text = "PKR ${netEarnings.toInt()}", color = FixoraSuccess, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Ledger Transaction History
            item {
                Text(text = "Recent Settled Jobs", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            if (myCompletedBookings.isEmpty()) {
                item {
                    Text(text = "No settled jobs yet. Complete active jobs to see earnings here.", color = FixoraTextMuted)
                }
            } else {
                items(myCompletedBookings) { booking ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(FixoraSuccessLight, shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = FixoraSuccess, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = booking.serviceTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text(text = "Customer: ${booking.customerName}", style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
                            }
                            Text(text = "+PKR ${booking.providerEarningsAed.toInt()}", fontWeight = FontWeight.Bold, color = FixoraSuccess, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}


