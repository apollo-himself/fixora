package com.fixora.marketplace.data.model

data class ServiceCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val startingPriceAed: Double,
    val popularTag: String? = null,
    val subSkills: List<String> = emptyList(),
    val serviceGroup: String = "Local Services" // e.g. "Local Services", "Digital Services", "Professional Services", "Learning & Training"
)

enum class PricingType {
    FIXED,
    INSPECTION_QUOTATION
}

data class ServiceItem(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val durationMinutes: Int,
    val inclusions: List<String>,
    val exclusions: List<String>,
    val priceAed: Double,
    val pricingType: PricingType = PricingType.FIXED,
    val isEmergencyEligible: Boolean = false,
    val iconName: String = "Wrench"
)
