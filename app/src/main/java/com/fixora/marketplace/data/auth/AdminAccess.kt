package com.fixora.marketplace.data.auth

object AdminAccess {
    fun canOpen(
        currentUid: String,
        authenticatedUid: String?,
        emailVerified: Boolean,
        hasAdminClaim: Boolean
    ): Boolean = emailVerified && hasAdminClaim && authenticatedUid != null &&
        currentUid.isNotBlank() && authenticatedUid == currentUid
}
