package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

private data class SearchResult(
    val key: String,
    val title: String,
    val subtitle: String,
    val keywords: String,
    val open: () -> Unit
)

@Composable
fun CustomerSearchScreen(
    onNavigateBack: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onServiceClick: (String) -> Unit,
    onGigClick: (String) -> Unit,
    onProviderClick: (String) -> Unit
) {
    val categories by FixoraRepository.categories.collectAsState()
    val catalog by FixoraRepository.catalog.collectAsState()
    val gigs by FixoraRepository.gigs.collectAsState()
    val providers by FixoraRepository.providers.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    val terms = query.trim().split(Regex("\\s+")).filter(String::isNotBlank)
    val categoryNames = categories.associate { it.id to it.name }
    val categoryResults = categories.map {
        SearchResult("category-" + it.id, it.name, "Category", it.subSkills.joinToString(" ")) { onCategoryClick(it.id) }
    }
    val results = if (terms.isEmpty()) {
        categoryResults
    } else {
        buildList {
            addAll(categoryResults)
            catalog.forEach {
                add(SearchResult("service-" + it.id, it.name, categoryNames[it.categoryId] ?: "Service", it.description) { onServiceClick(it.id) })
            }
            gigs.forEach {
                val keywords = categoryNames[it.categoryId].orEmpty() + " " + it.description
                add(SearchResult("gig-" + it.id, it.title, it.sellerName, keywords) { onGigClick(it.id) })
            }
            providers.filter { it.isVerified }.forEach {
                val skills = it.skills.mapNotNull(categoryNames::get).joinToString(", ")
                add(SearchResult("provider-" + it.id, it.name, skills.ifBlank { "Professional" }, it.serviceAreas.joinToString(" ")) { onProviderClick(it.id) })
            }
        }.filter { result ->
            val searchableText = listOf(result.title, result.subtitle, result.keywords).joinToString(" ")
            terms.all { searchableText.contains(it, ignoreCase = true) }
        }
    }

    Scaffold(topBar = { FixoraTopBar("Search", canNavigateBack = true, onNavigateBack = onNavigateBack) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(FixoraPageBrush).imePadding()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Services, categories, or professionals") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        if (terms.isEmpty()) "Browse categories" else if (results.isEmpty()) "No matching results" else "Search results",
                        style = MaterialTheme.typography.titleMedium,
                        color = FixoraTextPrimary
                    )
                }
                if (results.isEmpty() && terms.isNotEmpty()) {
                    item { Text("Try a different service, skill, or name.", color = FixoraTextSecondary) }
                }
                items(results, key = { it.key }) { result ->
                    Surface(onClick = result.open, shape = MaterialTheme.shapes.medium, color = FixoraSurface) {
                        ListItem(
                            headlineContent = { Text(result.title) },
                            supportingContent = { Text(result.subtitle) },
                            trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null) },
                            colors = ListItemDefaults.colors(containerColor = FixoraSurface)
                        )
                    }
                }
            }
        }
    }
}
