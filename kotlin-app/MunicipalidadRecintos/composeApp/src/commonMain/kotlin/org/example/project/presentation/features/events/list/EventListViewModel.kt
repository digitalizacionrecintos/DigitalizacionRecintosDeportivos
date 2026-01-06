package org.example.project.presentation.features.events.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.remote.ApiService
import org.example.project.domain.model.Event

class EventListViewModel : ViewModel() {

    private val _state = MutableStateFlow(EventListState())
    private val apiService = ApiService()

    val state = _state.asStateFlow()

    private var allEvents: List<Event> = emptyList()

    init {

        loadEvents()
    }

    fun onEvent(event: EventListEvent) {
        when (event) {
            is EventListEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
                applyFilters()
            }
            is EventListEvent.OnCategorySelect -> {
                _state.update { it.copy(selectedCategoryId = event.categoryId) }
                applyFilters()
            }
            is EventListEvent.OnDateSelect -> {
                _state.update { it.copy(selectedDate = event.date) }
                applyFilters()
            }
            is EventListEvent.OnSortOrderChange -> {
                _state.update { it.copy(selectedSortOrder = event.sortOrder) }
                applyFilters()
            }
            is EventListEvent.OnEventClick -> {}
        }
    }

    private fun applyFilters() {
        val currentState = _state.value
        val query = currentState.searchQuery
        val categoryId = currentState.selectedCategoryId
        val date = currentState.selectedDate
        val sortOrder = currentState.selectedSortOrder

        var filtered =
                allEvents.filter { event ->
                    val matchesQuery =
                            query.isBlank() ||
                                    event.title.contains(query, ignoreCase = true) ||
                                    event.location.contains(query, ignoreCase = true)

                    val matchesCategory =
                            categoryId == null ||
                                    event.categoria?.id == categoryId ||
                                    event.categoria?.nombre ==
                                            currentState.categories
                                                    .find { it.id == categoryId }
                                                    ?.nombre

                    val matchesDate = date == null || event.rawDate.startsWith(date)

                    matchesQuery && matchesCategory && matchesDate
                }

        filtered =
                when (sortOrder) {
                    DateSortOrder.ASCENDING -> {
                        filtered.sortedBy { event ->
                            try {
                                event.rawDate
                            } catch (e: Exception) {
                                "9999-12-31"
                            }
                        }
                    }
                    DateSortOrder.DESCENDING -> {
                        filtered.sortedByDescending { event ->
                            try {
                                event.rawDate
                            } catch (e: Exception) {
                                "0000-01-01"
                            }
                        }
                    }
                    DateSortOrder.NONE -> filtered
                }

        _state.update { it.copy(events = filtered) }
    }

    fun refresh() {
        loadEvents(isRefresh = true)
    }

    private fun loadEvents(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = true) }
            } else {
                _state.update { it.copy(isLoading = true) }
            }

            try {
                val eventsDto = apiService.getAllEvents()

                val allEventsDto =
                        eventsDto.filter { dto ->
                            val estado = dto.estado.uppercase()
                            estado != "TERMINADO" && estado != "EN_ESPERA" && estado != "FINALIZADO"
                        }

                allEvents =
                        allEventsDto.mapIndexed { index, dto ->

                            val eventId = dto.id?.toString() ?: "temp_$index"

                            val eventCategory =
                                    dto.categoria
                                            ?: org.example.project.domain.model.Categoria(
                                                    id = 0,
                                                    nombre = "General",
                                                    descripcion = "Categoría por defecto"
                                            )

                            Event(
                                    id = eventId,
                                    title = dto.titulo.ifBlank { "Sin título" },
                                    imagenUrl = dto.imagenUrl?.takeIf { it.isNotBlank() }
                                                    ?: dto.recinto?.imagenUrl ?: "",
                                    location = dto.recinto?.nombre ?: "Sin Recinto",
                                    date =
                                            org.example.project.domain.util.DateTimeUtils
                                                    .formatEventDate(
                                                            dto.fechaInicio,
                                                            dto.horaInicio
                                                    ),
                                    description = dto.descripcion.ifBlank { "Sin descripción" },
                                    organizerName = "Encargado ${dto.encargadoId ?: "?"}",
                                    recinto = dto.recinto,
                                    categoria = eventCategory,
                                    cupoMaximo = dto.cupoMaximo,
                                    rawDate =
                                            try {
                                                val datePart =
                                                        if (dto.fechaInicio.contains("T")) {
                                                            dto.fechaInicio.substringBefore("T")
                                                        } else if (dto.fechaInicio.contains(" ")) {
                                                            dto.fechaInicio.substringBefore(" ")
                                                        } else {
                                                            dto.fechaInicio
                                                        }

                                                kotlinx.datetime.LocalDate.parse(datePart)
                                                        .toString()
                                            } catch (e: Exception) {
                                                dto.fechaInicio
                                            }
                            )
                        }

                val uniqueCategories =
                        allEvents.mapNotNull { it.categoria }.distinctBy { it.id }.sortedBy {
                            it.nombre
                        }

                _state.update {
                    it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            events = allEvents,
                            categories = uniqueCategories
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()

                _state.update { it.copy(isLoading = false, isRefreshing = false) }
            }
        }
    }
}
