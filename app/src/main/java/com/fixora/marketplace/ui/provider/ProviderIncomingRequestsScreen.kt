package com.fixora.marketplace.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.BookingStatus
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun ProviderIncomingRequestsScreen(
    onNavigateBack: () -> Unit,
    onJobAccepted: (String) -> Unit
) {
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val providers by FixoraRepository.providers.collectAsState()
    val bookings by FixoraRepository.bookings.collectAsState()

    val myProfile = providers.firstOrNull { it.userId == currentUser.id }
        ?: run { Text("Your provider profile is loading or unavailable."); return }

    val requests = bookings.filter { b ->
        (b.status == BookingStatus.REQUESTED || b.status == BookingStatus.MATCHING) &&
        myProfile.skills.any { it.equals(b.serviceCategory, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Incoming Job Requests",
                subtitle = "${requests.size} Available in Your Categories",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (requests.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No pending requests right now in your coverage area.", color = FixoraTextSecondary)
                    }
                }
            } else {
                items(requests) { req ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = req.serviceTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(text = "PKR ${req.providerEarningsAed.toInt()}", fontWeight = FontWeight.Bold, color = FixoraSuccess, style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Customer: ${req.customerName} • ${req.address.area}, ${req.address.emirate}", color = FixoraTextSecondary, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Time: ${req.scheduledDate} (${req.scheduledTimeSlot})", color = FixoraTextSecondary, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Problem: ${req.issueDescription}", style = MaterialTheme.typography.bodyMedium, color = FixoraTextPrimary)

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { FixoraRepository.providerDeclineBooking(req.id, myProfile.id) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Decline")
                                }
                                Button(
                                    onClick = {
                                        FixoraRepository.providerAcceptBooking(req.id, myProfile.id)
                                        onJobAccepted(req.id)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                                ) {
                                    Text("Accept Job", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

