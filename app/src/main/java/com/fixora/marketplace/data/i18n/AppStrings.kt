package com.fixora.marketplace.data.i18n

interface AppStrings {
    val language: AppLanguage
    val isRtl: Boolean get() = language.isRtl

    // Common / Branding
    val appName: String
    val appTagline: String
    val open: String
    val cancel: String
    val save: String
    val continueText: String
    val done: String
    val ok: String
    val close: String

    // Top Bar / Greeting
    fun greeting(name: String): String
    val heroSubtitle: String
    val searchPlaceholder: String

    // Bottom Navigation
    val navHome: String
    val navBookings: String
    val navMessages: String
    val navProfile: String

    // Quick Actions
    val quickActions: String
    val emergency: String
    val postAJob: String
    val viewBids: String

    // Categories
    val categories: String
    val catAll: String
    val catAc: String
    val catElectrical: String
    val catPlumbing: String
    val catHandyman: String
    val catPainting: String
    val catCarpentry: String
    val catSmartHome: String

    // Home Sections
    val featuredServices: String
    val searchResults: String
    fun searchResultsFor(query: String): String
    val topTechnicians: String
    val matchingTechnicians: String
    val servicesAndGigs: String
    fun fromPrice(price: Int): String
    fun hourlyRate(price: Int): String
    val online: String
    fun jobsCompleted(count: Int): String
    val noGigsFound: String
    val noResultsFound: String
    val tryDifferentSearch: String
    fun resultsCount(count: Int): String

    // Gig Details & Booking
    val gigDetailsTitle: String
    fun orderGigBtn(price: Int): String
    val packagePriceLabel: String
    val whatsIncluded: String
    fun estimatedTime(mins: Int): String
    val deliveryAddressTitle: String
    val deliveryAddressPrompt: String
    val houseStreetLabel: String
    val houseStreetPlaceholder: String
    val areaLabel: String
    val areaPlaceholder: String
    val cityLabel: String
    val cityPlaceholder: String
    val saveAndContinue: String

    // Auth & Login
    val welcomeBack: String
    val createYourProfile: String
    val welcomeSubtitleRegister: String
    val welcomeSubtitleLogin: String
    val createAccountTab: String
    val signInTab: String
    val joinAs: String
    val customerRole: String
    val customerRoleDesc: String
    val freelancerRole: String
    val freelancerRoleDesc: String
    val customerDetails: String
    val freelancerProfileTrade: String
    val fullNameLabel: String
    val emailAddressLabel: String
    val continueWithGoogle: String
    val connectingToGoogle: String
    val orRegisterManually: String
    val orDivider: String
    val signInWithEmail: String
    val signInDesc: String
    val signInBtn: String
    val yourTradesSkills: String
    val hourlyRateInputLabel: String
    val bioLabel: String
    val bioPlaceholder: String
    val createAccountBtn: String
    val registerFreelancerBtn: String
    val dataEncryptionNote: String

    // Verification & Settings
    val verifyEmailTitle: String
    val selectLanguage: String
    val languageUrdu: String
    val languageEnglish: String
    val languageArabic: String
    val changeLanguage: String
    val adminPanel: String
    val adminSubtitle: String

    // Profile & Account
    val editProfileTitle: String
    val saveChanges: String
    val verifiedAccount: String
    val pendingVerification: String
    val customerAccount: String
    val freelancerAccount: String
    val customerAccountSubtitle: String
    val freelancerAccountSubtitle: String
    val savedAddresses: String
    val addAddress: String
    val noSavedAddresses: String
    val activity: String
    val myBookings: String
    val myBookingsSubtitle: String
    val myReviews: String
    val myReviewsSubtitle: String
    val supportAndLegal: String
    val helpCenter: String
    val submitComplaint: String
    val privacyPolicy: String
    val consumerProtection: String
    val signOut: String
    val signOutConfirmTitle: String
    val signOutConfirmMessage: String
    val adminPortalTitle: String
    val adminPortalSubtitle: String

    // Bookings
    fun activeBookingsTab(count: Int): String
    fun historyBookingsTab(count: Int): String
    val noActiveBookings: String
    val noPastBookings: String
    val exploreServicesPrompt: String

    // Post Job
    val postJobScreenTitle: String
    val postJobScreenSubtitle: String
    val postJobSubmitBtn: String
}

