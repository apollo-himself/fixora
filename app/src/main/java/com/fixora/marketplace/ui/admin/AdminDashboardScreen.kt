package com.fixora.marketplace.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.*
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraBrandMark
import com.fixora.marketplace.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private enum class AdminTab(val title: String, val icon: ImageVector) {
    OVERVIEW("Dashboard", Icons.Default.Dashboard),
    USERS("Users", Icons.Default.People),
    PROVIDERS("Service providers", Icons.Default.BusinessCenter),
    BOOKINGS("Orders and bookings", Icons.Default.CalendarMonth),
    FINANCE("Payments and payouts", Icons.Default.AccountBalanceWallet)
}

@Composable
fun AdminDashboardScreen(
    onViewVerifications: () -> Unit,
    onViewDisputes: () -> Unit,
    onViewCategories: () -> Unit,
    onRoleSwitched: (UserRole) -> Unit,
    onNavigateBack: () -> Unit = { onRoleSwitched(UserRole.CUSTOMER) },
    onViewJobRequests: () -> Unit,
    onViewBooking: (String) -> Unit
) {
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val providers by FixoraRepository.providers.collectAsState()
    val bookings by FixoraRepository.bookings.collectAsState()
    val ledger by FixoraRepository.ledger.collectAsState()
    val disputes by FixoraRepository.disputes.collectAsState()
    val settings by FixoraRepository.adminSettings.collectAsState()
    val registeredUsers by FixoraRepository.registeredUsers.collectAsState()
    val requests by FixoraRepository.jobRequests.collectAsState()
    val users = (registeredUsers + currentUser).distinctBy { it.id }
    var tab by rememberSaveable { mutableStateOf(AdminTab.OVERVIEW) }
    var days by rememberSaveable { mutableIntStateOf(30) }
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val startDate = today.minusDays((days - 1).toLong())
    val recent = bookings.filter {
        val date = Instant.ofEpochMilli(it.createdAt).atZone(zone).toLocalDate()
        date >= startDate && date <= today
    }
    val settled = recent.filter { it.status == BookingStatus.PAYMENT_SETTLED }
    val revenue = settled.sumOf { it.platformCommissionAed }
    val pending = providers.count { it.documentStatus == VerificationStatus.PENDING }
    val openDisputes = disputes.count { it.status == DisputeStatus.OPEN }
    val dailyBookings = (0 until days).map { day ->
        val date = startDate.plusDays(day.toLong())
        recent.count { Instant.ofEpochMilli(it.createdAt).atZone(zone).toLocalDate() == date }.toFloat()
    }
    val dailyRevenue = (0 until days).map { day ->
        val date = startDate.plusDays(day.toLong())
        settled.filter { Instant.ofEpochMilli(it.createdAt).atZone(zone).toLocalDate() == date }
            .sumOf { it.platformCommissionAed }.toFloat()
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(FixoraPageBrush).safeDrawingPadding()) {
        val wide = maxWidth >= 840.dp
        Row(Modifier.fillMaxSize()) {
            if (wide) {
                Surface(Modifier.width(220.dp).fillMaxHeight(), color = FixoraSurface, border = BorderStroke(1.dp, FixoraBorder)) {
                    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FixoraBrandMark(Modifier.size(36.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Fixora", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Admin portal", color = FixoraTextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        AdminTab.entries.forEach { item -> AdminNavigationItem(item, tab == item) { tab = item } }
                        HorizontalDivider(color = FixoraDivider)
                        TextButton(onClick = onViewCategories) { Text("Services and categories") }
                        TextButton(onClick = onViewJobRequests) { Text("Projects and bids") }
                        TextButton(onClick = onViewVerifications) { Text("Provider verification") }
                        TextButton(onClick = onViewDisputes) { Text("Disputes and support") }
                        Spacer(Modifier.height(16.dp))
                        TextButton(onClick = onNavigateBack) { Text("Back to app") }
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                    Column(Modifier.weight(1f)) {
                        Text(tab.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Fixora admin portal", color = FixoraTextSecondary, style = MaterialTheme.typography.labelMedium)
                    }
                    Box(Modifier.size(36.dp).background(FixoraAction, CircleShape), contentAlignment = Alignment.Center) {
                        Text(currentUser.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                if (!wide) {
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(AdminTab.entries) { item ->
                            FilterChip(selected = tab == item, onClick = { tab = item }, label = { Text(item.title) })
                        }
                    }
                }
                LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    when (tab) {
                        AdminTab.OVERVIEW -> {
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("Platform overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("Includes demo records", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                                    }
                                    var menu by remember { mutableStateOf(false) }
                                    Box {
                                        OutlinedButton(onClick = { menu = true }) { Text("Last " + days + " days") }
                                        DropdownMenu(menu, onDismissRequest = { menu = false }) {
                                            listOf(7, 30, 90).forEach { count ->
                                                DropdownMenuItem(text = { Text("Last " + count + " days") }, onClick = { days = count; menu = false })
                                            }
                                        }
                                    }
                                }
                            }
                            item {
                                val metrics = listOf(
                                    AdminMetric("Known users", users.size.toString(), Icons.Default.People, FixoraNeonPink),
                                    AdminMetric("Service providers", providers.size.toString(), Icons.Default.BusinessCenter, FixoraViolet),
                                    AdminMetric("Bookings · " + days + " days", recent.size.toString(), Icons.Default.CalendarMonth, FixoraBlue),
                                    AdminMetric("Commission · " + days + " days", "PKR " + "%,.0f".format(revenue), Icons.Default.Payments, FixoraWarning)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    metrics.chunked(if (wide) 4 else 2).forEach { row ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { row.forEach { MetricCard(it, Modifier.weight(1f)) } }
                                    }
                                }
                            }
                            item {
                                AdminPanel("Booking activity") {
                                    Text(recent.size.toString() + " bookings in this period", color = FixoraTextSecondary, style = MaterialTheme.typography.bodySmall)
                                    ActivityChart(dailyBookings, FixoraNeonPink, "Daily booking counts")
                                    ChartDates(startDate, today)
                                }
                            }
                            item {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    AdminAction("Approve providers", Icons.Default.VerifiedUser, pending, Modifier.weight(1f), onViewVerifications)
                                    AdminAction("Resolve disputes", Icons.Default.SupportAgent, openDisputes, Modifier.weight(1f), onViewDisputes)
                                }
                            }
                            item { AdminPanel("User types") { UserTypes(users) } }
                            item {
                                AdminPanel("Commission overview") {
                                    Text("PKR " + "%,.0f".format(revenue) + " settled", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FixoraWarning)
                                    ActivityChart(dailyRevenue, FixoraWarning, "Daily settled platform commission in PKR")
                                    ChartDates(startDate, today)
                                }
                            }
                            item {
                                AdminPanel("Bookings by location") {
                                    val locations = recent.groupingBy { it.address.emirate.ifBlank { "Unspecified" } }.eachCount()
                                    if (locations.isEmpty()) Text("No bookings in this period.", color = FixoraTextSecondary)
                                    locations.entries.sortedByDescending { it.value }.take(6).forEach { (location, count) ->
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(location, modifier = Modifier.weight(1f))
                                            Text(count.toString(), color = FixoraViolet, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            item {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    AdminAction("Manage services", Icons.Default.Category, null, Modifier.weight(1f), onViewCategories)
                                    AdminAction("Projects and bids", Icons.Default.Work, requests.count { it.status == JobRequestStatus.OPEN }, Modifier.weight(1f), onViewJobRequests)
                                }
                            }
                            item {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text("Recent bookings", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    TextButton(onClick = { tab = AdminTab.BOOKINGS }) { Text("View all") }
                                }
                            }
                            if (recent.isEmpty()) item { _EmptyState("No bookings", "Bookings in the selected period appear here.") }
                            items(recent.sortedByDescending { it.createdAt }.take(5), key = { it.id }) { booking ->
                                Box(Modifier.clickable { onViewBooking(booking.id) }) { _BookingAdminCard(booking) }
                            }
                        }
                        AdminTab.USERS -> {
                            item { _SectionHeader("Known users (" + users.size + ")") }
                            items(users, key = { it.id }) { user ->
                                Surface(shape = MaterialTheme.shapes.medium, color = FixoraSurface) {
                                    ListItem(
                                        headlineContent = { Text(user.name) },
                                        supportingContent = { Text(user.email) },
                                        trailingContent = { Text(user.role.displayName(), style = MaterialTheme.typography.labelSmall, color = FixoraPrimary) },
                                        colors = ListItemDefaults.colors(containerColor = FixoraSurface)
                                    )
                                }
                            }
                        }
                        AdminTab.BOOKINGS -> {
                            item { _SectionHeader("All bookings (" + bookings.size + ")") }
                            if (bookings.isEmpty()) item { _EmptyState("No bookings yet", "Bookings appear here when customers place orders.") }
                            items(bookings.sortedByDescending { it.createdAt }, key = { it.id }) { booking ->
                                Box(Modifier.clickable { onViewBooking(booking.id) }) { _BookingAdminCard(booking) }
                            }
                        }
                        AdminTab.PROVIDERS -> {
                            item { _SectionHeader("Service providers (" + providers.size + ")") }
                            if (providers.isEmpty()) item { _EmptyState("No providers yet", "Provider applications appear here.") }
                            items(providers.sortedByDescending { it.completedJobs }, key = { it.id }) { provider ->
                                _ProviderDetailCard(
                                    provider,
                                    onApprove = { FixoraRepository.adminSetVerificationStatus(provider.id, VerificationStatus.APPROVED) },
                                    onReject = { FixoraRepository.adminSetVerificationStatus(provider.id, VerificationStatus.REJECTED) }
                                )
                            }
                        }
                        AdminTab.FINANCE -> {
                            item {
                                _FinanceSummaryCard(
                                    totalGMV = bookings.sumOf { it.agreedFinalPriceAed },
                                    totalCommission = bookings.filter { it.status == BookingStatus.PAYMENT_SETTLED }.sumOf { it.platformCommissionAed },
                                    totalPayouts = bookings.filter { it.status == BookingStatus.PAYMENT_SETTLED }.sumOf { it.providerEarningsAed },
                                    commissionRate = settings.commissionPercentage,
                                    emergencySurge = settings.emergencySurgeFeeAed,
                                    vat = settings.vatRatePercentage
                                )
                            }
                            item { _SectionHeader("Financial ledger (" + ledger.size + ")") }
                            if (ledger.isEmpty()) item { _EmptyState("No transactions", "Ledger entries appear as transactions are recorded.") }
                            items(ledger.sortedByDescending { it.timestamp }, key = { it.id }) { _LedgerRow(it) }
                        }
                    }
                }
            }
        }
    }
}

private data class AdminMetric(val title: String, val value: String, val icon: ImageVector, val accent: Color)

@Composable
private fun MetricCard(metric: AdminMetric, modifier: Modifier) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier.clip(shape)
            .background(Brush.linearGradient(listOf(metric.accent.copy(alpha = 0.28f), FixoraSurface, metric.accent.copy(alpha = 0.12f))))
            .border(BorderStroke(1.dp, metric.accent.copy(alpha = 0.65f)), shape).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.size(36.dp).background(metric.accent.copy(alpha = 0.18f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Icon(metric.icon, contentDescription = null, tint = metric.accent, modifier = Modifier.size(22.dp))
        }
        Text(metric.title, color = FixoraTextSecondary, style = MaterialTheme.typography.labelMedium)
        Text(metric.value, color = FixoraTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AdminPanel(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = RoundedCornerShape(20.dp), color = FixoraSurface.copy(alpha = 0.9f), border = BorderStroke(1.dp, FixoraBorder)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun AdminAction(title: String, icon: ImageVector, count: Int?, modifier: Modifier, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = modifier, shape = MaterialTheme.shapes.large, color = FixoraSurface, border = BorderStroke(1.dp, FixoraBorder)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = FixoraViolet)
                Spacer(Modifier.weight(1f))
                if (count != null) Badge(containerColor = FixoraAction) { Text(count.toString()) }
            }
            Text(title, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun AdminNavigationItem(tab: AdminTab, selected: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = MaterialTheme.shapes.medium, color = if (selected) FixoraPrimaryLight else Color.Transparent) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(tab.icon, contentDescription = null, tint = if (selected) FixoraPrimary else FixoraTextSecondary)
            Text(tab.title, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ActivityChart(values: List<Float>, accent: Color, description: String) {
    val gridColor = FixoraBorder
    Canvas(Modifier.fillMaxWidth().height(120.dp).semantics { contentDescription = description }) {
        repeat(4) { line ->
            val y = size.height * line / 3f
            drawLine(gridColor.copy(alpha = 0.6f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
        }
        val maximum = (values.maxOrNull() ?: 0f).coerceAtLeast(1f)
        val step = size.width / values.size.coerceAtLeast(1)
        values.forEachIndexed { index, value ->
            val height = (value / maximum * (size.height - 4.dp.toPx())).coerceAtLeast(0f)
            if (height > 0f) drawRoundRect(
                brush = Brush.verticalGradient(listOf(accent, accent.copy(alpha = 0.3f))),
                topLeft = Offset(index * step + step * 0.1f, size.height - height),
                size = Size(step * 0.8f, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )
        }
    }
}

@Composable
private fun ChartDates(start: LocalDate, end: LocalDate) {
    val format = DateTimeFormatter.ofPattern("MMM d")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(start.format(format), style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
        Text(end.format(format), style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
    }
}

@Composable
private fun UserTypes(users: List<User>) {
    val groups = listOf(UserRole.CUSTOMER to FixoraNeonPink, UserRole.PROVIDER to FixoraViolet, UserRole.ADMIN to FixoraBlue)
    val counts = groups.map { (role, _) -> users.count { it.role == role } }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(Modifier.size(104.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize().semantics { contentDescription = "User roles in the current dataset" }) {
                var angle = -90f
                counts.forEachIndexed { index, count ->
                    val sweep = if (users.isEmpty()) 0f else count.toFloat() / users.size * 360f
                    drawArc(groups[index].second, angle, sweep, false, topLeft = Offset(10.dp.toPx(), 10.dp.toPx()), size = Size(size.width - 20.dp.toPx(), size.height - 20.dp.toPx()), style = Stroke(12.dp.toPx()))
                    angle += sweep
                }
            }
            Text(users.size.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            groups.forEachIndexed { index, (role, color) ->
                Text(role.displayName() + " · " + counts[index], color = color, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

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
                        booking.status.displayName(),
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

@Composable
private fun _ProviderDetailCard(
    provider: com.fixora.marketplace.data.model.ProviderProfile,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val categories by FixoraRepository.categories.collectAsState()
    val categoryNames = categories.associate { it.id to it.name }
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
                        Text(provider.name.take(1).uppercase(), fontWeight = FontWeight.Bold, color = FixoraPrimary, fontSize = 18.sp)
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
                text = "Skills: ${provider.skills.joinToString(" · ") { skill -> categoryNames[skill] ?: skill.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() } }}",
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
                Text("Ref: ${entry.referenceId}  ·  ${entry.type.displayName()}", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
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
