package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.fixora.marketplace.data.i18n.LocalAppStrings
import com.fixora.marketplace.data.model.Booking
import com.fixora.marketplace.data.model.BookingStatus
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.components.StatusBadge
import com.fixora.marketplace.ui.theme.*

@Composable
fun CustomerBookingsScreen(
    onNavigateBack: () -> Unit,
    onBookingClick: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val bookings by FixoraRepository.bookings.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Active, 1: Past

    val userBookings = bookings.filter { it.customerId == currentUser.id }
    val activeBookings = userBookings.filter {
        it.status != BookingStatus.PAYMENT_SETTLED &&
        it.status != BookingStatus.CUSTOMER_CANCELLED &&
        it.status != BookingStatus.PROVIDER_DECLINED &&
        it.status != BookingStatus.REFUNDED
    }
    val pastBookings = userBookings.filter {
        it.status == BookingStatus.PAYMENT_SETTLED ||
        it.status == BookingStatus.CUSTOMER_CANCELLED ||
        it.status == BookingStatus.PROVIDER_DECLINED ||
        it.status == BookingStatus.REFUNDED
    }

    val displayList = if (selectedTab == 0) activeBookings else pastBookings

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = strings.myBookings,
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
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = FixoraSurface,
                contentColor = FixoraPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(strings.activeBookingsTab(activeBookings.size), fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(strings.historyBookingsTab(pastBookings.size), fontWeight = FontWeight.Bold) }
                )
            }

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = FixoraTextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedTab == 0) strings.noActiveBookings else strings.noPastBookings,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.exploreServicesPrompt,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FixoraTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(displayList) { booking ->
                        BookingItemCard(
                            booking = booking,
                            onClick = { onBookingClick(booking.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItemCard(
    booking: Booking,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${booking.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = FixoraTextMuted,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = booking.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = booking.serviceTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Technician: ${booking.providerName ?: "Matching in progress..."}",
                style = MaterialTheme.typography.bodyMedium,
                color = FixoraTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Scheduled: ${booking.scheduledDate} • ${booking.scheduledTimeSlot}",
                style = MaterialTheme.typography.bodySmall,
                color = FixoraTextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = FixoraDivider)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PKR ${booking.agreedFinalPriceAed}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FixoraPrimary
                )
                Text(
                    text = "Track / View Details →",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = FixoraPrimary
                )
            }
        }
    }
}

