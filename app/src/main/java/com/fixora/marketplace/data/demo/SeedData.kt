package com.fixora.marketplace.data.demo

import com.fixora.marketplace.data.model.*

object SeedData {

    val demoUsers = listOf(
        User(
            id = "USR-CUST-01",
            name = "Ayesha Khan",
            email = "ayesha.khan@example.pk",
            phone = "+92 300 123 4567",
            role = UserRole.CUSTOMER,
            isVerified = true
        ),
        User(
            id = "USR-PROV-01",
            name = "Rashid Khan",
            email = "rashid.cooling@example.pk",
            phone = "+92 321 987 6543",
            role = UserRole.PROVIDER,
            isVerified = true
        ),
        User(
            id = "USR-PROV-02",
            name = "Muhammad Tariq",
            email = "tariq.electrician@example.pk",
            phone = "+92 312 456 7890",
            role = UserRole.PROVIDER,
            isVerified = true
        ),
        User(
            id = "USR-PROV-03",
            name = "Kamran Ashraf",
            email = "kamran.plumbing@example.pk",
            phone = "+92 333 789 1234",
            role = UserRole.PROVIDER,
            isVerified = true
        ),
        User(
            id = "USR-PROV-04",
            name = "Bilal Ahmed",
            email = "bilal.handyman@example.pk",
            phone = "+92 301 555 4321",
            role = UserRole.PROVIDER,
            isVerified = true
        ),
        User(
            id = "USR-PROV-05",
            name = "Zahid Hussain",
            email = "zahid.paints@example.pk",
            phone = "+92 345 678 9012",
            role = UserRole.PROVIDER,
            isVerified = true
        ),
        User(
            id = "USR-PROV-06",
            name = "Umer Farooq",
            email = "umer.carpenter@example.pk",
            phone = "+92 322 345 6789",
            role = UserRole.PROVIDER,
            isVerified = true
        ),
        User(
            id = "USR-PROV-07",
            name = "Sajid Ali",
            email = "sajid.smarthome@example.pk",
            phone = "+92 315 901 2345",
            role = UserRole.PROVIDER,
            isVerified = true
        ),
        User(
            id = "USR-ADMIN-01",
            name = "Hamza Malik",
            email = "ops.admin@fixora.pk",
            phone = "+92 42 111 FIXORA",
            role = UserRole.ADMIN,
            isVerified = true
        )
    )

    val demoAddresses = listOf(
        Address(
            id = "ADDR-01",
            label = "Home - DHA Phase 5",
            street = "Sector G, Street 14",
            buildingOrVilla = "House 42",
            apartmentNo = "Ground Floor",
            emirate = "Lahore",
            area = "DHA Phase 5",
            latitude = 31.4697,
            longitude = 74.4084,
            isDefault = true
        ),
        Address(
            id = "ADDR-02",
            label = "Office - Gulberg III",
            street = "Main Boulevard",
            buildingOrVilla = "Pace Tower",
            apartmentNo = "Suite 405",
            emirate = "Lahore",
            area = "Gulberg III",
            latitude = 31.5165,
            longitude = 74.3486,
            isDefault = false
        ),
        Address(
            id = "ADDR-03",
            label = "Karachi Residence",
            street = "Khayaban-e-Shahbaz",
            buildingOrVilla = "Ocean Breeze Heights",
            apartmentNo = "Flat 4B",
            emirate = "Karachi",
            area = "Clifton",
            latitude = 24.8138,
            longitude = 67.0298,
            isDefault = false
        )
    )

