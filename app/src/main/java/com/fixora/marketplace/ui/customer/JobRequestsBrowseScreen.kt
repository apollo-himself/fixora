package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.CustomJobRequest
import com.fixora.marketplace.data.model.UserRole
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.components.StarRatingBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun JobRequestsBrowseScreen(
    onNavigateBack: () -> Unit,
    onProposalAccepted: (String) -> Unit
) {
    val currentUser by FixoraRepository.currentUser.collectAsState()
    val jobRequests by FixoraRepository.jobRequests.collectAsState()
    val proposals by FixoraRepository.proposals.collectAsState()
    val selectedAddress by FixoraRepository.selectedAddress.collectAsState()
    val categories by FixoraRepository.categories.collectAsState()
    val categoryNames = categories.associate { it.id to it.name }
    val categoryOptions = jobRequests.map { it.categoryId }.distinct().associateWith { id ->
        categoryNames[id] ?: id.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
    }

    var query by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var categoryId by rememberSaveable { mutableStateOf<String?>(null) }
    var minimumBudget by rememberSaveable { mutableStateOf("") }
    var maximumBudget by rememberSaveable { mutableStateOf("") }
    var sortOrder by rememberSaveable { mutableStateOf(BidSort.NEWEST) }
    var filtersExpanded by rememberSaveable { mutableStateOf(false) }

    val min = minimumBudget.trim().toDoubleOrNull()
    val max = maximumBudget.trim().toDoubleOrNull()
    val invalidMin = minimumBudget.isNotBlank() && (min == null || !min.isFinite() || min < 0)
    val invalidMax = maximumBudget.isNotBlank() && (max == null || !max.isFinite() || max < 0)
    val invertedRange = !invalidMin && !invalidMax && min != null && max != null && min > max
    val hasBudgetError = invalidMin || invalidMax || invertedRange
    val hasFilters = query.isNotBlank() || location.isNotBlank() || categoryId != null ||
        minimumBudget.isNotBlank() || maximumBudget.isNotBlank() || sortOrder != BidSort.NEWEST
    val matchingRequests = if (hasBudgetError) emptyList() else jobRequests.filter { request ->
        val requestLocation = listOf(request.area, request.emirate).joinToString(" ")
        val searchableText = listOf(request.title, request.description, categoryOptions[request.categoryId].orEmpty())
            .joinToString(" ")
        (categoryId == null || request.categoryId == categoryId) &&
            searchableText.contains(query.trim(), ignoreCase = true) &&
            requestLocation.contains(location.trim(), ignoreCase = true) &&
            (min == null || request.budgetAed >= min) &&
            (max == null || request.budgetAed <= max)
    }
    val visibleRequests = when (sortOrder) {
        BidSort.NEWEST -> matchingRequests.sortedByDescending { it.createdAt }
        BidSort.LOWEST -> matchingRequests.sortedBy { it.budgetAed }
        BidSort.HIGHEST -> matchingRequests.sortedByDescending { it.budgetAed }
    }

    var selectedRequestForBidding by remember { mutableStateOf<CustomJobRequest?>(null) }
    var bidAmountText by remember { mutableStateOf("200") }
    var turnaroundText by remember { mutableStateOf("Within 2 hours today") }
    var coverLetterText by remember { mutableStateOf("Hi! I have 8 years experience with this exact issue. I can bring all necessary parts and guarantee my work for 30 days.") }

    if (selectedRequestForBidding != null) {
        val req = selectedRequestForBidding!!
        AlertDialog(
            onDismissRequest = { selectedRequestForBidding = null },
            title = { Text("Submit Proposal / Bid", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Job: ${req.title}", fontWeight = FontWeight.SemiBold, color = FixoraPrimary)
                    Text(text = "Client Budget: PKR ${req.budgetAed.toInt()}", style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = bidAmountText,
                        onValueChange = { bidAmountText = it },
                        label = { Text("Your Bid Price (PKR)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = turnaroundText,
                        onValueChange = { turnaroundText = it },
                        label = { Text("Arrival / Turnaround Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = coverLetterText,
                        onValueChange = { coverLetterText = it },
                        label = { Text("Cover Note to Client") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bid = bidAmountText.toDoubleOrNull() ?: req.budgetAed
                        FixoraRepository.submitProposal(
                            jobRequestId = req.id,
                            sellerId = currentUser.id,
                            bidAmountAed = bid,
                            deliveryTimeText = turnaroundText,
                            coverLetter = coverLetterText
                        )
                        selectedRequestForBidding = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                ) {
                    Text("Send Bid", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRequestForBidding = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = if (currentUser.role == UserRole.PROVIDER) "Client Maintenance Requests" else "Open Maintenance Bids",
                subtitle = "Freelance Bidding Board",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (currentUser.role == UserRole.PROVIDER) "Send custom quotes to clients in need" else "Compare bids from certified freelance technicians",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Fixora acts as the escrow & trust layer. Freelancers only get paid once the client inspects and approves the completed repair.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FixoraTextSecondary
                        )
                    }
                }
            }


            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search requests") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = { filtersExpanded = !filtersExpanded }) {
                            Icon(Icons.Default.FilterList, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (filtersExpanded) "Hide filters" else "Filters and sort")
                        }
                        if (hasFilters) {
                            TextButton(onClick = {
                                query = ""
                                location = ""
                                categoryId = null
                                minimumBudget = ""
                                maximumBudget = ""
                                sortOrder = BidSort.NEWEST
                            }) { Text("Reset") }
                        }
                    }

                    if (filtersExpanded) {
                        BidFilterMenu(
                            label = "Category",
                            value = categoryId?.let(categoryOptions::get) ?: "All categories"
                        ) { close ->
                            DropdownMenuItem(text = { Text("All categories") }, onClick = { categoryId = null; close() })
                            categoryOptions.entries.sortedBy { it.value }.forEach { (id, name) ->
                                DropdownMenuItem(text = { Text(name) }, onClick = { categoryId = id; close() })
                            }
                        }
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("City or area") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Client budget (PKR)", style = MaterialTheme.typography.labelLarge, color = FixoraTextPrimary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = minimumBudget,
                                onValueChange = { minimumBudget = it },
                                label = { Text("Min") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                isError = invalidMin || invertedRange,
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = maximumBudget,
                                onValueChange = { maximumBudget = it },
                                label = { Text("Max") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                isError = invalidMax || invertedRange,
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        BidFilterMenu(label = "Sort by", value = sortOrder.label) { close ->
                            BidSort.entries.forEach { order ->
                                DropdownMenuItem(text = { Text(order.label) }, onClick = { sortOrder = order; close() })
                            }
                        }
                    }

                    val summary = when {
                        invertedRange -> "Minimum budget must be lower than or equal to the maximum."
                        hasBudgetError -> "Enter a valid, non-negative budget."
                        visibleRequests.isEmpty() && hasFilters -> "No requests match these filters."
                        visibleRequests.isEmpty() -> "No requests yet."
                        visibleRequests.size == 1 -> "1 request"
                        else -> "${visibleRequests.size} requests"
                    }
                    Text(
                        summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hasBudgetError) FixoraEmergency else FixoraTextSecondary
                    )
                }
            }

            items(visibleRequests, key = { it.id }) { req ->
                val reqProposals = proposals.filter { it.jobRequestId == req.id }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = req.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Posted by ${req.customerName} • ${req.area}, ${req.emirate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FixoraTextSecondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "PKR ${req.budgetAed.toInt()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoraPrimary
                                )
                                Text(text = "Client Budget", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = categoryOptions[req.categoryId].orEmpty(),
                            style = MaterialTheme.typography.labelMedium,
                            color = FixoraPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = req.description, style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${reqProposals.size} Freelancer Bids Submitted",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                color = FixoraPrimary
                            )
                        }

                        // If user is Provider, show "Send Bid" button
                        if (currentUser.role == UserRole.PROVIDER) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { selectedRequestForBidding = req },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Send Proposal / Bid (PKR)")
                            }
                        }

                        // If user is Customer or viewing proposals, show submitted bids list
                        if (reqProposals.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = FixoraDivider)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Received Proposals:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))

                            reqProposals.forEach { prop ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = FixoraSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(FixoraDarkNavy),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(text = prop.sellerName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(text = prop.sellerName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                                    StarRatingBar(rating = prop.sellerRating)
                                                }
                                            }
                                            Text(
                                                text = "PKR ${prop.bidAmountAed.toInt()}",
                                                fontWeight = FontWeight.Bold,
                                                color = FixoraSuccess,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "\"${prop.coverLetter}\"", style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Timing: ${prop.deliveryTimeText}", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)

                                        if (currentUser.role == UserRole.CUSTOMER) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    val booking = FixoraRepository.acceptProposal(prop, req, selectedAddress)
                                                    onProposalAccepted(booking.id)
                                                },
                                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = FixoraSuccess)
                                            ) {
                                                Text("Accept Proposal & Hire (${prop.sellerName})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


private enum class BidSort(val label: String) {
    NEWEST("Newest first"),
    LOWEST("Lowest budget first"),
    HIGHEST("Highest budget first")
}

@Composable
private fun BidFilterMenu(
    label: String,
    value: String,
    options: @Composable (() -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$label: $value", modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options { expanded = false }
        }
    }
}