object EnglishStrings : AppStrings {
    override val language: AppLanguage = AppLanguage.ENGLISH

    override val appName = "FIXORA"
    override val appTagline = "Your Home. Fixed. Fast."
    override val open = "Open"
    override val cancel = "Cancel"
    override val save = "Save"
    override val continueText = "Continue"
    override val done = "Done"
    override val ok = "OK"
    override val close = "Close"

    override fun greeting(name: String) = "Good day, $name 👋"
    override val heroSubtitle = "Find and offer services worldwide"
    override val searchPlaceholder = "Search AC repair, electrician, plumber.."

    override val navHome = "Home"
    override val navBookings = "Bookings"
    override val navMessages = "Messages"
    override val navProfile = "Profile"

    override val quickActions = "Quick Actions"
    override val emergency = "Emergency"
    override val postAJob = "Post a Job"
    override val viewBids = "View Bids"

    override val categories = "Categories"
    override val catAll = "All"
    override val catAc = "AC & Cooling"
    override val catElectrical = "Electrician"
    override val catPlumbing = "Plumbing"
    override val catHandyman = "Handyman"
    override val catPainting = "Painting"
    override val catCarpentry = "Carpentry"
    override val catSmartHome = "Smart Home & CCTV"

    override val featuredServices = "Featured Services"
    override val searchResults = "Search Results"
    override fun searchResultsFor(query: String) = "Search Results for \"$query\""
    override val topTechnicians = "Top Technicians"
    override val matchingTechnicians = "Matching Technicians"
    override val servicesAndGigs = "Services & Gigs"
    override fun fromPrice(price: Int) = "From PKR $price"
    override fun hourlyRate(price: Int) = "PKR $price/hr"
    override val online = "Online"
    override fun jobsCompleted(count: Int) = "$count jobs"
    override val noGigsFound = "No gigs found"
    override val noResultsFound = "No results found"
    override val tryDifferentSearch = "Try a different search term or category"
    override fun resultsCount(count: Int) = "$count results"

    override val gigDetailsTitle = "Freelance Gig Details"
    override fun orderGigBtn(price: Int) = "Order Gig (PKR $price)"
    override val packagePriceLabel = "Package Price"
    override val whatsIncluded = "What's Included in this Package:"
    override fun estimatedTime(mins: Int) = "$mins mins estimated work time"
    override val deliveryAddressTitle = "Delivery / Service Address"
    override val deliveryAddressPrompt = "Please enter your address so the specialist knows where to deliver or perform the service."
    override val houseStreetLabel = "House / Street / Building *"
    override val houseStreetPlaceholder = "e.g. House 42, Street 10"
    override val areaLabel = "Area / Neighborhood"
    override val areaPlaceholder = "e.g. Downtown"
    override val cityLabel = "City"
    override val cityPlaceholder = "e.g. Lahore, Karachi, Islamabad"
    override val saveAndContinue = "Save & Continue to Order"

    override val welcomeBack = "Welcome Back 👋"
    override val createYourProfile = "Create Your Profile 🚀"
    override val welcomeSubtitleRegister = "Find skilled professionals or offer your services."
    override val welcomeSubtitleLogin = "Sign in with your email address or Google to continue."
    override val createAccountTab = "Create Account"
    override val signInTab = "Sign In"
    override val joinAs = "I want to join Fixora as:"
    override val customerRole = "Customer"
    override val customerRoleDesc = "Hire certified freelance pros for home repairs"
    override val freelancerRole = "Freelancer"
    override val freelancerRoleDesc = "Offer services & earn 85% of every job"
    override val customerDetails = "Customer Details"
    override val freelancerProfileTrade = "Freelancer Profile & Trade"
    override val fullNameLabel = "Full Name *"
    override val emailAddressLabel = "Email Address *"
    override val continueWithGoogle = "Continue with Google"
    override val connectingToGoogle = "Connecting to Google..."
    override val orRegisterManually = "or register manually"
    override val orDivider = "or"
    override val signInWithEmail = "Sign In with Email"
    override val signInDesc = "Enter your registered email address to access your Fixora account."
    override val signInBtn = "Sign In"
    override val yourTradesSkills = "Your Trades / Skills (Select all that apply):"
    override val hourlyRateInputLabel = "Hourly Labor Rate (PKR)"
    override val bioLabel = "Bio / Experience (Optional)"
    override val bioPlaceholder = "e.g. 8 years experienced AC technician, inverter repair"
    override val createAccountBtn = "Create Account & Continue"
    override val registerFreelancerBtn = "Register Freelancer Profile"
    override val dataEncryptionNote = "See the privacy policy for how your data is handled."