    val serviceCategories = listOf(
        // ── 24 Distinct Industry Categories ───────────────────────────
        // 1. Electrical
        ServiceCategory(
            id = "ELECTRICAL",
            name = "Electrical",
            description = "Wiring, lighting, installation, repairs, breaker panels and safety earthing.",
            iconName = "bolt",
            startingPriceAed = 1500.0,
            popularTag = "Essential",
            subSkills = listOf("Wiring", "Lighting", "Installation", "Repairs", "Circuit Breakers", "Solar Inverters"),
            serviceGroup = "Local Services"
        ),
        // 2. Plumbing
        ServiceCategory(
            id = "PLUMBING",
            name = "Plumbing",
            description = "Leak repairs, fixtures, drainage, sanitary installations and pipe welding.",
            iconName = "plumbing",
            startingPriceAed = 1800.0,
            popularTag = "Emergency Available",
            subSkills = listOf("Leak Repairs", "Fixtures", "Drainage", "Pipe Fitting", "Water Tanks", "Geysers"),
            serviceGroup = "Local Services"
        ),
        // 3. HVAC
        ServiceCategory(
            id = "HVAC",
            name = "HVAC",
            description = "AC installation, servicing, repairs, duct cleaning and gas recharge.",
            iconName = "ac_unit",
            startingPriceAed = 2500.0,
            popularTag = "High Demand",
            subSkills = listOf("AC Installation", "Servicing", "Repairs", "Gas Refill", "Duct Cleaning", "Compressor Diagnostics"),
            serviceGroup = "Local Services"
        ),
        // 4. Carpentry
        ServiceCategory(
            id = "CARPENTRY",
            name = "Carpentry",
            description = "Furniture, cabinets, doors, locks, shelving and custom woodwork.",
            iconName = "handyman",
            startingPriceAed = 2000.0,
            subSkills = listOf("Furniture", "Cabinets", "Doors", "Wood Polish", "Lock Installation", "Custom Shelves"),
            serviceGroup = "Local Services"
        ),
        // 5. Construction
        ServiceCategory(
            id = "CONSTRUCTION",
            name = "Construction",
            description = "Masonry, tiling, renovation, wall plastering and structural remodeling.",
            iconName = "foundation",
            startingPriceAed = 5000.0,
            popularTag = "Contractors",
            subSkills = listOf("Masonry", "Tiling", "Renovation", "Plastering", "Waterproofing", "Flooring"),
            serviceGroup = "Local Services"
        ),
        // 6. Cleaning
        ServiceCategory(
            id = "CLEANING",
            name = "Cleaning",
            description = "Residential, office, deep cleaning, sofa and carpet shampooing.",
            iconName = "cleaning_services",
            startingPriceAed = 2500.0,
            popularTag = "Verified Pros",
            subSkills = listOf("Residential", "Office", "Deep Cleaning", "Sofa Cleaning", "Carpet Shampooing", "Steam Sanitization"),
            serviceGroup = "Local Services"
        ),
        // 7. Gardening
        ServiceCategory(
            id = "GARDENING",
            name = "Gardening",
            description = "Landscaping, lawn maintenance, tree trimming and irrigation systems.",
            iconName = "yard",
            startingPriceAed = 1800.0,
            subSkills = listOf("Landscaping", "Lawn Maintenance", "Tree Trimming", "Grass Seeding", "Irrigation Setup", "Plant Care"),
            serviceGroup = "Local Services"
        ),
        // 8. Automotive
        ServiceCategory(
            id = "AUTOMOTIVE",
            name = "Automotive",
            description = "Mechanics, detailing, roadside assistance, computer scanning and battery boost.",
            iconName = "directions_car",
            startingPriceAed = 2000.0,
            popularTag = "Roadside 24/7",
            subSkills = listOf("Mechanics", "Detailing", "Roadside Assistance", "OBD2 Diagnostics", "Battery Boost", "Brake Pads"),
            serviceGroup = "Local Services"
        ),
        // 9. Moving
        ServiceCategory(
            id = "MOVING",
            name = "Moving",
            description = "Packing, furniture moving, relocation, loading and assembly.",
            iconName = "local_shipping",
            startingPriceAed = 4000.0,
            subSkills = listOf("Packing", "Furniture Moving", "Relocation", "Bubble Wrap Protection", "Loading & Unloading"),
            serviceGroup = "Local Services"
        ),
        // 10. Delivery
        ServiceCategory(
            id = "DELIVERY",
            name = "Delivery",
            description = "Courier, local delivery, parcel dispatch and document logistics.",
            iconName = "moped",
            startingPriceAed = 500.0,
            popularTag = "Express Courier",
            subSkills = listOf("Courier", "Local Delivery", "Express Dispatch", "Document Delivery", "Bulk Parcels"),
            serviceGroup = "Local Services"
        ),
        // 11. Beauty
        ServiceCategory(
            id = "BEAUTY",
            name = "Beauty",
            description = "Makeup, hair, nails, grooming and home salon treatments.",
            iconName = "face",
            startingPriceAed = 1800.0,
            popularTag = "At-Home Salon",
            subSkills = listOf("Makeup", "Hair", "Nails", "Grooming", "Bridal Glam", "Waxing & Facials"),
            serviceGroup = "Local Services"
        ),
        // 12. Wellness
        ServiceCategory(
            id = "WELLNESS",
            name = "Wellness",
            description = "Fitness, coaching, permitted wellness services, yoga and health guidance.",
            iconName = "fitness_center",
            startingPriceAed = 2500.0,
            subSkills = listOf("Fitness", "Coaching", "Permitted Wellness Services", "Personal Trainer", "Yoga Instructor"),
            serviceGroup = "Local Services"
        ),
        // 13. Pet Care
        ServiceCategory(
            id = "PET_CARE",
            name = "Pet Care",
            description = "Grooming, walking, pet sitting, boarding and hygiene baths.",
            iconName = "pets",
            startingPriceAed = 1200.0,
            subSkills = listOf("Grooming", "Walking", "Pet Sitting", "Nail Clipping", "Flea Treatment"),
            serviceGroup = "Local Services"
        ),
        // 14. Events
        ServiceCategory(
            id = "EVENTS",
            name = "Events",
            description = "Photography, catering, decoration, DJ, sound setup and stage lighting.",
            iconName = "celebration",
            startingPriceAed = 8000.0,
            popularTag = "Weddings & Parties",
            subSkills = listOf("Photography", "Catering", "Decoration", "DJ", "Sound Technician", "Wedding Coordination"),
            serviceGroup = "Digital Services"
        ),
        // 15. Software Development
        ServiceCategory(
            id = "SOFTWARE_DEVELOPMENT",
            name = "Software Development",
            description = "Websites, apps, backend systems, full stack platforms and custom APIs.",
            iconName = "code",
            startingPriceAed = 15000.0,
            popularTag = "Top Tech Freelancers",
            subSkills = listOf("Websites", "Apps", "Backend Systems", "APIs", "Cloud Deployment", "Database Architecture"),
            serviceGroup = "Digital Services"
        ),
        // 16. AI & Automation
        ServiceCategory(
            id = "AI_AUTOMATION",
            name = "AI & Automation",
            description = "Chatbots, workflows, AI integration, autonomous agents and LLM customization.",
            iconName = "smart_toy",
            startingPriceAed = 12000.0,
            popularTag = "Next Gen Tech",
            subSkills = listOf("Chatbots", "Workflows", "AI Integration", "Prompt Engineering", "Custom GPTs", "API Automation"),
            serviceGroup = "Digital Services"
        ),
        // 17. Design
        ServiceCategory(
            id = "DESIGN",
            name = "Design",
            description = "UI/UX, branding, illustration, product packaging and digital graphics.",
            iconName = "palette",
            startingPriceAed = 5000.0,
            subSkills = listOf("UI/UX", "Branding", "Illustration", "Logo Design", "Design Systems", "Social Media Graphics"),
            serviceGroup = "Digital Services"
        ),
        // 18. Marketing
        ServiceCategory(
            id = "MARKETING",
            name = "Marketing",
            description = "SEO, advertising, social media campaigns, PPC ads and growth marketing.",
            iconName = "trending_up",
            startingPriceAed = 6000.0,
            subSkills = listOf("SEO", "Advertising", "Social Media", "Meta Ads", "Google Ads", "Content Strategy"),
            serviceGroup = "Digital Services"
        ),
        // 19. Media Production
        ServiceCategory(
            id = "MEDIA_PRODUCTION",
            name = "Media Production",
            description = "Video, animation, audio, video editing, voiceovers and podcast editing.",
            iconName = "movie",
            startingPriceAed = 7000.0,
            subSkills = listOf("Video", "Animation", "Audio", "Reels/TikToks", "Motion Graphics", "Color Grading"),
            serviceGroup = "Digital Services"
        ),
        // 20. Writing & Translation
        ServiceCategory(
            id = "WRITING_TRANSLATION",
            name = "Writing & Translation",
            description = "Copywriting, editing, localization, Urdu/English translation and SEO articles.",
            iconName = "edit_note",
            startingPriceAed = 2500.0,
            subSkills = listOf("Copywriting", "Editing", "Localization", "Urdu Translation", "Blog Posts", "Technical Writing"),
            serviceGroup = "Digital Services"
        ),
        // 21. Business Support
        ServiceCategory(
            id = "BUSINESS_SUPPORT",
            name = "Business Support",
            description = "Administration, virtual assistance, data entry, CRM management and research.",
            iconName = "support_agent",
            startingPriceAed = 3000.0,
            subSkills = listOf("Administration", "Virtual Assistance", "Data Entry", "Email Management", "Customer Support"),
            serviceGroup = "Professional Services"
        ),
        // 22. Finance & Accounting
        ServiceCategory(
            id = "FINANCE_ACCOUNTING",
            name = "Finance & Accounting",
            description = "Bookkeeping, tax support, FBR filings, payroll and financial statement audits.",
            iconName = "account_balance",
            startingPriceAed = 5000.0,
            popularTag = "FBR & Tax Ready",
            subSkills = listOf("Bookkeeping", "Tax Support", "FBR Filings", "Financial Audits", "QuickBooks/Xero", "Payroll"),
            serviceGroup = "Professional Services"
        ),
        // 23. Legal & Consulting
        ServiceCategory(
            id = "LEGAL_CONSULTING",
            name = "Legal & Consulting",
            description = "Qualified advisory services, contract drafting, business registration and compliance.",
            iconName = "gavel",
            startingPriceAed = 8000.0,
            popularTag = "Verified Advocates",
            subSkills = listOf("Qualified Advisory Services", "Contract Drafting", "SECP Registration", "IP & Trademark", "Dispute Resolution"),
            serviceGroup = "Professional Services"
        ),
        // 24. Education
        ServiceCategory(
            id = "EDUCATION",
            name = "Education",
            description = "Tutoring, languages, professional training, exam prep and coding bootcamps.",
            iconName = "school",
            startingPriceAed = 2000.0,
            popularTag = "Certified Tutors",
            subSkills = listOf("Tutoring", "Languages", "Professional Training", "Mathematics", "Science", "Quran Studies"),
            serviceGroup = "Learning & Training"
        ),
        // Aliased & Compatibility IDs to prevent any broken links from existing bookings
        ServiceCategory(
            id = "HOME_REPAIR",
            name = "Home Maintenance & Repairs",
            description = "All-in-one home maintenance including electrical, plumbing, carpentry and repairs.",
            iconName = "build",
            startingPriceAed = 1500.0,
            subSkills = listOf("Electrician", "Plumber", "Carpenter", "Handyman"),
            serviceGroup = "Local Services"
        ),
        ServiceCategory(
            id = "AC_SERVICE",
            name = "AC Servicing & Repair",
            description = "Duct cleaning, gas top-up, coil wash, compressor diagnosis & thermostat setup.",
            iconName = "ac_unit",
            startingPriceAed = 2500.0,
            subSkills = listOf("AC Installation", "Coil Chemical Wash", "Gas Refill", "Compressor Diagnostics"),
            serviceGroup = "Local Services"
        ),
        ServiceCategory(
            id = "HANDYMAN",
            name = "Handyman Services",
            description = "Curtain rod drilling, TV mounting, shelf installation & minor fixture fixes.",
            iconName = "build",
            startingPriceAed = 1200.0,
            subSkills = listOf("TV Mounting", "Shelf Installation", "Drilling", "Curtain Rods"),
            serviceGroup = "Local Services"
        ),
        ServiceCategory(
            id = "PAINTING",
            name = "Painting & Wall Repairs",
            description = "Studio, apartment & house painting with moisture-proof primers.",
            iconName = "format_paint",
            startingPriceAed = 6000.0,
            subSkills = listOf("Interior Painting", "Wall Putty", "Waterproofing", "Exterior Painting"),
            serviceGroup = "Local Services"
        ),
        ServiceCategory(
            id = "APPLIANCE_REPAIR",
            name = "Appliance Repair",
            description = "Washing machine, refrigerator cooling coil, microwave & oven maintenance.",
            iconName = "kitchen",
            startingPriceAed = 2200.0,
            subSkills = listOf("Appliance Repair", "Fridge Repair", "Washing Machine Repair"),
            serviceGroup = "Local Services"
        ),
        ServiceCategory(
            id = "DEEP_CLEANING",
            name = "Deep Cleaning",
            description = "Move-in steam disinfection, sofa shampooing & kitchen grease removal.",
            iconName = "cleaning_services",
            startingPriceAed = 4500.0,
            subSkills = listOf("Home Cleaning", "Deep Cleaning", "Sofa Cleaning", "Carpet Cleaning"),
            serviceGroup = "Local Services"
        ),
        ServiceCategory(
            id = "SMART_HOME",
            name = "Smart Home Setup",
            description = "Smart doorbell, biometric lock installation, CCTV setup & WiFi extension.",
            iconName = "router",
            startingPriceAed = 3000.0,
            subSkills = listOf("CCTV Installation", "Smart Doorbell", "Biometric Lock"),
            serviceGroup = "Local Services"
        ),
        ServiceCategory(
            id = "EMERGENCY",
            name = "Emergency Maintenance",
            description = "24/7 Priority dispatch within 30-45 minutes for burst pipes or power loss.",
            iconName = "warning",
            startingPriceAed = 2500.0,
            subSkills = listOf("Roadside Assistance", "Burst Pipe Repair", "Power Outage"),
            serviceGroup = "Local Services"
        )
    )

