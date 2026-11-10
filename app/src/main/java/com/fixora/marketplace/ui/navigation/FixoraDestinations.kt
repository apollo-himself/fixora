package com.fixora.marketplace.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    // Auth & Intro
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object VerifyEmail : Screen("verify_email/{email}") {
        fun createRoute(email: String) = "verify_email/${android.net.Uri.encode(email)}"
    }

    // Customer
    object CustomerHome : Screen("customer_home")
    object CustomerSearch : Screen("customer_search")
    object ServiceCategory : Screen("service_category/{categoryId}") {
        fun createRoute(categoryId: String) = "service_category/$categoryId"
    }
    object ServiceDetail : Screen("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }
    object BookingForm : Screen("booking_form/{serviceId}") {
        fun createRoute(serviceId: String) = "booking_form/$serviceId"
    }
    object ProviderMatching : Screen("provider_matching/{bookingId}") {
        fun createRoute(bookingId: String) = "provider_matching/$bookingId"
    }
    object ProviderProfile : Screen("provider_profile/{providerId}") {
        fun createRoute(providerId: String) = "provider_profile/$providerId"
    }
    object BookingTracking : Screen("booking_tracking/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_tracking/$bookingId"
    }
    object JobCompletion : Screen("job_completion/{bookingId}") {
        fun createRoute(bookingId: String) = "job_completion/$bookingId"
    }
    object CustomerPayment : Screen("customer_payment/{bookingId}") {
        fun createRoute(bookingId: String) = "customer_payment/$bookingId"
    }
    object CustomerBookings : Screen("customer_bookings")
    object CustomerChat : Screen("customer_chat/{bookingId}") {
        fun createRoute(bookingId: String) = "customer_chat/$bookingId"
    }
    object CustomerProfileTab : Screen("customer_profile_tab")
    object CustomerNotifications : Screen("customer_notifications")
    object CustomerDispute : Screen("customer_dispute/{bookingId}") {
        fun createRoute(bookingId: String) = "customer_dispute/$bookingId"
    }

    // Provider
    object ProviderDashboard : Screen("provider_dashboard")
    object ProviderRequests : Screen("provider_requests")
    object ProviderActiveJob : Screen("provider_active_job/{bookingId}") {
        fun createRoute(bookingId: String) = "provider_active_job/$bookingId"
    }
    object ProviderQuotation : Screen("provider_quotation/{bookingId}") {
        fun createRoute(bookingId: String) = "provider_quotation/$bookingId"
    }
    object ProviderEarnings : Screen("provider_earnings")
    object ProviderAvailability : Screen("provider_availability")
    object ProviderVerification : Screen("provider_verification")

    // Freelance Marketplace (Fiverr / Upwork Style)
    object GigDetail : Screen("gig_detail/{gigId}") {
        fun createRoute(gigId: String) = "gig_detail/$gigId"
    }
    object PostJobRequest : Screen("post_job_request")
    object JobRequestsBrowse : Screen("job_requests_browse")
    object CreateGig : Screen("create_gig")

    // Admin
    object AdminDashboard : Screen("admin_dashboard")
    object AdminVerifications : Screen("admin_verifications")
    object AdminDisputes : Screen("admin_disputes")
    object AdminCategories : Screen("admin_categories")
}