    override val verifyEmailTitle = "Verify Your Email"
    override val selectLanguage = "Select Language"
    override val languageUrdu = "اردو (Urdu)"
    override val languageEnglish = "English"
    override val languageArabic = "العربية (Arabic)"
    override val changeLanguage = "Language / زبان / اللغة"
    override val adminPanel = "Admin Control Panel"
    override val adminSubtitle = "Process claims & tickets"

    // Profile & Account
    override val editProfileTitle = "Edit Profile"
    override val saveChanges = "Save Changes"
    override val verifiedAccount = "Verified Account"
    override val pendingVerification = "Pending Verification"
    override val customerAccount = "Customer Account"
    override val freelancerAccount = "Freelancer Account"
    override val customerAccountSubtitle = "Find verified professionals worldwide"
    override val freelancerAccountSubtitle = "You receive job offers & earn 85% per booking"
    override val savedAddresses = "Saved Addresses"
    override val addAddress = "Add Address"
    override val noSavedAddresses = "No saved addresses yet"
    override val activity = "Activity"
    override val myBookings = "My Bookings"
    override val myBookingsSubtitle = "View all your service requests"
    override val myReviews = "My Reviews"
    override val myReviewsSubtitle = "Ratings you've left for technicians"
    override val supportAndLegal = "Support & Legal"
    override val helpCenter = "Help Center & FAQs"
    override val submitComplaint = "Submit a Complaint"
    override val privacyPolicy = "Privacy Policy & Terms"
    override val consumerProtection = "Consumer protection"
    override val signOut = "Sign Out"
    override val signOutConfirmTitle = "Sign Out"
    override val signOutConfirmMessage = "Are you sure you want to sign out of Fixora?"
    override val adminPortalTitle = "Administrator Controls"
    override val adminPortalSubtitle = "Process disputes, verifications & financial ledger"

    // Bookings
    override fun activeBookingsTab(count: Int) = "Active ($count)"
    override fun historyBookingsTab(count: Int) = "History ($count)"
    override val noActiveBookings = "No active service bookings"
    override val noPastBookings = "No past bookings recorded"
    override val exploreServicesPrompt = "Explore services on the home screen to book an expert."

    // Post Job
    override val postJobScreenTitle = "Post a Custom Job Request"
    override val postJobScreenSubtitle = "Receive bids from independent freelancers"
    override val postJobSubmitBtn = "Post Job & Receive Bids"
}

object UrduStrings : AppStrings {
    override val language: AppLanguage = AppLanguage.URDU

    override val appName = "فکسورا"
    override val appTagline = "آپ کا گھر۔ بروقت مرمت۔"
    override val open = "کھولیں"
    override val cancel = "منسوخ"
    override val save = "محفوظ کریں"
    override val continueText = "جاری رکھیں"
    override val done = "مکمل"
    override val ok = "ٹھیک ہے"
    override val close = "بند کریں"

    override fun greeting(name: String) = "السلام علیکم، $name 👋"
    override val heroSubtitle = "دنیا بھر میں خدمات تلاش کریں یا فراہم کریں"
    override val searchPlaceholder = "اے سی، الیکٹریشن، پلمبر تلاش کریں.."

    override val navHome = "ہوم"
    override val navBookings = "بکنگز"
    override val navMessages = "پیغامات"
    override val navProfile = "پروفائل"

    override val quickActions = "فوری اقدامات"
    override val emergency = "ہنگامی"
    override val postAJob = "کام پوسٹ کریں"
    override val viewBids = "بولیاں دیکھیں"

    override val categories = "شعبہ جات"
    override val catAll = "تمام"
    override val catAc = "اے سی اور کولنگ"
    override val catElectrical = "الیکٹریشن"
    override val catPlumbing = "پلمبنگ"
    override val catHandyman = "ہینڈی مین"
    override val catPainting = "پینٹنگ"
    override val catCarpentry = "بڑھئی کا کام"
    override val catSmartHome = "سمارٹ ہوم اور کیمرے"

