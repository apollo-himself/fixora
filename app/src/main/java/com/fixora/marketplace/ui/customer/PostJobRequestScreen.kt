package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.i18n.LocalAppStrings
import com.fixora.marketplace.data.model.Urgency
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun PostJobRequestScreen(
    onNavigateBack: () -> Unit,
    onRequestCreated: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    val selectedAddress by FixoraRepository.selectedAddress.collectAsState()
    val categories by FixoraRepository.categories.collectAsState()

    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.first().id) }
    var description by remember { mutableStateOf("") }
    var budgetInput by remember { mutableStateOf("250") }
    var selectedUrgency by remember { mutableStateOf(Urgency.STANDARD) }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = strings.postJobScreenTitle,
                subtitle = strings.postJobScreenSubtitle,
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            Surface(color = FixoraSurface, tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            val budget = budgetInput.toDoubleOrNull() ?: 200.0
                            val req = FixoraRepository.postJobRequest(
                                title = if (title.isBlank()) "Home maintenance repair" else title,
                                categoryId = selectedCategory,
                                description = description,
                                budgetAed = budget,
                                emirate = selectedAddress.emirate,
                                area = selectedAddress.area,
                                urgency = selectedUrgency
                            )
                            onRequestCreated(req.id)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.postJobSubmitBtn, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                    colors = CardDefaults.cardColors(containerColor = FixoraPrimaryLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoraPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "How Fixora Buyer Requests Work:",
                            fontWeight = FontWeight.Bold,
                            color = FixoraPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "1. Describe what you need fixed.\n2. Independent freelance technicians in your area submit custom quotes/bids.\n3. Compare proposals, ratings, and choose the best technician.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FixoraTextPrimary
                        )
                    }
                }
            }

            // Job Title
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Job Title", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("e.g. Master bathroom water heater leaking") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Description
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Detailed Problem Description", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Mention specific issues, appliance brand, accessibility, or preferred timing...") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Budget in PKR
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Your Budget (PKR)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Freelancers can bid above or below this figure", style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = budgetInput,
                            onValueChange = { budgetInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Location Confirmation
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Location for Dispatch", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "${selectedAddress.area}, ${selectedAddress.emirate}", fontWeight = FontWeight.SemiBold, color = FixoraPrimary)
                        Text(text = selectedAddress.fullAddressDisplay, style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
                    }
                }
            }
        }
    }
}
