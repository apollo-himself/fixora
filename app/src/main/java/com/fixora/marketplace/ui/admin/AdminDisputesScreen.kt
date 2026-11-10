package com.fixora.marketplace.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.fixora.marketplace.data.model.DisputeStatus
import com.fixora.marketplace.data.model.DisputeTicket
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

private enum class DisputeFilter(val label: String) {
    ALL("All"),
    OPEN("Open"),
    UNDER_REVIEW("In Review"),
    RESOLVED("Resolved")
}

@Composable
fun AdminDisputesScreen(
    onNavigateBack: () -> Unit
) {
    val disputes by FixoraRepository.disputes.collectAsState()
    var filter by remember { mutableStateOf(DisputeFilter.OPEN) }

    val filtered = when (filter) {
        DisputeFilter.ALL          -> disputes
        DisputeFilter.OPEN         -> disputes.filter { it.status == DisputeStatus.OPEN }
        DisputeFilter.UNDER_REVIEW -> disputes.filter { it.status == DisputeStatus.UNDER_REVIEW }
        DisputeFilter.RESOLVED     -> disputes.filter {
            it.status == DisputeStatus.RESOLVED_REFUNDED || it.status == DisputeStatus.RESOLVED_DISMISSED
        }
    }

    val openCount     = disputes.count { it.status == DisputeStatus.OPEN }
    val reviewCount   = disputes.count { it.status == DisputeStatus.UNDER_REVIEW }
    val resolvedCount = disputes.count { it.status == DisputeStatus.RESOLVED_REFUNDED || it.status == DisputeStatus.RESOLVED_DISMISSED }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Customer Disputes & Claims",
                subtitle = "$openCount open · $reviewCount in review · $resolvedCount resolved",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FixoraPageBrush)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FixoraSurface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DisputeFilter.values().forEach { f ->
                    val selected = f == filter
                    val count = when (f) {
                        DisputeFilter.ALL          -> disputes.size
                        DisputeFilter.OPEN         -> openCount
                        DisputeFilter.UNDER_REVIEW -> reviewCount
                        DisputeFilter.RESOLVED     -> resolvedCount
                    }
                    FilterChip(
                        selected = selected,
                        onClick = { filter = f },
                        label = { Text("${f.label} ($count)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FixoraEmergency,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = FixoraTextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "No disputes in this category",
                                    color = FixoraTextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "All clear — keep it up!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FixoraTextMuted
                                )
                            }
                        }
                    }
                } else {
                    items(filtered.sortedByDescending { it.createdAt }, key = { it.id }) { dispute ->
                        _DisputeCard(dispute)
                    }
                }
            }
        }
    }
}

@Composable
private fun _DisputeCard(dispute: DisputeTicket) {
    var expanded by remember { mutableStateOf(dispute.status == DisputeStatus.OPEN) }

    val (statusText, statusColor, statusBg) = when (dispute.status) {
        DisputeStatus.OPEN                -> Triple("Open",       FixoraEmergency, FixoraEmergencyLight)
        DisputeStatus.UNDER_REVIEW        -> Triple("In Review",  FixoraWarning,   FixoraWarningLight)
        DisputeStatus.RESOLVED_REFUNDED   -> Triple("Refunded",   FixoraSuccess,   FixoraSuccessLight)
        DisputeStatus.RESOLVED_DISMISSED  -> Triple("Dismissed",  FixoraTextMuted, FixoraSurfaceVariant)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ReportProblem,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Dispute #${dispute.id}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Surface(color = statusBg, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Parties
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Customer", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                    Text(dispute.customerName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                }
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = FixoraTextMuted, modifier = Modifier.size(16.dp).align(Alignment.CenterVertically))
                Column(horizontalAlignment = Alignment.End) {
                    Text("Provider", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                    Text(dispute.providerName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Reason
            Surface(color = FixoraEmergencyLight, shape = RoundedCornerShape(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = FixoraEmergency, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = dispute.reason,
                        color = FixoraEmergency,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Expandable details
            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = dispute.details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FixoraTextSecondary
                )
                Text(
                    text = "Booking Ref: ${dispute.bookingId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = FixoraTextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (dispute.resolutionNotes != null) {
                    Spacer(Modifier.height(8.dp))
                    Surface(color = FixoraSuccessLight, shape = RoundedCornerShape(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FixoraSuccess, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(dispute.resolutionNotes, style = MaterialTheme.typography.bodySmall, color = FixoraSuccess)
                        }
                    }
                }

                if (dispute.status == DisputeStatus.OPEN || dispute.status == DisputeStatus.UNDER_REVIEW) {
                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Mark under review
                        if (dispute.status == DisputeStatus.OPEN) {
                            OutlinedButton(
                                onClick = {
                                    FixoraRepository.resolveDispute(
                                        disputeId = dispute.id,
                                        resolvedAsRefund = false,
                                        resolutionNotes = "Marked under review by admin"
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoraWarning)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Review", fontSize = 13.sp)
                            }
                        }
                        // Dismiss
                        OutlinedButton(
                            onClick = {
                                FixoraRepository.resolveDispute(
                                    disputeId = dispute.id,
                                    resolvedAsRefund = false,
                                    resolutionNotes = "Claim dismissed by admin after provider review"
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoraTextSecondary)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Dismiss", fontSize = 13.sp)
                        }
                        // Refund
                        Button(
                            onClick = {
                                FixoraRepository.resolveDispute(
                                    disputeId = dispute.id,
                                    resolvedAsRefund = true,
                                    resolutionNotes = "Full refund approved by Fixora admin operations"
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FixoraEmergency)
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Refund", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Show/hide toggle
            Spacer(Modifier.height(6.dp))
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.End),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    if (expanded) "Show Less" else "View Details",
                    color = FixoraPrimary,
                    fontSize = 12.sp
                )
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = FixoraPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
