package com.fixora.marketplace.ui.admin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.*
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

// ─── Tab definitions ──────────────────────────────────────────────────────────
private enum class AdminTab(val label: String, val icon: ImageVector) {
    OVERVIEW("Overview", Icons.Default.Dashboard),
    BOOKINGS("Bookings", Icons.Default.CalendarToday),
    PROVIDERS("Providers", Icons.Default.Build),
    FINANCE("Finance", Icons.Default.AccountBalance)
}

@Composable
fun AdminDashboardScreen(
    onViewVerifications: () -> Unit,
    onViewDisputes: () -> Unit,
    onViewCategories: () -> Unit,
    onRoleSwitched: (UserRole) -> Unit,
    onNavigateBack: () -> Unit = { onRoleSwitched(UserRole.CUSTOMER) }
) {
    val currentUser     by FixoraRepository.currentUser.collectAsState()
    val providers       by FixoraRepository.providers.collectAsState()
    val bookings        by FixoraRepository.bookings.collectAsState()
    val ledger          by FixoraRepository.ledger.collectAsState()
    val disputes        by FixoraRepository.disputes.collectAsState()
    val adminSettings   by FixoraRepository.adminSettings.collectAsState()
    val registeredUsers by FixoraRepository.registeredUsers.collectAsState()
    val gigs            by FixoraRepository.gigs.collectAsState()
    val jobRequests     by FixoraRepository.jobRequests.collectAsState()

    var selectedTab by remember { mutableStateOf(AdminTab.OVERVIEW) }

    // ── Computed KPIs ──────────────────────────────────────────────────────────
    val pendingVerifications = providers.filter { it.documentStatus == VerificationStatus.PENDING }
    val openDisputes         = disputes.filter { it.status == DisputeStatus.OPEN }
    val activeBookings       = bookings.filter {
        it.status !in listOf(
            BookingStatus.COMPLETED, BookingStatus.PAYMENT_SETTLED,
            BookingStatus.CUSTOMER_CANCELLED, BookingStatus.PROVIDER_CANCELLED,
            BookingStatus.REFUNDED, BookingStatus.EXPIRED
        )
    }
    val completedBookings    = bookings.filter { it.status == BookingStatus.PAYMENT_SETTLED || it.status == BookingStatus.COMPLETED }

    val totalGMV        = bookings.sumOf { it.agreedFinalPriceAed }
    val totalCommission = bookings.sumOf { it.platformCommissionAed }
    val totalPayouts    = bookings.sumOf { it.providerEarningsAed }

    val onlineProviders   = providers.count { it.isOnline }
    val verifiedProviders = providers.count { it.isVerified && it.documentStatus == VerificationStatus.APPROVED }
    val avgProviderRating = if (providers.isNotEmpty()) providers.map { it.rating }.average() else 0.0

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Admin Console",
                subtitle = "${currentUser.name} · Root Administrator",
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
            // ── Tab Row ────────────────────────────────────────────────────────
            _AdminTabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            // ── Tab Content ────────────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    AdminTab.OVERVIEW -> {
                        // Revenue header
                        item {
                            _RevenueHeroCard(
                                totalCommission = totalCommission,
                                totalGMV = totalGMV,
                                totalPayouts = totalPayouts,
                                commissionRate = adminSettings.commissionPercentage
                            )
                        }
                        // Alert row
                        item {
                            _AlertRow(
                                pendingCount = pendingVerifications.size,
                                disputeCount = openDisputes.size,
                                onViewVerifications = onViewVerifications,
                                onViewDisputes = onViewDisputes
                            )
                        }
                        // Platform stats grid
                        item {
                            _SectionHeader("Platform Stats")
                        }
                        item {
                            _StatsGrid(
                                bookingCount       = bookings.size,
                                activeCount        = activeBookings.size,
                                completedCount     = completedBookings.size,
                                providerCount      = providers.size,
                                onlineProviders    = onlineProviders,
                                userCount          = registeredUsers.size,
                                gigCount           = gigs.size,
                                openJobCount       = jobRequests.count { it.status == JobRequestStatus.OPEN }
                            )
                        }
                        // Booking status breakdown
                        item { _SectionHeader("Booking Status Breakdown") }
                        item { _BookingBreakdownBar(bookings) }
                        // Quick actions
                        item { _SectionHeader("Quick Actions") }
                        item {
                            _QuickActionsRow(
                                onViewVerifications = onViewVerifications,
                                onViewDisputes = onViewDisputes,
                                onViewCategories = onViewCategories
                            )
                        }
                        // Top providers
                        if (providers.isNotEmpty()) {
                            item { _SectionHeader("Top Providers") }
                            items(providers.sortedByDescending { it.rating }.take(5)) { prov ->
                                _ProviderRow(prov, onApprove = {
                                    FixoraRepository.adminSetVerificationStatus(prov.id, VerificationStatus.APPROVED)
                                })
                            }
                        }
                    }

                    AdminTab.BOOKINGS -> {
                        item { _SectionHeader("All Bookings (${bookings.size})") }
                        if (bookings.isEmpty()) {
                            item { _EmptyState("No bookings yet", "They'll appear here as customers place orders.") }
                        } else {
                            items(bookings.sortedByDescending { it.createdAt }) { booking ->
                                _BookingAdminCard(booking)
                            }
                        }
                    }

                    AdminTab.PROVIDERS -> {
                        item {
                            _ProviderStatsRow(
                                total    = providers.size,
                                online   = onlineProviders,
                                verified = verifiedProviders,
                                avgRating = avgProviderRating
                            )
                        }
                        item { _SectionHeader("All Providers (${providers.size})") }
                        items(providers.sortedByDescending { it.completedJobs }) { prov ->
                            _ProviderDetailCard(
                                provider = prov,
                                onApprove = { FixoraRepository.adminSetVerificationStatus(prov.id, VerificationStatus.APPROVED) },
                                onReject  = { FixoraRepository.adminSetVerificationStatus(prov.id, VerificationStatus.REJECTED) }
                            )
                        }
                    }

                    AdminTab.FINANCE -> {
                        item {
                            _FinanceSummaryCard(
                                totalGMV        = totalGMV,
                                totalCommission = totalCommission,
                                totalPayouts    = totalPayouts,
                                commissionRate  = adminSettings.commissionPercentage,
                                emergencySurge  = adminSettings.emergencySurgeFeeAed,
                                vat             = adminSettings.vatRatePercentage
                            )
                        }
                        item { _SectionHeader("Live Financial Ledger (${ledger.size} entries)") }
                        if (ledger.isEmpty()) {
                            item { _EmptyState("Ledger is empty", "Double-entry records will appear as transactions occur.") }
                        } else {
                            items(ledger.sortedByDescending { it.timestamp }) { entry ->
                                _LedgerRow(entry)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Tab Row ──────────────────────────────────────────────────────────────────
@Composable
private fun _AdminTabRow(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit
) {
    Surface(shadowElevation = 2.dp, color = FixoraSurface) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(AdminTab.values()) { tab ->
                val selected = tab == selectedTab
                val bgColor by animateColorAsState(
                    targetValue = if (selected) FixoraPrimary.copy(alpha = 0.12f) else Color.Transparent,
                    animationSpec = tween(200), label = "tab_bg"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (selected) FixoraPrimary else FixoraTextSecondary,
                    animationSpec = tween(200), label = "tab_fg"
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(tab.icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
                    Text(
                        text = tab.label,
                        color = contentColor,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ─── Revenue Hero Card ─────────────────────────────────────────────────────────
@Composable
private fun _RevenueHeroCard(
    totalCommission: Double,
    totalGMV: Double,
    totalPayouts: Double,
    commissionRate: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(FixoraDarkNavy, Color(0xFF1A2642))))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = FixoraPrimaryLight, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Platform Commission Revenue", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "PKR ${"%,.0f".format(totalCommission)}",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                _HeroStat("Gross GMV",         "PKR ${"%,.0f".format(totalGMV)}",     Color.White)
                _HeroStat("Provider Payouts",  "PKR ${"%,.0f".format(totalPayouts)}",  FixoraSuccess)
                _HeroStat("Commission Rate",   "${commissionRate.toInt()}%",            FixoraPrimaryLight)
            }
        }
    }
}

@Composable
private fun _HeroStat(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White.copy(alpha = 0.55f), fontSize = 10.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

// ─── Alert Row ────────────────────────────────────────────────────────────────
@Composable
private fun _AlertRow(
    pendingCount: Int,
    disputeCount: Int,
    onViewVerifications: () -> Unit,
    onViewDisputes: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        _AlertCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.VerifiedUser,
            iconTint = FixoraPrimary,
            iconBg = FixoraPrimaryLight,
            label = "Verifications",
            count = "$pendingCount Pending",
            countColor = if (pendingCount > 0) FixoraWarning else FixoraSuccess,
            sub = "Review IDs & permits",
            onClick = onViewVerifications
        )
        _AlertCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.ReportProblem,
            iconTint = FixoraEmergency,
            iconBg = FixoraEmergencyLight,
            label = "Disputes",
            count = "$disputeCount Open",
            countColor = if (disputeCount > 0) FixoraEmergency else FixoraSuccess,
            sub = "Process refunds/claims",
            onClick = onViewDisputes
        )
    }
}

@Composable
private fun _AlertCard(
    modifier: Modifier,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    count: String,
    countColor: Color,
    sub: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = FixoraTextSecondary)
            Text(count, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = countColor)
            Text(sub, style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
        }
    }
}