    val serviceCatalog = listOf(
        ServiceItem(
            id = "SRV-AC-01",
            categoryId = "AC_SERVICE",
            name = "AC Complete Deep Chemical Wash",
            description = "Full pressure coil wash, antibacterial chemical spray, drain tray unclogging, and gas pressure audit.",
            durationMinutes = 90,
            inclusions = listOf("Indoor coil chemical wash", "Filter sterilization", "Outdoor compressor check", "Refrigerant pressure measurement"),
            exclusions = listOf("Compressor mechanical replacement", "Motor rewiring"),
            priceAed = 3500.0,
            pricingType = PricingType.FIXED,
            isEmergencyEligible = true,
            iconName = "ac_unit"
        ),
        ServiceItem(
            id = "SRV-AC-02",
            categoryId = "AC_SERVICE",
            name = "AC Not Cooling Inspection & Fix",
            description = "Diagnostic inspection when AC is blowing warm air or freezing up.",
            durationMinutes = 60,
            inclusions = listOf("Comprehensive 14-point diagnostic", "Leak detection test", "Minor electrical connection tune-up"),
            exclusions = listOf("Parts and gas refill charged separately via in-app quotation"),
            priceAed = 1500.0,
            pricingType = PricingType.INSPECTION_QUOTATION,
            isEmergencyEligible = true,
            iconName = "ac_unit"
        ),
        ServiceItem(
            id = "SRV-PLUMB-01",
            categoryId = "PLUMBING",
            name = "Water Leakage & Pipe Repair",
            description = "Fix under-sink leaks, flexi-hose ruptures, bathroom mixer dripping, and angle valve replacements.",
            durationMinutes = 60,
            inclusions = listOf("Leak diagnostic", "Joint tightening", "Pipe section sealing"),
            exclusions = listOf("Concealed wall tile breaking if major burst"),
            priceAed = 1800.0,
            pricingType = PricingType.FIXED,
            isEmergencyEligible = true,
            iconName = "plumbing"
        ),
        ServiceItem(
            id = "SRV-ELEC-01",
            categoryId = "ELECTRICAL",
            name = "Breaker Tripping & Short Circuit Fix",
            description = "Identify and isolate short circuits, repair faulty circuit breakers, and balance electrical load.",
            durationMinutes = 75,
            inclusions = listOf("Insulation resistance testing", "Distribution board check", "Wiring connection tightening"),
            exclusions = listOf("WAPDA/LESCO/K-Electric main meter replacement"),
            priceAed = 2000.0,
            pricingType = PricingType.FIXED,
            isEmergencyEligible = true,
            iconName = "bolt"
        ),
        ServiceItem(
            id = "SRV-HANDY-01",
            categoryId = "HANDYMAN",
            name = "TV Mounting & Wall Fixtures",
            description = "Precision heavy-duty wall bracket mounting for TVs up to 85 inches with concealed wiring.",
            durationMinutes = 45,
            inclusions = listOf("Heavy duty steel wall anchors", "Spirit level laser alignment", "Cable trunking up to 1m"),
            exclusions = listOf("TV bracket hardware (customer supplies or quotes)"),
            priceAed = 1500.0,
            pricingType = PricingType.FIXED,
            isEmergencyEligible = false,
            iconName = "build"
        ),
        // Additional Services for New Categories
        ServiceItem(
            id = "SRV-CLEAN-01",
            categoryId = "CLEANING",
            name = "Deep House & Sofa Sanitization",
            description = "Complete home vacuuming, chemical sofa extraction, kitchen steam degreasing & washroom disinfection.",
            durationMinutes = 120,
            inclusions = listOf("Sofa fabric extraction", "Floor scrubbing", "Tile grout cleaning", "Bathroom descaling"),
            exclusions = listOf("Exterior terrace pressure wash"),
            priceAed = 4500.0,
            pricingType = PricingType.FIXED,
            iconName = "cleaning_services"
        ),
        ServiceItem(
            id = "SRV-AUTO-01",
            categoryId = "AUTOMOTIVE",
            name = "Mobile Car Diagnostic & Battery Service",
            description = "OBD2 computer scanning for check engine lights, battery health boost/replacement, and brake check at doorstep.",
            durationMinutes = 45,
            inclusions = listOf("Complete OBD2 scan report", "Battery terminal cleaning", "Alternator charging check"),
            exclusions = listOf("New battery unit cost"),
            priceAed = 2000.0,
            pricingType = PricingType.FIXED,
            isEmergencyEligible = true,
            iconName = "directions_car"
        ),
        ServiceItem(
            id = "SRV-IT-01",
            categoryId = "IT_DIGITAL",
            name = "Full-Stack Web or Mobile App MVP",
            description = "Custom responsive web portal or mobile app with modern architecture, database setup, and user authentication.",
            durationMinutes = 240,
            inclusions = listOf("UI/UX layout", "Database schema", "Responsive frontend", "API integrations"),
            exclusions = listOf("Third party cloud hosting fees"),
            priceAed = 25000.0,
            pricingType = PricingType.INSPECTION_QUOTATION,
            iconName = "computer"
        ),
        ServiceItem(
            id = "SRV-BEAUTY-01",
            categoryId = "BEAUTY_WELLNESS",
            name = "Signature Bridal & Party Makeup",
            description = "At-home luxury bridal glam, HD party makeup, false lashes, and customized hairstyling by certified artists.",
            durationMinutes = 90,
            inclusions = listOf("HD foundation finish", "Hairstyling with hair accessories", "High-end luxury cosmetics"),
            exclusions = listOf("Jewelry rental"),
            priceAed = 8000.0,
            pricingType = PricingType.FIXED,
            iconName = "face"
        ),
        ServiceItem(
            id = "SRV-EDU-01",
            categoryId = "EDUCATION_TUTORING",
            name = "1-on-1 Academic & Tech Tutoring (Monthly)",
            description = "Personalized private coaching for Mathematics, Science, Quran, or Programming (Python, Web, Mobile).",
            durationMinutes = 60,
            inclusions = listOf("Weekly interactive sessions", "Custom homework assignments", "Test prep drills"),
            exclusions = listOf("Official exam registration fees"),
            priceAed = 6000.0,
            pricingType = PricingType.FIXED,
            iconName = "school"
        ),
        ServiceItem(
            id = "SRV-MOVE-01",
            categoryId = "MOVING_DELIVERY",
            name = "Full House Shifting & Furniture Packing",
            description = "Complete home relocation with high-grade bubble wrap, carton boxing, furniture assembly, and covered transport.",
            durationMinutes = 300,
            inclusions = listOf("Packing cartons & bubble wrap", "Loading/unloading labor", "Covered truck transport"),
            exclusions = listOf("Wall painting touch-ups"),
            priceAed = 15000.0,
            pricingType = PricingType.INSPECTION_QUOTATION,
            iconName = "local_shipping"
        )
    )

