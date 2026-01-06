package org.example.project.domain.manager

import kotlin.text.uppercase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.remote.dto.UserDTO
import org.example.project.domain.model.UserRole

object SessionManager {
    private val _currentRole = MutableStateFlow(UserRole.USER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()
    private var currentUser: UserDTO? = null

    fun startSession(user: UserDTO) {
        this.currentUser = user
    }

    fun updateCurrentUser(user: UserDTO) {
        this.currentUser = user
    }

    fun getCurrentUser(): UserDTO? {
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
