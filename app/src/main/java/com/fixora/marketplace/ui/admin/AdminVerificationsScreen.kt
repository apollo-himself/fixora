package com.fixora.marketplace.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.fixora.marketplace.data.model.ProviderProfile
import com.fixora.marketplace.data.model.VerificationStatus
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

private enum class VerifFilter(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected")
}

@Composable
fun AdminVerificationsScreen(
    onNavigateBack: () -> Unit
) {
    val providers by FixoraRepository.providers.collectAsState()
    var filter by remember { mutableStateOf(VerifFilter.ALL) }

    val filtered = when (filter) {
        VerifFilter.ALL      -> providers
        VerifFilter.PENDING  -> providers.filter { it.documentStatus == VerificationStatus.PENDING }
        VerifFilter.APPROVED -> providers.filter { it.documentStatus == VerificationStatus.APPROVED }
        VerifFilter.REJECTED -> providers.filter { it.documentStatus == VerificationStatus.REJECTED }
    }

    val pendingCount  = providers.count { it.documentStatus == VerificationStatus.PENDING }
    val approvedCount = providers.count { it.documentStatus == VerificationStatus.APPROVED }
    val rejectedCount = providers.count { it.documentStatus == VerificationStatus.REJECTED }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Technician Verifications",
                subtitle = "${pendingCount} pending • ${approvedCount} approved • ${rejectedCount} rejected",
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
                VerifFilter.values().forEach { f ->
                    val selected = f == filter
                    val count = when (f) {
                        VerifFilter.ALL      -> providers.size
                        VerifFilter.PENDING  -> pendingCount
                        VerifFilter.APPROVED -> approvedCount
                        VerifFilter.REJECTED -> rejectedCount
                    }
                    FilterChip(
                        selected = selected,
                        onClick = { filter = f },
                        label = { Text("${f.label} ($count)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FixoraAction,
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
                            modifier = Modifier.fillMaxWidth().padding(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = FixoraTextMuted, modifier = Modifier.size(48.dp))
                                Spacer(Modifier.height(12.dp))
                                Text("No providers in this category", color = FixoraTextSecondary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                } else {
                    items(filtered, key = { it.id }) { provider ->
                        _VerificationCard(provider = provider)
                    }
                }
            }
        }
    }
}

@Composable
private fun _VerificationCard(provider: ProviderProfile) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(FixoraPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            provider.name.first().uppercase(),
                            fontWeight = FontWeight.ExtraBold,
                            color = FixoraPrimary,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(provider.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            if (provider.isVerified) {
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(14.dp))
                            }
                        }
                        Text(provider.email, style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    _VerifStatusChip(provider.documentStatus)
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = FixoraTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (expanded) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = FixoraDivider)
                Spacer(Modifier.height(14.dp))

                // Stats row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    _MiniStat("Rating", "${"%.1f".format(provider.rating)} ★")
                    _MiniStat("Jobs", "${provider.completedJobs}")
                    _MiniStat("Reviews", "${provider.reviewCount}")
                    _MiniStat("Rate/hr", "PKR ${provider.hourlyRateAed.toInt()}")
                }

                Spacer(Modifier.height(10.dp))

                // Document info
                _InfoRow(icon = Icons.Default.Badge, label = "CNIC / Emirates ID", value = provider.emiratesIdNumber ?: "Not uploaded")
                _InfoRow(icon = Icons.Default.BusinessCenter, label = "Trade/Business License", value = provider.tradeLicenseNumber ?: "Not submitted")
                _InfoRow(icon = Icons.Default.Phone, label = "Phone", value = provider.phone)
                _InfoRow(icon = Icons.Default.LocationOn, label = "Service Areas", value = provider.serviceAreas.joinToString(", "))
                _InfoRow(icon = Icons.Default.Build, label = "Specialties", value = provider.skills.joinToString(", "))
                _InfoRow(icon = Icons.Default.Schedule, label = "Working Hours", value = "${provider.workingHoursStart} – ${provider.workingHoursEnd}")

                // Actions (only show when pending or rejected)
                if (provider.documentStatus == VerificationStatus.PENDING ||
                    provider.documentStatus == VerificationStatus.REJECTED
                ) {
                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { FixoraRepository.adminSetVerificationStatus(provider.id, VerificationStatus.REJECTED) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoraEmergency)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reject")
                        }
                        Button(
                            onClick = { FixoraRepository.adminSetVerificationStatus(provider.id, VerificationStatus.APPROVED) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FixoraSuccess)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Approve Permit", fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (provider.documentStatus == VerificationStatus.APPROVED) {
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { FixoraRepository.adminSetVerificationStatus(provider.id, VerificationStatus.REJECTED) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoraEmergency)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Revoke Approval")
                    }
                }
            }
        }
    }
}

@Composable
private fun _InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, contentDescription = null, tint = FixoraTextMuted, modifier = Modifier.size(14.dp).padding(top = 2.dp))
        Spacer(Modifier.width(6.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun _MiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        Text(label, style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
    }
}

@Composable
private fun _VerifStatusChip(status: VerificationStatus) {
    val (text, color, bg) = when (status) {
        VerificationStatus.APPROVED      -> Triple("Approved",   FixoraSuccess,   FixoraSuccessLight)
        VerificationStatus.PENDING       -> Triple("Pending",    FixoraWarning,   FixoraWarningLight)
        VerificationStatus.REJECTED      -> Triple("Rejected",   FixoraEmergency, FixoraEmergencyLight)
        VerificationStatus.NOT_SUBMITTED -> Triple("No Docs",    FixoraTextMuted, FixoraSurfaceVariant)
    }
    Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
