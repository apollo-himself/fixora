# Fixora — On-Demand Home Maintenance & Repair Marketplace (UAE)

> **"Your Home. Fixed. Fast."**

Fixora is a production-oriented, two-sided freelance home maintenance marketplace built for Android. It connects property owners and tenants across the United Arab Emirates (Dubai, Sharjah, Ajman, and Abu Dhabi) with certified independent technicians, contractors, and handymen.

---

## 1. Key Highlights & Architecture

- **Platform & Language:** Android (Kotlin, Jetpack Compose, Material 3, Navigation Compose, Coroutines & Flow).
- **Architecture Pattern:** Clean MVVM (Model-View-ViewModel) with clear domain boundary separation and unidirectional data flow (UDF).
- **Target Market & Localization:**
  - Initial Market: United Arab Emirates (UAE)
  - Currency: UAE Dirham (`AED`)
  - Supported Areas: Dubai Marina, Downtown, Al Barsha, JBR, Business Bay, Deira, Sharjah Al Majaz, Ajman, and Abu Dhabi.
  - Tax Compliance: UAE 5% VAT calculations.
  - Verification: Emirates ID and Department of Economy and Tourism (DET / DED) Freelance Permit.
- **Three Core User Roles:**
  - **Customer:** Discover services, book appointments, track technician en route, in-app messaging, review quotes, settle payments in AED, and rate service.
  - **Independent Freelance Provider:** Toggle online status, accept/decline nearby jobs, navigate via Google Maps, submit inspection quotations, update progress milestones, upload proof photos, and monitor net 85% payouts.
  - **Administrator:** Overview GMV, platform commission revenue, verify technician licenses, resolve disputes, approve refunds, and configure commission rates.
- **Instant Role Switcher:** One-tap demo persona switcher located at the top bar and profile screen allows instant switching between:
  - 👤 **Sarah Al Mansoori** (Customer — Dubai Marina)
  - 🔧 **Rashid Khan** (Certified HVAC Freelancer — 4.9 ★)
  - 🛡️ **Tariq Al Hashemi** (Fixora Operations Admin)

---

## 2. Business & Commission Model

Fixora operates on a 15% marketplace commission structure:
- **Provider Registration:** Free.
- **Customer Account:** Free.
- **Platform Marketplace Commission:** 15% of eligible service amount.
- **Provider Payout:** 85% of eligible service amount.
- **Pass-through Costs:** Parts, replacement materials, and hardware specified in approved quotations pass directly to the technician without commission penalty.
- **Double-Entry Financial Ledger:** Every transaction logs a customer charge, platform commission credit, and provider payable allocation.

---

## 3. Project Structure

```
resilient-maxwell/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fixora/marketplace/
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/           # Data models (User, Booking, Provider, Address, Financials, etc.)
│   │   │   │   │   ├── repository/      # FixoraRepository with reactive StateFlows
│   │   │   │   │   └── demo/            # SeedData (12 UAE providers, catalog, sample bookings)
│   │   │   │   ├── domain/engine/       # PricingCommissionEngine, BookingMatchingEngine, PaymentProcessor
│   │   │   │   ├── ui/
│   │   │   │   │   ├── theme/           # Color, Type, Theme (Fixora Blue & Navy)
│   │   │   │   │   ├── components/      # FixoraTopBar, StatusBadge, StarRatingBar, RoleSwitcherDialog
│   │   │   │   │   ├── navigation/      # FixoraDestinations, FixoraNavGraph
│   │   │   │   │   ├── auth/            # SplashScreen, OnboardingScreen, LoginScreen
│   │   │   │   │   ├── customer/        # CustomerHome, Category, Detail, Form, Matching, Tracking, Payment
│   │   │   │   │   ├── provider/        # Dashboard, Requests, ActiveJob, Quotation, Earnings, Availability
│   │   │   │   │   └── admin/           # AdminDashboard, Verifications, Disputes, Categories
│   │   │   │   ├── FixoraApplication.kt
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/                     # Adaptive launcher icons, themes, strings, xml rules
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/com/fixora/marketplace/
│   │       └── FixoraMarketplaceTests.kt # Automated unit tests
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── firestore.rules                      # Production Firestore security rules
├── firestore.indexes.json               # Firestore compound query indexes
├── firebase.json                        # Firebase deployment configuration
├── .env.example                         # Production credentials & API keys template
├── settings.gradle.kts
├── build.gradle.kts
└── gradlew / gradlew.bat
```

---

## 4. End-to-End Acceptance Test Walkthrough

To verify the complete marketplace lifecycle:

1. **Step 1 (Customer Booking):**
   - Launch app (or switch to Sarah Al Mansoori).
   - On the Home screen, tap **"AC Servicing & Repair"**.
   - Select **"AC Complete Deep Chemical Wash"** (AED 180).
   - In the booking form, confirm location (Princess Tower, Dubai Marina), set urgency to **Standard**, and tap **"Find Technicians"**.
   - The radar screen matches nearby verified technicians (Rashid Khan).
   - Tap **"Select Pro"** to assign Rashid Khan.

2. **Step 2 (Provider Execution):**
   - Tap the top-right persona badge to switch to **Rashid Khan (Provider)**.
   - Open **"Open Job Controls"** on the active assignment.
   - Progress the job through the real-world workflow:
     - Tap **"Start Travel (En Route)"**
     - Tap **"Mark Arrived on Site"**
     - Tap **"Start Work (In Progress)"**
     - *(Optional)* Tap **"Submit Parts/Labor Quotation"** to add replacement parts.
     - Tap **"Complete Job & Submit Photos"**.

3. **Step 3 (Customer Sign-off & Payment):**
   - Switch back to **Sarah Al Mansoori (Customer)**.
   - Tap **"Review & Confirm Completion"**.
   - Inspect the before & after verification photos and the itemized AED invoice.
   - Tap **"Confirm & Pay AED 189"**.
   - Choose **"Fixora Demo Card (•••• 4242)"** or **"Apple Pay / Google Pay"**.
   - Tap **"Authorize & Pay"**.
   - Provide a 5-star rating and comment.

4. **Step 4 (Admin Audit & Ledger):**
   - Switch to **Tariq Al Hashemi (Admin)**.
   - Verify that the gross GMV increased, the 15% platform commission was logged, and the provider payout was credited to the ledger.

---

## 5. Build & Test Instructions

### Running Automated Unit Tests
```bash
./gradlew test
```

### Assembling Debug APK
```bash
./gradlew assembleDebug
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 6. External Services Configuration for Production

When transitioning from Demo Mode to Production:
1. **Firebase Authentication:**
   - Enable Phone Authentication under Firebase Console -> Authentication -> Sign-in method.
   - Add your SHA-1 / SHA-256 fingerprints to Project Settings.
2. **Cloud Firestore:**
   - Deploy `firestore.rules` using Firebase CLI:
     ```bash
     firebase deploy --only firestore:rules
     ```
3. **Stripe / UAE Payment Gateway:**
   - Inject live keys via environment variables or encrypted secrets in `local.properties`.
   - Setup webhook listener for payment intent authorization and refund settlements.
4. **Google Maps Platform:**
   - Generate an Android API Key on Google Cloud Console with Maps SDK for Android and Geocoding API enabled.