    override val featuredServices = "نمایاں خدمات"
    override val searchResults = "تلاش کے نتائج"
    override fun searchResultsFor(query: String) = "تلاش کے نتائج برائے: \"$query\""
    override val topTechnicians = "بہترین ٹیکنیشنز"
    override val matchingTechnicians = "متعلقہ ٹیکنیشنز"
    override val servicesAndGigs = "خدمات اور گیگز"
    override fun fromPrice(price: Int) = "کم از کم $price روپے"
    override fun hourlyRate(price: Int) = "$price روپے/گھنٹہ"
    override val online = "آن لائن"
    override fun jobsCompleted(count: Int) = "$count مکمل کام"
    override val noGigsFound = "کوئی سروس نہیں ملی"
    override val noResultsFound = "کوئی نتیجہ نہیں ملا"
    override val tryDifferentSearch = "کوئی دوسرا لفظ یا شعبہ منتخب کریں"
    override fun resultsCount(count: Int) = "$count نتائج"

    override val gigDetailsTitle = "سروس کی تفصیلات"
    override fun orderGigBtn(price: Int) = "آرڈر کریں ($price روپے)"
    override val packagePriceLabel = "پیکیج کی قیمت"
    override val whatsIncluded = "اس پیکیج میں کیا شامل ہے:"
    override fun estimatedTime(mins: Int) = "$mins منٹ متوقع وقت"
    override val deliveryAddressTitle = "سروس / ڈیلیوری کا پتہ"
    override val deliveryAddressPrompt = "براہ کرم اپنا پتہ درج کریں تاکہ کاریگر آپ کے مطلوبہ مقام پر پہنچ سکے۔"
    override val houseStreetLabel = "مکان نمبر / گلی / عمارت *"
    override val houseStreetPlaceholder = "مثلاً مکان 42، گلی 10"
    override val areaLabel = "علاقہ / محلہ"
    override val areaPlaceholder = "مثلاً شہر کا مرکز"
    override val cityLabel = "شہر"
    override val cityPlaceholder = "مثلاً لاہور، کراچی، اسلام آباد"
    override val saveAndContinue = "محفوظ کریں اور آرڈر جاری رکھیں"

    override val welcomeBack = "خوش آمدید 👋"
    override val createYourProfile = "اپنا پروفائل بنائیں 🚀"
    override val welcomeSubtitleRegister = "ماہرین تلاش کریں یا اپنی خدمات پیش کریں۔"
    override val welcomeSubtitleLogin = "جاری رکھنے کے لیے ای میل یا گوگل کے ذریعے سائن ان کریں۔"
    override val createAccountTab = "اکاؤنٹ بنائیں"
    override val signInTab = "سائن ان"
    override val joinAs = "میں فکسورا میں شامل ہونا چاہتا ہوں بطور:"
    override val customerRole = "گاہک (کسٹمر)"
    override val customerRoleDesc = "گھر کی مرمت کے لیے تصدیق شدہ کاریگر تلاش کریں"
    override val freelancerRole = "کاریگر (فری لانسر)"
    override val freelancerRoleDesc = "اپنی خدمات فراہم کریں اور ہر کام کا 85٪ کمائیں"
    override val customerDetails = "کسٹمر کی تفصیلات"
    override val freelancerProfileTrade = "کاریگر کا پروفائل اور ہنر"
    override val fullNameLabel = "پورا نام *"
    override val emailAddressLabel = "ای میل ایڈریس *"
    override val continueWithGoogle = "گوگل کے ساتھ جاری رکھیں"
    override val connectingToGoogle = "گوگل سے رابطہ ہو رہا ہے..."
    override val orRegisterManually = "یا دستی طور پر رجسٹر کریں"
    override val orDivider = "یا"
    override val signInWithEmail = "ای میل کے ساتھ سائن ان کریں"
    override val signInDesc = "اپنے فکسورا اکاؤنٹ تک رسائی کے لیے رجسٹرڈ ای میل درج کریں۔"
    override val signInBtn = "سائن ان کریں"
    override val yourTradesSkills = "آپ کی مہارتیں (تمام متعلقہ منتخب کریں):"
    override val hourlyRateInputLabel = "فی گھنٹہ اجرت (روپے)"
    override val bioLabel = "تجربہ / تعارف (اختیاری)"
    override val bioPlaceholder = "مثلاً 8 سالہ تجربہ کار اے سی ٹیکنیشن، انورٹر مرمت کا ماہر"
    override val createAccountBtn = "اکاؤنٹ بنائیں اور آگے بڑھیں"
    override val registerFreelancerBtn = "کاریگر کا پروفائل رجسٹر کریں"
    override val dataEncryptionNote = "اپنے ڈیٹا سے متعلق معلومات کے لیے رازداری کی پالیسی دیکھیں۔"

