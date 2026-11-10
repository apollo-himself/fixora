package com.fixora.marketplace.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.BookingStatus
import com.fixora.marketplace.ui.theme.*

@Composable
fun SimulatedLiveTrackingMap(
    providerName: String,
    destinationArea: String,
    status: BookingStatus,
    modifier: Modifier = Modifier
) {
    // Pulse animation for technician location
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseRadius"
    )

    // Position progress along route based on status
    val progress = when (status) {
        BookingStatus.REQUESTED, BookingStatus.MATCHING -> 0.05f
        BookingStatus.PROVIDER_ACCEPTED, BookingStatus.CONFIRMED -> 0.15f
        BookingStatus.EN_ROUTE -> 0.55f
        BookingStatus.ARRIVED -> 0.90f
        BookingStatus.IN_PROGRESS, BookingStatus.AWAITING_CUSTOMER_APPROVAL -> 0.98f
        BookingStatus.COMPLETED, BookingStatus.PAYMENT_SETTLED -> 1.0f
        else -> 0.15f
    }

    val etaMinutes = when (status) {
        BookingStatus.EN_ROUTE -> 12
        BookingStatus.ARRIVED -> 0
        BookingStatus.PROVIDER_ACCEPTED, BookingStatus.CONFIRMED -> 28
        else -> null
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Live GPS badge & ETA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (status == BookingStatus.EN_ROUTE || status == BookingStatus.ARRIVED) FixoraSuccess else FixoraPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (status == BookingStatus.EN_ROUTE) "LIVE GPS EN-ROUTE" else if (status == BookingStatus.ARRIVED) "ARRIVED ON SITE" else "GPS ROUTE READY",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = if (status == BookingStatus.EN_ROUTE || status == BookingStatus.ARRIVED) FixoraSuccess else FixoraPrimary,
                        letterSpacing = 0.5.sp
                    )
                }

                if (etaMinutes != null) {
                    Surface(
                        color = if (etaMinutes == 0) FixoraSuccessLight else FixoraPrimaryLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (etaMinutes == 0) "Arrived at location" else "ETA: ~$etaMinutes mins",
                            color = if (etaMinutes == 0) FixoraSuccess else FixoraPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Map Vector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(FixoraSurfaceVariant)
            ) {
                val pathColor = FixoraBorder
                val destinationColor = FixoraPrimary

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid lines to simulate city map streets
                    val gridColor = pathColor.copy(alpha = 0.45f)
                    val stepY = height / 4
                    for (i in 1..3) {
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, stepY * i),
                            end = Offset(width, stepY * i),
                            strokeWidth = 1.5f
                        )
                    }
                    val stepX = width / 5
                    for (i in 1..4) {
                        drawLine(
                            color = gridColor,
                            start = Offset(stepX * i, 0f),
                            end = Offset(stepX * i, height),
                            strokeWidth = 1.5f
                        )
                    }

                    // Curving route path from Technician (start) to Customer Destination (end)
                    val startPt = Offset(width * 0.15f, height * 0.75f)
                    val controlPt1 = Offset(width * 0.40f, height * 0.85f)
                    val midPt = Offset(width * 0.50f, height * 0.45f)
                    val controlPt2 = Offset(width * 0.65f, height * 0.15f)
                    val endPt = Offset(width * 0.85f, height * 0.30f)

                    // Draw Planned Route (dashed gray)
                    val fullPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(startPt.x, startPt.y)
                        cubicTo(controlPt1.x, controlPt1.y, midPt.x, midPt.y, midPt.x, midPt.y)
                        cubicTo(controlPt2.x, controlPt2.y, endPt.x, endPt.y, endPt.x, endPt.y)
                    }
                    drawPath(
                        path = fullPath,
                        color = pathColor,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 6f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                        )
                    )

                    // Start Point (Technician Base / Origin)
                    drawCircle(
                        color = Color(0xFF64748B),
                        radius = 8f,
                        center = startPt
                    )

                    // End Point (Customer Villa/Apartment)
                    drawCircle(
                        color = destinationColor,
                        radius = 12f,
                        center = endPt
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 5f,
                        center = endPt
                    )
                }

                // Technician Moving Marker Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Technician Location Marker positioned dynamically
                    val techBiasX = (progress * 2 - 1).coerceIn(-0.75f, 0.75f)
                    val techBiasY = (0.5f - progress * 0.7f).coerceIn(-0.6f, 0.6f)

                    Box(
                        modifier = Modifier
                            .align(BiasAlignment(techBiasX, techBiasY)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pulsing halo
                        Box(
                            modifier = Modifier
                                .size((34 * pulseFraction + 16).dp)
                                .clip(CircleShape)
                                .background(FixoraPrimary.copy(alpha = 0.20f * (1.2f - pulseFraction)))
                        )
                        // Marker circle with icon
                        Surface(
                            shape = CircleShape,
                            color = FixoraPrimary,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (status == BookingStatus.EN_ROUTE) Icons.Default.DirectionsCar else Icons.Default.Build,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Destination Pin Card Overlay
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FixoraSurface,
                        shadowElevation = 3.dp,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = FixoraPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = destinationArea.take(16),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoraTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer note
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = FixoraTextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$providerName • Tracking active",
                        style = MaterialTheme.typography.labelSmall,
                        color = FixoraTextSecondary
                    )
                }

                Text(
                    text = if (status == BookingStatus.EN_ROUTE) "Simulated live transit" else "Real-time updates",
                    style = MaterialTheme.typography.labelSmall,
                    color = FixoraTextMuted
                )
            }
        }
    }
}