// ─── Stats Grid ───────────────────────────────────────────────────────────────
@Composable
private fun _StatsGrid(
    bookingCount: Int,
    activeCount: Int,
    completedCount: Int,
    providerCount: Int,
    onlineProviders: Int,
    userCount: Int,
    gigCount: Int,
    openJobCount: Int
) {
    val stats = listOf(
        Triple("Total Bookings",  "$bookingCount",   Icons.Default.Receipt),
        Triple("Active Now",      "$activeCount",    Icons.Default.FlashOn),
        Triple("Completed",       "$completedCount", Icons.Default.CheckCircle),
        Triple("Providers",       "$providerCount",  Icons.Default.Build),
        Triple("Online Now",      "$onlineProviders",Icons.Default.Circle),
        Triple("Customers",       "$userCount",      Icons.Default.People),
        Triple("Gigs Live",       "$gigCount",       Icons.Default.Storefront),
        Triple("Open Jobs",       "$openJobCount",   Icons.Default.WorkOutline)
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stats.chunked(4).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (label, value, icon) ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(icon, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.height(4.dp))
                            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            Text(label, style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

// ─── Booking Breakdown Bar ────────────────────────────────────────────────────
@Composable
private fun _BookingBreakdownBar(bookings: List<Booking>) {
    if (bookings.isEmpty()) return

    val groups = listOf(
        "Active"     to bookings.count { it.status in listOf(BookingStatus.CONFIRMED, BookingStatus.EN_ROUTE, BookingStatus.IN_PROGRESS, BookingStatus.ARRIVED) },
        "Completed"  to bookings.count { it.status == BookingStatus.COMPLETED || it.status == BookingStatus.PAYMENT_SETTLED },
        "Cancelled"  to bookings.count { it.status == BookingStatus.CUSTOMER_CANCELLED || it.status == BookingStatus.PROVIDER_CANCELLED },
        "Disputed"   to bookings.count { it.status == BookingStatus.DISPUTED || it.status == BookingStatus.REFUNDED },
        "Pending"    to bookings.count { it.status == BookingStatus.REQUESTED || it.status == BookingStatus.MATCHING }
    ).filter { it.second > 0 }

    val colors = listOf(FixoraPrimary, FixoraSuccess, FixoraEmergency, FixoraWarning, Color(0xFF8B5CF6))
    val total = groups.sumOf { it.second }.toFloat()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Segmented bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                groups.forEachIndexed { i, (_, count) ->
                    Box(
                        modifier = Modifier
                            .weight(count / total)
                            .fillMaxHeight()
                            .background(colors.getOrElse(i) { FixoraTextMuted })
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            // Legend
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                groups.forEachIndexed { i, (label, count) ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f, fill = false)) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colors.getOrElse(i) { FixoraTextMuted })
                        )
                        Spacer(Modifier.width(4.dp))
                        Column {
                            Text(label, fontSize = 10.sp, color = FixoraTextMuted)
                            Text("$count", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ─── Quick Actions ─────────────────────────────────────────────────────────────
@Composable
private fun _QuickActionsRow(
    onViewVerifications: () -> Unit,
    onViewDisputes: () -> Unit,
    onViewCategories: () -> Unit
) {
    val actions = listOf(
        Triple(Icons.Default.VerifiedUser,    "Verifications",   onViewVerifications),
        Triple(Icons.Default.ReportProblem,   "Disputes",        onViewDisputes),
        Triple(Icons.Default.Tune,            "Config",          onViewCategories)
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        actions.forEach { (icon, label, action) ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.weight(1f).clickable { action() }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FixoraPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(label, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }
    }
}

// ─── Provider Row (compact, overview) ─────────────────────────────────────────
@Composable
private fun _ProviderRow(provider: com.fixora.marketplace.data.model.ProviderProfile, onApprove: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(FixoraPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = provider.name.first().uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = FixoraPrimary,
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(provider.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    if (provider.isVerified) {
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(14.dp))
                    }
                }
                Text(
                    text = "${provider.skills.take(2).joinToString(" · ")} • ${provider.completedJobs} jobs",
                    style = MaterialTheme.typography.labelSmall,
                    color = FixoraTextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = FixoraWarning, modifier = Modifier.size(12.dp))
                    Text(" ${provider.rating}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                if (provider.documentStatus == VerificationStatus.PENDING) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = FixoraWarningLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "Pending",
                            color = FixoraWarning,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Booking Admin Card ────────────────────────────────────────────────────────
@Composable
private fun _BookingAdminCard(booking: Booking) {
    val (statusColor, statusBg) = when (booking.status) {
        BookingStatus.COMPLETED, BookingStatus.PAYMENT_SETTLED -> FixoraSuccess to FixoraSuccessLight
        BookingStatus.CUSTOMER_CANCELLED, BookingStatus.PROVIDER_CANCELLED -> FixoraTextMuted to FixoraSurfaceVariant
        BookingStatus.DISPUTED, BookingStatus.REFUNDED -> FixoraEmergency to FixoraEmergencyLight
        BookingStatus.IN_PROGRESS, BookingStatus.ARRIVED, BookingStatus.EN_ROUTE -> FixoraPrimary to FixoraPrimaryLight
        else -> FixoraWarning to FixoraWarningLight
    }
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(booking.serviceTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text("${booking.customerName} → ${booking.providerName ?: "Unassigned"}", style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
                Text("${booking.scheduledDate}  ${booking.scheduledTimeSlot}", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
            }
            Spacer(Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                Surface(color = statusBg, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        booking.status.name.replace('_', ' '),
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text("PKR ${booking.agreedFinalPriceAed.toInt()}", fontWeight = FontWeight.ExtraBold, color = FixoraTextPrimary, fontSize = 14.sp)
                Text("Commission: PKR ${booking.platformCommissionAed.toInt()}", fontSize = 9.sp, color = FixoraPrimary)
            }
        }
    }
}

// ─── Provider Stats Row ────────────────────────────────────────────────────────
@Composable
private fun _ProviderStatsRow(total: Int, online: Int, verified: Int, avgRating: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            Triple("Total",    "$total",                        FixoraTextPrimary),
            Triple("Online",   "$online",                       FixoraPrimary),
            Triple("Verified", "$verified",                     FixoraSuccess),
            Triple("Avg ★",   "${"%.1f".format(avgRating)}",   FixoraWarning)
        ).forEach { (label, value, color) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = color)
                    Text(label, fontSize = 10.sp, color = FixoraTextMuted)
                }
            }
        }
    }
}

// ─── Provider Detail Card ──────────────────────────────────────────────────────
@Composable
private fun _ProviderDetailCard(
    provider: com.fixora.marketplace.data.model.ProviderProfile,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
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
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (provider.isOnline) FixoraPrimary.copy(alpha = 0.15f) else FixoraSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(provider.name.first().uppercase(), fontWeight = FontWeight.Bold, color = FixoraPrimary, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(provider.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            if (provider.isOnline) {
                                Spacer(Modifier.width(5.dp))
                                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(FixoraSuccess))
                            }
                        }
                        Text(provider.email, style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                    }
                }
                _StatusChip(provider.documentStatus)
            }
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = FixoraDivider)
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                _ProviderStat("Rating", "${"%.1f".format(provider.rating)} ★")
                _ProviderStat("Jobs Done", "${provider.completedJobs}")
                _ProviderStat("Reviews", "${provider.reviewCount}")
                _ProviderStat("Rate/hr", "PKR ${provider.hourlyRateAed.toInt()}")
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Skills: ${provider.skills.joinToString(" · ")}",
                style = MaterialTheme.typography.labelSmall,
                color = FixoraTextSecondary
            )
            Text(
                text = "Areas: ${provider.serviceAreas.take(3).joinToString(", ")}",
                style = MaterialTheme.typography.labelSmall,
                color = FixoraTextSecondary
            )
            if (provider.tradeLicenseNumber != null) {
                Text("License: ${provider.tradeLicenseNumber}", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
            }
            if (provider.documentStatus == VerificationStatus.PENDING) {
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoraEmergency)
                    ) { Text("Reject") }
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoraSuccess)
                    ) { Text("Approve", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
private fun _ProviderStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        Text(label, style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
    }
}

@Composable
private fun _StatusChip(status: VerificationStatus) {
    val (text, color, bg) = when (status) {
        VerificationStatus.APPROVED      -> Triple("Approved",  FixoraSuccess,   FixoraSuccessLight)
        VerificationStatus.PENDING       -> Triple("Pending",   FixoraWarning,   FixoraWarningLight)
        VerificationStatus.REJECTED      -> Triple("Rejected",  FixoraEmergency, FixoraEmergencyLight)
        VerificationStatus.NOT_SUBMITTED -> Triple("No Docs",   FixoraTextMuted, FixoraSurfaceVariant)
    }
    Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ─── Finance Summary ───────────────────────────────────────────────────────────
@Composable
private fun _FinanceSummaryCard(
    totalGMV: Double,
    totalCommission: Double,
    totalPayouts: Double,
    commissionRate: Double,
    emergencySurge: Double,
    vat: Double
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Financial Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(14.dp))
            _FinanceLine("Gross Merchandise Value",    "PKR ${"%,.0f".format(totalGMV)}",        FixoraTextPrimary)
            _FinanceLine("Platform Commission (${commissionRate.toInt()}%)", "PKR ${"%,.0f".format(totalCommission)}", FixoraPrimary)
            _FinanceLine("Provider Payouts (${(100 - commissionRate).toInt()}%)", "PKR ${"%,.0f".format(totalPayouts)}", FixoraSuccess)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = FixoraDivider)
            _FinanceLine("Emergency Surge Fee",        "PKR ${emergencySurge.toInt()} flat",     FixoraWarning)
            _FinanceLine("VAT Rate",                   "${vat.toInt()}%",                         FixoraTextSecondary)
        }
    }
}

@Composable
private fun _FinanceLine(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)
        Text(value, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

// ─── Ledger Row ───────────────────────────────────────────────────────────────
@Composable
private fun _LedgerRow(entry: com.fixora.marketplace.data.model.LedgerEntry) {
    val isCredit = entry.creditAed > 0
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isCredit) FixoraSuccessLight else FixoraEmergencyLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (isCredit) FixoraSuccess else FixoraEmergency,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.description, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Ref: ${entry.referenceId}  ·  ${entry.type.name.replace('_', ' ')}", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isCredit) "+PKR ${entry.creditAed.toInt()}" else "-PKR ${entry.debitAed.toInt()}",
                    color = if (isCredit) FixoraSuccess else FixoraEmergency,
                    fontWeight = FontWeight.Bold
                )
                Text("Bal: PKR ${entry.balanceAed.toInt()}", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
@Composable
private fun _SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun _EmptyState(title: String, subtitle: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Inbox, contentDescription = null, tint = FixoraTextMuted, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, color = FixoraTextSecondary, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = FixoraTextMuted)
        }
    }
}