    override val verifyEmailTitle = "ای میل کی تصدیق کریں"
    override val selectLanguage = "زبان منتخب کریں"
    override val languageUrdu = "اردو"
    override val languageEnglish = "English"
    override val languageArabic = "العربية"
    override val changeLanguage = "زبان تبدیل کریں / Change Language"
    override val adminPanel = "ایڈمن کنٹرول پینل"
    override val adminSubtitle = "ٹکٹ اور کلیمز پراسیس کریں"

    // Profile & Account
    override val editProfileTitle = "پروفائل میں ترمیم"
    override val saveChanges = "تبدیلیاں محفوظ کریں"
    override val verifiedAccount = "تصدیق شدہ اکاؤنٹ"
    override val pendingVerification = "تصدیق زیر التواء"
    override val customerAccount = "کسٹمر اکاؤنٹ"
    override val freelancerAccount = "کاریگر اکاؤنٹ"
    override val customerAccountSubtitle = "دنیا بھر میں تصدیق شدہ ماہرین تلاش کریں"
    override val freelancerAccountSubtitle = "آپ کو کام کی پیشکشیں ملتی ہیں اور فی بکنگ 85٪ کمائی ہوتی ہے"
    override val savedAddresses = "محفوظ شدہ پتے"
    override val addAddress = "پتہ شامل کریں"
    override val noSavedAddresses = "ابھی کوئی پتہ محفوظ نہیں ہے"
    override val activity = "سرگرمیاں"
    override val myBookings = "میری بکنگز"
    override val myBookingsSubtitle = "اپنی تمام سروس درخواستیں دیکھیں"
    override val myReviews = "میرے جائزے اور آراء"
    override val myReviewsSubtitle = "وہ ریٹنگز جو آپ نے کاریگروں کو دی ہیں"
    override val supportAndLegal = "مدد اور قانونی معلومات"
    override val helpCenter = "مدد مرکز اور عمومی سوالات"
    override val submitComplaint = "شکایت درج کریں"
    override val privacyPolicy = "رازداری کی پالیسی اور شرائط"
    override val consumerProtection = "صارفین کے حقوق کا تحفظ"
    override val signOut = "لاگ آؤٹ"
    override val signOutConfirmTitle = "لاگ آؤٹ کی تصدیق"
    override val signOutConfirmMessage = "کیا آپ واقعی فکسورا سے لاگ آؤٹ کرنا چاہتے ہیں؟"
    override val adminPortalTitle = "ایڈمنسٹریٹر کنٹرولز"
    override val adminPortalSubtitle = "تنازعات، تصدیق اور مالی معاملات کا نظم کریں"

    // Bookings
    override fun activeBookingsTab(count: Int) = "فعال ($count)"
    override fun historyBookingsTab(count: Int) = "سابقہ ریکارڈ ($count)"
    override val noActiveBookings = "کوئی فعال بکنگ نہیں ہے"
    override val noPastBookings = "کوئی سابقہ بکنگ ریکارڈ موجود نہیں ہے"
    override val exploreServicesPrompt = "ماہرین کو بک کرنے کے لیے ہوم اسکرین پر خدمات دیکھیں۔"

    // Post Job
    override val postJobScreenTitle = "کسٹم کام کی درخواست پوسٹ کریں"
    override val postJobScreenSubtitle = "آزاد کاریگروں سے پیشکشیں اور بولیاں حاصل کریں"
    override val postJobSubmitBtn = "کام پوسٹ کریں اور بولیاں وصول کریں"
}

object ArabicStrings : AppStrings {
    override val language: AppLanguage = AppLanguage.ARABIC

    override val appName = "فيكسورا"
    override val appTagline = "منزلك. صيانة سريعة ومضمونة."
    override val open = "فتح"
    override val cancel = "إلغاء"
    override val save = "حفظ"
    override val continueText = "متابعة"
    override val done = "تم"
    override val ok = "حسناً"
    override val close = "إغلاق"

