package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fixora.marketplace.data.model.PricingType
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun ServiceCategoryScreen(
    categoryId: String,
    onNavigateBack: () -> Unit,
    onServiceSelect: (String) -> Unit,
    onGigSelect: (String) -> Unit
) {
    val categories by FixoraRepository.categories.collectAsState()
    val catalog by FixoraRepository.catalog.collectAsState()

    val gigs by FixoraRepository.gigs.collectAsState()
    val category = categories.firstOrNull { it.id == categoryId }
    val services = catalog.filter { it.categoryId == categoryId }
    val categoryGigs = gigs.filter { it.categoryId == categoryId }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = category?.name ?: "Services",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FixoraPageBrush),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            if (category != null && category.description.isNotBlank()) {
                item {
                    Text(
                        category.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FixoraTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (services.isEmpty() && categoryGigs.isEmpty()) {
                item { Text("No services are listed in this category yet.", color = FixoraTextSecondary) }
            }
            if (services.isNotEmpty()) {
                item { Text("Service packages", style = MaterialTheme.typography.titleMedium) }
            }
            items(services, key = { it.id }) { service ->
                Surface(onClick = { onServiceSelect(service.id) }, shape = MaterialTheme.shapes.medium, color = FixoraSurface) {
                    val priceType = if (service.pricingType == PricingType.FIXED) "Fixed price" else "Diagnostic fee"
                    ListItem(
                        headlineContent = { Text(service.name) },
                        supportingContent = { Text("${service.durationMinutes} min · $priceType") },
                        trailingContent = { Text("PKR ${service.priceAed.toInt()}", color = FixoraPrimary, fontWeight = FontWeight.SemiBold) },
                        colors = ListItemDefaults.colors(containerColor = FixoraSurface)
                    )
                }
            }
            if (categoryGigs.isNotEmpty()) {
                item { Text("Professional listings", style = MaterialTheme.typography.titleMedium) }
                items(categoryGigs, key = { it.id }) { gig ->
                    Surface(onClick = { onGigSelect(gig.id) }, shape = MaterialTheme.shapes.medium, color = FixoraSurface) {
                        ListItem(
                            headlineContent = { Text(gig.title) },
                            supportingContent = { Text(gig.sellerName) },
                            colors = ListItemDefaults.colors(containerColor = FixoraSurface)
                        )
                    }
                }
            }

        }
    }
}


