package com.fixora.marketplace.ui.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import com.fixora.marketplace.ui.navigation.Screen
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.i18n.AppLocaleManager
import com.fixora.marketplace.data.local.AppPreferences
import com.fixora.marketplace.data.model.*
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraBrandMark
import com.fixora.marketplace.ui.components.LanguageSelectionDialog
import com.fixora.marketplace.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Category Grid Data (Matching the 2x6 grid from screenshot)
// ─────────────────────────────────────────────────────────────────────────────
private data class ScreenshotCategoryItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val iconTint: Color,
    val bgTint: Color
)


private enum class HomeServiceGroup(
    val title: String,
    val icon: ImageVector,
    val accent: Color,
    val gradientTop: Color,
    val gradientBottom: Color,
    val highlight: Color
) {
    LOCAL(
        "Local Services", Icons.Default.LocationOn, FixoraNeonPink,
        Color(0xFFFF267D), Color(0xFF57052C), Color(0xFFFFA3C7)
    ),
    DIGITAL(
        "Digital Services", Icons.Default.Computer, FixoraBlue,
        Color(0xFF167BDD), Color(0xFF061D66), Color(0xFF9FDFFF)
    ),
    PROFESSIONAL(
        "Professional Services", Icons.Default.BusinessCenter, FixoraViolet,
        Color(0xFF883BE5), Color(0xFF2B095D), Color(0xFFE4C4FF)
    ),
    LEARNING(
        "Learning & Training", Icons.Default.School, FixoraWarning,
        Color(0xFFE88A0A), Color(0xFF623002), Color(0xFFFFDF9E)
    )
}

