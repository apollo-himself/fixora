package com.fixora.marketplace.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun ProviderAvailabilityScreen(
    onNavigateBack: () -> Unit
) {
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val providers by FixoraRepository.providers.collectAsState()

    val myProfile = providers.firstOrNull { it.userId == currentUser.id }
        ?: run { Text("Your provider profile is loading or unavailable."); return }

    val availableAreas = listOf("DHA Phase 5, Lahore", "Downtown", "Al Barsha", "JBR", "Business Bay", "Deira", "Jumeirah", "Karachi", "Al Majaz", "Islamabad")
    var selectedAreas by remember { mutableStateOf(myProfile.serviceAreas.toSet()) }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Service Areas & Hours",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            Surface(color = FixoraSurface, tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            FixoraRepository.updateProviderServiceAreas(myProfile.id, selectedAreas.toList())
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                    ) {
                        Text("Save Authorized Areas", fontWeight = FontWeight.Bold)
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
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Working Hours", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Daily 08:00 AM - 08:00 PM (Emergency 24/7 if enabled)", color = FixoraTextSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                Text(text = "Select Service Coverage Areas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        availableAreas.forEach { area ->
                            val isChecked = selectedAreas.contains(area)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAreas = if (isChecked) selectedAreas - area else selectedAreas + area
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        selectedAreas = if (isChecked) selectedAreas - area else selectedAreas + area
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = area, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}


