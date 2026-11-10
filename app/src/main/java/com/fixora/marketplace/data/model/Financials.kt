package com.fixora.marketplace.data.model

enum class PaymentMethod {
    CREDIT_DEBIT_CARD,
    APPLE_GOOGLE_PAY,
    CASH_ON_COMPLETION
}

enum class PaymentRecordStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED
}

enum class LedgerType {
    CUSTOMER_CHARGE,
    PLATFORM_COMMISSION_EARNED,
    PROVIDER_PAYABLE_CREDIT,
    PROVIDER_PAYOUT,
    REFUND_ISSUED,
    ADJUSTMENT
}

data class PaymentRecord(
    val transactionId: String,
    val bookingId: String,
    val customerId: String,
    val providerId: String?,
    val totalAmountAed: Double,
    val baseAmountAed: Double,
    val commissionAed: Double,
    val providerPayoutAed: Double,
    val vatAed: Double,
    val method: PaymentMethod,
    val status: PaymentRecordStatus,
    val idempotencyKey: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class LedgerEntry(
    val id: String,
    val referenceId: String, // Booking ID or Payment ID
    val type: LedgerType,
    val debitAed: Double,
    val creditAed: Double,
    val balanceAed: Double,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
