package com.fixora.marketplace.data.model

enum class VerificationStatus {
    NOT_SUBMITTED,
    PENDING,
    APPROVED,
    REJECTED
}

data class ProviderProfile(
    val id: String,
    val userId: String,
    val name: String,
    val phone: String,
    val email: String,
    val bio: String,
    val skills: List<String>, // e.g. ["AC_SERVICE", "ELECTRICAL"]
    val serviceAreas: List<String>, // e.g. ["DHA Phase 5, Lahore", "Downtown", "Al Barsha", "JBR"]
    val rating: Double = 4.8,
    val reviewCount: Int = 0,
    val completedJobs: Int = 0,
    val isOnline: Boolean = true,
    val isVerified: Boolean = true,
    val hourlyRateAed: Double = 120.0,
    val documentStatus: VerificationStatus = VerificationStatus.APPROVED,
    val tradeLicenseNumber: String? = null,
    val emiratesIdNumber: String? = null,
    val workingHoursStart: String = "08:00",
    val workingHoursEnd: String = "20:00",
    val avatarUrl: String? = null
)

