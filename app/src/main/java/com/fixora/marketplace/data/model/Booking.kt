package com.fixora.marketplace.data.model

enum class BookingStatus {
    REQUESTED,
    MATCHING,
    PROVIDER_ACCEPTED,
    CONFIRMED,
    EN_ROUTE,
    ARRIVED,
    IN_PROGRESS,
    AWAITING_CUSTOMER_APPROVAL,
    COMPLETED,
    PAYMENT_SETTLED,
    PROVIDER_DECLINED,
    NO_PROVIDER_AVAILABLE,
    CUSTOMER_CANCELLED,
    PROVIDER_CANCELLED,
    DISPUTED,
    REFUNDED,
    EXPIRED
}

enum class PropertyType {
    APARTMENT,
    VILLA,
    OFFICE,
    TOWNHOUSE
}

enum class Urgency {
    STANDARD,
    SAME_DAY,
    EMERGENCY_IMMEDIATE
}

enum class PaymentStatus {
    UNPAID,
    AUTHORIZED,
    PAID_ONLINE,
    CASH_ON_COMPLETION,
    REFUNDED
}

data class BookingStatusEvent(
    val status: BookingStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null,
    val updatedBy: String? = null
)

data class Booking(
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val providerId: String? = null,
    val providerName: String? = null,
    val providerPhone: String? = null,
    val providerRating: Double? = null,
    val serviceId: String,
    val serviceTitle: String,
    val serviceCategory: String,
    val pricingType: PricingType = PricingType.FIXED,
    val basePriceAed: Double,
    val partsLaborQuotationAed: Double? = null,
    val emergencySurgeFeeAed: Double = 0.0,
    val vatAed: Double = 0.0,
    val agreedFinalPriceAed: Double,
    val platformCommissionAed: Double,
    val providerEarningsAed: Double,
    val status: BookingStatus = BookingStatus.REQUESTED,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val urgency: Urgency = Urgency.STANDARD,
    val propertyType: PropertyType = PropertyType.APARTMENT,
    val scheduledDate: String,
    val scheduledTimeSlot: String,
    val address: Address,
    val issueDescription: String,
    val issuePhotoUrls: List<String> = emptyList(),
    val beforePhotoUrl: String? = null,
    val afterPhotoUrl: String? = null,
    val isCompletedByProvider: Boolean = false,
    val isCompletedByCustomer: Boolean = false,
    val cancellationReason: String? = null,
    val statusHistory: List<BookingStatusEvent> = listOf(BookingStatusEvent(BookingStatus.REQUESTED)),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
