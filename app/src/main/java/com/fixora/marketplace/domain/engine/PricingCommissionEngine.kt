package com.fixora.marketplace.domain.engine

import com.fixora.marketplace.data.model.AdminSettings
import com.fixora.marketplace.data.model.Urgency
import java.math.BigDecimal
import java.math.RoundingMode

data class PricingBreakdown(
    val baseServiceAed: Double,
    val partsLaborAed: Double,
    val emergencyFeeAed: Double,
    val subtotalAed: Double,
    val vatAed: Double,
    val totalAed: Double,
    val platformCommissionAed: Double,
    val providerEarningsAed: Double,
    val commissionPercentage: Double
)

object PricingCommissionEngine {

    fun calculateBreakdown(
        basePriceAed: Double,
        partsLaborAed: Double? = null,
        urgency: Urgency = Urgency.STANDARD,
        settings: AdminSettings = AdminSettings()
    ): PricingBreakdown {
        val base = round2(basePriceAed)
        val quotationExtra = round2(partsLaborAed ?: 0.0)
        val emergencyFee = if (urgency == Urgency.EMERGENCY_IMMEDIATE) round2(settings.emergencySurgeFeeAed) else 0.0

        // Subtotal before tax
        val subtotal = round2(base + quotationExtra + emergencyFee)

        // Commissionable amount: Base service + emergency fee (parts/materials are passed through without commission penalty)
        val commissionableAmount = round2(base + emergencyFee)
        val platformCommission = round2(commissionableAmount * (settings.commissionPercentage / 100.0))

        // Provider earnings: (Commissionable - Commission) + Parts/Labor passthrough
        val providerEarnings = round2((commissionableAmount - platformCommission) + quotationExtra)

        // VAT on total customer service bill (UAE 5% standard rate)
        val vat = round2(subtotal * (settings.vatRatePercentage / 100.0))
        val total = round2(subtotal + vat)

        return PricingBreakdown(
            baseServiceAed = base,
            partsLaborAed = quotationExtra,
            emergencyFeeAed = emergencyFee,
            subtotalAed = subtotal,
            vatAed = vat,
            totalAed = total,
            platformCommissionAed = platformCommission,
            providerEarningsAed = providerEarnings,
            commissionPercentage = settings.commissionPercentage
        )
    }

    private fun round2(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}