    val demoProviders = listOf(
        ProviderProfile(
            id = "PROV-01",
            userId = "USR-PROV-01",
            name = "Rashid Khan",
            phone = "+92 321 987 6543",
            email = "rashid.cooling@example.pk",
            bio = "Certified HVAC technician with 9 years of experience servicing inverter ACs, gas charging & electrical systems across Lahore, Karachi, and Islamabad.",
            skills = listOf("AC_SERVICE", "ELECTRICAL", "PLUMBING", "HANDYMAN", "EMERGENCY"),
            serviceAreas = listOf("DHA Phase 5", "Gulberg", "Bahria Town", "Model Town", "Johar Town", "Lahore", "Clifton", "Karachi", "Islamabad"),
            rating = 4.9,
            reviewCount = 128,
            completedJobs = 142,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 1200.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "NTN-984421-4 (TEVTA / NAVTTC Certified)",
            emiratesIdNumber = "35201-1234567-1 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-02",
            userId = "USR-PROV-02",
            name = "Muhammad Tariq",
            phone = "+92 312 456 7890",
            email = "tariq.electrician@example.pk",
            bio = "Master certified electrician with 11 years in distribution board wiring, breaker troubleshooting, short circuit isolation & solar inverter integration.",
            skills = listOf("ELECTRICAL", "EMERGENCY", "HANDYMAN", "SMART_HOME"),
            serviceAreas = listOf("DHA Phase 5", "Gulberg III", "Model Town", "Cantt", "Johar Town", "Lahore", "Islamabad"),
            rating = 4.9,
            reviewCount = 94,
            completedJobs = 115,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 1500.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "TEVTA Master Electrical Certification #ET-9921",
            emiratesIdNumber = "35202-8472911-3 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-03",
            userId = "USR-PROV-03",
            name = "Kamran Ashraf",
            phone = "+92 333 789 1234",
            email = "kamran.plumbing@example.pk",
            bio = "Master sanitary plumber. Specialist in concealed water leak detection, PPR-C pipe replacement, water tank cleaning, and booster pump installations.",
            skills = listOf("PLUMBING", "EMERGENCY", "HANDYMAN"),
            serviceAreas = listOf("Clifton", "DHA Karachi", "PECHS", "Gulshan-e-Iqbal", "Karachi", "Lahore"),
            rating = 4.8,
            reviewCount = 86,
            completedJobs = 98,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 1400.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "Sanitary Engineering Board #SAN-4401",
            emiratesIdNumber = "42301-6543219-5 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-04",
            userId = "USR-PROV-04",
            name = "Bilal Ahmed",
            phone = "+92 301 555 4321",
            email = "bilal.handyman@example.pk",
            bio = "All-in-one handyman equipped with laser levels and heavy-duty masonry drills. TV wall mounting up to 85\", curtain rod drilling, and smart lock fittings.",
            skills = listOf("HANDYMAN", "CARPENTRY", "SMART_HOME"),
            serviceAreas = listOf("F-6", "F-7", "F-10", "G-11", "Bahria Town", "Islamabad", "Rawalpindi", "Lahore"),
            rating = 4.9,
            reviewCount = 110,
            completedJobs = 134,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 1200.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "Certified Handyman Guild PK #HM-1039",
            emiratesIdNumber = "61101-3829104-7 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-05",
            userId = "USR-PROV-05",
            name = "Zahid Hussain",
            phone = "+92 345 678 9012",
            email = "zahid.paints@example.pk",
            bio = "Professional home painter & wall treatment expert. High-durability washable emulsions, damp-proof base primers, putty smoothing, and feature walls.",
            skills = listOf("PAINTING", "HANDYMAN"),
            serviceAreas = listOf("DHA Phase 5", "Gulberg", "Johar Town", "WAPDA Town", "Lahore"),
            rating = 4.7,
            reviewCount = 64,
            completedJobs = 78,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 1100.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "NAVTTC Decorative Coatings License #PC-3329",
            emiratesIdNumber = "35201-9018274-1 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-06",
            userId = "USR-PROV-06",
            name = "Umer Farooq",
            phone = "+92 322 345 6789",
            email = "umer.carpenter@example.pk",
            bio = "Master craftsman in woodwork with 14 years experience. Custom wardrobe repairs, hydraulic bed fittings, kitchen cabinet hinges, and IKEA/Habitt assembly.",
            skills = listOf("CARPENTRY", "HANDYMAN"),
            serviceAreas = listOf("Clifton", "DHA Karachi", "Gulshan-e-Iqbal", "Karachi"),
            rating = 4.8,
            reviewCount = 75,
            completedJobs = 89,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 1300.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "Sindh Technical Board Woodcraft #CP-8201",
            emiratesIdNumber = "42101-5582910-3 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-07",
            userId = "USR-PROV-07",
            name = "Sajid Ali",
            phone = "+92 315 901 2345",
            email = "sajid.smarthome@example.pk",
            bio = "Smart home automation specialist. Certified setup of biometric digital locks, WiFi video doorbells, Hikvision CCTV security cameras, and Tuya smart switches.",
            skills = listOf("SMART_HOME", "ELECTRICAL", "HANDYMAN", "HOME_REPAIR"),
            serviceAreas = listOf("DHA Phase 5", "Gulberg", "Bahria Town", "Lahore", "Islamabad", "Karachi"),
            rating = 4.9,
            reviewCount = 52,
            completedJobs = 63,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 1600.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "Certified IoT & Security Installer #IoT-4910",
            emiratesIdNumber = "35202-4829103-9 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-08",
            userId = "USR-PROV-08",
            name = "Zainab Sheikh",
            phone = "+92 300 445 6789",
            email = "zainab.clean@example.pk",
            bio = "Eco-friendly deep cleaning & disinfection specialist. Carpet steam shampooing, sofa dry extraction, and luxury villa move-in cleaning.",
            skills = listOf("CLEANING", "DEEP_CLEANING", "HOME_REPAIR"),
            serviceAreas = listOf("DHA Phase 5", "Gulberg", "Model Town", "Lahore"),
            rating = 4.95,
            reviewCount = 88,
            completedJobs = 104,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 1400.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "Certified Sanitary Cleaning PK #CLN-2041",
            emiratesIdNumber = "35201-7788991-2 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-09",
            userId = "USR-PROV-09",
            name = "Waqas AutoCare",
            phone = "+92 321 776 5432",
            email = "waqas.auto@example.pk",
            bio = "Mobile automotive mechanic & car electrician. Computer diagnostic scanning, battery boost & replacement, roadside emergency, and brake pad service.",
            skills = listOf("AUTOMOTIVE", "EMERGENCY"),
            serviceAreas = listOf("Lahore", "Karachi", "Islamabad", "Motorway M-2"),
            rating = 4.88,
            reviewCount = 72,
            completedJobs = 90,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 1800.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "Automotive Tech Board #AUT-9912",
            emiratesIdNumber = "35202-1122334-5 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-10",
            userId = "USR-PROV-10",
            name = "Hamza DevStudio",
            phone = "+92 334 112 3344",
            email = "hamza.code@example.pk",
            bio = "Senior Full-Stack & Mobile Developer. Kotlin Android, Flutter, React, Next.js, and custom AI chatbot development for startups and businesses.",
            skills = listOf("IT_DIGITAL", "BUSINESS_PROFESSIONAL"),
            serviceAreas = listOf("Worldwide Remote", "Lahore", "Karachi", "Islamabad", "Dubai"),
            rating = 5.0,
            reviewCount = 140,
            completedJobs = 168,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 3500.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "PSEB Registered Freelancer #PSEB-8821",
            emiratesIdNumber = "35201-9988776-3 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-11",
            userId = "USR-PROV-11",
            name = "Nadia Glam Studio",
            phone = "+92 301 998 8776",
            email = "nadia.glam@example.pk",
            bio = "Certified celebrity makeup artist & hair stylist. Bridal makeup, party glam, Russian manicure/nail art, and skincare facials at the comfort of your home.",
            skills = listOf("BEAUTY_WELLNESS", "LIFESTYLE_ASSISTANCE"),
            serviceAreas = listOf("DHA", "Gulberg", "Bahria Town", "Lahore", "Islamabad"),
            rating = 4.96,
            reviewCount = 115,
            completedJobs = 132,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 2500.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "Guild of Beauty Professionals #BEA-4029",
            emiratesIdNumber = "35202-6655443-1 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-12",
            userId = "USR-PROV-12",
            name = "Prof. Ali Raza",
            phone = "+92 345 123 9876",
            email = "ali.tutor@example.pk",
            bio = "O/A Levels & University Mathematics & Science tutor. 9+ years experience preparing students for Cambridge exams, SAT, and university entry tests.",
            skills = listOf("EDUCATION_TUTORING"),
            serviceAreas = listOf("Worldwide Remote", "Lahore", "Islamabad", "Karachi"),
            rating = 4.98,
            reviewCount = 96,
            completedJobs = 110,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 2000.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "HEC Recognized Educator #EDU-1102",
            emiratesIdNumber = "35201-3344556-7 (CNIC Verified)"
        ),
        ProviderProfile(
            id = "PROV-13",
            userId = "USR-PROV-13",
            name = "SafeMovers PK",
            phone = "+92 322 888 1122",
            email = "safemovers@example.pk",
            bio = "Professional house shifting and office relocation service. Bubble wrapping, furniture dismantling/assembly, hydraulic tailgate trucks, and damage guarantee.",
            skills = listOf("MOVING_DELIVERY", "CARPENTRY", "HANDYMAN"),
            serviceAreas = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi"),
            rating = 4.85,
            reviewCount = 67,
            completedJobs = 85,
            isOnline = true,
            isVerified = true,
            hourlyRateAed = 3000.0,
            documentStatus = VerificationStatus.APPROVED,
            tradeLicenseNumber = "Goods Transport Federation #MOV-5501",
            emiratesIdNumber = "35202-9900112-4 (CNIC Verified)"
        )
    )

