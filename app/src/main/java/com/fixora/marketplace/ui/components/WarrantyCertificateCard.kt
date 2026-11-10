package com.fixora.marketplace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WarrantyCertificateCard(
    bookingId: String,
    serviceTitle: String,
    providerName: String,
    warrantyDays: Int = 7,
    completedTimestamp: Long = System.currentTimeMillis(),
    modifier: Modifier = Modifier
) {
    val expiryDate = remember(completedTimestamp, warrantyDays) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = completedTimestamp
            add(Calendar.DAY_OF_YEAR, warrantyDays)
        }
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FixoraPrimary.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Gold/Emerald header ribbon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "$warrantyDays-Day Fixora Guarantee",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = FixoraTextPrimary
                        )
                        Text(
                            text = "Official Service Warranty Certificate",
                            style = MaterialTheme.typography.labelSmall,
                            color = FixoraTextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FixoraSuccessLight
                ) {
                    Text(
                        text = "ACTIVE",
                        color = FixoraSuccess,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = FixoraDivider)
            Spacer(modifier = Modifier.height(14.dp))

            // Certificate Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Protected Service", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                    Text(text = serviceTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FixoraTextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Certified Pro", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                    Text(text = providerName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = FixoraTextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Valid Until", style = MaterialTheme.typography.labelSmall, color = FixoraTextMuted)
                    Text(text = expiryDate, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = FixoraPrimary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Warranty Terms callout box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = FixoraPrimaryLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = FixoraPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "If any issue resurfaces within $warrantyDays days, Fixora will send a certified pro to inspect & rectify it free of charge.",
                        style = MaterialTheme.typography.labelSmall,
                        color = FixoraTextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
