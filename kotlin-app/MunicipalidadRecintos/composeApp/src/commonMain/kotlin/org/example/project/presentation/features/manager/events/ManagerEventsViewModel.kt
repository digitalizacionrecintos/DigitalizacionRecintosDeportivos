package org.example.project.presentation.features.manager.events

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.ManagerEvent
import org.example.project.domain.usecase.event.GetManagerEventsUseCase

data class ManagerEventsState(
    val events: List<ManagerEvent> = emptyList(),
    val isLoading: Boolean = true,
    val managerId: Int = 0,
    val searchQuery: String = "",
    val sortOrder: String = "Reciente",
    val selectedYear: String? = null,
    val selectedCategory: String? = null,
    val errorMessage: String? = null
)

class ManagerEventsViewModel(
    private val getManagerEventsUseCase: GetManagerEventsUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(ManagerEventsState())
    val state: StateFlow<ManagerEventsState> = _state.asStateFlow()

    private var allEvents: List<ManagerEvent> = emptyList()

    init {
        loadEvents()
    }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun updateSortOrder(order: String) {
        _state.value = _state.value.copy(sortOrder = order)
        applyFilters()
    }

    fun updateSelectedYear(year: String?) {
        _state.value = _state.value.copy(selectedYear = year)
        applyFilters()
    }

    fun updateSelectedCategory(category: String?) {
        _state.value = _state.value.copy(selectedCategory = category)
        applyFilters()
    }

    private fun loadEvents() {
        val currentUser = SessionManager.getCurrentUser() ?: return
        val idEncargado = currentUser.idUsuario

        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            when (val result = getManagerEventsUseCase(idEncargado)) {
                is Try.Success -> {
                    val filtered = result.value.filter {
                        it.estado.uppercase() != "FINALIZADO" && it.estado.uppercase() != "TERMINADO"
                    }

                    allEvents = filtered.map { detail ->
                        ManagerEvent(
                            id = detail.idEvento.toString(),
                            title = detail.titulo,
                            date = detail.fechaInicio,
                            location = detail.ubicacionRecinto,
                            categoryName = detail.categoriaName,
                            imagenUrl = detail.imagenUrl
                        )
                    }

                    _state.value = _state.value.copy(
                        events = allEvents,
                        isLoading = false,
                        managerId = idEncargado
                    )
                }
                is Try.Failure -> {
                    val msg = when (val err = result.error) {
                        is AppError.Network -> "Error de conexión: ${err.message}"
                        is AppError.Server -> "Error del servidor: ${err.message}"
                        is AppError.Unauthorized -> "Credenciales inválidas"
                        is AppError.NotFound -> "Recurso no encontrado"
                        is AppError.Unknown -> err.message
                    }
                    _state.value = _state.value.copy(isLoading = false, errorMessage = msg)
                }
            }
        }
    }

    private fun applyFilters() {
        val s = _state.value
        var filtered = allEvents

        if (s.searchQuery.isNotBlank()) {
            filtered = filtered.filter { event ->
                event.title.contains(s.searchQuery, ignoreCase = true) ||
                    event.location.contains(s.searchQuery, ignoreCase = true)
            }
        }

        if (s.selectedYear != null) {
            filtered = filtered.filter { event -> event.date.contains(s.selectedYear!!) }
        }

        if (s.selectedCategory != null) {
            filtered = filtered.filter { event -> event.categoryName == s.selectedCategory }
        }

        filtered = when (s.sortOrder) {
            "Reciente" -> filtered.sortedByDescending { it.date }
            "Antiguo" -> filtered.sortedBy { it.date }
            else -> filtered
        }

        _state.value = _state.value.copy(events = filtered)
    }

    val availableYears: List<String>
        get() = allEvents
            .mapNotNull { event -> event.date.substringAfterLast("/").take(4).toIntOrNull() }
            .distinct()
            .sorted()
            .map { it.toString() }

    val availableCategories: List<String>
        get() = allEvents.map { it.categoryName }.distinct().sorted()
}