private val gridCategories = listOf(
    ScreenshotCategoryItem("HOME_REPAIR", "Home\nRepair", Icons.Default.Handyman, FixoraNeonPink, Color(0xFFFAF5FF)),
    ScreenshotCategoryItem("CLEANING", "Cleaning", Icons.Default.CleaningServices, Color(0xFFC026D3), Color(0xFFFDF4FF)),
    ScreenshotCategoryItem("AUTOMOTIVE", "Automotive", Icons.Default.DirectionsCar, FixoraBlue, Color(0xFFEEF2FF)),
    ScreenshotCategoryItem("SOFTWARE_DEVELOPMENT", "IT &\nTech", Icons.Default.Computer, Color(0xFF0284C7), Color(0xFFF0F9FF)),
    ScreenshotCategoryItem("DESIGN", "Design &\nCreative", Icons.Default.Palette, FixoraViolet, Color(0xFFF5F3FF)),
    ScreenshotCategoryItem("EDUCATION", "Tutoring", Icons.Default.School, Color(0xFF2563EB), Color(0xFFEFF6FF)),
    ScreenshotCategoryItem("BEAUTY", "Beauty &\nWellness", Icons.Default.Face, Color(0xFFEC4899), Color(0xFFFDF2F8)),
    ScreenshotCategoryItem("PET_CARE", "Pets &\nCare", Icons.Default.Pets, FixoraWarning, Color(0xFFF5F3FF)),
    ScreenshotCategoryItem("MOVING", "Moving &\nDelivery", Icons.Default.LocalShipping, Color(0xFF0284C7), Color(0xFFF0F9FF)),
    ScreenshotCategoryItem("BUSINESS_SUPPORT", "Business\nServices", Icons.Default.BusinessCenter, Color(0xFF9333EA), Color(0xFFFAF5FF)),
    ScreenshotCategoryItem("EVENTS", "Events &\nMedia", Icons.Default.Celebration, Color(0xFF6366F1), Color(0xFFEEF2FF)),
    ScreenshotCategoryItem("ALL", "More\nCategories", Icons.Default.Apps, Color(0xFF8B5CF6), Color(0xFFF5F3FF)),
)

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun CustomerHomeScreen(
    onCategoryClick:       (String) -> Unit,
    onProviderClick:       (String) -> Unit,
    onTrackBookingClick:   (String) -> Unit,
    onNotificationsClick:  () -> Unit,
    onBottomNavClick:      (String) -> Unit,
    onPostJobClick:        () -> Unit          = {},
    onSearchClick: () -> Unit,
    onAdminPanelClick:     () -> Unit          = {}
) {
    val currentUser   by FixoraRepository.currentUser.collectAsState()
    val providers     by FixoraRepository.providers.collectAsState()
    val bookings      by FixoraRepository.bookings.collectAsState()
    val notifications by FixoraRepository.notifications.collectAsState()

    val isAdmin = FixoraRepository.isUserAdmin(currentUser.email)

    var favoritePros by remember { mutableStateOf(emptySet<String>()) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val unread = notifications.count { !it.isRead }

    val activeBooking = bookings.firstOrNull {
        it.customerId == currentUser.id &&
        it.status !in listOf(
            BookingStatus.PAYMENT_SETTLED,
            BookingStatus.CUSTOMER_CANCELLED,
            BookingStatus.PROVIDER_DECLINED,
            BookingStatus.REFUNDED
        )
    }

    val featuredProviders = providers.filter { it.isVerified && it.isOnline }
    val categories by FixoraRepository.categories.collectAsState()
    var selectedGroup by rememberSaveable { mutableStateOf<HomeServiceGroup?>(null) }
    val group = selectedGroup
    val visibleCategories = if (group == null) {
        gridCategories
    } else {
        val matching = categories.filter { it.serviceGroup == group.title }
        val matchingIds = matching.mapTo(mutableSetOf()) { it.id }
        val curated = gridCategories.filter { it.id in matchingIds }
        val curatedIds = curated.mapTo(mutableSetOf()) { it.id }
        curated + matching.filterNot { it.id in curatedIds }.map {
            ScreenshotCategoryItem(it.id, it.name, group.icon, group.accent, FixoraPrimaryLight)
        }
    }

    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            initialLanguage = AppPreferences.getSelectedLanguage(context),
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { lang ->
                AppPreferences.setSelectedLanguage(context, lang)
                AppLocaleManager.setLanguageByName(lang)
                showLanguageDialog = false
            }
        )
    }

    Scaffold(
        containerColor = FixoraBackground,
        bottomBar = {
            ScreenshotBottomNav(
                onNavClick = onBottomNavClick,
                bookings = bookings,
                userId = currentUser.id,
                onAddClick = onPostJobClick
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FixoraPageBrush),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
            // ── 1. COSMIC HERO SECTION (Directly from Screenshot) ────────────
            item {
                CosmicHeroHeader(
                    onMenuClick = { showLanguageDialog = true },
                    onSearchClick = onSearchClick,
                    unreadCount = unread,
                    onNotificationsClick = onNotificationsClick,
                    onProfileClick = { onBottomNavClick(Screen.CustomerProfileTab.route) }
                )
            }


            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HomeServiceGroup.entries.forEach { serviceGroup ->
                        val selected = group == serviceGroup
                        val shape = RoundedCornerShape(14.dp)
                        Column(
                            modifier = Modifier.weight(1f)
                                .heightIn(min = 82.dp)
                                .clip(shape)
                                .background(Brush.verticalGradient(listOf(serviceGroup.gradientTop, serviceGroup.gradientBottom)))
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            if (selected) Color.White else serviceGroup.highlight,
                                            serviceGroup.gradientTop.copy(alpha = if (selected) 1f else 0.6f)
                                        )
                                    ),
                                    shape = shape
                                )
                                .selectable(
                                    selected = selected,
                                    role = Role.Tab,
                                    onClick = { selectedGroup = if (selected) null else serviceGroup }
                                )
                                .padding(horizontal = 3.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
                        ) {
                            Icon(serviceGroup.icon, contentDescription = null, tint = serviceGroup.highlight, modifier = Modifier.size(20.dp))
                            Text(
                                serviceGroup.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Active booking banner if applicable
            if (activeBooking != null) {
                item {
                    ActiveBookingBanner(activeBooking) { onTrackBookingClick(activeBooking.id) }
                }
            }

            // ── 3. BROWSE BY CATEGORY (2x6 Grid as in Screenshot) ────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Browse by category",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = FixoraTextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                if (group == null) onSearchClick() else selectedGroup = null
                            }
                        ) {
                            Text(
                                text = if (group == null) "View All" else "Show all",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoraPrimary
                            )
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = FixoraPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (visibleCategories.isEmpty()) {
                        Text("No categories in this group yet.", color = FixoraTextSecondary)
                    } else {
                        key(group) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(visibleCategories.chunked(2), key = { it.first().id }) { pair ->
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        pair.forEach { category ->
                                            ScreenshotCategoryIconCard(category) {
                                                if (category.id == "ALL") onSearchClick() else onCategoryClick(category.id)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── 4. FEATURED PROFESSIONALS (Screenshot Cards) ─────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Featured professionals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FixoraTextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featuredProviders.take(5), key = { it.id }) { prov ->
                            val isFav = favoritePros.contains(prov.name)
                            FeaturedProCard(
                                name = prov.name,
                                role = categories.firstOrNull { it.id == prov.skills.firstOrNull() }?.name ?: "Specialist",
                                rating = prov.rating,
                                reviews = "${prov.reviewCount}",
                                location = prov.serviceAreas.firstOrNull() ?: "Verified",
                                avatarInitials = prov.name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() },
                                avatarBg = FixoraPrimary,
                                isFavorite = isFav,
                                onToggleFavorite = {
                                    favoritePros = if (isFav) favoritePros - prov.name else favoritePros + prov.name
                                    toastMessage = if (!isFav) "Saved ${prov.name} to Favorites ❤️" else "Removed from Favorites"
                                },
                                onClick = { onProviderClick(prov.id) }
                            )
                        }
                    }
                }
            }

            // ── 5. DUAL ACTION PILLS: "Post a Project" & "Book a Service" ────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Post a Project
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraSurfaceVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPostJobClick() },
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(FixoraPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PostAdd, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Post a Project", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FixoraTextPrimary)
                                Text("Get multiple offers", fontSize = 11.sp, color = FixoraTextSecondary)
                            }
                        }
                    }

                    // Book a Service
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraAction),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSearchClick() },
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.26f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.20f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Book a Service", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("Browse available services", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                            }
                        }
                    }
                }
            }

            // ── 6. FIXORA TRUST & MONEY-BACK DIGITAL GUARANTEE BANNER ───────
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = FixoraSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoraBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(FixoraSuccessLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (LocalFixoraColors.current.isDark) FixoraSuccess else Color(0xFF06745B),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Fixora 7-Day Free Warranty",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = FixoraTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = FixoraSuccessLight,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "100% Guaranteed",
                                        color = if (LocalFixoraColors.current.isDark) FixoraSuccess else Color(0xFF06745B),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Every completed service includes free re-work or full escrow refund if unsatisfied.",
                                fontSize = 11.sp,
                                color = FixoraTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // ── Admin Shortcut if Admin ──────────────────────────────────────
            if (isAdmin) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { onAdminPanelClick() },
                        shape = RoundedCornerShape(14.dp),
                        color = FixoraTextPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FixoraAction),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Fixora Admin Console", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("Manage your marketplace", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                            Surface(color = FixoraAction, shape = RoundedCornerShape(8.dp)) {
                                Text("OPEN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }

        // Animated Interactive SnackBar/Toast for Favorites
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            toastMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = FixoraTextPrimary,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { toastMessage = null }
                        )
                    }
                }
            }
        }
    }
}
} // end CustomerHomeScreen

