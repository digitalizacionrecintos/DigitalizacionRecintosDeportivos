package org.example.project.presentation.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.data.remote.ApiService
import org.example.project.data.remote.dto.LoginRequest
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.UserRole

data class LoginState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val isLoggedIn: Boolean = false
)

sealed interface LoginEvent {
    data class OnEmailChange(val email: String) : LoginEvent
    data class OnPasswordChange(val password: String) : LoginEvent
    data object OnLoginClick : LoginEvent
    data object OnManagerLoginClick : LoginEvent
    data object OnRegisterClick : LoginEvent
}

class LoginViewModel : ViewModel() {
    private val apiService = ApiService()
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChange ->
                    _state.update { it.copy(email = event.email, error = null) }
            is LoginEvent.OnPasswordChange ->
                    _state.update { it.copy(password = event.password, error = null) }
            is LoginEvent.OnLoginClick -> performLogin(UserRole.USER)
            is LoginEvent.OnManagerLoginClick -> performLogin(UserRole.MANAGER)
            else -> {}
        }
    }

    private fun performLogin(role: UserRole) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val response =
                        withContext(Dispatchers.IO) {
                            apiService.login(
                                    LoginRequest(
                                            correo = _state.value.email,
                                            contrasena = _state.value.password,
                                    )
                            )
                        }

                SessionManager.startSession(response)

                val apiRole =
                        if (response.rol.equals("ROLE_ENCARGADO", ignoreCase = true))
                                UserRole.MANAGER
                        else UserRole.USER
                SessionManager.switchRole(apiRole)

                _state.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }
}
