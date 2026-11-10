package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.Address
import com.fixora.marketplace.data.model.FreelanceGig
import com.fixora.marketplace.data.model.GigPackage
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.data.i18n.LocalAppStrings
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.components.StarRatingBar
import com.fixora.marketplace.ui.theme.*
import java.util.UUID

@Composable
fun GigDetailScreen(
    gigId: String,
    onNavigateBack: () -> Unit,
    onOrderGig: (FreelanceGig, GigPackage) -> Unit,
    onContactSeller: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    val gigs by FixoraRepository.gigs.collectAsState()
    val gig = gigs.firstOrNull { it.id == gigId } ?: gigs.first()

    val customerAddresses by FixoraRepository.customerAddresses.collectAsState()
    var showAddressPrompt by remember { mutableStateOf(false) }

    var addressStreet by remember { mutableStateOf("") }
    var addressArea by remember { mutableStateOf("DHA Phase 5") }
    var addressCity by remember { mutableStateOf("Lahore") }
    var addressError by remember { mutableStateOf<String?>(null) }

    var selectedPackageIndex by remember { mutableIntStateOf(1) } // Default to Standard
    val currentPackage = gig.packages.getOrElse(selectedPackageIndex) { gig.packages.first() }

    // Address Prompt Dialog if user has no address
    if (showAddressPrompt) {
        AlertDialog(
            onDismissRequest = { showAddressPrompt = false },
            title = {
                Text(
                    text = strings.deliveryAddressTitle,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = strings.deliveryAddressPrompt,
                        style = MaterialTheme.typography.bodySmall,
                        color = FixoraTextSecondary
                    )

                    OutlinedTextField(
                        value = addressStreet,
                        onValueChange = {
                            addressStreet = it
                            addressError = null
                        },
                        label = { Text(strings.houseStreetLabel) },
                        placeholder = { Text(strings.houseStreetPlaceholder) },
                        singleLine = true,
                        isError = addressError != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = addressArea,
                        onValueChange = { addressArea = it },
                        label = { Text(strings.areaLabel) },
                        placeholder = { Text(strings.areaPlaceholder) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = addressCity,
                        onValueChange = { addressCity = it },
                        label = { Text(strings.cityLabel) },
                        placeholder = { Text(strings.cityPlaceholder) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (addressError != null) {
                        Text(
                            text = addressError!!,
                            color = FixoraEmergency,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedStreet = addressStreet.trim()
                        if (trimmedStreet.isBlank()) {
                            addressError = "Please enter your address"
                            return@Button
                        }
                        val newAddress = Address(
                            id = "ADDR-${UUID.randomUUID().toString().take(6)}",
                            label = "Home - $addressArea",
                            street = trimmedStreet,
                            buildingOrVilla = "Residence",
                            apartmentNo = "",
                            emirate = addressCity.ifBlank { "Lahore" },
                            area = addressArea.ifBlank { "DHA Phase 5" },
                            latitude = 31.5204,
                            longitude = 74.3587,
                            isDefault = true
                        )
                        FixoraRepository.addAddress(newAddress)
                        FixoraRepository.selectAddress(newAddress)
                        showAddressPrompt = false
                        onOrderGig(gig, currentPackage)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                ) {
                    Text(strings.saveAndContinue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressPrompt = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = strings.gigDetailsTitle,
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${currentPackage.name} ${strings.packagePriceLabel}",
                            style = MaterialTheme.typography.labelSmall,
                            color = FixoraTextSecondary
                        )
                        Text(
                            text = "PKR ${currentPackage.priceAed.toInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = FixoraPrimary
                        )
                    }
                    Button(
                        onClick = {
                            if (customerAddresses.isEmpty()) {
                                showAddressPrompt = true
                            } else {
                                onOrderGig(gig, currentPackage)
                            }
                        },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                    ) {
                        Text(strings.orderGigBtn(currentPackage.priceAed.toInt()), fontWeight = FontWeight.Bold)
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
            // Seller Header Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(FixoraDarkNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = gig.sellerName.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = gig.sellerName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                color = FixoraPrimaryLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = gig.sellerBadge,
                                    color = FixoraPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            StarRatingBar(rating = gig.sellerRating, reviewCount = gig.sellerReviewCount)
                        }
                        IconButton(onClick = { onContactSeller(gig.sellerId) }) {
                            Icon(Icons.Default.Chat, contentDescription = "Message Seller", tint = FixoraPrimary)
                        }
                    }
                }
            }

            // Gig Title & Description
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = gig.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = gig.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = FixoraTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = FixoraTextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Service coverage: ${gig.emirates.joinToString(", ")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = FixoraTextSecondary
                            )
                        }
                    }
                }
            }

            // Fiverr-style Package Selector Tabs
            item {
                Text(
                    text = "Select Package",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                TabRow(
                    selectedTabIndex = selectedPackageIndex,
                    containerColor = FixoraSurface,
                    contentColor = FixoraPrimary
                ) {
                    gig.packages.forEachIndexed { index, pkg ->
                        Tab(
                            selected = selectedPackageIndex == index,
                            onClick = { selectedPackageIndex = index },
                            text = { Text(pkg.name.split(" ").firstOrNull() ?: "Pkg", fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            // Package Details Card
            item {
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
                            Text(text = currentPackage.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = "PKR ${currentPackage.priceAed.toInt()}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall, color = FixoraPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = currentPackage.description, style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = strings.estimatedTime(currentPackage.durationMinutes), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = FixoraDivider)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = strings.whatsIncluded, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        currentPackage.inclusions.forEach { inc ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FixoraSuccess, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = inc, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}