    val sampleBookings = listOf(
        Booking(
            id = "FIX-7821",
            customerId = "USR-CUST-01",
            customerName = "Ayesha Khan",
            customerPhone = "+92 300 123 4567",
            providerId = "PROV-01",
            providerName = "Rashid Khan",
            providerPhone = "+92 321 987 6543",
            providerRating = 4.9,
            serviceId = "SRV-AC-01",
            serviceTitle = "AC Complete Deep Chemical Wash",
            serviceCategory = "AC_SERVICE",
            pricingType = PricingType.FIXED,
            basePriceAed = 3500.0,
            emergencySurgeFeeAed = 0.0,
            vatAed = 175.0,
            agreedFinalPriceAed = 3675.0,
            platformCommissionAed = 525.0, // 15% of 3500
            providerEarningsAed = 2975.0, // 85% of 3500
            status = BookingStatus.PAYMENT_SETTLED,
            paymentStatus = PaymentStatus.PAID_ONLINE,
            urgency = Urgency.STANDARD,
            propertyType = PropertyType.APARTMENT,
            scheduledDate = "Yesterday",
            scheduledTimeSlot = "10:00 AM - 12:00 PM",
            address = demoAddresses[0],
            issueDescription = "Master bedroom inverter AC is blowing low airflow and smelling dusty before summer.",
            issuePhotoUrls = listOf("https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=500"),
            beforePhotoUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=500",
            afterPhotoUrl = "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=500",
            statusHistory = listOf(
                BookingStatusEvent(BookingStatus.REQUESTED, 1713400000000L),
                BookingStatusEvent(BookingStatus.PROVIDER_ACCEPTED, 1713400100000L),
                BookingStatusEvent(BookingStatus.EN_ROUTE, 1713401000000L),
                BookingStatusEvent(BookingStatus.ARRIVED, 1713401800000L),
                BookingStatusEvent(BookingStatus.IN_PROGRESS, 1713402000000L),
                BookingStatusEvent(BookingStatus.COMPLETED, 1713406000000L),
                BookingStatusEvent(BookingStatus.PAYMENT_SETTLED, 1713406200000L)
            ),
            createdAt = 1713400000000L,
            updatedAt = 1713406200000L
        )
    )

