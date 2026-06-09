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
import org.example.project.core.error.AppError
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.core.error.Try
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.Categoria
import org.example.project.domain.usecase.event.GetManagerEventsUseCase

data class ManagerHistoryEvent(
    val id: String,
    val title: String,
    val date: String,
    val attendeesCount: Int,
    val categoryName: String,
    val originalDate: String,
    val attendees: List<AttendeeInfo>
)

data class AttendeeInfo(
    val id: Int,
    val name: String,
    val email: String,
    val attendanceStatus: String
)

sealed class ManagerHistoryState {
    data object Loading : ManagerHistoryState()
    data class Success(val events: List<ManagerHistoryEvent>) : ManagerHistoryState()
    data class Error(val message: String) : ManagerHistoryState()
}

class ManagerHistoryViewModel(
    private val getManagerEventsUseCase: GetManagerEventsUseCase
) : ScreenModel {
    private val _state = MutableStateFlow<ManagerHistoryState>(ManagerHistoryState.Loading)
    val state: StateFlow<ManagerHistoryState> = _state.asStateFlow()

    private val _years = MutableStateFlow<List<Int>>(emptyList())
    val years: StateFlow<List<Int>> = _years.asStateFlow()

    private val _categories = MutableStateFlow<List<Categoria>>(emptyList())
    val categories: StateFlow<List<Categoria>> = _categories.asStateFlow()

    var selectedYear by mutableStateOf<Int?>(null)
    var selectedMonth by mutableStateOf<Int?>(null)
    var selectedCategoryId by mutableStateOf<Int?>(null)
    var searchQuery by mutableStateOf("")

    init {
        loadHistory()
    }

    fun loadHistory() {
        screenModelScope.launch {
            _state.value = ManagerHistoryState.Loading
            val currentUser = SessionManager.getCurrentUser()
            val idEncargado = currentUser?.idUsuario ?: return@launch

            when (val result = getManagerEventsUseCase(idEncargado)) {
                is Try.Success -> {
                    val finishedEvents = result.value.filter {
                        val status = it.estado.uppercase()
                        status == "FINALIZADO" || status == "TERMINADO"
                    }

                    val historyEvents = finishedEvents.map { dto ->
                        ManagerHistoryEvent(
                            id = dto.idEvento.toString(),
                            title = dto.titulo,
                            date = dto.fechaInicio,
                            attendeesCount = dto.asistentes.size,
                            categoryName = dto.categoriaName,
                            originalDate = dto.fechaInicio,
                            attendees = dto.asistentes.map { a ->
                                AttendeeInfo(
                                    id = a.idInscripcion,
                                    name = a.nombreCompleto,
                                    email = a.correo,
                                    attendanceStatus = a.estadoAsistencia
                                )
                            }
                        )
                    }

                    val eventYears = finishedEvents
                        .mapNotNull {
                            try {
                                val datePart = it.fechaInicio.substringBefore("T").substringBefore(" ")
                                LocalDate.parse(datePart).year
                            } catch (e: Exception) { null }
                        }
                        .distinct()
                        .sortedDescending()

                    val currentYear = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).year
                    _years.value = if (eventYears.isNotEmpty()) eventYears else listOf(currentYear)

                    val eventCategories = finishedEvents
                        .map { Categoria(0, it.categoriaName, "") }
                        .distinctBy { it.nombre }
                        .sortedBy { it.nombre }
                    _categories.value = eventCategories

                    if (selectedYear == null && _years.value.isNotEmpty()) {
                        selectedYear = _years.value.first()
                    }

                    applyFilters(historyEvents)
                }
                is Try.Failure -> {
                    val msg = when (val err = result.error) {
                        is AppError.Network -> err.message
                        is AppError.Server -> err.message
                        is AppError.Unauthorized -> "Credenciales inválidas"
                        is AppError.NotFound -> "Recurso no encontrado"
                        is AppError.Unknown -> err.message
                    }
                    _state.value = ManagerHistoryState.Error("Error al cargar el historial: $msg")
                }
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
        val filtered = allHistoryEvents.filter { event ->
            val searchMatch = searchQuery.isBlank() ||
                event.title.contains(searchQuery, ignoreCase = true) ||
                event.categoryName.contains(searchQuery, ignoreCase = true)

            val eventDatePart = event.originalDate.substringBefore("T").substringBefore(" ")
            val eventYear = try { LocalDate.parse(eventDatePart).year } catch (e: Exception) { -1 }
            val yearMatch = selectedYear == null || eventYear == selectedYear

            val eventMonth = try { LocalDate.parse(eventDatePart).monthNumber } catch (e: Exception) { -1 }
            val monthMatch = selectedMonth == null || eventMonth == selectedMonth

            val categoryName = _categories.value.find { it.id == selectedCategoryId }?.nombre
            val categoryMatch = selectedCategoryId == null || event.categoryName == categoryName

            searchMatch && yearMatch && monthMatch && categoryMatch
        }
        _state.value = ManagerHistoryState.Success(filtered)
    }
}
