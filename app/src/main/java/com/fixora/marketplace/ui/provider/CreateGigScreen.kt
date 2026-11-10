package com.fixora.marketplace.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.GigPackage
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun CreateGigScreen(
    onNavigateBack: () -> Unit,
    onGigCreated: () -> Unit
) {
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val providers by FixoraRepository.providers.collectAsState()
    val myProfile = providers.firstOrNull { it.userId == currentUser.id }
        ?: providers.firstOrNull { it.id == "PROV-01" } ?: providers.first()

    var title by remember { mutableStateOf("I will ") }
    var selectedCategory by remember { mutableStateOf("AC_SERVICE") }
    var description by remember { mutableStateOf("") }
    var basicPriceText by remember { mutableStateOf("120") }
    var standardPriceText by remember { mutableStateOf("180") }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Create New Freelance Gig",
                subtitle = "Offer your maintenance services on Fixora",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            Surface(color = FixoraSurface, tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            val basicPrice = basicPriceText.toDoubleOrNull() ?: 120.0
                            val standardPrice = standardPriceText.toDoubleOrNull() ?: 180.0
                            FixoraRepository.createGig(
                                sellerId = myProfile.id,
                                title = title,
                                categoryId = selectedCategory,
                                description = description,
                                packages = listOf(
                                    GigPackage("PKG-B", "Basic Package", "Standard diagnostic or basic fix", 45, listOf("Diagnostic", "Inspection"), basicPrice),
                                    GigPackage("PKG-S", "Standard Package", "Complete service with guarantee", 90, listOf("Full repair", "30-day warranty"), standardPrice)
                                )
                            )
                            onGigCreated()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publish Gig to Fixora Marketplace", fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                        Text(text = "Gig Title", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Start with 'I will...' describing what repair or installation you will perform", style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("e.g. I will replace dripping bathroom faucets and valves") },
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
                        Text(text = "Gig Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Explain your expertise, tools, equipment, and warranty", style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Describe the scope of work and why clients should choose you...") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Pricing Packages
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Set Your PKR Pricing", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Fixora fee is 15% on completion. You keep 85%.", style = MaterialTheme.typography.labelSmall, color = FixoraSuccess, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = basicPriceText,
                            onValueChange = { basicPriceText = it },
                            label = { Text("Basic Package Price (PKR)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = standardPriceText,
                            onValueChange = { standardPriceText = it },
                            label = { Text("Standard Package Price (PKR)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }
    }
}
