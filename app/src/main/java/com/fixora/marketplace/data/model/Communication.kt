package com.fixora.marketplace.data.model

data class ChatMessage(
    val id: String,
    val bookingId: String,
    val participantIds: List<String> = emptyList(),
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isAutoReply: Boolean = false
)

data class NotificationItem(
    val id: String,
    val userId: String,
    val title: String,
    val body: String,
    val bookingId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "BOOKING_UPDATE"
)

data class Review(
    val id: String,
    val bookingId: String,
    val customerId: String,
    val customerName: String,
    val providerId: String,
    val rating: Double,
    val comment: String,
    val tags: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class Quotation(
    val id: String,
    val bookingId: String,
    val providerId: String,
    val providerName: String,
    val laborCostAed: Double,
    val partsCostAed: Double,
    val totalAed: Double,
    val notes: String,
    val isApprovedByCustomer: Boolean = false,
    val isRejectedByCustomer: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class DisputeStatus {
    OPEN,
    UNDER_REVIEW,
    RESOLVED_REFUNDED,
    RESOLVED_DISMISSED
}

data class DisputeTicket(
    val id: String,
    val bookingId: String,
    val customerId: String,
    val customerName: String,
    val providerId: String,
    val providerName: String,
    val reason: String,
    val details: String,
    val status: DisputeStatus = DisputeStatus.OPEN,
    val resolutionNotes: String? = null,
    val refundAmountAed: Double? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)

data class AdminSettings(
    val commissionPercentage: Double = 15.0,
    val emergencySurgeFeeAed: Double = 500.0,
    val vatRatePercentage: Double = 5.0,
    val autoMatchingTimeoutSeconds: Int = 45
)

data class AuditLog(
    val id: String,
    val adminId: String,
    val adminName: String,
    val action: String,
    val targetEntity: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
