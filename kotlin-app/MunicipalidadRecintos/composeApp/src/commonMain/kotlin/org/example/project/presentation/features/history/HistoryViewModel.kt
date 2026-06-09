package org.example.project.presentation.features.history

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.error.Try
import org.example.project.core.error.displayMessage
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.CursoHistorial
import org.example.project.domain.model.Event
import org.example.project.domain.repository.UserRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HistoryState(
    val upcomingEvents: List<Event> = emptyList(),
    val pastEvents: List<Event> = emptyList(),
    val cursos: List<CursoHistorial> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedYear: String? = null,
    val selectedMonth: String? = null,
    val availableYears: List<String> = emptyList(),
    val availableMonths: List<Pair<String, Int?>> = emptyList(),
    val selectedTab: Int = 0,
    val selectedEventSubTab: Int = 0
)

class HistoryViewModel(
    private val userRepository: UserRepository
) : ScreenModel {
    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    private var allUpcomingEventsWithDates: List<Pair<Event, String>> = emptyList()
    private var allPastEventsWithDates: List<Pair<Event, String>> = emptyList()
    private var allCursos: List<CursoHistorial> = emptyList()

    init {
        loadHistory()
    }

    fun refresh() {
        loadHistory(isRefresh = true)
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onYearSelected(year: String?) {
        _state.update { it.copy(selectedYear = year) }
        updateAvailableMonths(year)
        applyFilters()
    }

    fun onMonthSelected(month: String?) {
        _state.update { it.copy(selectedMonth = month) }
        updateAvailableMonths(_state.value.selectedYear)
        applyFilters()
    }

    fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    fun onEventSubTabSelected(index: Int) {
        _state.update { it.copy(selectedEventSubTab = index) }
    }

    private fun loadHistory(isRefresh: Boolean = false) {
        val currentUser = SessionManager.getCurrentUser()
        println("=== DEBUG HistoryViewModel.loadHistory: currentUser=$currentUser ===")
        if (currentUser == null) {
            println("=== DEBUG HistoryViewModel: No user logged in ===")
            _state.update { it.copy(error = "Usuario no identificado") }
            return
        }

        screenModelScope.launch {
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = true) }
            } else {
                _state.update { it.copy(isLoading = true) }
            }

            var eventsError: String? = null
            var coursesError: String? = null

            println("=== DEBUG HistoryViewModel: fetching history for userId=${currentUser.idUsuario} ===")
            val eventsResult = userRepository.getUserEventHistory(currentUser.idUsuario)
            val coursesResult = userRepository.getUserCourseHistory(currentUser.idUsuario)

            println("=== DEBUG HistoryViewModel: eventsResult=${eventsResult::class.simpleName}, coursesResult=${coursesResult::class.simpleName} ===")

            when (eventsResult) {
                is Try.Success -> {
                    println("=== DEBUG HistoryViewModel: events count=${eventsResult.value.size} ===")
                    val currentDateIso = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).toString()

                    val (upcoming, past) = eventsResult.value.partition { event ->
                        event.rawDate >= currentDateIso.take(10)
                    }
                    println("=== DEBUG HistoryViewModel: upcoming=${upcoming.size}, past=${past.size} ===")

                    allUpcomingEventsWithDates = upcoming.map { it to it.rawDate }
                    allPastEventsWithDates = past.map { it to it.rawDate }

                    val years = (allUpcomingEventsWithDates + allPastEventsWithDates)
                        .mapNotNull { (_, dateStr) ->
                            dateStr.split("-", " ", "T").firstOrNull()?.toIntOrNull()
                        }
                        .distinct()
                        .sorted()
                        .map { it.toString() }

                    _state.update { it.copy(availableYears = years) }
                }
                is Try.Failure -> {
                    println("=== DEBUG HistoryViewModel: events error=${eventsResult.error.displayMessage()} ===")
                    eventsError = "Error al cargar eventos"
                }
            }

            when (coursesResult) {
                is Try.Success -> {
                    println("=== DEBUG HistoryViewModel: cursos count=${coursesResult.value.size} ===")
                    allCursos = coursesResult.value
                }
                is Try.Failure -> {
                    println("=== DEBUG HistoryViewModel: cursos error=${coursesResult.error.displayMessage()} ===")
                    coursesError = "Error al cargar cursos"
                }
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = eventsError ?: coursesError
                )
            }
            applyFilters()
        }
    }

    private fun applyFilters() {
        val query = _state.value.searchQuery
        val selectedYear = _state.value.selectedYear
        val selectedMonth = _state.value.selectedMonth

        fun filterEvents(eventsWithDates: List<Pair<Event, String>>): List<Event> {
            return eventsWithDates
                .filter { (event, dateStr) ->
                    val searchMatch = query.isBlank() ||
                        event.title.contains(query, ignoreCase = true) ||
                        event.location.contains(query, ignoreCase = true)

                    val yearMatch = selectedYear == null || dateStr.startsWith(selectedYear)

                    val monthMatch = if (selectedMonth == null) true
                    else dateStr.split("-").getOrNull(1) == selectedMonth

                    searchMatch && yearMatch && monthMatch
                }
                .map { it.first }
        }

        fun filterCursos(cursos: List<CursoHistorial>): List<CursoHistorial> {
            if (query.isBlank()) return cursos
            return cursos.filter { curso ->
                curso.nombreCurso.contains(query, ignoreCase = true)
            }
        }

        _state.update {
            it.copy(
                upcomingEvents = filterEvents(allUpcomingEventsWithDates),
                pastEvents = filterEvents(allPastEventsWithDates),
                cursos = filterCursos(allCursos)
            )
        }
    }

    private fun updateAvailableMonths(year: String?) {
        val months = listOf(
            "Todos" to null,
            "Enero" to 1, "Febrero" to 2, "Marzo" to 3, "Abril" to 4,
            "Mayo" to 5, "Junio" to 6, "Julio" to 7, "Agosto" to 8,
            "Septiembre" to 9, "Octubre" to 10, "Noviembre" to 11, "Diciembre" to 12
        )
        _state.update { it.copy(availableMonths = months) }
    }
}
