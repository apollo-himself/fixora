package com.fixora.marketplace

import com.fixora.marketplace.data.auth.AdminAccess
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminAccessTests {
    @Test fun adminRequiresMatchingVerifiedFirebaseIdentity() {
        assertTrue(AdminAccess.canOpen("admin-uid", "admin-uid", true, true))
        assertFalse(AdminAccess.canOpen("admin-uid", null, true, true))
        assertFalse(AdminAccess.canOpen("admin-uid", "admin-uid", false, true))
        assertFalse(AdminAccess.canOpen("admin-uid", "admin-uid", true, false))
        assertFalse(AdminAccess.canOpen("admin-uid", "other-uid", true, true))
    }
}
