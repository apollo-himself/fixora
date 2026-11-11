package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.i18n.AppLocaleManager
import com.fixora.marketplace.data.i18n.LocalAppStrings
import com.fixora.marketplace.data.i18n.LocalCurrentLanguage
import com.fixora.marketplace.data.local.AppPreferences
import com.fixora.marketplace.data.model.Address
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.components.LanguageSelectionDialog
import com.fixora.marketplace.ui.components.ThemeSelectionDialog
import com.fixora.marketplace.ui.theme.*
import java.util.UUID

@Composable
fun CustomerProfileScreen(
    onNavigateBack: () -> Unit,
    onHelpCenterClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onAdminPanelClick: () -> Unit = {},
    onMyBookingsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val currentLanguage = LocalCurrentLanguage.current
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val addresses by FixoraRepository.customerAddresses.collectAsState()
    val allReviews by FixoraRepository.reviews.collectAsState()

    // Activity dialog state
    var showReviewsDialog by remember { mutableStateOf(false) }

    // Language selection state
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Theme selection state
    val currentThemeMode by AppThemeManager.currentThemeMode.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    // Edit profile dialog state
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }

    // Add address dialog state
    var showAddresses by remember { mutableStateOf(false) }
    var showAddAddressDialog by remember { mutableStateOf(false) }
    var addrLabel by remember { mutableStateOf("") }
    var addrArea by remember { mutableStateOf("") }
    var addrCity by remember { mutableStateOf("") }

    // Logout confirmation
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Support dialogs
    var showHelpDialog by remember { mutableStateOf(false) }
    var showComplaintDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var complaintText by remember { mutableStateOf("") }
    var complaintSubmitted by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            initialLanguage = currentLanguage.title,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { selected ->
                AppLocaleManager.setLanguageByName(selected)
                AppPreferences.setSelectedLanguage(context, selected)
                showLanguageDialog = false
            }
        )
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            initialTheme = currentThemeMode,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { selected ->
                AppThemeManager.setThemeMode(selected)
                AppPreferences.setThemeMode(context, selected.name)
                showThemeDialog = false
            }
        )
    }


    // Edit Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(strings.editProfileTitle, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(strings.fullNameLabel) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FixoraPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text(strings.emailAddressLabel) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = FixoraPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        FixoraRepository.updateUserProfile(
                            name = editName.ifBlank { currentUser.name },
                            email = editEmail.ifBlank { currentUser.email }
                        )
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.saveChanges, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text(strings.cancel) }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Add Address Dialog
    if (showAddAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text(strings.addAddress, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = addrLabel,
                        onValueChange = { addrLabel = it },
                        label = { Text(strings.houseStreetLabel) },
                        placeholder = { Text(strings.houseStreetPlaceholder) },
                        leadingIcon = { Icon(Icons.Default.Label, contentDescription = null, tint = FixoraPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = addrArea,
                        onValueChange = { addrArea = it },
                        label = { Text(strings.areaLabel) },
                        placeholder = { Text(strings.areaPlaceholder) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = FixoraPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = addrCity,
                        onValueChange = { addrCity = it },
                        label = { Text(strings.cityLabel) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (addrLabel.isNotBlank() && addrArea.isNotBlank() && addrCity.isNotBlank()) {
                            FixoraRepository.addAddress(
                                Address(
                                    id = "ADDR-${UUID.randomUUID().toString().take(6)}",
                                    label = addrLabel,
                                    street = addrArea,
                                    buildingOrVilla = "",
                                    apartmentNo = "",
                                    emirate = addrCity.trim(),
                                    area = addrArea,
                                    latitude = 0.0,
                                    longitude = 0.0,
                                    isDefault = addresses.isEmpty()
                                )
                            )
                            addrLabel = ""
                            addrArea = ""
                            addrCity = ""
                        }
                        showAddAddressDialog = false
                    },
                    enabled = addrLabel.isNotBlank() && addrArea.isNotBlank() && addrCity.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.save, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) { Text(strings.cancel) }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(strings.signOutConfirmTitle, fontWeight = FontWeight.Bold) },
            text = { Text(strings.signOutConfirmMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FixoraEmergency),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.signOut, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text(strings.cancel) }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Help Center Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            icon = { Icon(Icons.Default.HelpOutline, contentDescription = null, tint = FixoraPrimary) },
            title = { Text("Help Center & FAQs", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        "How do I book a technician?" to "Browse categories on the home screen, pick a gig or service, choose a time slot, and pay securely in PKR.",
                        "How does payment work?" to "Pay via JazzCash, Easypaisa, or card. Funds are held until the job is marked complete.",
                        "What's the platform fee?" to "Fixora charges a 15% service fee. Freelancers receive 85% of every booking.",
                        "Is there a warranty?" to "Yes — all jobs carry a 14-day service guarantee. Contact support if you're unsatisfied."
                    ).forEach { (q, a) ->
                        Column {
                            Text(q, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text(a, style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
                        }
                        HorizontalDivider(color = FixoraDivider, thickness = 0.5.dp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Close", fontWeight = FontWeight.Bold, color = FixoraPrimary)
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Submit Complaint Dialog
    if (showComplaintDialog) {
        AlertDialog(
            onDismissRequest = { showComplaintDialog = false; complaintSubmitted = false; complaintText = "" },
            icon = { Icon(Icons.Default.ReportProblem, contentDescription = null, tint = FixoraWarning) },
            title = { Text("Submit a Complaint", fontWeight = FontWeight.Bold) },
            text = {
                if (complaintSubmitted) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FixoraSuccess, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Complaint Submitted!", fontWeight = FontWeight.Bold, color = FixoraSuccess)
                        Text("Our team will review and respond within 24 hours.", style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Describe your issue and we'll investigate promptly.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FixoraTextSecondary
                        )
                        OutlinedTextField(
                            value = complaintText,
                            onValueChange = { complaintText = it },
                            label = { Text("Describe your complaint") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 5
                        )
                    }
                }
            },
            confirmButton = {
                if (complaintSubmitted) {
                    TextButton(onClick = { showComplaintDialog = false; complaintSubmitted = false; complaintText = "" }) {
                        Text("Done", fontWeight = FontWeight.Bold, color = FixoraPrimary)
                    }
                } else {
                    Button(
                        onClick = {
                            if (complaintText.isNotBlank()) {
                                FixoraRepository.submitComplaintTicket(currentUser.name, currentUser.email, complaintText.trim())
                                complaintSubmitted = true
                            }
                        },
                        enabled = complaintText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoraAction),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Submit", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (!complaintSubmitted) {
                    TextButton(onClick = { showComplaintDialog = false; complaintText = "" }) { Text("Cancel") }
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            icon = { Icon(Icons.Default.Security, contentDescription = null, tint = FixoraPrimary) },
            title = { Text("Privacy & Terms", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Privacy and support", fontWeight = FontWeight.SemiBold, color = FixoraPrimary)
                    Text(
                        "Contact support for questions about your personal information, service disputes, or account privacy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FixoraTextSecondary
                    )
                    HorizontalDivider(color = FixoraDivider)
                    Text("Data We Collect", fontWeight = FontWeight.SemiBold)
                    Text(
                        "• Name, phone number, and email\n• Service location (city & area)\n• Booking history and payment records",
                        style = MaterialTheme.typography.bodySmall,
                        color = FixoraTextSecondary
                    )
                    HorizontalDivider(color = FixoraDivider)
                    Text("Your Rights", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Contact support through the app to request account or data deletion.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FixoraTextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Got it", fontWeight = FontWeight.Bold, color = FixoraPrimary)
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // My Reviews Dialog
    if (showReviewsDialog) {
        val myReviewsList = allReviews.filter { it.customerId == currentUser.id }
        AlertDialog(
            onDismissRequest = { showReviewsDialog = false },
            icon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(32.dp)) },
            title = { Text(strings.myReviews, fontWeight = FontWeight.Bold) },
            text = {
                if (myReviewsList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Reviews Submitted Yet",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = FixoraTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ratings and feedback you provide after job completion will appear here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FixoraTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(myReviewsList.size) { idx ->
                            val rev = myReviewsList[idx]
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = FixoraSurfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            (1..5).forEach { star ->
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (star <= rev.rating) Color(0xFFF59E0B) else Color.LightGray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${rev.rating}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFFF59E0B)
                                            )
                                        }
                                        Text(
                                            text = "#${rev.bookingId.takeLast(6)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = FixoraTextMuted
                                        )
                                    }
                                    if (rev.comment.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = rev.comment,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FixoraTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReviewsDialog = false }) {
                    Text(strings.close, fontWeight = FontWeight.Bold, color = FixoraPrimary)
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }


    if (showAddresses) {
        AlertDialog(
            onDismissRequest = { showAddresses = false },
            title = { Text(strings.savedAddresses) },
            text = {
                if (addresses.isEmpty()) {
                    Text(strings.noSavedAddresses)
                } else {
                    LazyColumn(Modifier.heightIn(max = 300.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(addresses.size) { index ->
                            val address = addresses[index]
                            Column {
                                Text(address.label, style = MaterialTheme.typography.titleSmall)
                                Text(address.fullAddressDisplay, color = FixoraTextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showAddresses = false
                    addrLabel = ""
                    addrArea = ""
                    addrCity = ""
                    showAddAddressDialog = true
                }) { Text(strings.addAddress) }
            },
            dismissButton = { TextButton(onClick = { showAddresses = false }) { Text(strings.close) } }
        )
    }

    Scaffold(topBar = { FixoraTopBar("Profile", canNavigateBack = true, onNavigateBack = onNavigateBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(FixoraPageBrush),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(shape = MaterialTheme.shapes.large, color = FixoraSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            Modifier.size(48.dp).background(FixoraPrimaryLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = currentUser.name.trim().split(' ').filter { it.isNotBlank() }
                                .take(2).joinToString("") { it.take(1).uppercase() }
                            Text(initials, color = FixoraPrimary, fontWeight = FontWeight.Bold)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(currentUser.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(currentUser.email, style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
                        }
                        IconButton(onClick = {
                            editName = currentUser.name
                            editEmail = currentUser.email
                            showEditDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = strings.editProfileTitle, tint = FixoraPrimary)
                        }
                    }
                }
            }
            item {
                ProfileSection(strings.activity) {
                    ProfileMenuRow(Icons.Default.DateRange, strings.myBookings, onClick = onMyBookingsClick)
                    ProfileMenuRow(Icons.Default.Star, strings.myReviews) { showReviewsDialog = true }
                    ProfileMenuRow(Icons.Default.LocationOn, strings.savedAddresses) { showAddresses = true }
                }
            }
            item {
                ProfileSection("Preferences") {
                    ProfileMenuRow(Icons.Default.Language, strings.changeLanguage, currentLanguage.nativeTitle) { showLanguageDialog = true }
                    ProfileMenuRow(Icons.Default.DarkMode, "Appearance", currentThemeMode.title) { showThemeDialog = true }
                }
            }
            item {
                ProfileSection(strings.supportAndLegal) {
                    ProfileMenuRow(Icons.AutoMirrored.Filled.HelpOutline, strings.helpCenter) { showHelpDialog = true }
                    ProfileMenuRow(Icons.Default.ReportProblem, strings.submitComplaint) {
                        complaintText = ""
                        complaintSubmitted = false
                        showComplaintDialog = true
                    }
                    ProfileMenuRow(Icons.Default.Security, strings.privacyPolicy) { showPrivacyDialog = true }
                }
            }
            if (FixoraRepository.isUserAdmin(currentUser.email)) {
                item { ProfileMenuRow(Icons.Default.AdminPanelSettings, strings.adminPanel, onClick = onAdminPanelClick) }
            }
            item {
                TextButton(onClick = { showLogoutDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(strings.signOut, color = FixoraEmergency)
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = FixoraTextSecondary)
        Surface(shape = MaterialTheme.shapes.large, color = FixoraSurface) {
            Column(content = content)
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    label: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(20.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = FixoraTextMuted)
    }
}
