package com.fixora.marketplace.ui.navigation

import android.content.pm.ApplicationInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fixora.marketplace.data.local.AppPreferences
import com.fixora.marketplace.data.model.User
import com.fixora.marketplace.data.model.UserRole
import com.fixora.marketplace.data.repository.FixoraRepository
import com.fixora.marketplace.ui.admin.*
import com.fixora.marketplace.ui.auth.*
import com.fixora.marketplace.ui.components.LanguageSelectionDialog
import com.fixora.marketplace.ui.customer.*
import com.fixora.marketplace.ui.provider.*

@Composable
fun FixoraNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    var showLanguagePrompt by remember {
        mutableStateOf(!AppPreferences.hasSelectedLanguage(context))
    }

    if (showLanguagePrompt) {
        LanguageSelectionDialog(
            initialLanguage = AppPreferences.getSelectedLanguage(context),
            onLanguageSelected = { selected ->
                AppPreferences.setSelectedLanguage(context, selected)
                com.fixora.marketplace.data.i18n.AppLocaleManager.setLanguageByName(selected)
                showLanguagePrompt = false
            }
        )
    }

    val initialDestination = remember {
        val savedUser = AppPreferences.getSavedUser(context)
        if (savedUser != null) {
            FixoraRepository.restoreUser(
                id = savedUser.id,
                name = savedUser.name,
                email = savedUser.email,
                role = savedUser.role,
                isVerified = savedUser.isVerified
            )
            if (!savedUser.isVerified) {
                val pendingEmail = AppPreferences.getPendingVerifyEmail(context)
                if (pendingEmail != null) {
                    Screen.VerifyEmail.createRoute(pendingEmail)
                } else {
                    Screen.Login.route
                }
            } else {
                when (savedUser.role) {
                    UserRole.CUSTOMER -> Screen.CustomerHome.route
                    UserRole.PROVIDER -> Screen.ProviderDashboard.route
                    UserRole.ADMIN -> Screen.AdminDashboard.route
                }
            }
        } else if (AppPreferences.hasCompletedOnboarding(context)) {
            Screen.Login.route
        } else {
            Screen.Splash.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = initialDestination
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onRoleSelected = { role ->
                    AppPreferences.setOnboardingRole(context, role)
                    navController.navigate(Screen.Login.route) { launchSingleTop = true }
                }
            )
        }

        // Login
        composable(Screen.Login.route) {
            val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            LoginScreen(
                initialRole = AppPreferences.getOnboardingRole(context),
                onNavigateBack = if (navController.previousBackStackEntry?.destination?.route == Screen.Onboarding.route) {
                    { navController.popBackStack() }
                } else null,
                onSkipSignIn = if (isDebuggable) {
                    {
                        FixoraRepository.switchUser(
                            User(
                                id = AppPreferences.DEBUG_GUEST_ID,
                                name = "Debug Guest",
                                email = "debug-guest@fixora.invalid",
                                phone = "",
                                role = UserRole.CUSTOMER
                            )
                        )
                        navController.navigate(Screen.CustomerHome.route) {
                            popUpTo(navController.graph.id) { inclusive = false }
                        }
                    }
                } else null,
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        UserRole.CUSTOMER -> Screen.CustomerHome.route
                        UserRole.PROVIDER -> Screen.ProviderDashboard.route
                        UserRole.ADMIN -> Screen.AdminDashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(navController.graph.id) { inclusive = false }
                    }
                },
                onVerifyEmail = { email ->
                    navController.navigate(Screen.VerifyEmail.createRoute(email)) {
                        popUpTo(Screen.Login.route) { inclusive = false }
                    }
                }
            )
        }

        // Email Verification
        composable(
            route = Screen.VerifyEmail.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                email = email,
                onVerified = {
                    val user = FixoraRepository.currentUser.value
                    FixoraRepository.markUserEmailVerified(user.id)
                    AppPreferences.setEmailVerified(context, true)
                    AppPreferences.setPendingVerifyEmail(context, null)
                    AppPreferences.saveUserSession(context, FixoraRepository.currentUser.value, isEmailVerified = true)
                    val dest = when (user.role) {
                        UserRole.CUSTOMER -> Screen.CustomerHome.route
                        UserRole.PROVIDER -> Screen.ProviderDashboard.route
                        UserRole.ADMIN -> Screen.AdminDashboard.route
                    }
                    navController.navigate(dest) {
                        popUpTo(navController.graph.id) { inclusive = false }
                    }
                },
                onSignOut = {
                    AppPreferences.clearSession(context)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                    }
                }
            )
        }

        // Customer Home
        composable(Screen.CustomerHome.route) {
            CustomerHomeScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.ServiceCategory.createRoute(categoryId))
                },
                onProviderClick = { providerId ->
                    navController.navigate(Screen.ProviderProfile.createRoute(providerId))
                },
                onTrackBookingClick = { bookingId ->
                    navController.navigate(Screen.BookingTracking.createRoute(bookingId))
                },
                onNotificationsClick = {
                    navController.navigate(Screen.CustomerNotifications.route)
                },
                onBottomNavClick = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                },
                onPostJobClick = { navController.navigate(Screen.PostJobRequest.route) },
                onSearchClick = { navController.navigate(Screen.CustomerSearch.route) { launchSingleTop = true } },
                onAdminPanelClick = {
                    navController.navigate(Screen.AdminDashboard.route)
                }
            )
        }


        composable(Screen.CustomerSearch.route) {
            CustomerSearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onCategoryClick = { navController.navigate(Screen.ServiceCategory.createRoute(it)) },
                onServiceClick = { navController.navigate(Screen.ServiceDetail.createRoute(it)) },
                onGigClick = { navController.navigate(Screen.GigDetail.createRoute(it)) },
                onProviderClick = { navController.navigate(Screen.ProviderProfile.createRoute(it)) }
            )
        }

        // Service Category
        composable(
            route = Screen.ServiceCategory.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "AC_SERVICE"
            ServiceCategoryScreen(
                categoryId = categoryId,
                onNavigateBack = { navController.popBackStack() },
                onServiceSelect = { serviceId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                },
                onGigSelect = { navController.navigate(Screen.GigDetail.createRoute(it)) }
            )
        }

        // Service Detail
        composable(
            route = Screen.ServiceDetail.route,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: "SRV-AC-01"
            ServiceDetailScreen(
                serviceId = serviceId,
                onNavigateBack = { navController.popBackStack() },
                onBookNow = { srvId ->
                    navController.navigate(Screen.BookingForm.createRoute(srvId))
                }
            )
        }

        // Booking Form
        composable(
            route = Screen.BookingForm.route,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: "SRV-AC-01"
            BookingFormScreen(
                serviceId = serviceId,
                onNavigateBack = { navController.popBackStack() },
                onBookingCreated = { bookingId ->
                    navController.navigate(Screen.ProviderMatching.createRoute(bookingId))
                }
            )
        }

        // Provider Matching
        composable(
            route = Screen.ProviderMatching.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            ProviderMatchingScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() },
                onProviderSelected = { bId ->
                    navController.navigate(Screen.BookingTracking.createRoute(bId))
                },
                onViewProfile = { providerId ->
                    navController.navigate(Screen.ProviderProfile.createRoute(providerId))
                }
            )
        }

        // Provider Profile
        composable(
            route = Screen.ProviderProfile.route,
            arguments = listOf(navArgument("providerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId") ?: "PROV-01"
            ProviderProfileScreen(
                providerId = providerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Booking Tracking
        composable(
            route = Screen.BookingTracking.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingTrackingScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() },
                onOpenChat = { bId ->
                    navController.navigate(Screen.CustomerChat.createRoute(bId))
                },
                onViewCompletion = { bId ->
                    navController.navigate(Screen.JobCompletion.createRoute(bId))
                },
                onPayNow = { bId ->
                    navController.navigate(Screen.CustomerPayment.createRoute(bId))
                }
            )
        }

        // Job Completion
        composable(
            route = Screen.JobCompletion.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            JobCompletionCustomerScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() },
                onProceedToPayment = { bId ->
                    navController.navigate(Screen.CustomerPayment.createRoute(bId))
                },
                onRaiseDispute = { bId ->
                    navController.navigate(Screen.CustomerDispute.createRoute(bId))
                }
            )
        }

        // Customer Payment
        composable(
            route = Screen.CustomerPayment.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            CustomerPaymentScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() },
                onPaymentSuccess = { bId ->
                    val trackingRoute = Screen.BookingTracking.createRoute(bId)
                    if (!navController.popBackStack(trackingRoute, inclusive = false)) {
                        navController.navigate(trackingRoute) {
                            popUpTo(Screen.CustomerPayment.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Customer Bookings
        composable(Screen.CustomerBookings.route) {
            CustomerBookingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onBookingClick = { bookingId ->
                    navController.navigate(Screen.BookingTracking.createRoute(bookingId))
                }
            )
        }

        // Customer Chat
        composable(
            route = Screen.CustomerChat.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            CustomerChatScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Customer Profile Tab
        composable(Screen.CustomerProfileTab.route) {
            CustomerProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onHelpCenterClick = {
                    navController.navigate(Screen.CustomerNotifications.route)
                },
                onSignOut = {
                    AppPreferences.clearSession(context)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = false }
                    }
                },
                onAdminPanelClick = {
                    navController.navigate(Screen.AdminDashboard.route)
                },
                onMyBookingsClick = {
                    navController.navigate(Screen.CustomerBookings.route)
                }
            )
        }

        // Customer Notifications
        composable(Screen.CustomerNotifications.route) {
            CustomerNotificationsScreen(
                onNavigateBack = { navController.popBackStack() },
                onBookingClick = { bookingId ->
                    navController.navigate(Screen.BookingTracking.createRoute(bookingId))
                }
            )
        }

        // Customer Dispute
        composable(
            route = Screen.CustomerDispute.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            CustomerDisputeScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() },
                onDisputeSubmitted = {
                    navController.popBackStack()
                }
            )
        }

        // Provider Dashboard
        composable(Screen.ProviderDashboard.route) {
            ProviderDashboardScreen(
                onViewRequests = {
                    navController.navigate(Screen.ProviderRequests.route)
                },
                onActiveJobClick = { bookingId ->
                    navController.navigate(Screen.ProviderActiveJob.createRoute(bookingId))
                },
                onViewEarnings = {
                    navController.navigate(Screen.ProviderEarnings.route)
                },
                onViewAvailability = {
                    navController.navigate(Screen.ProviderAvailability.route)
                },
                onViewVerification = {
                    navController.navigate(Screen.ProviderVerification.route)
                },
                onRoleSwitched = { role ->
                    val dest = when (role) {
                        UserRole.CUSTOMER -> Screen.CustomerHome.route
                        UserRole.PROVIDER -> Screen.ProviderDashboard.route
                        UserRole.ADMIN -> Screen.AdminDashboard.route
                    }
                    navController.navigate(dest) {
                        popUpTo(0)
                    }
                },
                onCreateGigClick = {
                    navController.navigate(Screen.CreateGig.route)
                },
                onBrowseClientRequestsClick = {
                    navController.navigate(Screen.JobRequestsBrowse.route)
                }
            )
        }


        // Provider Requests
        composable(Screen.ProviderRequests.route) {
            ProviderIncomingRequestsScreen(
                onNavigateBack = { navController.popBackStack() },
                onJobAccepted = { bookingId ->
                    navController.navigate(Screen.ProviderActiveJob.createRoute(bookingId))
                }
            )
        }

        // Provider Active Job
        composable(
            route = Screen.ProviderActiveJob.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            ProviderActiveJobScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() },
                onOpenChat = { bId ->
                    navController.navigate(Screen.CustomerChat.createRoute(bId))
                },
                onCreateQuotation = { bId ->
                    navController.navigate(Screen.ProviderQuotation.createRoute(bId))
                }
            )
        }

        // Provider Quotation
        composable(
            route = Screen.ProviderQuotation.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            ProviderQuotationScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() },
                onQuotationSubmitted = {
                    navController.popBackStack()
                }
            )
        }

        // Provider Earnings
        composable(Screen.ProviderEarnings.route) {
            ProviderEarningsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Provider Availability
        composable(Screen.ProviderAvailability.route) {
            ProviderAvailabilityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Provider Verification
        composable(Screen.ProviderVerification.route) {
            ProviderVerificationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Admin Dashboard
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onViewVerifications = {
                    navController.navigate(Screen.AdminVerifications.route)
                },
                onViewDisputes = {
                    navController.navigate(Screen.AdminDisputes.route)
                },
                onViewCategories = {
                    navController.navigate(Screen.AdminCategories.route)
                },
                onRoleSwitched = { role ->
                    val dest = when (role) {
                        UserRole.CUSTOMER -> Screen.CustomerHome.route
                        UserRole.PROVIDER -> Screen.ProviderDashboard.route
                        UserRole.ADMIN -> Screen.AdminDashboard.route
                    }
                    navController.navigate(dest) {
                        popUpTo(0)
                    }
                },
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.CustomerHome.route)
                    }
                }
            )
        }

        // Admin Verifications
        composable(Screen.AdminVerifications.route) {
            AdminVerificationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Admin Disputes
        composable(Screen.AdminDisputes.route) {
            AdminDisputesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Admin Categories
        composable(Screen.AdminCategories.route) {
            AdminCategoriesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Freelance Gig Details
        composable(
            route = Screen.GigDetail.route,
            arguments = listOf(navArgument("gigId") { type = NavType.StringType })
        ) { backStackEntry ->
            val gigId = backStackEntry.arguments?.getString("gigId") ?: ""
            GigDetailScreen(
                gigId = gigId,
                onNavigateBack = { navController.popBackStack() },
                onOrderGig = { gig, pkg ->
                    val booking = FixoraRepository.orderGig(gig, pkg)
                    navController.navigate(Screen.CustomerPayment.createRoute(booking.id))
                },
                onContactSeller = { providerId ->
                    navController.navigate(Screen.ProviderProfile.createRoute(providerId))
                }
            )
        }

        // Post Custom Job Request
        composable(Screen.PostJobRequest.route) {
            PostJobRequestScreen(
                onNavigateBack = { navController.popBackStack() },
                onRequestCreated = { _ ->
                    navController.navigate(Screen.JobRequestsBrowse.route)
                }
            )
        }

        // Browse Job Requests & Bids
        composable(Screen.JobRequestsBrowse.route) {
            JobRequestsBrowseScreen(
                onNavigateBack = { navController.popBackStack() },
                onProposalAccepted = { bookingId ->
                    navController.navigate(Screen.CustomerPayment.createRoute(bookingId))
                }
            )
        }

        // Create Freelance Gig
        composable(Screen.CreateGig.route) {
            CreateGigScreen(
                onNavigateBack = { navController.popBackStack() },
                onGigCreated = {
                    navController.popBackStack()
                }
            )
        }
    }
}

