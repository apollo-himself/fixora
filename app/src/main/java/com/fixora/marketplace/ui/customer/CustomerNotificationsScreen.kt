package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerNotificationsScreen(
    onNavigateBack: () -> Unit,
    onBookingClick: (String) -> Unit
) {
    val notifications by FixoraRepository.notifications.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") } // "All" or "Unread"

    val filteredNotifications = remember(notifications, selectedFilter) {
        if (selectedFilter == "Unread") {
            notifications.filter { !it.isRead }
        } else {
            notifications
        }
    }

    val unreadCount = remember(notifications) {
        notifications.count { !it.isRead }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Activity & Alerts",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = FixoraTextPrimary
                        )
                        Text(
                            text = if (unreadCount > 0) "$unreadCount unread notifications" else "All caught up",
                            style = MaterialTheme.typography.labelSmall,
                            color = FixoraTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FixoraTextPrimary
                        )
                    }
                },
                actions = {
                    if (unreadCount > 0) {
                        IconButton(onClick = { FixoraRepository.markAllNotificationsAsRead() }) {
                            Icon(
                                Icons.Outlined.DoneAll,
                                contentDescription = "Mark all as read",
                                tint = FixoraPrimary
                            )
                        }
                    }
                    if (notifications.isNotEmpty()) {
                        IconButton(onClick = { FixoraRepository.clearAllNotifications() }) {
                            Icon(
                                Icons.Outlined.DeleteSweep,
                                contentDescription = "Clear all",
                                tint = FixoraTextMuted
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FixoraSurface,
                    titleContentColor = FixoraTextPrimary,
                    navigationIconContentColor = FixoraTextPrimary,
                    actionIconContentColor = FixoraTextPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FixoraPageBrush)
        ) {
            // Filter Chips Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "All",
                    onClick = { selectedFilter = "All" },
                    label = { Text("All (${notifications.size})", fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FixoraAction,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedFilter == "Unread",
                    onClick = { selectedFilter = "Unread" },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Unread", fontWeight = FontWeight.SemiBold)
                            if (unreadCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(if (selectedFilter == "Unread") Color.White else FixoraPrimary, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$unreadCount",
                                        color = if (selectedFilter == "Unread") FixoraPrimary else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FixoraAction,
                        selectedLabelColor = Color.White
                    )
                )
            }

            HorizontalDivider(color = FixoraDivider)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredNotifications.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Outlined.NotificationsOff,
                                    contentDescription = null,
                                    tint = FixoraTextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (selectedFilter == "Unread") "No unread notifications" else "No notifications yet",
                                    color = FixoraTextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    items(filteredNotifications, key = { it.id }) { notif ->
                        val (icon, iconTint, bgTint) = when (notif.type) {
                            "STATUS_CHANGE" -> Triple(Icons.Default.Navigation, Color(0xFF0284C7), Color(0xFF0284C7).copy(alpha = 0.12f))
                            "QUOTATION_RECEIVED" -> Triple(Icons.AutoMirrored.Filled.ReceiptLong, Color(0xFFD97706), Color(0xFFD97706).copy(alpha = 0.12f))
                            "PAYMENT_SETTLED" -> Triple(Icons.Default.Verified, Color(0xFF059669), Color(0xFF059669).copy(alpha = 0.12f))
                            else -> Triple(Icons.Default.Handyman, FixoraPrimary, FixoraPrimaryLight)
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!notif.isRead) FixoraSurfaceVariant else FixoraSurface
                            ),
                            elevation = CardDefaults.cardElevation(if (!notif.isRead) 3.dp else 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    FixoraRepository.markNotificationAsRead(notif.id)
                                    notif.bookingId?.let { onBookingClick(it) }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(bgTint, shape = RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        tint = iconTint,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notif.title,
                                            fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = FixoraTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )

                                        if (!notif.isRead) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(FixoraAction, shape = CircleShape)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = notif.body,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FixoraTextSecondary,
                                        lineHeight = 18.sp
                                    )

                                    if (notif.bookingId != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Tap to track booking",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = FixoraPrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Icon(
                                                Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = FixoraPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
