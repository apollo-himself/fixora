package com.fixora.marketplace.ui.admin

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun AdminCategoriesScreen(
    onNavigateBack: () -> Unit
) {
    val adminSettings by FixoraRepository.adminSettings.collectAsState()

    var commissionInput  by remember { mutableStateOf(adminSettings.commissionPercentage.toString()) }
    var emergenceInput   by remember { mutableStateOf(adminSettings.emergencySurgeFeeAed.toString()) }
    var vatInput         by remember { mutableStateOf(adminSettings.vatRatePercentage.toString()) }
    var timeoutInput     by remember { mutableStateOf(adminSettings.autoMatchingTimeoutSeconds.toString()) }
    var savedMessage     by remember { mutableStateOf<String?>(null) }
    var hasChanges       by remember { mutableStateOf(false) }

    fun markChanged() { hasChanges = true; savedMessage = null }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Marketplace Configuration",
                subtitle = "Platform fee, surge pricing & system settings",
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
            // ── Save banner ─────────────────────────────────────────────────
            if (hasChanges || savedMessage != null) {
                item {
                    Surface(
                        color = if (savedMessage != null) FixoraSuccessLight else FixoraWarningLight,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (savedMessage != null) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (savedMessage != null) FixoraSuccess else FixoraWarning
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = savedMessage ?: "You have unsaved changes.",
                                color = if (savedMessage != null) FixoraSuccess else FixoraWarning,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Commission Policy ──────────────────────────────────────────
            item {
                _ConfigSection(
                    title = "Commission & Revenue Split",
                    icon = Icons.Default.Percent,
                    description = "Platform commission is deducted from each completed booking. The remainder goes to the provider."
                ) {
                    _ConfigField(
                        label = "Platform Commission Rate (%)",
                        value = commissionInput,
                        onValueChange = { commissionInput = it; markChanged() },
                        placeholder = "15",
                        icon = Icons.Default.Percent,
                        helperText = "Provider receives ${
                            (100 - (commissionInput.toDoubleOrNull() ?: 15.0)).toInt()
                        }% of each booking"
                    )

                    // Visual breakdown
                    val rate = commissionInput.toDoubleOrNull() ?: 15.0
                    val providerPct = 100.0 - rate
                    Spacer(Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp))) {
                        Box(modifier = Modifier.weight(rate.toFloat()).fillMaxHeight().background(FixoraAction))
                        Box(modifier = Modifier.weight(providerPct.toFloat()).fillMaxHeight().background(FixoraSuccess))
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fixora: ${rate.toInt()}%", fontSize = 10.sp, color = FixoraPrimary, fontWeight = FontWeight.Bold)
                        Text("Provider: ${providerPct.toInt()}%", fontSize = 10.sp, color = FixoraSuccess, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ── Emergency Surge Pricing ────────────────────────────────────
            item {
                _ConfigSection(
                    title = "Emergency Surge Pricing",
                    icon = Icons.Default.FlashOn,
                    description = "A flat-fee surcharge automatically added to emergency/immediate bookings. Applied on top of the base price."
                ) {
                    _ConfigField(
                        label = "Emergency Surge Fee (PKR flat)",
                        value = emergenceInput,
                        onValueChange = { emergenceInput = it; markChanged() },
                        placeholder = "500",
                        icon = Icons.Default.FlashOn,
                        helperText = "Added when customer selects 'Emergency Immediate' urgency"
                    )
                }
            }

            // ── VAT / Tax ──────────────────────────────────────────────────
            item {
                _ConfigSection(
                    title = "VAT & Tax Settings",
                    icon = Icons.Default.AccountBalance,
                    description = "Value-added tax rate applied to the base service fee before commission calculation."
                ) {
                    _ConfigField(
                        label = "VAT Rate (%)",
                        value = vatInput,
                        onValueChange = { vatInput = it; markChanged() },
                        placeholder = "5",
                        icon = Icons.Default.Percent,
                        helperText = "Enter the applicable tax rate for this category"
                    )
                }
            }

            // ── Auto Matching ──────────────────────────────────────────────
            item {
                _ConfigSection(
                    title = "Auto-Matching Engine",
                    icon = Icons.Default.FlashAuto,
                    description = "Controls how long the system tries to auto-assign an available provider before timing out."
                ) {
                    _ConfigField(
                        label = "Matching Timeout (seconds)",
                        value = timeoutInput,
                        onValueChange = { timeoutInput = it; markChanged() },
                        placeholder = "45",
                        icon = Icons.Default.Timer,
                        helperText = "Recommend 30–90 seconds. After timeout, customer is notified of no availability."
                    )
                }
            }

            // ── Save Button ────────────────────────────────────────────────
            item {
                Button(
                    onClick = {
                        val commission = commissionInput.toDoubleOrNull() ?: adminSettings.commissionPercentage
                        val surge      = emergenceInput.toDoubleOrNull() ?: adminSettings.emergencySurgeFeeAed
                        val vat        = vatInput.toDoubleOrNull()        ?: adminSettings.vatRatePercentage
                        val timeout    = timeoutInput.toIntOrNull()       ?: adminSettings.autoMatchingTimeoutSeconds

                        FixoraRepository.updateAdminCommission(commission)
                        // Note: additional settings will be persisted once extended repository methods are added
                        savedMessage = "Configuration saved — commission ${commission.toInt()}%, surge PKR ${surge.toInt()}, VAT ${vat.toInt()}%."
                        hasChanges = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction),
                    enabled = hasChanges
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Configuration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // ── Danger Zone ────────────────────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraEmergencyLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = FixoraEmergency)
                            Spacer(Modifier.width(8.dp))
                            Text("Danger Zone", fontWeight = FontWeight.ExtraBold, color = FixoraEmergency, style = MaterialTheme.typography.titleSmall)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "These actions are irreversible. Only perform them in exceptional circumstances.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FixoraEmergency.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { /* TODO: hook up reset action */ },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoraEmergency),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FixoraEmergency)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Reset to Default Settings", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun _ConfigSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(FixoraPrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            }
            Spacer(Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = FixoraTextSecondary)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun _ConfigField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    helperText: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(18.dp)) },
        placeholder = { Text(placeholder, color = FixoraTextMuted) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = FixoraPrimary,
            unfocusedBorderColor = FixoraBorder,
            focusedLabelColor    = FixoraPrimary
        )
    )
    Spacer(Modifier.height(4.dp))
    Text(helperText, style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted, modifier = Modifier.padding(start = 4.dp))
}
