package com.fixora.marketplace.data.model

enum class UserRole {
    CUSTOMER,
    PROVIDER,
    ADMIN
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
