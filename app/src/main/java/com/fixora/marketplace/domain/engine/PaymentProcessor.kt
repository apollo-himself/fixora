package com.fixora.marketplace.domain.engine

import com.fixora.marketplace.data.model.Booking
import com.fixora.marketplace.data.model.LedgerEntry
import com.fixora.marketplace.data.model.LedgerType
import com.fixora.marketplace.data.model.PaymentMethod
import com.fixora.marketplace.data.model.PaymentRecord
import com.fixora.marketplace.data.model.PaymentRecordStatus
import com.fixora.marketplace.data.model.PaymentStatus
import java.util.UUID

data class PaymentProcessingResult(
    val updatedBooking: Booking,
    val paymentRecord: PaymentRecord,
    val ledgerEntries: List<LedgerEntry>
)

object PaymentProcessor {

    /**
     * Processes customer payment safely and generates ledger records.
     * Guaranteed idempotency by key.
     */
    fun processPayment(
        booking: Booking,
        method: PaymentMethod,
        currentLedgerBalance: Double,
        idempotencyKey: String = UUID.randomUUID().toString()
    ): PaymentProcessingResult {
        val transactionId = "TXN-${UUID.randomUUID().toString().take(8).uppercase()}"
        val now = System.currentTimeMillis()

        val recordStatus = if (method == PaymentMethod.CASH_ON_COMPLETION) {
            PaymentRecordStatus.PENDING
        } else {
            PaymentRecordStatus.SUCCESS
        }

        val updatedPaymentStatus = if (method == PaymentMethod.CASH_ON_COMPLETION) {
            PaymentStatus.CASH_ON_COMPLETION
        } else {
            PaymentStatus.PAID_ONLINE
        }

        val paymentRecord = PaymentRecord(
            transactionId = transactionId,
            bookingId = booking.id,
            customerId = booking.customerId,
            providerId = booking.providerId,
            totalAmountAed = booking.agreedFinalPriceAed,
            baseAmountAed = booking.basePriceAed,
            commissionAed = booking.platformCommissionAed,
            providerPayoutAed = booking.providerEarningsAed,
            vatAed = booking.vatAed,
            method = method,
            status = recordStatus,
            idempotencyKey = idempotencyKey,
            timestamp = now
        )

        // Ledger entries
        val ledgerList = mutableListOf<LedgerEntry>()
        var runningBalance = currentLedgerBalance

        // 1. Customer gross payment
        runningBalance += booking.agreedFinalPriceAed
        ledgerList.add(
            LedgerEntry(
                id = "LED-${UUID.randomUUID().toString().take(8)}",
                referenceId = booking.id,
                type = LedgerType.CUSTOMER_CHARGE,
                debitAed = 0.0,
                creditAed = booking.agreedFinalPriceAed,
                balanceAed = runningBalance,
                description = "Customer payment for ${booking.serviceTitle} (${method.name})",
                timestamp = now
            )
        )

        // 2. Fixora Platform Commission
        ledgerList.add(
            LedgerEntry(
                id = "LED-${UUID.randomUUID().toString().take(8)}",
                referenceId = booking.id,
                type = LedgerType.PLATFORM_COMMISSION_EARNED,
                debitAed = 0.0,
                creditAed = booking.platformCommissionAed,
                balanceAed = runningBalance,
                description = "Platform commission 15% on booking ${booking.id}",
                timestamp = now
            )
        )

        // 3. Provider Payable Balance allocation
        runningBalance -= booking.providerEarningsAed
        ledgerList.add(
            LedgerEntry(
                id = "LED-${UUID.randomUUID().toString().take(8)}",
                referenceId = booking.id,
                type = LedgerType.PROVIDER_PAYABLE_CREDIT,
                debitAed = booking.providerEarningsAed,
                creditAed = 0.0,
                balanceAed = runningBalance,
                description = "Payable allocated to provider ${booking.providerName ?: "N/A"}",
                timestamp = now
            )
        )

        val updatedBooking = booking.copy(
            paymentStatus = updatedPaymentStatus,
            updatedAt = now
        )

        return PaymentProcessingResult(
            updatedBooking = updatedBooking,
            paymentRecord = paymentRecord,
            ledgerEntries = ledgerList
        )
    }

    /**
     * Issues refund for a disputed booking and adjusts ledger.
     */
    fun processRefund(
        booking: Booking,
        refundAmountAed: Double,
        currentBalance: Double,
        reason: String
    ): Pair<Booking, LedgerEntry> {
        val now = System.currentTimeMillis()
        val newBalance = currentBalance - refundAmountAed

        val ledgerEntry = LedgerEntry(
            id = "LED-REF-${UUID.randomUUID().toString().take(6)}",
            referenceId = booking.id,
            type = LedgerType.REFUND_ISSUED,
            debitAed = refundAmountAed,
            creditAed = 0.0,
            balanceAed = newBalance,
            description = "Refund issued for booking ${booking.id}: $reason",
            timestamp = now
        )

        val updated = booking.copy(
            paymentStatus = PaymentStatus.REFUNDED,
            updatedAt = now
        )

        return Pair(updated, ledgerEntry)
    }
}
