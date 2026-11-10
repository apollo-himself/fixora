package com.fixora.marketplace.ui.customer

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fixora.marketplace.data.model.BookingStatus
import com.fixora.marketplace.data.model.ProviderProfile
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.domain.engine.BookingMatchingEngine
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.components.StarRatingBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun ProviderMatchingScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    onProviderSelected: (String) -> Unit,
    onViewProfile: (String) -> Unit
) {
    val bookings by FixoraRepository.bookings.collectAsState()
    val allProviders by FixoraRepository.providers.collectAsState()

    val booking = bookings.firstOrNull { it.id == bookingId }

    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    if (booking == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Booking not found")
        }
        return
    }

    // Match providers using the engine
    val matchResults = remember(booking, allProviders) {
        BookingMatchingEngine.findEligibleProviders(
            serviceCategory = booking.serviceCategory,
            customerAddress = booking.address,
            allProviders = allProviders
        )
    }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Technician Matching",
                subtitle = "Booking #${booking.id}",
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
            // Radar Match Header
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraDarkNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(FixoraPrimary.copy(alpha = 0.35f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(FixoraAction),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Matching Verified Technicians",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Found ${matchResults.size} available specialists in ${booking.address.area}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Recommended Freelance Technicians",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (matchResults.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = FixoraWarning, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Technicians Currently Online in this Area", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("You can reschedule or choose our emergency 24/7 hotline.", color = FixoraTextSecondary)
                        }
                    }
                }
            } else {
                items(matchResults) { match ->
                    val provider = match.provider
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(FixoraAction),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = provider.name.take(1),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = provider.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = FixoraPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "~${match.distanceKmEstimated} km away • ${provider.completedJobs} jobs done",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FixoraTextSecondary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    StarRatingBar(rating = provider.rating, reviewCount = provider.reviewCount)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = FixoraSuccessLight,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Online Now",
                                            color = FixoraSuccess,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = provider.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = FixoraTextSecondary,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onViewProfile(provider.id) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("View Profile")
                                }
                                Button(
                                    onClick = {
                                        // Customer assigns/accepts this provider
                                        FixoraRepository.providerAcceptBooking(booking.id, provider.id)
                                        onProviderSelected(booking.id)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FixoraAction)
                                ) {
                                    Text("Select Pro", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
