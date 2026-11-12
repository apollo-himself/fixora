package com.fixora.marketplace.data.model

data class GigPackage(
    val id: String,
    val name: String, // e.g. "Basic Inspection", "Standard Repair", "Full Overhaul"
    val description: String,
    val durationMinutes: Int,
    val inclusions: List<String>,
    val priceAed: Double
)

data class FreelanceGig(
    val id: String,
    val sellerId: String,
    val sellerName: String,
    val sellerRating: Double,
    val sellerReviewCount: Int,
    val sellerCompletedOrders: Int,
    val sellerBadge: String, // "Top Rated Seller", "Verified Freelancer", "Rising Star"
    val title: String, // e.g. "I will deep clean and chemically sanitize your AC unit"
    val categoryId: String,
    val description: String,
    val packages: List<GigPackage>,
    val coverIcon: String,
    val emirates: List<String> = listOf("Lahore", "Karachi"),
    val createdAt: Long = System.currentTimeMillis()
) {
    val startingPriceAed: Double
        get() = packages.minOfOrNull { it.priceAed } ?: 99.0
}

data class CustomJobRequest(
    val id: String,
    val customerId: String,
    val customerName: String,
    val title: String,
    val categoryId: String,
    val description: String,
    val budgetAed: Double,
    val emirate: String,
    val area: String,
    val urgency: Urgency = Urgency.STANDARD,
    val proposalsCount: Int = 0,
    val status: JobRequestStatus = JobRequestStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis()
)

enum class JobRequestStatus {
    OPEN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class JobProposal(
    val id: String,
    val jobRequestId: String,
    val sellerId: String,
    val sellerName: String,
    val sellerRating: Double,
    val sellerCompletedJobs: Int,
    val bidAmountAed: Double,
    val deliveryTimeText: String,
    val coverLetter: String,
    val createdAt: Long = System.currentTimeMillis()
)

