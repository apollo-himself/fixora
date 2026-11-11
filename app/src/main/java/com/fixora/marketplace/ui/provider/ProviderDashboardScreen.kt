package com.fixora.marketplace.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.local.AppPreferences
import com.fixora.marketplace.data.model.BookingStatus
import com.fixora.marketplace.data.model.UserRole
import com.fixora.marketplace.data.model.displayName
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.components.StarRatingBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun ProviderDashboardScreen(
    onViewRequests: () -> Unit,
    onActiveJobClick: (String) -> Unit,
    onViewEarnings: () -> Unit,
    onViewAvailability: () -> Unit,
    onViewVerification: () -> Unit,
    onRoleSwitched: (UserRole) -> Unit,
    onCreateGigClick: () -> Unit = {},
    onBrowseClientRequestsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val providers by FixoraRepository.providers.collectAsState()
    val bookings by FixoraRepository.bookings.collectAsState()

    // Find current provider profile
    val myProfile = providers.firstOrNull { it.userId == currentUser.id }
        ?: run { Text("Your provider profile is loading or unavailable."); return }

    // Active assigned job
    val activeJob = bookings.firstOrNull {
        it.providerId == myProfile.id &&
        it.status != BookingStatus.PAYMENT_SETTLED &&
        it.status != BookingStatus.CUSTOMER_CANCELLED &&
        it.status != BookingStatus.PROVIDER_DECLINED &&
        it.status != BookingStatus.REFUNDED
    }

    // Pending incoming job requests in provider's service area & skills
    val incomingRequests = bookings.filter { b ->
        (b.status == BookingStatus.REQUESTED || b.status == BookingStatus.MATCHING) &&
        myProfile.skills.any { it.equals(b.serviceCategory, ignoreCase = true) } &&
        myProfile.serviceAreas.any { it.equals(b.address.area, ignoreCase = true) || it.equals(b.address.emirate, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Technician Console",
                subtitle = myProfile.name
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
            // ── Switch to Customer Mode Banner ──
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FixoraSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoraPrimary.copy(alpha = 0.4f)),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            FixoraRepository.switchToCustomerRole()
                            AppPreferences.saveUserSession(context, FixoraRepository.currentUser.value)
                            onRoleSwitched(UserRole.CUSTOMER)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(FixoraPrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = FixoraPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Looking to hire a pro instead?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoraTextPrimary
                                )
                                Text(
                                    text = "Switch back to Customer Mode",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FixoraPrimary
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FixoraPrimaryLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Switch",
                                    color = FixoraPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = FixoraPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Online/Offline Availability Bar
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (myProfile.isOnline) FixoraSuccessLight else FixoraSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (myProfile.isOnline) FixoraSuccess else FixoraBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (myProfile.isOnline) FixoraSuccess else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (myProfile.isOnline) "You are Online & Receiving Jobs" else "You are Currently Offline",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (myProfile.isOnline) FixoraSuccess else FixoraTextPrimary
                                )
                                Text(
                                    text = if (myProfile.isOnline) "Ready for dispatch in ${myProfile.serviceAreas.take(2).joinToString(", ")}" else "Turn on to get matched with bookings",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FixoraTextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = myProfile.isOnline,
                            onCheckedChange = { FixoraRepository.toggleProviderOnline(myProfile.id) },
                            colors = SwitchDefaults.colors(checkedThumbColor = FixoraSuccess, checkedTrackColor = FixoraSuccessLight)
                        )
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(
                        title = "Completed",
                        value = "${myProfile.completedJobs}",
                        subtitle = "Jobs",
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Rating",
                        value = "${myProfile.rating}",
                        subtitle = "${myProfile.reviewCount} Reviews",
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Net Rate",
                        value = "85%",
                        subtitle = "15% Fee",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Active Job Card
            if (activeJob != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraPrimaryDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onActiveJobClick(activeJob.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE ASSIGNMENT",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = "Status: ${activeJob.status.displayName()}",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = activeJob.serviceTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Customer: ${activeJob.customerName} • ${activeJob.address.buildingOrVilla}, ${activeJob.address.area}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Your Payout: PKR ${activeJob.providerEarningsAed}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = { onActiveJobClick(activeJob.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = FixoraPrimaryDark),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Open Job Controls", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Incoming Job Requests
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Incoming Nearby Requests (${incomingRequests.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onViewRequests) {
                        Text("See All", color = FixoraPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (incomingRequests.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No pending requests right now. Ensure you are online.", color = FixoraTextSecondary)
                        }
                    }
                }
            } else {
                items(incomingRequests.take(2)) { req ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = req.serviceTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(text = "PKR ${req.providerEarningsAed.toInt()}", fontWeight = FontWeight.Bold, color = FixoraSuccess, style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Location: ${req.address.area}, ${req.address.emirate}", color = FixoraTextSecondary, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Issue: ${req.issueDescription}", maxLines = 2, style = MaterialTheme.typography.bodySmall, color = FixoraTextMuted)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { FixoraRepository.providerDeclineBooking(req.id, myProfile.id) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Decline")
                                }
                                Button(
                                    onClick = {
                                        FixoraRepository.providerAcceptBooking(req.id, myProfile.id)
                                        onActiveJobClick(req.id)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                                ) {
                                    Text("Accept Job", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Quick Tools Menu
            item {
                Text(text = "Technician Tools", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        ToolMenuRow(icon = Icons.Default.AddBusiness, title = "Create New Maintenance Gig", subtitle = "Offer a new freelance service with custom packages & PKR rates", onClick = onCreateGigClick)
                        HorizontalDivider(color = FixoraDivider)
                        ToolMenuRow(icon = Icons.Default.WorkHistory, title = "Browse Open Client Requests", subtitle = "Send bids/quotes to clients who posted jobs", onClick = onBrowseClientRequestsClick)
                        HorizontalDivider(color = FixoraDivider)
                        ToolMenuRow(icon = Icons.Default.AccountBalanceWallet, title = "Earnings & Ledger", subtitle = "View payouts and 15% platform commission fees", onClick = onViewEarnings)
                        HorizontalDivider(color = FixoraDivider)
                        ToolMenuRow(icon = Icons.Default.Map, title = "Service Areas & Hours", subtitle = "Manage service coverage and schedule", onClick = onViewAvailability)
                        HorizontalDivider(color = FixoraDivider)
                        ToolMenuRow(icon = Icons.Default.VerifiedUser, title = "Document Verification", subtitle = "Identity and business documents", onClick = onViewVerification)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FixoraPrimary)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
        }
    }
}

@Composable
fun ToolMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = FixoraTextMuted)
    }
}


