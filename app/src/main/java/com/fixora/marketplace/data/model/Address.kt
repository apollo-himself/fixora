package com.fixora.marketplace.data.model

data class Address(
    val id: String,
    val label: String, // e.g. "Home", "Villa - Springs", "Office"
    val street: String,
    val buildingOrVilla: String,
    val apartmentNo: String,
    val emirate: String, // e.g. "Lahore", "Karachi", "Islamabad", "Abu Dhabi"
    val area: String, // e.g. "DHA Phase 5, Lahore", "Downtown", "Al Barsha", "Al Majaz"
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean = false
) {
    val fullAddressDisplay: String
        get() = listOf(apartmentNo, buildingOrVilla, street, area, emirate)
            .filter { it.isNotBlank() }.distinct().joinToString(", ")
}

data class CustomerProfile(
    val userId: String,
    val addresses: List<Address> = emptyList(),
    val defaultAddressId: String? = null,
    val savedPaymentMethod: String? = "Fixora Demo Card (•••• 4242)"
)