// ─────────────────────────────────────────────────────────────────────────────
// Cosmic Hero Header Component (Screenshot Replica)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CosmicHeroHeader(
    onSearchClick: () -> Unit,
    onMenuClick: () -> Unit,
    unreadCount: Int,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Language, contentDescription = "Language settings", tint = FixoraTextPrimary)
            }
            FixoraBrandMark(Modifier.size(36.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Fixora", color = FixoraTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text("One Platform. Every Skill.", color = FixoraTextSecondary, fontSize = 11.sp)
            }
            IconButton(onClick = onNotificationsClick) {
                BadgedBox(badge = {
                    if (unreadCount > 0) Badge(containerColor = FixoraAction)
                }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = FixoraTextPrimary)
                }
            }
            IconButton(onClick = onProfileClick) {
                Box(
                    modifier = Modifier.size(36.dp).background(FixoraAction, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Hire experts.", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = FixoraTextPrimary)
                Text("Get things done.", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = FixoraPrimary)
                Text("Find skilled people worldwide.", style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
            }
            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(76.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF3D2777), Color(0xFF20143F), FixoraBackground)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Find skilled professionals worldwide",
                    tint = Color(0xFFD6C8FF),
                    modifier = Modifier.size(44.dp)
                )
                Box(
                    Modifier.align(Alignment.BottomEnd).padding(end = 15.dp, bottom = 14.dp)
                        .size(8.dp).clip(CircleShape).background(FixoraNeonPink)
                )
            }
        }

        OutlinedButton(
            onClick = onSearchClick,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, FixoraBorder)
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = FixoraPrimary)
            Spacer(Modifier.width(8.dp))
            Text("Search services or professionals", color = FixoraTextPrimary, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ScreenshotCategoryIconCard(
    category: ScreenshotCategoryItem,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val isDark = LocalFixoraColors.current.isDark
    val rimLight = if (isDark) Color(0xFFBBA6FF) else Color(0xFF8765BF)
    Surface(
        onClick = onClick,
        shape = shape,
        color = FixoraSurface.copy(alpha = if (isDark) 0.78f else 0.92f),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(rimLight.copy(alpha = 0.32f), rimLight.copy(alpha = 0.09f), rimLight.copy(alpha = 0.23f))
            )
        ),
        modifier = Modifier
            .width(82.dp)
            .height(76.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(3.dp, rimLight.copy(alpha = 0.035f), shape)
                .border(5.dp, rimLight.copy(alpha = 0.018f), shape)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) category.iconTint.copy(alpha = 0.18f) else category.bgTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    category.icon,
                    contentDescription = null,
                    tint = category.iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.name,
                fontSize = 9.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = FixoraTextPrimary,
                maxLines = 2
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Featured Professional Card (Screenshot Persona Card)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FeaturedProCard(
    name: String,
    role: String,
    rating: Double,
    reviews: String,
    location: String,
    avatarInitials: String,
    avatarBg: Color,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.26f)),
        modifier = Modifier
            .width(155.dp)
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Heart favorite icon in top right
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = 6.dp)
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Save",
                    tint = if (isFavorite) Color(0xFFEF4444) else FixoraTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Avatar with Verified Badge
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(avatarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatarInitials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(FixoraAction),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = FixoraTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = FixoraPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Text(
                    text = role,
                    fontSize = 11.sp,
                    color = FixoraTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "$rating",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoraTextPrimary
                    )
                    Text(
                        text = "($reviews)",
                        fontSize = 10.sp,
                        color = FixoraTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = FixoraTextSecondary,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = location,
                        fontSize = 10.sp,
                        color = FixoraTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screenshot Replica Bottom Navigation with Center Floating '+' Button
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ScreenshotBottomNav(
    onNavClick: (String) -> Unit,
    bookings: List<Booking>,
    userId: String,
    onAddClick: () -> Unit
) {
    Surface(
        color = FixoraSurface,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        border = BorderStroke(1.dp, FixoraBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                Icon(Icons.Default.Home, contentDescription = "Home", tint = FixoraPrimary, modifier = Modifier.size(24.dp))
                Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FixoraPrimary)
            }

            // Search Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onNavClick(Screen.CustomerSearch.route) }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = FixoraTextSecondary, modifier = Modifier.size(24.dp))
                Text("Search", fontSize = 10.sp, color = FixoraTextSecondary)
            }

            // Floating Center '+' Action Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(FixoraGradientStart, FixoraGradientEnd))
                    )
                    .clickable(onClick = onAddClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post", tint = Color.White, modifier = Modifier.size(26.dp))
            }

            // Messages Tab with unread badge
            Box(
                modifier = Modifier.clickable {
                    val lb = bookings.firstOrNull { it.customerId == userId }
                    onNavClick(if (lb != null) "customer_chat/${lb.id}" else "customer_bookings")
                }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Messages", tint = FixoraTextSecondary, modifier = Modifier.size(24.dp))
                    Text("Messages", fontSize = 10.sp, color = FixoraTextSecondary)
                }

            }

            // Profile Tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onNavClick(Screen.CustomerProfileTab.route) }
            ) {
                Icon(Icons.Default.PersonOutline, contentDescription = "Profile", tint = FixoraTextSecondary, modifier = Modifier.size(24.dp))
                Text("Profile", fontSize = 10.sp, color = FixoraTextSecondary)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Active Booking Banner
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ActiveBookingBanner(booking: Booking, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = FixoraPrimaryLight
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(FixoraAction),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.BuildCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Active Job in Progress", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FixoraPrimary)
                Text(booking.serviceTitle, fontSize = 12.sp, color = FixoraTextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = FixoraPrimary)
        }
    }
}
