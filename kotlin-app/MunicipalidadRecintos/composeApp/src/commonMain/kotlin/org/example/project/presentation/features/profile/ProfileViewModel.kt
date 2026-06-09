package org.example.project.presentation.features.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.usecase.user.UpdateProfileUseCase

data class ProfileState(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val about: String = "",
    val role: String = "",
    val avatarUrl: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

sealed interface ProfileEvent {
    data class OnNameChange(val name: String) : ProfileEvent
    data class OnLastNameChange(val lastName: String) : ProfileEvent
    data class OnEmailChange(val email: String) : ProfileEvent
    data class OnPhoneChange(val phone: String) : ProfileEvent
    data class OnAboutChange(val about: String) : ProfileEvent
    data object SaveProfile : ProfileEvent
    data object ClearMessages : ProfileEvent
}

class ProfileViewModel(
    private val updateProfileUseCase: UpdateProfileUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        getProfileData()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnNameChange -> _state.update { it.copy(name = event.name) }
            is ProfileEvent.OnLastNameChange -> _state.update { it.copy(lastName = event.lastName) }
            is ProfileEvent.OnEmailChange -> _state.update { it.copy(email = event.email) }
            is ProfileEvent.OnPhoneChange -> _state.update { it.copy(phone = event.phone) }
            is ProfileEvent.OnAboutChange -> _state.update { it.copy(about = event.about) }
            is ProfileEvent.SaveProfile -> saveProfile()
            is ProfileEvent.ClearMessages -> _state.update { it.copy(errorMessage = null, successMessage = null) }
        }
    }

    fun getProfileData() {
        val user = SessionManager.getCurrentUser()
        if (user != null) {
            _state.update {
                it.copy(
                    name = user.nombre,
                    lastName = user.apellido,
                    email = user.correo,
                    phone = user.telefono ?: "",
                    about = user.informacion ?: "",
                    role = user.rol
                )
            }
        }
    }

    private fun saveProfile() {
        val user = SessionManager.getCurrentUser() ?: return
        val current = _state.value

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

            when (val result = updateProfileUseCase(
                userId = user.idUsuario,
                nombre = current.name,
                apellido = current.lastName,
                correo = current.email,
                telefono = current.phone,
                informacion = current.about
            )) {
                is Try.Success -> {
                    SessionManager.updateCurrentUser(result.value)
                    _state.update { it.copy(isLoading = false, successMessage = "Perfil actualizado con éxito") }
                }
                is Try.Failure -> {
                    val msg = when (val err = result.error) {
                        is AppError.Network -> err.message
                        is AppError.Server -> err.message
                        is AppError.Unauthorized -> "Credenciales inválidas"
                        is AppError.NotFound -> "Recurso no encontrado"
                        is AppError.Unknown -> err.message
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = msg
                        )
                    }
                }
            }
        }
    }
}
