package org.example.project.presentation.features.auth.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.UserRole
import org.example.project.domain.usecase.auth.LoginUseCase

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

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChange -> _state.update { it.copy(email = event.email, error = null) }
            is LoginEvent.OnPasswordChange -> _state.update { it.copy(password = event.password, error = null) }
            is LoginEvent.OnLoginClick -> performLogin(UserRole.USER)
            is LoginEvent.OnManagerLoginClick -> performLogin(UserRole.MANAGER)
            else -> {}
        }
    }

    private fun performLogin(role: UserRole) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = loginUseCase(_state.value.email, _state.value.password)) {
                is Try.Success -> {
                    SessionManager.startSession(result.value.user)
                    SessionManager.switchRole(result.value.role)
                    _state.update { it.copy(isLoading = false, isLoggedIn = true) }
                }
                is Try.Failure -> {
                    val errorMessage = when (val err = result.error) {
                        is AppError.Network -> err.message
                        is AppError.Server -> err.message
                        is AppError.Unauthorized -> "Credenciales inválidas"
                        is AppError.NotFound -> "Recurso no encontrado"
                        is AppError.Unknown -> err.message
                    }
                    _state.update { it.copy(isLoading = false, error = errorMessage) }
                }
            }
        }
    }
}
