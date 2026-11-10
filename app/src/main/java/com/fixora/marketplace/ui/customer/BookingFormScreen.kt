package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.fixora.marketplace.data.model.*
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.domain.engine.PricingCommissionEngine
import com.fixora.marketplace.ui.components.FixoraButton
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun BookingFormScreen(
    serviceId: String,
    onNavigateBack: () -> Unit,
    onBookingCreated: (String) -> Unit
) {
    val catalog by FixoraRepository.catalog.collectAsState()
    val selectedAddress by FixoraRepository.selectedAddress.collectAsState()
    val adminSettings by FixoraRepository.adminSettings.collectAsState()

    val service = catalog.firstOrNull { it.id == serviceId } ?: catalog.first()

    var selectedPropertyType by remember { mutableStateOf(PropertyType.APARTMENT) }
    var selectedUrgency by remember { mutableStateOf(Urgency.STANDARD) }
    var selectedDate by remember { mutableStateOf("Today, Oct 19") }
    var selectedTimeSlot by remember { mutableStateOf("2:00 PM - 4:00 PM") }
    var issueDescription by remember { mutableStateOf("AC not cooling efficiently and filter light blinking.") }
    var hasPhotoAttached by remember { mutableStateOf(true) }

    val pricing = PricingCommissionEngine.calculateBreakdown(
        basePriceAed = service.priceAed,
        partsLaborAed = null,
        urgency = selectedUrgency,
        settings = adminSettings
    )

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Book Service",
                subtitle = service.name,
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Total Payable", style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
                            Text(
                                text = "PKR ${pricing.totalAed}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = FixoraPrimary
                            )
                        }
                        Button(
                            onClick = {
                                val booking = FixoraRepository.createBooking(
                                    serviceItem = service,
                                    customerAddress = selectedAddress,
                                    propertyType = selectedPropertyType,
                                    urgency = selectedUrgency,
                                    scheduledDate = selectedDate,
                                    scheduledTimeSlot = selectedTimeSlot,
                                    issueDescription = issueDescription,
                                    photoUrls = if (hasPhotoAttached) listOf("sample_ac_issue.jpg") else emptyList()
                                )
                                onBookingCreated(booking.id)
                            },
                            modifier = Modifier.height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                        ) {
                            Text("Find Technicians", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
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
            // Address Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = FixoraPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Service Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = selectedAddress.label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                        Text(text = selectedAddress.fullAddressDisplay, style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)
                    }
                }
            }

            // Property Type
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Property Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PropertyType.values().forEach { type ->
                                val isSelected = selectedPropertyType == type
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) FixoraPrimaryLight else FixoraSurfaceVariant,
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, FixoraPrimary) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedPropertyType = type }
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) FixoraPrimary else FixoraTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Urgency Selector
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Service Speed & Urgency", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        Urgency.values().forEach { urgency ->
                            val isSelected = selectedUrgency == urgency
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) FixoraPrimaryLight else FixoraSurfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, FixoraPrimary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedUrgency = urgency }
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedUrgency = urgency }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = when (urgency) {
                                                Urgency.STANDARD -> "Standard Scheduled Visit"
                                                Urgency.SAME_DAY -> "Same Day Express (+0 PKR)"
                                                Urgency.EMERGENCY_IMMEDIATE -> "24/7 Immediate Emergency (+PKR 50 surge)"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            color = if (urgency == Urgency.EMERGENCY_IMMEDIATE) FixoraEmergency else FixoraTextPrimary
                                        )
                                        Text(
                                            text = when (urgency) {
                                                Urgency.STANDARD -> "Book convenient slot across next 7 days"
                                                Urgency.SAME_DAY -> "Arrival within 2-4 hours today"
                                                Urgency.EMERGENCY_IMMEDIATE -> "Priority dispatch within 30-45 minutes"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = FixoraTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Issue Description & Photos
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Describe the Issue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = issueDescription,
                            onValueChange = { issueDescription = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { hasPhotoAttached = !hasPhotoAttached },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = hasPhotoAttached, onCheckedChange = { hasPhotoAttached = it })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Attach photo of equipment / leak (Simulated)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Pricing Breakdown Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Price Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Base Service Fee", color = FixoraTextSecondary)
                            Text(text = "PKR ${pricing.baseServiceAed.toInt()}")
                        }
                        if (pricing.emergencyFeeAed > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Emergency Surge Dispatch", color = FixoraEmergency)
                                Text(text = "+PKR ${pricing.emergencyFeeAed.toInt()}", color = FixoraEmergency)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Sales Tax/GST (5%)", color = FixoraTextSecondary)
                            Text(text = "PKR ${pricing.vatAed}")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = FixoraDivider)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total Price", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text(text = "PKR ${pricing.totalAed}", fontWeight = FontWeight.Bold, color = FixoraPrimary, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
    }
}