    val sampleReviews = listOf(
        Review(
            id = "REV-01",
            bookingId = "FIX-7821",
            customerId = "USR-CUST-01",
            customerName = "Ayesha Khan",
            providerId = "PROV-01",
            rating = 5.0,
            comment = "Rashid was punctual, professional, and wore protective covers. The inverter AC cooling dropped the room temperature in 10 minutes. Best freelance service in Lahore!",
            tags = listOf("Punctual", "Clean Work", "True Professional", "Quick Fix"),
            timestamp = 1713406300000L
        )
    )

    val sampleChatMessages = listOf(
        ChatMessage(
            id = "MSG-01",
            bookingId = "FIX-7821",
            senderId = "USR-PROV-01",
            senderName = "Rashid Khan",
            senderRole = UserRole.PROVIDER,
            text = "Assalam-o-Alaikum Ayesha, I am en route to Sector G, DHA Phase 5. Estimated arrival in 15 minutes.",
            timestamp = 1713401050000L,
            isRead = true
        ),
        ChatMessage(
            id = "MSG-02",
            bookingId = "FIX-7821",
            senderId = "USR-CUST-01",
            senderName = "Ayesha Khan",
            senderRole = UserRole.CUSTOMER,
            text = "Walaikum Assalam! Please inform security at the gate that you are visiting House 42.",
            timestamp = 1713401120000L,
            isRead = true
        )
    )