    override fun greeting(name: String) = "مرحباً، $name 👋"
    override val heroSubtitle = "المنصة الرائدة لخدمات وصيانة المنازل"
    override val searchPlaceholder = "ابحث عن صيانة التكييف، كهربائي، سباك.."

    override val navHome = "الرئيسية"
    override val navBookings = "الحجوزات"
    override val navMessages = "الرسائل"
    override val navProfile = "الملف الشخصي"

    override val quickActions = "إجراءات سريعة"
    override val emergency = "طوارئ"
    override val postAJob = "طلب خدمة"
    override val viewBids = "عرض العروض"

    override val categories = "الفئات"
    override val catAll = "الكل"
    override val catAc = "تكييف وتبريد"
    override val catElectrical = "كهربائي"
    override val catPlumbing = "سباكة"
    override val catHandyman = "فني صيانة عامة"
    override val catPainting = "دهان وديكور"
    override val catCarpentry = "نجارة"
    override val catSmartHome = "منزل ذكي وكاميرات"

    override val featuredServices = "الخدمات المميزة"
    override val searchResults = "نتائج البحث"
    override fun searchResultsFor(query: String) = "نتائج البحث عن: \"$query\""
    override val topTechnicians = "أفضل الفنيين المعتمدين"
    override val matchingTechnicians = "الفنيون المطابقون"
    override val servicesAndGigs = "الخدمات المتاحة"
    override fun fromPrice(price: Int) = "ابتداءً من $price روبية"
    override fun hourlyRate(price: Int) = "$price روبية/ساعة"
    override val online = "متصل"
    override fun jobsCompleted(count: Int) = "$count مهمة منجزة"
    override val noGigsFound = "لم يتم العثور على خدمات"
    override val noResultsFound = "لا توجد نتائج مطابقة"
    override val tryDifferentSearch = "يرجى تجربة كلمة بحث أخرى أو تصفح فئة مختلفة"
    override fun resultsCount(count: Int) = "$count نتيجة"

    override val gigDetailsTitle = "تفاصيل الخدمة"
    override fun orderGigBtn(price: Int) = "طلب الخدمة ($price روبية)"
    override val packagePriceLabel = "سعر الباقة"
    override val whatsIncluded = "ما تتضمنه هذه الباقة:"
    override fun estimatedTime(mins: Int) = "$mins دقيقة وقت تقديري للإنجاز"
    override val deliveryAddressTitle = "عنوان تقديم الخدمة"
    override val deliveryAddressPrompt = "يرجى إدخال عنوانك ليتمكن الفني المختص من الحضور إلى موقعك بدقة."
    override val houseStreetLabel = "المنزل / الشارع / المبنى *"
    override val houseStreetPlaceholder = "مثال: مبنى 42، شارع 10"
    override val areaLabel = "المنطقة / الحي"
    override val areaPlaceholder = "مثال: وسط المدينة"
    override val cityLabel = "المدينة"
    override val cityPlaceholder = "مثال: لاهور، كراتشي، إسلام آباد"
    override val saveAndContinue = "حفظ ومتابعة الطلب"

