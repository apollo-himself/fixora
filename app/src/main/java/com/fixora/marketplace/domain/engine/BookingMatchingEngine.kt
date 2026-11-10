package com.fixora.marketplace.domain.engine

import com.fixora.marketplace.data.model.Address
import com.fixora.marketplace.data.model.Booking
import com.fixora.marketplace.data.model.BookingStatus
import com.fixora.marketplace.data.model.BookingStatusEvent
import com.fixora.marketplace.data.model.ProviderProfile
import com.fixora.marketplace.data.model.VerificationStatus

data class ProviderMatchScore(
    val provider: ProviderProfile,
    val score: Double,
    val distanceKmEstimated: Double,
    val isExactAreaMatch: Boolean
)

object BookingMatchingEngine {

    /**
     * Filters and ranks providers for a booking request.
     */
    fun findEligibleProviders(
        serviceCategory: String,
        customerAddress: Address,
        allProviders: List<ProviderProfile>
    ): List<ProviderMatchScore> {
        val eligible = allProviders.filter { provider ->
            provider.isOnline &&
            provider.isVerified &&
            provider.documentStatus == VerificationStatus.APPROVED &&
            provider.skills.any { it.equals(serviceCategory, ignoreCase = true) }
        }

        return eligible.map { provider ->
            val exactAreaMatch = provider.serviceAreas.any {
                it.equals(customerAddress.area, ignoreCase = true) ||
                it.equals(customerAddress.emirate, ignoreCase = true)
            }

            // Approximate distance simulation based on area match
            val estimatedDistance = if (exactAreaMatch) 3.5 else 9.2

            // Scoring algorithm (weights: Rating 40%, Experience 30%, Area proximity 30%)
            val ratingScore = (provider.rating / 5.0) * 0.4
            val experienceScore = (provider.completedJobs.coerceAtMost(100) / 100.0) * 0.3
            val proximityScore = (if (exactAreaMatch) 1.0 else 0.4) * 0.3
            val totalScore = ratingScore + experienceScore + proximityScore

            ProviderMatchScore(
                provider = provider,
                score = totalScore,
                distanceKmEstimated = estimatedDistance,
                isExactAreaMatch = exactAreaMatch
            )
        }.sortedByDescending { it.score }
    }

    /**
     * Atomic acceptance helper to prevent double bookings.
     * Returns updated booking if successfully claimed, or null if already taken.
     */
    @Synchronized
    fun attemptAcceptBooking(
        currentBooking: Booking,
        provider: ProviderProfile
    ): Booking? {
        // Can only be accepted if in MATCHING or REQUESTED status and not yet claimed
        if (currentBooking.status != BookingStatus.REQUESTED && currentBooking.status != BookingStatus.MATCHING) {
            return null
        }
        if (currentBooking.providerId != null && currentBooking.providerId != provider.id) {
            return null
        }

        val updatedHistory = currentBooking.statusHistory + BookingStatusEvent(
            status = BookingStatus.PROVIDER_ACCEPTED,
            note = "Accepted by provider ${provider.name}",
            updatedBy = provider.id
        )

        return currentBooking.copy(
            providerId = provider.id,
            providerName = provider.name,
            providerPhone = provider.phone,
            providerRating = provider.rating,
            status = BookingStatus.PROVIDER_ACCEPTED,
            statusHistory = updatedHistory,
            updatedAt = System.currentTimeMillis()
        )
    }
}