    val sampleLedger = listOf(
        LedgerEntry(
            id = "LED-001",
            referenceId = "FIX-7821",
            type = LedgerType.CUSTOMER_CHARGE,
            debitAed = 0.0,
            creditAed = 3675.0,
            balanceAed = 3675.0,
            description = "Customer payment for AC Complete Deep Chemical Wash",
            timestamp = 1713406200000L
        ),
        LedgerEntry(
            id = "LED-002",
            referenceId = "FIX-7821",
            type = LedgerType.PLATFORM_COMMISSION_EARNED,
            debitAed = 0.0,
            creditAed = 525.0,
            balanceAed = 3675.0,
            description = "Platform commission 15% on booking FIX-7821",
            timestamp = 1713406200000L
        ),
        LedgerEntry(
            id = "LED-003",
            referenceId = "FIX-7821",
            type = LedgerType.PROVIDER_PAYABLE_CREDIT,
            debitAed = 2975.0,
            creditAed = 0.0,
            balanceAed = 700.0,
            description = "Payable allocated to Rashid Khan",
            timestamp = 1713406200000L
        )
    )

    val sampleGigs = listOf(
        FreelanceGig(
            id = "GIG-01",
            sellerId = "PROV-01",
            sellerName = "Rashid Khan",
            sellerRating = 4.9,
            sellerReviewCount = 128,
            sellerCompletedOrders = 142,
            sellerBadge = "Top Rated Freelancer",
            title = "I will chemically deep-clean and gas recharge your split or inverter AC",
            categoryId = "AC_SERVICE",
            description = "Experienced HVAC freelancer in Lahore & Karachi. I bring industrial pressure wash equipment and safe eco-friendly antibacterial cleaners. 30-day cooling warranty included.",
            packages = listOf(
                GigPackage("PKG-01", "Basic Filter & Tray Wash", "Filter sanitization and drain tray unclog", 45, listOf("Filter wash", "Coil deodorizing"), 2000.0),
                GigPackage("PKG-02", "Standard Deep Chemical Wash", "Full indoor & outdoor coil chemical pressure flush", 90, listOf("Chemical coil wash", "Gas check", "30-day warranty"), 3500.0),
                GigPackage("PKG-03", "Full Overhaul & Gas Top-up", "Deep wash + R410A gas top-up + capacitor inspection", 120, listOf("Full wash", "Full gas refill", "Thermostat calibration"), 5500.0)
            ),
            coverIcon = "ac_unit",
            emirates = listOf("Lahore", "Karachi", "Islamabad")
        ),
        FreelanceGig(
            id = "GIG-02",
            sellerId = "PROV-02",
            sellerName = "Muhammad Tariq",
            sellerRating = 4.9,
            sellerReviewCount = 94,
            sellerCompletedOrders = 115,
            sellerBadge = "Master Certified",
            title = "I will diagnose short circuits, balance DB breaker panels & fix tripped lines",
            categoryId = "ELECTRICAL",
            description = "TEVTA certified senior electrician. Complete insulation resistance testing, distribution board load balancing, and heavy appliance wiring.",
            packages = listOf(
                GigPackage("PKG-04", "Single Circuit Diagnostic", "Identify tripping causes, replace faulty 16A/32A breaker", 45, listOf("Breaker diagnostic", "Short circuit isolation"), 1500.0),
                GigPackage("PKG-05", "Distribution Board Overhaul", "Complete DB terminal tightening, phase balancing & earthing test", 90, listOf("Full DB service", "Safety load audit", "14-day warranty"), 3200.0),
                GigPackage("PKG-06", "Whole-House Electrical Audit", "Full wiring inspection with laser thermal scanner for loose connections", 150, listOf("Complete house audit", "Report & preventive fixes"), 6000.0)
            ),
            coverIcon = "bolt",
            emirates = listOf("Lahore", "Islamabad")
        ),
        FreelanceGig(
            id = "GIG-03",
            sellerId = "PROV-03",
            sellerName = "Kamran Ashraf",
            sellerRating = 4.8,
            sellerReviewCount = 86,
            sellerCompletedOrders = 98,
            sellerBadge = "Verified Specialist",
            title = "I will fix concealed bathroom leaks, replace PPR-C pipes & install booster pumps",
            categoryId = "PLUMBING",
            description = "Specialized plumbing with non-invasive acoustic leak detection. Fast resolution for low water pressure, dripping mixers, and sanitary replacements.",
            packages = listOf(
                GigPackage("PKG-07", "Dripping Tap & Mixer Repair", "Cartridge replacement and teflon leak sealing", 30, listOf("Mixer repair", "Angle valve check"), 1400.0),
                GigPackage("PKG-08", "Water Pressure Pump Setup", "Automatic pressure switch calibration and booster pump mounting", 75, listOf("Pump fitting", "Pressure testing", "Pipe union joint"), 3000.0),
                GigPackage("PKG-09", "Concealed Leak Repair", "Acoustic detection and precise wall tile opening for pipe weld", 120, listOf("Acoustic leak test", "PPR-C welding", "Pressure test"), 5000.0)
            ),
            coverIcon = "plumbing",
            emirates = listOf("Karachi", "Lahore")
        ),
        FreelanceGig(
            id = "GIG-04",
            sellerId = "PROV-04",
            sellerName = "Bilal Ahmed",
            sellerRating = 4.9,
            sellerReviewCount = 110,
            sellerCompletedOrders = 134,
            sellerBadge = "Top Rated Freelancer",
            title = "I will securely wall mount your LED TV, curtain rods & heavy wall shelves",
            categoryId = "HANDYMAN",
            description = "Equipped with Bosch laser level and concrete hammer drills. Concealed wire raceways and heavy steel toggle bolts provided for maximum safety.",
            packages = listOf(
                GigPackage("PKG-10", "Standard TV Mounting (Up to 55\")", "Flush or tilt mount with laser alignment on concrete or brick", 45, listOf("Steel anchor bolts", "Laser alignment"), 1500.0),
                GigPackage("PKG-11", "Large TV (65\"-85\") + Wire Concealment", "Heavy swivel arm installation with hidden PVC cable casing", 75, listOf("Heavy bracket mount", "1m cable concealment"), 2800.0),
                GigPackage("PKG-12", "Full Room Handyman Bundle", "Mount TV, 2 curtain rods, 3 mirrors/frames and shelf fixtures", 120, listOf("TV mount", "2 curtain rods", "Wall hangings"), 4500.0)
            ),
            coverIcon = "build",
            emirates = listOf("Islamabad", "Rawalpindi", "Lahore")
        ),
        FreelanceGig(
            id = "GIG-05",
            sellerId = "PROV-05",
            sellerName = "Zahid Hussain",
            sellerRating = 4.7,
            sellerReviewCount = 64,
            sellerCompletedOrders = 78,
            sellerBadge = "Verified Pro",
            title = "I will paint your room with anti-damp primer and high-washable luxury emulsion",
            categoryId = "PAINTING",
            description = "Professional masking tape coverage, 2 coats of putty, damp seal primer, and top-tier Berger/Dulux finish. No floor stains or mess left behind.",
            packages = listOf(
                GigPackage("PKG-13", "Single Accent / Feature Wall", "Texture finish or bold contrast color with precision border masking", 90, listOf("Putty repair", "2 coats luxury finish"), 3000.0),
                GigPackage("PKG-14", "Complete Standard Room (12x14)", "Full ceiling and walls with scraping, anti-damp primer and double coat", 240, listOf("Surface preparation", "Ceiling + 4 walls", "Clean-up"), 6500.0),
                GigPackage("PKG-15", "Studio / Small Apartment Fresh Coat", "Quick-turnaround move-in paint with odorless washable emulsion", 480, listOf("Full apartment", "Touch-ups & post clean"), 14000.0)
            ),
            coverIcon = "format_paint",
            emirates = listOf("Lahore")
        ),
        FreelanceGig(
            id = "GIG-06",
            sellerId = "PROV-06",
            sellerName = "Umer Farooq",
            sellerRating = 4.8,
            sellerReviewCount = 75,
            sellerCompletedOrders = 89,
            sellerBadge = "Craftsman",
            title = "I will assemble flatpack furniture, fix sticking doors & align wardrobe hinges",
            categoryId = "CARPENTRY",
            description = "14 years of woodwork craftsmanship. Repair sagging drawer runners, install soft-close hinges, and build custom modular shelving.",
            packages = listOf(
                GigPackage("PKG-16", "Door & Wardrobe Hinge Tune-up", "Re-align up to 4 doors or wardrobe shutters with magnetic catches", 45, listOf("Hinge adjustment", "Leveling check"), 1500.0),
                GigPackage("PKG-17", "Wardrobe / Bed Assembly", "Complete assembly of 3-door wardrobe or hydraulic double bed", 120, listOf("Full assembly", "Fastener tightening", "Clean-up"), 3000.0),
                GigPackage("PKG-18", "Complete Bedroom Furniture Setup", "Assembly of bed, side tables, dresser, and mirror wall hanging", 210, listOf("Bed + 2 side tables", "Dresser + Mirror"), 5500.0)
            ),
            coverIcon = "handyman",
            emirates = listOf("Karachi")
        ),
        FreelanceGig(
            id = "GIG-07",
            sellerId = "PROV-07",
            sellerName = "Sajid Ali",
            sellerRating = 4.9,
            sellerReviewCount = 52,
            sellerCompletedOrders = 63,
            sellerBadge = "Smart Home Certified",
            title = "I will install smart WiFi video doorbells, biometric door locks & CCTV cameras",
            categoryId = "SMART_HOME",
            description = "Upgrade your home security. Seamless integration of fingerprint smart locks, ring doorbells, phone app notifications, and night-vision IP cameras.",
            packages = listOf(
                GigPackage("PKG-19", "Smart Video Doorbell Installation", "Mounting, chime pairing, transformer check, and phone app config", 60, listOf("Doorbell mounting", "WiFi app pairing"), 2500.0),
                GigPackage("PKG-20", "Biometric Smart Door Lock Setup", "Precision door mortise routing, biometric lock fitting, RFID card setup", 90, listOf("Mortise cutting", "Lock installation", "App setup"), 4000.0),
                GigPackage("PKG-21", "4-Camera Smart CCTV System Setup", "Concealed wiring, NVR setup, mobile live-streaming configuration", 240, listOf("4x Camera mounting", "NVR & app setup", "30-day support"), 8500.0)
            ),
            coverIcon = "router",
            emirates = listOf("Lahore", "Islamabad", "Karachi")
        )
    )

