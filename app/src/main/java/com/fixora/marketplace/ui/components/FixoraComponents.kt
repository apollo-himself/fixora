package com.fixora.marketplace.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.R
import com.fixora.marketplace.data.model.BookingStatus
import com.fixora.marketplace.data.model.UserRole
import com.fixora.marketplace.ui.theme.*

@Composable
fun FixoraBrandMark(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_fixora_mark),
        contentDescription = null,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixoraTopBar(
    title: String,
    subtitle: String? = null,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    currentRole: UserRole? = null,
    onRoleSwitchClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    unreadNotificationsCount: Int = 0
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FixoraSurface,
            titleContentColor = FixoraTextPrimary,
            navigationIconContentColor = FixoraTextPrimary,
            actionIconContentColor = FixoraTextPrimary
        ),
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = FixoraTextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = FixoraTextSecondary
                    )
                }
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = FixoraPrimary
                    )
                }
            } else {
                FixoraBrandMark(Modifier.padding(start = 12.dp, end = 4.dp).size(34.dp))
            }
        },
        actions = {
            if (onNotificationClick != null) {
                IconButton(onClick = onNotificationClick) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationsCount > 0) {
                                Badge(containerColor = FixoraEmergency) {
                                    Text(unreadNotificationsCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = FixoraTextPrimary
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun StatusBadge(status: BookingStatus) {
    val (bgColor, textColor, label) = when (status) {
        BookingStatus.REQUESTED -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), "Requested")
        BookingStatus.MATCHING -> Triple(Color(0xFFE0E7FF), Color(0xFF3730A3), "Matching...")
        BookingStatus.PROVIDER_ACCEPTED -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "Accepted")
        BookingStatus.CONFIRMED -> Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), "Confirmed")
        BookingStatus.EN_ROUTE -> Triple(Color(0xFFFDE68A), Color(0xFF854D0E), "En Route")
        BookingStatus.ARRIVED -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), "Arrived")
        BookingStatus.IN_PROGRESS -> Triple(Color(0xFFDDD6FE), Color(0xFF5B21B6), "In Progress")
        BookingStatus.AWAITING_CUSTOMER_APPROVAL -> Triple(Color(0xFFFED7AA), Color(0xFF9A3412), "Needs Approval")
        BookingStatus.COMPLETED -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), "Completed")
        BookingStatus.PAYMENT_SETTLED -> Triple(Color(0xFFD1FAE5), Color(0xFF047857), "Paid & Settled")
        BookingStatus.PROVIDER_DECLINED -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "Declined")
        BookingStatus.NO_PROVIDER_AVAILABLE -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "Unavailable")
        BookingStatus.CUSTOMER_CANCELLED -> Triple(Color(0xFFF3F4F6), Color(0xFF4B5563), "Cancelled")
        BookingStatus.PROVIDER_CANCELLED -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "Provider Cancelled")
        BookingStatus.DISPUTED -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "Disputed")
        BookingStatus.REFUNDED -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "Refunded")
        BookingStatus.EXPIRED -> Triple(Color(0xFFF3F4F6), Color(0xFF6B7280), "Expired")
    }

    val dark = LocalFixoraColors.current.isDark
    Surface(
        color = if (dark) bgColor.copy(alpha = 0.12f).compositeOver(FixoraSurface) else bgColor,
        border = if (dark) BorderStroke(1.dp, bgColor.copy(alpha = 0.35f)) else null,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = label,
            color = if (dark) bgColor else textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StarRatingBar(rating: Double, reviewCount: Int? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.1f", rating),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = FixoraTextPrimary
        )
        if (reviewCount != null) {
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "($reviewCount)",
                fontSize = 12.sp,
                color = FixoraTextMuted
            )
        }
    }
}

@Composable
fun FixoraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    isDestructive: Boolean = false,
    icon: ImageVector? = null
) {
    val containerColor = when {
        isDestructive -> FixoraEmergency
        isPrimary -> FixoraAction
        else -> FixoraSurfaceVariant
    }
    val contentColor = if (isPrimary || isDestructive) Color.White else FixoraTextPrimary

    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(50.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        border = if (isPrimary) null else BorderStroke(1.dp, FixoraBorder),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = FixoraBorder,
            disabledContentColor = FixoraTextMuted
        )
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
