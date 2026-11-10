package com.fixora.marketplace.ui.customer

import androidx.compose.foundation.background
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
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.components.FixoraTopBar
import com.fixora.marketplace.ui.components.StarRatingBar
import com.fixora.marketplace.ui.theme.*

@Composable
fun ProviderProfileScreen(
    providerId: String,
    onNavigateBack: () -> Unit
) {
    val providers by FixoraRepository.providers.collectAsState()
    val reviews by FixoraRepository.reviews.collectAsState()

    val provider = providers.firstOrNull { it.id == providerId } ?: providers.first()
    val providerReviews = reviews.filter { it.providerId == provider.id }

    Scaffold(
        topBar = {
            FixoraTopBar(
                title = "Technician Profile",
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
            // Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(FixoraDarkNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = provider.name.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = provider.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Freelancer",
                                tint = FixoraPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Independent professional",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FixoraTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        StarRatingBar(rating = provider.rating, reviewCount = provider.reviewCount)

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${provider.completedJobs}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(text = "Jobs Done", style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "PKR ${provider.hourlyRateAed.toInt()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FixoraPrimary)
                                Text(text = "Indicative Rate", style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = if (provider.isOnline) "Active" else "Away", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (provider.isOnline) FixoraSuccess else FixoraTextMuted)
                                Text(text = "Availability", style = MaterialTheme.typography.labelSmall, color = FixoraTextSecondary)
                            }
                        }
                    }
                }
            }

            // Bio
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "About Technician", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = provider.bio, style = MaterialTheme.typography.bodyLarge, color = FixoraTextSecondary)
                    }
                }
            }

            // Skills & Service Areas
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Specialties", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            provider.skills.forEach { skill ->
                                Surface(
                                    color = FixoraPrimaryLight,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = skill.replace('_', ' '),
                                        color = FixoraPrimary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Authorized Service Areas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = provider.serviceAreas.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = FixoraTextSecondary
                        )
                    }
                }
            }

            // Customer Reviews
            item {
                Text(
                    text = "Customer Reviews & Ratings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (providerReviews.isEmpty()) {
                item {
                    Text(
                        text = "No written reviews yet for this newly verified technician.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FixoraTextMuted
                    )
                }
            } else {
                items(providerReviews) { rev ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoraSurface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = rev.customerName, fontWeight = FontWeight.Bold)
                                StarRatingBar(rating = rev.rating)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = rev.comment, style = MaterialTheme.typography.bodyMedium, color = FixoraTextSecondary)
                        }
                    }
                }
            }
        }
    }
}


