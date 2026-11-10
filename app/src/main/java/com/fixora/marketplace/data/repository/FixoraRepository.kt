package com.fixora.marketplace.data.repository

import com.fixora.marketplace.data.demo.SeedData
import com.fixora.marketplace.data.model.*
import com.fixora.marketplace.domain.engine.BookingMatchingEngine
import com.fixora.marketplace.domain.engine.PaymentProcessor
import com.fixora.marketplace.domain.engine.PricingCommissionEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object FixoraRepository {

    // Current logged-in user
    private val _currentUser = MutableStateFlow<User>(
        User(
            id = "USR-CUST-INIT",
            name = "Welcome Guest",
            email = "guest@fixora.pk",
            phone = "+92 300 0000000",
            role = UserRole.CUSTOMER,
            isVerified = true
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Registered users list
    private val _registeredUsers = MutableStateFlow<List<User>>(emptyList())
    val registeredUsers: StateFlow<List<User>> = _registeredUsers.asStateFlow()

    // Service Categories & Items
    private val _categories = MutableStateFlow(SeedData.serviceCategories)
    val categories: StateFlow<List<ServiceCategory>> = _categories.asStateFlow()

    private val _catalog = MutableStateFlow(SeedData.serviceCatalog)
    val catalog: StateFlow<List<ServiceItem>> = _catalog.asStateFlow()

    // Providers
    private val _providers = MutableStateFlow(SeedData.demoProviders)
    val providers: StateFlow<List<ProviderProfile>> = _providers.asStateFlow()

    // Bookings (Initialized with sample booking so tracking, map, completion and warranty can be tested right away)
    private val _bookings = MutableStateFlow<List<Booking>>(SeedData.sampleBookings)
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    // Chat messages (Real empty list, populated on active chats)
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(
        listOf(
            NotificationItem(
                id = "NOTIF-SEED-1",
                userId = "USR-CUST-INIT",
                title = "Technician Assigned!",
                body = "Rashid Khan accepted your booking for AC Deep Chemical Wash.",
                bookingId = "FIX-7821",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                isRead = false,
                type = "PROVIDER_ACCEPTED"
            ),
            NotificationItem(
                id = "NOTIF-SEED-2",
                userId = "USR-CUST-INIT",
                title = "Technician En Route 🚗",
                body = "Rashid Khan is on his way to your address. Estimated arrival in 12 mins.",
                bookingId = "FIX-7821",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
                isRead = false,
                type = "STATUS_CHANGE"
            ),
            NotificationItem(
                id = "NOTIF-SEED-3",
                userId = "USR-CUST-INIT",
                title = "Job Completed & 7-Day Warranty Active 🛡️",
                body = "Before & After work photos uploaded. Tap to view your digital warranty certificate.",
                bookingId = "FIX-7821",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                isRead = true,
                type = "PAYMENT_SETTLED"
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Reviews (Real empty list, populated when customer leaves a review)
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    // Quotations
    private val _quotations = MutableStateFlow<List<Quotation>>(emptyList())
    val quotations: StateFlow<List<Quotation>> = _quotations.asStateFlow()

    // Freelance Gigs (Fiverr-style seller offers)
    private val _gigs = MutableStateFlow<List<FreelanceGig>>(SeedData.sampleGigs)
    val gigs: StateFlow<List<FreelanceGig>> = _gigs.asStateFlow()

    // Custom Job Requests (Client posted jobs, populated when client posts)
    private val _jobRequests = MutableStateFlow<List<CustomJobRequest>>(emptyList())
    val jobRequests: StateFlow<List<CustomJobRequest>> = _jobRequests.asStateFlow()

    // Freelance Proposals / Bids (Populated when freelancers send bids)
    private val _proposals = MutableStateFlow<List<JobProposal>>(emptyList())
    val proposals: StateFlow<List<JobProposal>> = _proposals.asStateFlow()

    // Financial Ledger (Real empty list, populated on live escrow transactions)
    private val _ledger = MutableStateFlow<List<LedgerEntry>>(emptyList())
    val ledger: StateFlow<List<LedgerEntry>> = _ledger.asStateFlow()

    // Admin Settings
    private val _adminSettings = MutableStateFlow(AdminSettings())
    val adminSettings: StateFlow<AdminSettings> = _adminSettings.asStateFlow()

    // Disputes & Tickets (seeded with actionable customer claim tickets)
    private val _disputes = MutableStateFlow<List<DisputeTicket>>(
        listOf(
            DisputeTicket(
                id = "TICK-801",
                bookingId = "BK-9021",
                customerId = "USR-CUST-01",
                customerName = "Farhan Sheikh",
                providerId = "PROV-01",
                providerName = "Tariq Mehmood",
                reason = "Incomplete AC Gas Refill",
                details = "Technician arrived on time but AC is still not cooling properly. Requesting full refund or warranty revisit.",
                status = DisputeStatus.OPEN
            ),
            DisputeTicket(
                id = "TICK-802",
                bookingId = "BK-9044",
                customerId = "USR-CUST-02",
                customerName = "Zainab Bibi",
                providerId = "PROV-03",
                providerName = "Kamran Siddiqui",
                reason = "Overcharged for Pipe Fitting",
                details = "Estimate was PKR 2,500 but provider demanded PKR 4,000 without prior quotation approval.",
                status = DisputeStatus.OPEN
            )
        )
    )
    val disputes: StateFlow<List<DisputeTicket>> = _disputes.asStateFlow()

    // Audit Log
    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    // Customer Saved Addresses (starts empty — populated when user registers or adds manually)
    private val _customerAddresses = MutableStateFlow<List<Address>>(emptyList())
    val customerAddresses: StateFlow<List<Address>> = _customerAddresses.asStateFlow()

    // Selected Address for current booking flow (default fallback)
    private val _defaultAddress = Address(
        id = "ADDR-DEFAULT",
        label = "Home",
        street = "Main Street",
        buildingOrVilla = "",
        apartmentNo = "",
        emirate = "Lahore",
        area = "Gulberg",
        latitude = 31.5204,
        longitude = 74.3587,
        isDefault = true
    )
    private val _selectedAddress = MutableStateFlow(_defaultAddress)
    val selectedAddress: StateFlow<Address> = _selectedAddress.asStateFlow()

    // --- User & Role Switching ---

    fun switchUser(user: User) {
        _currentUser.value = user
    }

    fun becomeProvider(
        skills: List<String> = listOf("AC_SERVICE", "ELECTRICAL"),
        hourlyRatePkr: Double = 500.0,
        serviceAreas: List<String> = listOf("Lahore", "Karachi"),
        cnicOrId: String? = null
    ): ProviderProfile {
        val user = _currentUser.value
        val updatedUser = user.copy(role = UserRole.PROVIDER)
        _currentUser.value = updatedUser

        // Check if provider profile exists already
        val existingProfile = _providers.value.firstOrNull { it.userId == user.id }
        val profile = if (existingProfile != null) {
            val updated = existingProfile.copy(
                name = user.name.ifBlank { existingProfile.name },
                email = user.email.ifBlank { existingProfile.email },
                phone = if (user.phone.isNotBlank()) user.phone else existingProfile.phone,
                skills = if (skills.isNotEmpty()) skills else existingProfile.skills,
                hourlyRateAed = hourlyRatePkr,
                serviceAreas = if (serviceAreas.isNotEmpty()) serviceAreas else existingProfile.serviceAreas,
                emiratesIdNumber = cnicOrId ?: existingProfile.emiratesIdNumber
            )
            _providers.value = _providers.value.map { if (it.id == updated.id) updated else it }
            updated
        } else {
            val newProfile = ProviderProfile(
                id = "PROV-${user.id.takeLast(6).ifBlank { UUID.randomUUID().toString().take(6) }}",
                userId = user.id,
                name = user.name.ifBlank { "Fixora Freelancer" },
                phone = user.phone.ifBlank { "+92 300 1234567" },
                email = user.email.ifBlank { "provider@fixora.pk" },
                bio = "Certified home repair and maintenance professional on Fixora.",
                skills = if (skills.isNotEmpty()) skills else listOf("AC_SERVICE", "ELECTRICAL"),
                serviceAreas = if (serviceAreas.isNotEmpty()) serviceAreas else listOf("Lahore", "Karachi"),
                rating = 5.0,
                reviewCount = 1,
                completedJobs = 0,
                isOnline = true,
                isVerified = true,
                hourlyRateAed = hourlyRatePkr,
                documentStatus = VerificationStatus.APPROVED,
                emiratesIdNumber = cnicOrId
            )
            _providers.value = listOf(newProfile) + _providers.value
            newProfile
        }
        return profile
    }

    fun switchToCustomerRole() {
        val user = _currentUser.value
        _currentUser.value = user.copy(role = UserRole.CUSTOMER)
    }

    fun switchRole(role: UserRole) {
        val current = _currentUser.value
        when (role) {
            UserRole.CUSTOMER -> {
                switchToCustomerRole()
            }
            UserRole.PROVIDER -> {
                becomeProvider()
            }
            UserRole.ADMIN -> {
                _currentUser.value = current.copy(role = UserRole.ADMIN)
            }
        }
    }

    fun isUserAdmin(emailOrUser: String? = null): Boolean {
        val checkEmail = emailOrUser ?: _currentUser.value.email
        return checkEmail.trim().equals("three3bross@gmail.com", ignoreCase = true) || _currentUser.value.role == UserRole.ADMIN
    }

    fun submitComplaintTicket(customerName: String, customerEmail: String, complaintText: String): DisputeTicket {
        val ticket = DisputeTicket(
            id = "TICK-${(100..999).random()}",
            bookingId = "GENERAL-SUPPORT",
            customerId = _currentUser.value.id,
            customerName = customerName.ifBlank { "App Customer" },
            providerId = "OPS-TEAM",
            providerName = "Fixora Operations",
            reason = "Customer Feedback / Complaint",
            details = complaintText,
            status = DisputeStatus.OPEN
        )
        _disputes.value = listOf(ticket) + _disputes.value
        return ticket
    }

    fun registerUser(
        name: String,
        email: String,
        role: UserRole,
        phone: String = "",
        city: String = "Lahore",
        area: String = "DHA Phase 5",
        category: String = "AC_SERVICE",
        categories: List<String> = emptyList(),
        bio: String = "",
        hourlyRate: Double = 1500.0,
        cnic: String = "",
        isVerified: Boolean = false
    ): User {
        val effectiveRole = if (email.trim().equals("three3bross@gmail.com", ignoreCase = true)) UserRole.ADMIN else role
        val userId = "USR-${effectiveRole.name.take(4)}-${UUID.randomUUID().toString().take(6).uppercase()}"
        val newUser = User(
            id = userId,
            name = if (email.trim().equals("three3bross@gmail.com", ignoreCase = true) && name.isBlank()) "Super Admin" else name,
            email = email.ifBlank { "${name.lowercase().replace(" ", ".")}@fixora.pk" },
            phone = phone,
            role = effectiveRole,
            isVerified = if (email.trim().equals("three3bross@gmail.com", ignoreCase = true)) true else isVerified
        )
        _currentUser.value = newUser
        _registeredUsers.value = _registeredUsers.value + newUser

        if (role == UserRole.CUSTOMER && city.isNotBlank() && area.isNotBlank()) {
            val address = Address(
                id = "ADDR-${UUID.randomUUID().toString().take(6)}",
                label = "Home - $area",
                street = "Main Street",
                buildingOrVilla = "Residence",
                apartmentNo = "",
                emirate = city,
                area = area,
                latitude = 0.0,
                longitude = 0.0,
                isDefault = true
            )
            _customerAddresses.value = listOf(address) + _customerAddresses.value
            _selectedAddress.value = address
        } else if (role == UserRole.PROVIDER) {
            val provId = "PROV-${UUID.randomUUID().toString().take(6).uppercase()}"
            val newProvider = ProviderProfile(
                id = provId,
                userId = userId,
                name = name,
                phone = phone,
                email = newUser.email,
                bio = bio.ifBlank { "Independent ${if (categories.isNotEmpty()) categories.joinToString(", ") else category} specialist in $city. Certified professional with modern tools and service guarantee." },
                skills = if (categories.isNotEmpty()) categories else listOf(category, "HANDYMAN"),
                serviceAreas = listOf(area, city).filter { it.isNotBlank() },
                rating = 5.0,
                reviewCount = 0,
                completedJobs = 0,
                isOnline = true,
                isVerified = true,
                hourlyRateAed = hourlyRate,
                documentStatus = VerificationStatus.APPROVED,
                tradeLicenseNumber = "NAVTTC Certified Freelancer",
                emiratesIdNumber = cnic
            )
            _providers.value = listOf(newProvider) + _providers.value
        }

        return newUser
    }

    fun findUserByEmail(email: String): User? {
        val clean = email.trim().lowercase()
        if (clean.isBlank()) return null
        if (clean == "three3bross@gmail.com") {
            val existing = _registeredUsers.value.firstOrNull { it.email.trim().lowercase() == clean }
            if (existing != null) return existing
            val adminUser = User(
                id = "USR-ADMIN-SUPER",
                name = "Super Admin",
                email = "three3bross@gmail.com",
                phone = "",
                role = UserRole.ADMIN,
                isVerified = true
            )
            _registeredUsers.value = listOf(adminUser) + _registeredUsers.value
            return adminUser
        }
        return _registeredUsers.value.firstOrNull { it.email.trim().lowercase() == clean }
            ?: SeedData.demoUsers.firstOrNull { it.email.trim().lowercase() == clean }
    }

    fun restoreUser(id: String, name: String, email: String, role: UserRole, phone: String = "", isVerified: Boolean = true): User {
        val user = User(
            id = id,
            name = name,
            email = email,
            phone = phone,
            role = role,
            isVerified = if (email.trim().equals("three3bross@gmail.com", ignoreCase = true)) true else isVerified
        )
        _currentUser.value = user
        if (_registeredUsers.value.none { it.id == id }) {
            _registeredUsers.value = _registeredUsers.value + user
        }
        return user
    }

    fun markUserEmailVerified(userId: String) {
        val updated = _currentUser.value.copy(isVerified = true)
        _currentUser.value = updated
        _registeredUsers.value = _registeredUsers.value.map {
            if (it.id == userId || it.email.equals(updated.email, ignoreCase = true)) {
                it.copy(isVerified = true)
            } else {
                it
            }
        }
    }

    fun findUserByPhone(phone: String): User? {
        val clean = phone.replace(" ", "").replace("-", "")
        return _registeredUsers.value.firstOrNull { it.phone.replace(" ", "").replace("-", "") == clean }
            ?: SeedData.demoUsers.firstOrNull { it.phone.replace(" ", "").replace("-", "") == clean }
    }

    // --- Address Management ---
    fun updateUserProfile(name: String, email: String) {
        _currentUser.value = _currentUser.value.copy(
            name = name.ifBlank { _currentUser.value.name },
            email = email.ifBlank { _currentUser.value.email }
        )
    }

    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun addAddress(address: Address) {
        _customerAddresses.value = _customerAddresses.value + address
    }

    // --- Booking Lifecycle ---

    fun createBooking(
        serviceItem: ServiceItem,
        customerAddress: Address,
        propertyType: PropertyType,
        urgency: Urgency,
        scheduledDate: String,
        scheduledTimeSlot: String,
        issueDescription: String,
        photoUrls: List<String> = emptyList(),
        preferredProviderId: String? = null
    ): Booking {
        val breakdown = PricingCommissionEngine.calculateBreakdown(
            basePriceAed = serviceItem.priceAed,
            partsLaborAed = null,
            urgency = urgency,
            settings = _adminSettings.value
        )

        val bookingId = "FIX-${(1000..9999).random()}"
        val user = _currentUser.value

        var assignedProvider: ProviderProfile? = null
        var initialStatus = BookingStatus.MATCHING

        if (preferredProviderId != null) {
            assignedProvider = _providers.value.firstOrNull { it.id == preferredProviderId }
            if (assignedProvider != null) {
                initialStatus = BookingStatus.PROVIDER_ACCEPTED
            }
        }

        val newBooking = Booking(
            id = bookingId,
            customerId = user.id,
            customerName = user.name,
            customerPhone = user.phone,
            providerId = assignedProvider?.id,
            providerName = assignedProvider?.name,
            providerPhone = assignedProvider?.phone,
            providerRating = assignedProvider?.rating,
            serviceId = serviceItem.id,
            serviceTitle = serviceItem.name,
            serviceCategory = serviceItem.categoryId,
            pricingType = serviceItem.pricingType,
            basePriceAed = breakdown.baseServiceAed,
            emergencySurgeFeeAed = breakdown.emergencyFeeAed,
            vatAed = breakdown.vatAed,
            agreedFinalPriceAed = breakdown.totalAed,
            platformCommissionAed = breakdown.platformCommissionAed,
            providerEarningsAed = breakdown.providerEarningsAed,
            status = initialStatus,
            paymentStatus = PaymentStatus.UNPAID,
            urgency = urgency,
            propertyType = propertyType,
            scheduledDate = scheduledDate,
            scheduledTimeSlot = scheduledTimeSlot,
            address = customerAddress,
            issueDescription = issueDescription,
            issuePhotoUrls = photoUrls,
            statusHistory = listOf(
                BookingStatusEvent(
                    status = BookingStatus.REQUESTED,
                    note = "Booking created by customer"
                ),
                BookingStatusEvent(
                    status = initialStatus,
                    note = if (assignedProvider != null) "Accepted by ${assignedProvider.name}" else "Searching for nearby certified technicians"
                )
            )
        )

        _bookings.value = listOf(newBooking) + _bookings.value

        // Notify provider if assigned
        assignedProvider?.let {
            addNotification(
                userId = it.userId,
                title = "New Booking Assigned!",
                body = "New job: ${serviceItem.name} at ${customerAddress.area}",
                bookingId = bookingId,
                type = "NEW_BOOKING"
            )
        }

        return newBooking
    }

    /**
     * Provider accepts a pending/matching booking.
     */
    fun providerAcceptBooking(bookingId: String, providerId: String): Boolean {
        val current = _bookings.value.firstOrNull { it.id == bookingId } ?: return false
        val provider = _providers.value.firstOrNull { it.id == providerId } ?: return false

        val accepted = BookingMatchingEngine.attemptAcceptBooking(current, provider) ?: return false

        _bookings.value = _bookings.value.map { if (it.id == bookingId) accepted else it }

        addNotification(
            userId = current.customerId,
            title = "Technician Assigned!",
            body = "${provider.name} accepted your booking for ${current.serviceTitle}.",
            bookingId = bookingId,
            type = "PROVIDER_ACCEPTED"
        )
        return true
    }

    /**
     * Provider declines a booking request.
     */
    fun providerDeclineBooking(bookingId: String, providerId: String, reason: String = "Unavailable") {
        _bookings.value = _bookings.value.map { b ->
            if (b.id == bookingId) {
                b.copy(
                    status = BookingStatus.PROVIDER_DECLINED,
                    cancellationReason = reason,
                    statusHistory = b.statusHistory + BookingStatusEvent(
                        status = BookingStatus.PROVIDER_DECLINED,
                        note = "Declined: $reason",
                        updatedBy = providerId
                    ),
                    updatedAt = System.currentTimeMillis()
                )
            } else b
        }
    }

    /**
     * Advance booking status along lifecycle.
     */
    fun updateBookingStatus(
        bookingId: String,
        newStatus: BookingStatus,
        note: String? = null,
        beforePhotoUrl: String? = null,
        afterPhotoUrl: String? = null
    ): Booking? {
        var updated: Booking? = null

        _bookings.value = _bookings.value.map { b ->
            if (b.id == bookingId) {
                val newHistory = b.statusHistory + BookingStatusEvent(
                    status = newStatus,
                    note = note ?: "Status updated to ${newStatus.name}",
                    updatedBy = _currentUser.value.id
                )
                val modified = b.copy(
                    status = newStatus,
                    beforePhotoUrl = beforePhotoUrl ?: b.beforePhotoUrl,
                    afterPhotoUrl = afterPhotoUrl ?: b.afterPhotoUrl,
                    isCompletedByProvider = if (newStatus == BookingStatus.COMPLETED || newStatus == BookingStatus.PAYMENT_SETTLED) true else b.isCompletedByProvider,
                    isCompletedByCustomer = if (newStatus == BookingStatus.PAYMENT_SETTLED) true else b.isCompletedByCustomer,
                    statusHistory = newHistory,
                    updatedAt = System.currentTimeMillis()
                )
                updated = modified
                modified
            } else b
        }

        // Notify other party
        updated?.let { b ->
            val recipientId = if (_currentUser.value.role == UserRole.PROVIDER) b.customerId else (b.providerId ?: "")
            addNotification(
                userId = recipientId,
                title = "Booking Update: ${newStatus.name.replace('_', ' ')}",
                body = note ?: "Your booking ${b.id} status changed to ${newStatus.name}",
                bookingId = bookingId,
                type = "STATUS_CHANGE"
            )
        }

        return updated
    }

    /**
     * Dual Sign-off: Provider confirms the work is finished on their side.
     */
    fun providerConfirmJobCompleted(bookingId: String, note: String = "Technician marked job done & verified work"): Booking? {
        var updated: Booking? = null
        _bookings.value = _bookings.value.map { b ->
            if (b.id == bookingId) {
                val bothDone = b.isCompletedByCustomer
                val nextStatus = if (bothDone) BookingStatus.COMPLETED else BookingStatus.COMPLETED
                val newHistory = b.statusHistory + BookingStatusEvent(
                    status = nextStatus,
                    note = if (bothDone) "Both Customer and Technician confirmed job done!" else "Technician confirmed work completed. Awaiting customer confirmation.",
                    updatedBy = _currentUser.value.id
                )
                val modified = b.copy(
                    isCompletedByProvider = true,
                    status = nextStatus,
                    statusHistory = newHistory,
                    updatedAt = System.currentTimeMillis()
                )
                updated = modified
                modified
            } else b
        }
        updated?.let { b ->
            addNotification(
                userId = b.customerId,
                title = "Technician Marked Job Done!",
                body = "Technician has signed off on the job. Please inspect and tap 'Confirm Done' on your app.",
                bookingId = bookingId,
                type = "STATUS_CHANGE"
            )
        }
        return updated
    }

    /**
     * Dual Sign-off: Customer confirms the work is finished satisfactorily.
     */
    fun customerConfirmJobCompleted(bookingId: String, note: String = "Customer confirmed work completed satisfactorily"): Booking? {
        var updated: Booking? = null
        _bookings.value = _bookings.value.map { b ->
            if (b.id == bookingId) {
                val nextStatus = if (b.isCompletedByProvider) BookingStatus.COMPLETED else b.status
                val newHistory = b.statusHistory + BookingStatusEvent(
                    status = nextStatus,
                    note = if (b.isCompletedByProvider) "Both Customer and Technician confirmed job completed!" else "Customer marked job done. Awaiting technician sign-off.",
                    updatedBy = _currentUser.value.id
                )
                val modified = b.copy(
                    isCompletedByCustomer = true,
                    status = nextStatus,
                    statusHistory = newHistory,
                    updatedAt = System.currentTimeMillis()
                )
                updated = modified
                modified
            } else b
        }
        updated?.let { b ->
            b.providerId?.let { pid ->
                val prov = _providers.value.firstOrNull { it.id == pid }
                addNotification(
                    userId = prov?.userId ?: pid,
                    title = "Customer Confirmed Job Done!",
                    body = "The customer inspected and confirmed completion for booking #${b.id}.",
                    bookingId = bookingId,
                    type = "STATUS_CHANGE"
                )
            }
        }
        return updated
    }

    /**
     * Process payment for a completed booking.
     */
    fun settleBookingPayment(
        bookingId: String,
        method: PaymentMethod
    ): Boolean {
        val current = _bookings.value.firstOrNull { it.id == bookingId } ?: return false
        val currentBalance = _ledger.value.lastOrNull()?.balanceAed ?: 0.0

        val result = PaymentProcessor.processPayment(
            booking = current,
            method = method,
            currentLedgerBalance = currentBalance
        )

        // Advance booking status to PAYMENT_SETTLED
        val completedBooking = result.updatedBooking.copy(
            status = BookingStatus.PAYMENT_SETTLED,
            statusHistory = result.updatedBooking.statusHistory + BookingStatusEvent(
                status = BookingStatus.PAYMENT_SETTLED,
                note = "Payment settled via ${method.name}"
            )
        )

        _bookings.value = _bookings.value.map { if (it.id == bookingId) completedBooking else it }
        _ledger.value = _ledger.value + result.ledgerEntries

        // Increment provider completed jobs
        current.providerId?.let { pId ->
            _providers.value = _providers.value.map { p ->
                if (p.id == pId) p.copy(completedJobs = p.completedJobs + 1) else p
            }
        }

        addNotification(
            userId = current.customerId,
            title = "Payment Successful",
            body = "Payment of PKR ${completedBooking.agreedFinalPriceAed.toInt()} received. Thank you for choosing Fixora!",
            bookingId = bookingId,
            type = "PAYMENT_SETTLED"
        )

        return true
    }

    /**
     * Customer cancels a booking.
     */
    fun cancelBooking(bookingId: String, reason: String) {
        _bookings.value = _bookings.value.map { b ->
            if (b.id == bookingId) {
                b.copy(
                    status = BookingStatus.CUSTOMER_CANCELLED,
                    cancellationReason = reason,
                    statusHistory = b.statusHistory + BookingStatusEvent(
                        status = BookingStatus.CUSTOMER_CANCELLED,
                        note = "Cancelled: $reason",
                        updatedBy = _currentUser.value.id
                    ),
                    updatedAt = System.currentTimeMillis()
                )
            } else b
        }
    }

    // --- Quotation Flow ---

    fun submitQuotation(
        bookingId: String,
        providerId: String,
        providerName: String,
        laborCostAed: Double,
        partsCostAed: Double,
        notes: String
    ): Quotation {
        val total = laborCostAed + partsCostAed
        val quotation = Quotation(
            id = "QUOTE-${UUID.randomUUID().toString().take(6)}",
            bookingId = bookingId,
            providerId = providerId,
            providerName = providerName,
            laborCostAed = laborCostAed,
            partsCostAed = partsCostAed,
            totalAed = total,
            notes = notes
        )
        _quotations.value = _quotations.value + quotation

        // Update booking pricing breakdown
        val currentBooking = _bookings.value.firstOrNull { it.id == bookingId }
        currentBooking?.let { b ->
            val breakdown = PricingCommissionEngine.calculateBreakdown(
                basePriceAed = b.basePriceAed,
                partsLaborAed = total,
                urgency = b.urgency,
                settings = _adminSettings.value
            )
            _bookings.value = _bookings.value.map {
                if (it.id == bookingId) {
                    it.copy(
                        partsLaborQuotationAed = total,
                        vatAed = breakdown.vatAed,
                        agreedFinalPriceAed = breakdown.totalAed,
                        platformCommissionAed = breakdown.platformCommissionAed,
                        providerEarningsAed = breakdown.providerEarningsAed,
                        statusHistory = it.statusHistory + BookingStatusEvent(
                            status = it.status,
                            note = "Quotation submitted: PKR $total"
                        )
                    )
                } else it
            }

            addNotification(
                userId = b.customerId,
                title = "New Quotation Received",
                body = "Technician submitted quotation: PKR $total for additional parts/labor.",
                bookingId = bookingId,
                type = "QUOTATION_RECEIVED"
            )
        }

        return quotation
    }

    fun approveQuotation(quotationId: String): Boolean {
        val quote = _quotations.value.firstOrNull { it.id == quotationId } ?: return false
        _quotations.value = _quotations.value.map {
            if (it.id == quotationId) it.copy(isApprovedByCustomer = true) else it
        }

        val booking = _bookings.value.firstOrNull { it.id == quote.bookingId }
        booking?.let {
            addNotification(
                userId = quote.providerId,
                title = "Quotation Approved!",
                body = "Customer approved your quotation of PKR ${quote.totalAed.toInt()}. You may proceed with the repair.",
                bookingId = quote.bookingId,
                type = "QUOTATION_APPROVED"
            )
        }
        return true
    }

    // --- Chat Messages ---

    fun sendChatMessage(bookingId: String, text: String) {
        val user = _currentUser.value
        val msg = ChatMessage(
            id = "MSG-${UUID.randomUUID().toString().take(8)}",
            bookingId = bookingId,
            senderId = user.id,
            senderName = user.name,
            senderRole = user.role,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.value = _chatMessages.value + msg

        // If customer is sending the message, trigger an intelligent provider auto-reply
        if (user.role == UserRole.CUSTOMER) {
            val booking = _bookings.value.firstOrNull { it.id == bookingId }
            val providerName = booking?.providerName ?: "Technician"
            val providerId = booking?.providerId ?: "PROV-REPLY"

            val lower = text.lowercase()
            val replyText = when {
                lower.contains("where") || lower.contains("reach") || lower.contains("eta") || lower.contains("arrived") || lower.contains("time") -> {
                    "Salam! I am on my way to your location. Expected arrival within 10-15 minutes. See you shortly!"
                }
                lower.contains("cost") || lower.contains("price") || lower.contains("fee") || lower.contains("charge") || lower.contains("quote") -> {
                    "Understood. The base diagnostic and standard repair fee is locked at PKR ${booking?.basePriceAed?.toInt() ?: 1500}. Any extra parts will be submitted via quotation for your approval first."
                }
                lower.contains("gate") || lower.contains("flat") || lower.contains("door") || lower.contains("building") || lower.contains("villa") -> {
                    "Noted, thank you for the directions! I will inform security upon arrival."
                }
                lower.contains("done") || lower.contains("complete") || lower.contains("finish") -> {
                    "Almost finished! I will submit before/after verification photos and sign off the completion in the app."
                }
                else -> {
                    "Salam! Received your message. Working on it right now and keeping you updated!"
                }
            }

            val autoReply = ChatMessage(
                id = "MSG-AUTO-${UUID.randomUUID().toString().take(6)}",
                bookingId = bookingId,
                senderId = providerId,
                senderName = providerName,
                senderRole = UserRole.PROVIDER,
                text = replyText,
                timestamp = System.currentTimeMillis() + 800,
                isAutoReply = true
            )
            _chatMessages.value = _chatMessages.value + autoReply
        }
    }

    // --- Reviews ---

    fun submitReview(
        bookingId: String,
        providerId: String,
        rating: Double,
        comment: String,
        tags: List<String>
    ) {
        val user = _currentUser.value
        val review = Review(
            id = "REV-${UUID.randomUUID().toString().take(6)}",
            bookingId = bookingId,
            customerId = user.id,
            customerName = user.name,
            providerId = providerId,
            rating = rating,
            comment = comment,
            tags = tags
        )
        _reviews.value = _reviews.value + review

        // Recalculate provider rating
        val providerReviews = _reviews.value.filter { it.providerId == providerId }
        val newAvg = providerReviews.map { it.rating }.average()

        _providers.value = _providers.value.map { p ->
            if (p.id == providerId) {
                p.copy(
                    rating = (Math.round(newAvg * 10.0) / 10.0),
                    reviewCount = providerReviews.size
                )
            } else p
        }
    }

    // --- Disputes ---

    fun raiseDispute(
        bookingId: String,
        reason: String,
        details: String
    ) {
        val booking = _bookings.value.firstOrNull { it.id == bookingId } ?: return
        val dispute = DisputeTicket(
            id = "DISP-${UUID.randomUUID().toString().take(6)}",
            bookingId = bookingId,
            customerId = booking.customerId,
            customerName = booking.customerName,
            providerId = booking.providerId ?: "UNASSIGNED",
            providerName = booking.providerName ?: "Unassigned Provider",
            reason = reason,
            details = details
        )
        _disputes.value = _disputes.value + dispute

        // Mark booking status DISPUTED
        updateBookingStatus(bookingId, BookingStatus.DISPUTED, "Customer opened dispute: $reason")
    }

    fun resolveDispute(
        disputeId: String,
        resolvedAsRefund: Boolean,
        resolutionNotes: String
    ) {
        val dispute = _disputes.value.firstOrNull { it.id == disputeId } ?: return
        val booking = _bookings.value.firstOrNull { it.id == dispute.bookingId }

        val newStatus = if (resolvedAsRefund) DisputeStatus.RESOLVED_REFUNDED else DisputeStatus.RESOLVED_DISMISSED
        _disputes.value = _disputes.value.map {
            if (it.id == disputeId) it.copy(
                status = newStatus,
                resolutionNotes = resolutionNotes,
                resolvedAt = System.currentTimeMillis()
            ) else it
        }

        if (resolvedAsRefund && booking != null) {
            val balance = _ledger.value.lastOrNull()?.balanceAed ?: 0.0
            val (updatedBooking, ledgerEntry) = PaymentProcessor.processRefund(
                booking = booking,
                refundAmountAed = booking.agreedFinalPriceAed,
                currentBalance = balance,
                reason = "Dispute resolution: $resolutionNotes"
            )
            _bookings.value = _bookings.value.map { if (it.id == booking.id) updatedBooking else it }
            _ledger.value = _ledger.value + ledgerEntry
        }

        recordAuditLog(
            action = "RESOLVE_DISPUTE",
            targetEntity = "Dispute $disputeId",
            details = "Status: $newStatus | Notes: $resolutionNotes"
        )
    }

    // --- Provider Profile & Availability ---

    fun toggleProviderOnline(providerId: String) {
        _providers.value = _providers.value.map {
            if (it.id == providerId) it.copy(isOnline = !it.isOnline) else it
        }
    }

    fun updateProviderServiceAreas(providerId: String, newAreas: List<String>) {
        _providers.value = _providers.value.map {
            if (it.id == providerId) it.copy(serviceAreas = newAreas) else it
        }
    }

    fun submitProviderDocuments(providerId: String, tradeLicense: String, cnicNumber: String) {
        _providers.value = _providers.value.map {
            if (it.id == providerId) it.copy(
                tradeLicenseNumber = tradeLicense,
                emiratesIdNumber = cnicNumber,
                documentStatus = VerificationStatus.PENDING
            ) else it
        }
    }

    // --- Admin Operations ---

    fun adminSetVerificationStatus(providerId: String, status: VerificationStatus) {
        _providers.value = _providers.value.map {
            if (it.id == providerId) it.copy(
                documentStatus = status,
                isVerified = (status == VerificationStatus.APPROVED)
            ) else it
        }
        recordAuditLog(
            action = "VERIFY_PROVIDER",
            targetEntity = "Provider $providerId",
            details = "Set verification status to ${status.name}"
        )
    }

    fun updateAdminCommission(percentage: Double) {
        _adminSettings.value = _adminSettings.value.copy(commissionPercentage = percentage)
        recordAuditLog(
            action = "UPDATE_COMMISSION",
            targetEntity = "MARKETPLACE_SETTINGS",
            details = "Platform commission updated to $percentage%"
        )
    }

    fun recordAuditLog(action: String, targetEntity: String, details: String) {
        val user = _currentUser.value
        val log = AuditLog(
            id = "AUD-${UUID.randomUUID().toString().take(6)}",
            adminId = user.id,
            adminName = user.name,
            action = action,
            targetEntity = targetEntity,
            details = details
        )
        _auditLogs.value = listOf(log) + _auditLogs.value
    }

    // --- Freelance Maintenance Marketplace (Fiverr Model) ---

    fun createGig(
        sellerId: String,
        title: String,
        categoryId: String,
        description: String,
        packages: List<GigPackage>,
        emirates: List<String> = listOf("Lahore", "Karachi")
    ): FreelanceGig {
        val seller = _providers.value.firstOrNull { it.id == sellerId }
        val newGig = FreelanceGig(
            id = "GIG-${UUID.randomUUID().toString().take(6).uppercase()}",
            sellerId = sellerId,
            sellerName = seller?.name ?: _currentUser.value.name,
            sellerRating = seller?.rating ?: 5.0,
            sellerReviewCount = seller?.reviewCount ?: 0,
            sellerCompletedOrders = seller?.completedJobs ?: 0,
            sellerBadge = if ((seller?.rating ?: 5.0) >= 4.8) "Top Rated Freelancer" else "Verified Freelancer",
            title = title,
            categoryId = categoryId,
            description = description,
            packages = packages,
            coverIcon = when (categoryId) {
                "AC_SERVICE" -> "ac_unit"
                "PLUMBING" -> "plumbing"
                "ELECTRICAL" -> "bolt"
                "HANDYMAN" -> "build"
                "PAINTING" -> "format_paint"
                else -> "handyman"
            },
            emirates = emirates
        )
        _gigs.value = listOf(newGig) + _gigs.value
        return newGig
    }

    fun postJobRequest(
        title: String,
        categoryId: String,
        description: String,
        budgetAed: Double,
        emirate: String,
        area: String,
        urgency: Urgency = Urgency.STANDARD
    ): CustomJobRequest {
        val user = _currentUser.value
        val request = CustomJobRequest(
            id = "REQ-${(100..999).random()}",
            customerId = user.id,
            customerName = user.name,
            title = title,
            categoryId = categoryId,
            description = description,
            budgetAed = budgetAed,
            emirate = emirate,
            area = area,
            urgency = urgency,
            proposalsCount = 0,
            status = JobRequestStatus.OPEN
        )
        _jobRequests.value = listOf(request) + _jobRequests.value
        return request
    }

    fun submitProposal(
        jobRequestId: String,
        sellerId: String,
        bidAmountAed: Double,
        deliveryTimeText: String,
        coverLetter: String
    ): JobProposal {
        val seller = _providers.value.firstOrNull { it.id == sellerId }
        val proposal = JobProposal(
            id = "PROP-${UUID.randomUUID().toString().take(6).uppercase()}",
            jobRequestId = jobRequestId,
            sellerId = sellerId,
            sellerName = seller?.name ?: _currentUser.value.name,
            sellerRating = seller?.rating ?: 4.9,
            sellerCompletedJobs = seller?.completedJobs ?: 10,
            bidAmountAed = bidAmountAed,
            deliveryTimeText = deliveryTimeText,
            coverLetter = coverLetter
        )
        _proposals.value = listOf(proposal) + _proposals.value

        // Increment proposalsCount on the request
        _jobRequests.value = _jobRequests.value.map { req ->
            if (req.id == jobRequestId) req.copy(proposalsCount = req.proposalsCount + 1) else req
        }

        val request = _jobRequests.value.firstOrNull { it.id == jobRequestId }
        request?.let {
            addNotification(
                userId = it.customerId,
                title = "New Freelancer Proposal Received!",
                body = "${proposal.sellerName} bid PKR ${proposal.bidAmountAed.toInt()} on '${it.title}'",
                bookingId = null,
                type = "NEW_PROPOSAL"
            )
        }

        return proposal
    }

    fun orderGig(
        gig: FreelanceGig,
        selectedPackage: GigPackage,
        customerAddress: Address = _selectedAddress.value,
        scheduledDate: String = "Today",
        scheduledTimeSlot: String = "2:00 PM - 4:00 PM",
        issueDescription: String = "Ordered freelance gig: ${gig.title} (${selectedPackage.name})"
    ): Booking {
        val breakdown = PricingCommissionEngine.calculateBreakdown(
            basePriceAed = selectedPackage.priceAed,
            partsLaborAed = null,
            urgency = Urgency.STANDARD,
            settings = _adminSettings.value
        )
        val user = _currentUser.value
        val bookingId = "ORD-${(1000..9999).random()}"

        val newBooking = Booking(
            id = bookingId,
            customerId = user.id,
            customerName = user.name,
            customerPhone = user.phone,
            providerId = gig.sellerId,
            providerName = gig.sellerName,
            providerPhone = "+92 55 987 6543",
            providerRating = gig.sellerRating,
            serviceId = gig.id,
            serviceTitle = gig.title,
            serviceCategory = gig.categoryId,
            pricingType = PricingType.FIXED,
            basePriceAed = breakdown.baseServiceAed,
            vatAed = breakdown.vatAed,
            agreedFinalPriceAed = breakdown.totalAed,
            platformCommissionAed = breakdown.platformCommissionAed,
            providerEarningsAed = breakdown.providerEarningsAed,
            status = BookingStatus.PROVIDER_ACCEPTED,
            paymentStatus = PaymentStatus.UNPAID,
            urgency = Urgency.STANDARD,
            scheduledDate = scheduledDate,
            scheduledTimeSlot = scheduledTimeSlot,
            address = customerAddress,
            issueDescription = issueDescription,
            statusHistory = listOf(
                BookingStatusEvent(
                    status = BookingStatus.REQUESTED,
                    note = "Gig ordered directly from freelance seller ${gig.sellerName}"
                ),
                BookingStatusEvent(
                    status = BookingStatus.PROVIDER_ACCEPTED,
                    note = "Assigned to ${gig.sellerName} (${selectedPackage.name})"
                )
            )
        )

        _bookings.value = listOf(newBooking) + _bookings.value

        addNotification(
            userId = gig.sellerId,
            title = "New Gig Order Received!",
            body = "${user.name} ordered your gig '${gig.title}' (PKR ${selectedPackage.priceAed.toInt()})",
            bookingId = bookingId,
            type = "NEW_ORDER"
        )

        return newBooking
    }

    fun acceptProposal(
        proposal: JobProposal,
        request: CustomJobRequest,
        customerAddress: Address
    ): Booking {
        val breakdown = PricingCommissionEngine.calculateBreakdown(
            basePriceAed = proposal.bidAmountAed,
            partsLaborAed = null,
            urgency = request.urgency,
            settings = _adminSettings.value
        )
        val user = _currentUser.value
        val bookingId = "BID-${(1000..9999).random()}"

        val newBooking = Booking(
            id = bookingId,
            customerId = user.id,
            customerName = user.name,
            customerPhone = user.phone,
            providerId = proposal.sellerId,
            providerName = proposal.sellerName,
            providerPhone = "+92 55 987 6543",
            providerRating = proposal.sellerRating,
            serviceId = request.id,
            serviceTitle = request.title,
            serviceCategory = request.categoryId,
            pricingType = PricingType.FIXED,
            basePriceAed = breakdown.baseServiceAed,
            vatAed = breakdown.vatAed,
            agreedFinalPriceAed = breakdown.totalAed,
            platformCommissionAed = breakdown.platformCommissionAed,
            providerEarningsAed = breakdown.providerEarningsAed,
            status = BookingStatus.PROVIDER_ACCEPTED,
            paymentStatus = PaymentStatus.UNPAID,
            urgency = request.urgency,
            scheduledDate = "Agreed via proposal",
            scheduledTimeSlot = proposal.deliveryTimeText,
            address = customerAddress,
            issueDescription = request.description,
            statusHistory = listOf(
                BookingStatusEvent(
                    status = BookingStatus.REQUESTED,
                    note = "Accepted proposal from ${proposal.sellerName} for PKR ${proposal.bidAmountAed.toInt()}"
                ),
                BookingStatusEvent(
                    status = BookingStatus.PROVIDER_ACCEPTED,
                    note = "Job contracted to ${proposal.sellerName}"
                )
            )
        )

        _bookings.value = listOf(newBooking) + _bookings.value

        // Update request status to IN_PROGRESS
        _jobRequests.value = _jobRequests.value.map {
            if (it.id == request.id) it.copy(status = JobRequestStatus.IN_PROGRESS) else it
        }

        addNotification(
            userId = proposal.sellerId,
            title = "Proposal Accepted!",
            body = "${user.name} accepted your proposal for '${request.title}'. Time to start work!",
            bookingId = bookingId,
            type = "PROPOSAL_ACCEPTED"
        )

        return newBooking
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    fun addNotification(userId: String, title: String, body: String, bookingId: String?, type: String) {
        val notif = NotificationItem(
            id = "NOTIF-${UUID.randomUUID().toString().take(8)}",
            userId = userId,
            title = title,
            body = body,
            bookingId = bookingId,
            type = type
        )
        _notifications.value = listOf(notif) + _notifications.value
    }
}

