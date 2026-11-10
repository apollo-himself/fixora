package com.fixora.marketplace

import com.fixora.marketplace.data.demo.SeedData
import com.fixora.marketplace.data.model.*
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.domain.engine.BookingMatchingEngine
import com.fixora.marketplace.domain.engine.PaymentProcessor
import com.fixora.marketplace.domain.engine.PricingCommissionEngine
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FixoraMarketplaceTests {

    private val sampleAddress = Address(
        id = "ADDR-TEST",
        label = "Test Marina Villa",
        street = "Marina Walk",
        buildingOrVilla = "Villa 12",
        apartmentNo = "V-12",
        emirate = "Lahore",
        area = "DHA Phase 5, Lahore",
        latitude = 25.08,
        longitude = 55.14
    )

    private val sampleService = ServiceItem(
        id = "SRV-TEST",
        categoryId = "AC_SERVICE",
        name = "AC Complete Wash",
        description = "Test AC service",
        durationMinutes = 60,
        inclusions = listOf("Clean coil"),
        exclusions = listOf("Motor change"),
        priceAed = 200.0,
        pricingType = PricingType.FIXED
    )

    @Before
    fun setup() {
        FixoraRepository.switchRole(UserRole.CUSTOMER)
    }

    // 1. Commission and Pricing calculations
    @Test
    fun testPricingAndCommissionEngine_Calculates15PercentCommission() {
        val breakdown = PricingCommissionEngine.calculateBreakdown(
            basePriceAed = 200.0,
            partsLaborAed = null,
            urgency = Urgency.STANDARD,
            settings = AdminSettings(commissionPercentage = 15.0, vatRatePercentage = 5.0)
        )

        // Base = 200
        assertEquals(200.0, breakdown.baseServiceAed, 0.01)
        // 15% commission on 200 = 30 AED
        assertEquals(30.0, breakdown.platformCommissionAed, 0.01)
        // 85% provider earnings on 200 = 170 AED
        assertEquals(170.0, breakdown.providerEarningsAed, 0.01)
        // 5% VAT on 200 = 10 AED
        assertEquals(10.0, breakdown.vatAed, 0.01)
        // Customer total = 200 + 10 = 210 AED
        assertEquals(210.0, breakdown.totalAed, 0.01)
    }

    @Test
    fun testPricingWithEmergencySurge_SurgeFeeIncludedInCommission() {
        val breakdown = PricingCommissionEngine.calculateBreakdown(
            basePriceAed = 200.0,
            partsLaborAed = null,
            urgency = Urgency.EMERGENCY_IMMEDIATE,
            settings = AdminSettings(commissionPercentage = 15.0, emergencySurgeFeeAed = 50.0, vatRatePercentage = 5.0)
        )

        // Subtotal = 200 + 50 = 250
        assertEquals(250.0, breakdown.subtotalAed, 0.01)
        // Commission = 15% of 250 = 37.5 AED
        assertEquals(37.5, breakdown.platformCommissionAed, 0.01)
        // Provider earnings = 250 - 37.5 = 212.5 AED
        assertEquals(212.5, breakdown.providerEarningsAed, 0.01)
        // VAT = 5% of 250 = 12.5 AED
        assertEquals(12.5, breakdown.vatAed, 0.01)
        // Total = 262.5 AED
        assertEquals(262.5, breakdown.totalAed, 0.01)
    }

    // 2. Provider Matching Engine
    @Test
    fun testProviderMatching_FindsVerifiedProvidersInDHALahore() {
        val matches = BookingMatchingEngine.findEligibleProviders(
            serviceCategory = "AC_SERVICE",
            customerAddress = sampleAddress,
            allProviders = SeedData.demoProviders
        )

        assertTrue("Should find at least 1 provider", matches.isNotEmpty())
        val topMatch = matches.first()
        assertTrue("Top match should have AC_SERVICE skill", topMatch.provider.skills.contains("AC_SERVICE"))
        assertTrue("Top match must be verified", topMatch.provider.isVerified)
        assertTrue("Top match must be online", topMatch.provider.isOnline)
        assertTrue("Top match rating should be high", topMatch.provider.rating >= 4.5)
    }

    // 3. Concurrent Booking Acceptance (Double-booking prevention)
    @Test
    fun testConcurrentBookingAcceptance_PreventsDoubleBooking() {
        val initialBooking = Booking(
            id = "FIX-CONCURRENT-01",
            customerId = "USR-CUST-01",
            customerName = "Sarah",
            customerPhone = "+92501234567",
            serviceId = "SRV-AC-01",
            serviceTitle = "AC Clean",
            serviceCategory = "AC_SERVICE",
            basePriceAed = 200.0,
            agreedFinalPriceAed = 210.0,
            platformCommissionAed = 30.0,
            providerEarningsAed = 170.0,
            status = BookingStatus.MATCHING,
            scheduledDate = "Today",
            scheduledTimeSlot = "10 AM",
            address = sampleAddress,
            issueDescription = "Test"
        )

        val provider1 = SeedData.demoProviders[0] // Rashid
        val provider2 = provider1.copy(id = "PROV-TEST-02", name = "Second Tech")

        // First provider attempts acceptance
        val acceptedBooking = BookingMatchingEngine.attemptAcceptBooking(initialBooking, provider1)
        assertNotNull("First provider should successfully claim", acceptedBooking)
        assertEquals(BookingStatus.PROVIDER_ACCEPTED, acceptedBooking!!.status)
        assertEquals(provider1.id, acceptedBooking.providerId)

        // Second provider attempts to claim already accepted booking
        val rejectedSecondClaim = BookingMatchingEngine.attemptAcceptBooking(acceptedBooking, provider2)
        assertNull("Second provider must be blocked from double-booking", rejectedSecondClaim)
    }

    // 4. Booking Lifecycle Transitions
    @Test
    fun testBookingLifecycleTransitions_RequestedToPaymentSettled() {
        val created = FixoraRepository.createBooking(
            serviceItem = sampleService,
            customerAddress = sampleAddress,
            propertyType = PropertyType.APARTMENT,
            urgency = Urgency.STANDARD,
            scheduledDate = "Tomorrow",
            scheduledTimeSlot = "2:00 PM - 4:00 PM",
            issueDescription = "Unit not blowing cold air"
        )
        assertEquals(BookingStatus.MATCHING, created.status)

        // 1. Provider Accepts
        val acceptSuccess = FixoraRepository.providerAcceptBooking(created.id, "PROV-01")
        assertTrue(acceptSuccess)

        // 2. En Route
        val enRoute = FixoraRepository.updateBookingStatus(created.id, BookingStatus.EN_ROUTE, "Technician driving")
        assertEquals(BookingStatus.EN_ROUTE, enRoute?.status)

        // 3. Arrived
        val arrived = FixoraRepository.updateBookingStatus(created.id, BookingStatus.ARRIVED, "At location")
        assertEquals(BookingStatus.ARRIVED, arrived?.status)

        // 4. In Progress
        val inProgress = FixoraRepository.updateBookingStatus(created.id, BookingStatus.IN_PROGRESS, "Started repair")
        assertEquals(BookingStatus.IN_PROGRESS, inProgress?.status)

        // 5. Completed
        val completed = FixoraRepository.updateBookingStatus(created.id, BookingStatus.COMPLETED, "Work done", "before.jpg", "after.jpg")
        assertEquals(BookingStatus.COMPLETED, completed?.status)

        // 6. Payment Settled
        val paymentSuccess = FixoraRepository.settleBookingPayment(created.id, PaymentMethod.CREDIT_DEBIT_CARD)
        assertTrue(paymentSuccess)

        val finalBooking = FixoraRepository.bookings.value.first { it.id == created.id }
        assertEquals(BookingStatus.PAYMENT_SETTLED, finalBooking.status)
        assertEquals(PaymentStatus.PAID_ONLINE, finalBooking.paymentStatus)
    }

    // 5. Quotation Approval Workflow
    @Test
    fun testQuotationWorkflow_ApprovalUpdatesPriceAndNotifies() {
        val booking = FixoraRepository.createBooking(
            serviceItem = sampleService,
            customerAddress = sampleAddress,
            propertyType = PropertyType.VILLA,
            urgency = Urgency.STANDARD,
            scheduledDate = "Today",
            scheduledTimeSlot = "4 PM",
            issueDescription = "Compressor clicking"
        )

        // Provider submits quotation for AED 150 (labor 50 + parts 100)
        val quote = FixoraRepository.submitQuotation(
            bookingId = booking.id,
            providerId = "PROV-01",
            providerName = "Rashid Khan",
            laborCostAed = 50.0,
            partsCostAed = 100.0,
            notes = "Need new copper valve"
        )
        assertEquals(150.0, quote.totalAed, 0.01)
        assertFalse(quote.isApprovedByCustomer)

        // Customer approves quotation
        val approved = FixoraRepository.approveQuotation(quote.id)
        assertTrue(approved)

        val updatedQuote = FixoraRepository.quotations.value.first { it.id == quote.id }
        assertTrue(updatedQuote.isApprovedByCustomer)

        val updatedBooking = FixoraRepository.bookings.value.first { it.id == booking.id }
        assertEquals(150.0, updatedBooking.partsLaborQuotationAed ?: 0.0, 0.01)
    }

    // 6. Payment Processing and Financial Ledger
    @Test
    fun testPaymentProcessing_GeneratesDoubleEntryLedger() {
        val booking = Booking(
            id = "FIX-PAY-01",
            customerId = "USR-CUST-01",
            customerName = "Sarah",
            customerPhone = "+92501234567",
            providerId = "PROV-01",
            providerName = "Rashid",
            serviceId = "SRV-AC-01",
            serviceTitle = "AC Maintenance",
            serviceCategory = "AC_SERVICE",
            basePriceAed = 200.0,
            vatAed = 10.0,
            agreedFinalPriceAed = 210.0,
            platformCommissionAed = 30.0,
            providerEarningsAed = 170.0,
            status = BookingStatus.COMPLETED,
            scheduledDate = "Today",
            scheduledTimeSlot = "10 AM",
            address = sampleAddress,
            issueDescription = "Test"
        )

        val initialBalance = 1000.0
        val result = PaymentProcessor.processPayment(
            booking = booking,
            method = PaymentMethod.CREDIT_DEBIT_CARD,
            currentLedgerBalance = initialBalance
        )

        assertEquals(PaymentStatus.PAID_ONLINE, result.updatedBooking.paymentStatus)
        assertEquals(3, result.ledgerEntries.size)

        // Entry 1: Customer charge (+210)
        assertEquals(LedgerType.CUSTOMER_CHARGE, result.ledgerEntries[0].type)
        assertEquals(210.0, result.ledgerEntries[0].creditAed, 0.01)

        // Entry 2: Platform commission (30)
        assertEquals(LedgerType.PLATFORM_COMMISSION_EARNED, result.ledgerEntries[1].type)
        assertEquals(30.0, result.ledgerEntries[1].creditAed, 0.01)

        // Entry 3: Provider payable allocation (-170)
        assertEquals(LedgerType.PROVIDER_PAYABLE_CREDIT, result.ledgerEntries[2].type)
        assertEquals(170.0, result.ledgerEntries[2].debitAed, 0.01)
    }

    // 7. Cancellation and Refund Logic
    @Test
    fun testCancellationAndDisputeRefund_ReversesLedger() {
        val booking = FixoraRepository.createBooking(
            serviceItem = sampleService,
            customerAddress = sampleAddress,
            propertyType = PropertyType.APARTMENT,
            urgency = Urgency.STANDARD,
            scheduledDate = "Tomorrow",
            scheduledTimeSlot = "10 AM",
            issueDescription = "Test cancellation"
        )

        FixoraRepository.cancelBooking(booking.id, "Customer travel rescheduled")
        val cancelled = FixoraRepository.bookings.value.first { it.id == booking.id }
        assertEquals(BookingStatus.CUSTOMER_CANCELLED, cancelled.status)
        assertEquals("Customer travel rescheduled", cancelled.cancellationReason)

        // Test Refund Processor
        val (refundedBooking, ledgerEntry) = PaymentProcessor.processRefund(
            booking = cancelled,
            refundAmountAed = 210.0,
            currentBalance = 500.0,
            reason = "Customer cancellation before dispatch"
        )
        assertEquals(PaymentStatus.REFUNDED, refundedBooking.paymentStatus)
        assertEquals(LedgerType.REFUND_ISSUED, ledgerEntry.type)
        assertEquals(210.0, ledgerEntry.debitAed, 0.01)
        assertEquals(290.0, ledgerEntry.balanceAed, 0.01)
    }

    // 8. Role Permissions & User Switching
    @Test
    fun testRoleSwitching_UpdatesActivePersona() {
        FixoraRepository.switchRole(UserRole.PROVIDER)
        assertEquals(UserRole.PROVIDER, FixoraRepository.currentUser.value.role)

        FixoraRepository.switchRole(UserRole.ADMIN)
        assertEquals(UserRole.ADMIN, FixoraRepository.currentUser.value.role)

        FixoraRepository.switchRole(UserRole.CUSTOMER)
        assertEquals(UserRole.CUSTOMER, FixoraRepository.currentUser.value.role)
    }
}


