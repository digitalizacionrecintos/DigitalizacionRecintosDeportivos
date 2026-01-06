package org.example.project.domain.manager

import kotlin.test.Test
import kotlin.test.assertEquals
import org.example.project.domain.model.UserRole

class SessionManagerTest {

    @Test
    fun `initial role is USER`() {
        assertEquals(UserRole.USER, SessionManager.currentRole.value)
    }

    @Test
    fun `switchRole updates the current role`() {
        SessionManager.switchRole(UserRole.MANAGER)
        assertEquals(UserRole.MANAGER, SessionManager.currentRole.value)

        SessionManager.switchRole(UserRole.USER)
        assertEquals(UserRole.USER, SessionManager.currentRole.value)
    }
}
