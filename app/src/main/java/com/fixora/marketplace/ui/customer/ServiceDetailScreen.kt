package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onNavigateBack: () -> Unit,
    onBookNow: (String) -> Unit
) {
    val catalog by FixoraRepository.catalog.collectAsState()
    val categories by FixoraRepository.categories.collectAsState()

    val service = catalog.firstOrNull { it.id == serviceId } ?: catalog.first()
    val category = categories.firstOrNull { it.id == service.categoryId }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Service Details",
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
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Estimated Total",
                            style = MaterialTheme.typography.labelSmall,
                            color = FixoraTextSecondary
                        )
                        Text(
                            text = "PKR ${service.priceAed.toInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = FixoraPrimary
                        )
                    }
                    Button(
                        onClick = { onBookNow(service.id) },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                    ) {
                        Text("Book Service", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
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
                Text(category?.name ?: "Service", style = MaterialTheme.typography.labelLarge, color = FixoraPrimary)
                Text(service.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("${service.durationMinutes} min · 14-day guarantee", style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
            }
            item {
                Text(service.description, style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)
            }
            item {
                var expanded by rememberSaveable { mutableStateOf(false) }
                Surface(shape = MaterialTheme.shapes.medium, color = FixoraSurface) {
                    Column {
                        TextButton(
                            onClick = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        ) {
                            Text(if (expanded) "Hide package details" else "View package details", modifier = Modifier.weight(1f))
                            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                        }
                        if (expanded) {
                            Column(
                                Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (service.inclusions.isNotEmpty()) {
                                    Text("Included", style = MaterialTheme.typography.titleSmall)
                                    service.inclusions.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
                                }
                                if (service.exclusions.isNotEmpty()) {
                                    Text("Excluded", style = MaterialTheme.typography.titleSmall)
                                    service.exclusions.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
                                }
                                Text("Payment", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "The platform fee is 15%. The professional receives the remaining 85% after you approve the completed service.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FixoraTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
