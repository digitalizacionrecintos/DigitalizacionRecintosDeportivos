package org.example.project.domain.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.domain.model.Encargado
import org.example.project.domain.model.UserRole

object SessionManager {
    private val _currentRole = MutableStateFlow(UserRole.USER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()
    private var currentUser: Encargado? = null

    fun startSession(user: Encargado) {
        this.currentUser = user
    }

    fun updateCurrentUser(user: Encargado) {
        this.currentUser = user
    }

    fun getCurrentUser(): Encargado? {
        return currentUser
    }

    fun endSession() {
        currentUser = null
    }

    fun getUserRole(): UserRole {
        return when (currentUser?.rol?.uppercase()) {
            "ROLE_ENCARGADO" -> UserRole.MANAGER
            else -> UserRole.USER
        }
    }

    fun switchRole(role: UserRole) {
        _currentRole.value = role
    }
}
