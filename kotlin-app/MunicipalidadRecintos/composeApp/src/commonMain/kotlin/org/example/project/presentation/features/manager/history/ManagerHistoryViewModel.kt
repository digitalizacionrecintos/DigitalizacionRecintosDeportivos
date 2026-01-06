package org.example.project.presentation.features.manager.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.remote.ApiService
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.Categoria
import org.example.project.domain.util.DateTimeUtils

class ManagerHistoryViewModel : ScreenModel {
    private val apiService = ApiService()

    private val _state = MutableStateFlow<ManagerHistoryState>(ManagerHistoryState.Loading)
    val state: StateFlow<ManagerHistoryState> = _state.asStateFlow()

    private val _years = MutableStateFlow<List<Int>>(emptyList())
    val years: StateFlow<List<Int>> = _years.asStateFlow()

    private val _categories = MutableStateFlow<List<Categoria>>(emptyList())
    val categories: StateFlow<List<Categoria>> = _categories.asStateFlow()

    var selectedYear by mutableStateOf<Int?>(null)
    var selectedMonth by mutableStateOf<Int?>(null)
    var selectedCategoryId by
            mutableStateOf<Int?>(null)
    var searchQuery by mutableStateOf("")

    init {
        loadHistory()
    }

    fun loadHistory() {
        screenModelScope.launch {
            _state.value = ManagerHistoryState.Loading
            try {
                val currentUser = SessionManager.getCurrentUser()
                val idEncargado = currentUser?.id

                if (idEncargado == null) {
                    return@launch
                }

                val allEventsDto = apiService.getManagerEventsByEncargado(idEncargado)

                val managerHistoryEvents =
                        allEventsDto.filter {
                            val status = it.estado.uppercase()
                            val isFinished = status == "FINALIZADO" || status == "TERMINADO"
                            isFinished
                        }

                val historyEvents =
                        managerHistoryEvents.map { dto ->
                            ManagerHistoryEvent(
                                    id = dto.idEvento.toString(),
                                    title = dto.titulo,
                                    date =
                                            DateTimeUtils.formatEventDate(
                                                    dto.fechaInicio,
                                                    dto.horaInicio
                                            ),
                                    attendeesCount = dto.asistentes.size,
                                    categoryName = dto.categoria?.nombre ?: "Sin categoría",
                                    originalDate = dto.fechaInicio,
                                    attendees =
                                            dto.asistentes.map { attendee ->
                                                AttendeeInfo(
                                                        id = attendee.idInscripcion,
                                                        name = attendee.nombreCompleto,
                                                        email = attendee.correo,
                                                        attendanceStatus = attendee.estadoAsistencia
                                                )
                                            }
                            )
                        }

                val eventYears =
                        historyEvents
                                .mapNotNull {
                                    try {
                                        val datePart =
                                                it.originalDate
                                                        .substringBefore("T")
                                                        .substringBefore(" ")
                                        LocalDate.parse(datePart).year
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                                .distinct()
                                .sortedDescending()

                val currentYear =
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
                val displayYears = if (eventYears.isNotEmpty()) eventYears else listOf(currentYear)

                _years.value = displayYears

                val eventCategories =
                        managerHistoryEvents
                                .mapNotNull { it.categoria }
                                .distinctBy { it.id }
                                .map { Categoria(it.id, it.nombre, "") }
                                .sortedBy { it.nombre }

                _categories.value = eventCategories

                if (selectedYear == null && displayYears.isNotEmpty()) {
                    selectedYear = displayYears.first()
                }

                applyFilters(historyEvents)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value =
                        ManagerHistoryState.Error("Error al cargar el historial: ${e.message}")
            }
        }
    }

    fun onYearSelected(year: Int) {
        selectedYear = year
        filterCurrentData()
    }

    fun onMonthSelected(month: Int?) {
        selectedMonth = month
        filterCurrentData()
    }

    fun onCategorySelected(categoryId: Int?) {
        selectedCategoryId = categoryId
        filterCurrentData()
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        filterCurrentData()
    }

    private var allHistoryEvents: List<ManagerHistoryEvent> = emptyList()

    private fun applyFilters(events: List<ManagerHistoryEvent>) {
        allHistoryEvents = events
        filterCurrentData()
    }

    private fun filterCurrentData() {
        val filtered =
                allHistoryEvents.filter { event ->

                    val searchMatch =
                            if (searchQuery.isBlank()) {
                                true
                            } else {
                                event.title.contains(searchQuery, ignoreCase = true) ||
                                        event.categoryName.contains(searchQuery, ignoreCase = true)
                            }

                    val eventDatePart = event.originalDate.substringBefore("T").substringBefore(" ")
                    val eventYear =
                            try {
                                LocalDate.parse(eventDatePart).year
                            } catch (e: Exception) {
                                -1
                            }
                    val yearMatch = selectedYear == null || eventYear == selectedYear

                    val eventMonth =
                            try {
                                LocalDate.parse(eventDatePart).monthNumber
                            } catch (e: Exception) {
                                -1
                            }
                    val monthMatch = selectedMonth == null || eventMonth == selectedMonth

                    val selectedCategoryName =
                            _categories.value.find { it.id == selectedCategoryId }?.nombre
                    val categoryMatch =
                            selectedCategoryId == null || event.categoryName == selectedCategoryName

                    searchMatch && yearMatch && monthMatch && categoryMatch
                }
        _state.value = ManagerHistoryState.Success(filtered)
    }
}

sealed class ManagerHistoryState {
    object Loading : ManagerHistoryState()
    data class Success(val events: List<ManagerHistoryEvent>) : ManagerHistoryState()
    data class Error(val message: String) : ManagerHistoryState()
}