    override val welcomeBack = "مرحباً بعودتك 👋"
    override val createYourProfile = "أنشئ حسابك الجديد 🚀"
    override val welcomeSubtitleRegister = "انضم إلى آلاف العملاء والفنيين المحترفين والمعتمدين."
    override val welcomeSubtitleLogin = "سجل الدخول عبر بريدك الإلكتروني أو Google للمتابعة."
    override val createAccountTab = "إنشاء حساب"
    override val signInTab = "تسجيل الدخول"
    override val joinAs = "أرغب في الانضمام إلى فيكسورا كـ:"
    override val customerRole = "عميل"
    override val customerRoleDesc = "اطلب فنيين معتمدين لصيانة وإصلاح منزلك"
    override val freelancerRole = "مقدم خدمة (مستقل)"
    override val freelancerRoleDesc = "قدم خدماتك واكسب 85% من كل طلب تنجزه"
    override val customerDetails = "بيانات العميل"
    override val freelancerProfileTrade = "ملف الفني والتخصص"
    override val fullNameLabel = "الاسم الكامل *"
    override val emailAddressLabel = "البريد الإلكتروني *"
    override val continueWithGoogle = "المتابعة باستخدام Google"
    override val connectingToGoogle = "جارٍ الاتصال بـ Google..."
    override val orRegisterManually = "أو التسجيل يدوياً"
    override val orDivider = "أو"
    override val signInWithEmail = "الدخول عبر البريد الإلكتروني"
    override val signInDesc = "أدخل بريدك الإلكتروني المسجل للوصول إلى حسابك في فيكسورا."
    override val signInBtn = "تسجيل الدخول"
    override val yourTradesSkills = "مجالات اختصاصك ومهاراتك (اختر ما ينطبق):"
    override val hourlyRateInputLabel = "أجر الساعة التقريبي (روبية)"
    override val bioLabel = "نبذة تعريفية / الخبرة (اختياري)"
    override val bioPlaceholder = "مثال: خبرة 8 سنوات في صيانة أجهزة التكييف والإنفرتر"
    override val createAccountBtn = "إنشاء الحساب والمتابعة"
    override val registerFreelancerBtn = "تسجيل ملف مقدم الخدمة"
    override val dataEncryptionNote = "بياناتك مشفرة ومحمية بموجب معايير حماية البيانات"

    override val verifyEmailTitle = "التحقق من البريد الإلكتروني"
    override val selectLanguage = "اختر اللغة"
    override val languageUrdu = "اردو (Urdu)"
    override val languageEnglish = "English"
    override val languageArabic = "العربية"
    override val changeLanguage = "تغيير اللغة / Change Language"
    override val adminPanel = "لوحة تحكم الإدارة"
    override val adminSubtitle = "معالجة التذاكر والشكاوى"

    // Profile & Account
    override val editProfileTitle = "تعديل الملف الشخصي"
    override val saveChanges = "حفظ التعديلات"
    override val verifiedAccount = "حساب موثق"
    override val pendingVerification = "قيد التوثيق"
    override val customerAccount = "حساب العميل"
    override val freelancerAccount = "حساب مقدم الخدمة"
    override val customerAccountSubtitle = "اعثر على محترفين معتمدين حول العالم"
    override val freelancerAccountSubtitle = "تتلقى عروض العمل وتربح 85% من كل طلب تنجزه"
    override val savedAddresses = "العناوين المحفوظة"
    override val addAddress = "إضافة عنوان"
    override val noSavedAddresses = "لا توجد عناوين محفوظة بعد"
    override val activity = "النشاط"
    override val myBookings = "حجوزاتي"
    override val myBookingsSubtitle = "عرض جميع طلبات الخدمة الخاصة بك"
    override val myReviews = "تقييماتي"
    override val myReviewsSubtitle = "التقييمات التي تركتها للفنيين"
    override val supportAndLegal = "الدعم والشروط القانونية"
    override val helpCenter = "مركز المساعدة والأسئلة الشائعة"
    override val submitComplaint = "تقديم شكوى"
    override val privacyPolicy = "سياسة الخصوصية والشروط"
    override val consumerProtection = "حماية حقوق المستهلك"
    override val signOut = "تسجيل الخروج"
    override val signOutConfirmTitle = "تأكيد تسجيل الخروج"
    override val signOutConfirmMessage = "هل أنت متأكد من رغبتك في تسجيل الخروج من فيكسورا؟"
    override val adminPortalTitle = "لوحة تحكم المسؤول"
    override val adminPortalSubtitle = "معالجة النزاعات والتوثيق والسجل المالي"

    // Bookings
    override fun activeBookingsTab(count: Int) = "النشطة ($count)"
    override fun historyBookingsTab(count: Int) = "السجل السابق ($count)"
    override val noActiveBookings = "لا توجد حجوزات نشطة حالياً"
    override val noPastBookings = "لا يوجد سجل حجوزات سابقة"
    override val exploreServicesPrompt = "تصفح الخدمات على الشاشة الرئيسية لحجز فني مختص."

    // Post Job
    override val postJobScreenTitle = "نشر طلب خدمة مخصص"
    override val postJobScreenSubtitle = "استقبل العروض والأسعار من الفنيين المستقلين"
    override val postJobSubmitBtn = "نشر الطلب واستقبال العروض"
}

fun getStrings(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishStrings
        AppLanguage.URDU -> UrduStrings
        AppLanguage.ARABIC -> ArabicStrings
    }
}
