package org.example.project.presentation.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.remote.ApiService
import org.example.project.data.remote.dto.UpdateUserRequest
import org.example.project.domain.manager.SessionManager

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
    object SaveProfile : ProfileEvent
    object ClearMessages : ProfileEvent
}

class ProfileViewModel : ViewModel() {
    private val apiService = ApiService()
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
            is ProfileEvent.ClearMessages ->
                    _state.update { it.copy(errorMessage = null, successMessage = null) }
        }
    }

    public fun getProfileData() {
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

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                val updatedUser =
                        apiService.updateUser(
                                user.id,
                                UpdateUserRequest(
                                        nombre = current.name,
                                        apellido = current.lastName,
                                        correo = current.email,
                                        telefono = current.phone,
                                        informacion = current.about
                                )
                        )

                SessionManager.updateCurrentUser(updatedUser)
                _state.update {
                    it.copy(isLoading = false, successMessage = "Perfil actualizado con éxito")
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Error al actualizar perfil"
                    )
                }
            }
        }
    }
}