    val sampleJobRequests = listOf(
        CustomJobRequest(
            id = "REQ-101",
            customerId = "USR-CUST-01",
            customerName = "Ayesha Khan",
            title = "Need 2 inverter AC units chemically cleaned before summer heat",
            categoryId = "AC_SERVICE",
            description = "Living room and master bedroom inverter ACs are not blowing cold enough. Looking for an independent certified HVAC freelancer to do chemical wash this weekend in DHA Phase 5, Lahore.",
            budgetAed = 6000.0,
            emirate = "Lahore",
            area = "DHA Phase 5",
            urgency = Urgency.STANDARD,
            proposalsCount = 1,
            status = JobRequestStatus.OPEN
        )
    )

    val sampleProposals = listOf(
        JobProposal(
            id = "PROP-01",
            jobRequestId = "REQ-101",
            sellerId = "PROV-01",
            sellerName = "Rashid Khan",
            sellerRating = 4.9,
            sellerCompletedJobs = 142,
            bidAmountAed = 5500.0,
            deliveryTimeText = "Tomorrow at 11:00 AM",
            coverLetter = "Assalam-o-Alaikum Ayesha, I can service both units with pressure coil wash and check refrigerant levels. I live in Lahore and can visit this Saturday. 30-day warranty included."
        )
    )
}
