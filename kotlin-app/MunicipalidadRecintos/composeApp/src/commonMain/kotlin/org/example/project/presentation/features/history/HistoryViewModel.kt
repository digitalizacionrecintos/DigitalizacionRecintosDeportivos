package org.example.project.presentation.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.remote.ApiService
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.model.Event
import org.example.project.domain.util.DateHelper

data class HistoryState(
        val upcomingEvents: List<Event> = emptyList(),
        val pastEvents: List<Event> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isRefreshing: Boolean = false,
        val searchQuery: String = "",
        val selectedYear: String? = null,
        val selectedMonth: String? = null,
        val availableYears: List<String> = emptyList(),
        val availableMonths: List<Pair<String, Int?>> = emptyList()
)

class HistoryViewModel : ViewModel() {
        private val _state = MutableStateFlow(HistoryState())
        val state = _state.asStateFlow()
        private val apiService = ApiService()
        private val dateHelper = DateHelper()

        private var allUpcomingEvents: List<Event> = emptyList()
        private var allPastEvents: List<Event> = emptyList()
        private var allUpcomingEventsWithDates: List<Pair<Event, String>> = emptyList()
        private var allPastEventsWithDates: List<Pair<Event, String>> = emptyList()

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
                applyFilters()
        }

        private fun loadHistory(isRefresh: Boolean = false) {
                val currentUser = SessionManager.getCurrentUser()
                if (currentUser == null) {
                        _state.update { it.copy(error = "Usuario no identificado") }
                        return
                }

                viewModelScope.launch {
                        if (isRefresh) {
                                _state.update { it.copy(isRefreshing = true) }
                        } else {
                                _state.update { it.copy(isLoading = true) }
                        }
                        try {
                                val enrollments = apiService.getUserEnrollments(currentUser.id)
                                val currentDateIso =
                                        dateHelper.getCurrentDateTimeIso()

                                val (upcomingDto, pastDto) =
                                        enrollments.partition { dto ->

                                                dto.fecha >= currentDateIso
                                        }

                                allUpcomingEventsWithDates =
                                        upcomingDto.map { dto -> mapDtoToEvent(dto) to dto.fecha }
                                allPastEventsWithDates =
                                        pastDto.map { dto -> mapDtoToEvent(dto) to dto.fecha }
                                allUpcomingEvents = allUpcomingEventsWithDates.map { it.first }
                                allPastEvents = allPastEventsWithDates.map { it.first }

                                val years =
                                        (allUpcomingEventsWithDates + allPastEventsWithDates)
                                                .mapNotNull { (_, dateStr) ->
                                                        dateStr.split("-", " ", "T").firstOrNull()
                                                }
                                                .mapNotNull { it.toIntOrNull() }
                                                .distinct()
                                                .sorted()
                                                .map { it.toString() }

                                _state.update {
                                        it.copy(
                                                isLoading = false,
                                                isRefreshing = false,
                                                availableYears = years
                                        )
                                }
                                applyFilters()
                        } catch (e: Exception) {
                                _state.update {
                                        it.copy(
                                                isLoading = false,
                                                isRefreshing = false,
                                                error = "Error al cargar historial"
                                        )
                                }
                        }
                }
        }

        private fun applyFilters() {
                val query = _state.value.searchQuery
                val selectedYear = _state.value.selectedYear
                val selectedMonth = _state.value.selectedMonth

                val filteredUpcoming =
                        allUpcomingEventsWithDates
                                .filter { (event, dateStr) ->
                                        val searchMatch =
                                                if (query.isBlank()) true
                                                else
                                                        event.title.contains(
                                                                query,
                                                                ignoreCase = true
                                                        ) ||
                                                                event.location.contains(
                                                                        query,
                                                                        ignoreCase = true
                                                                )

                                        val yearMatch =
                                                if (selectedYear == null) true
                                                else dateStr.startsWith(selectedYear)

                                        val monthMatch =
                                                if (selectedMonth == null) true
                                                else {
                                                        val dateParts = dateStr.split("-")
                                                        dateParts.getOrNull(1) == selectedMonth
                                                }

                                        searchMatch && yearMatch && monthMatch
                                }
                                .map { it.first }

                val filteredPast =
                        allPastEventsWithDates
                                .filter { (event, dateStr) ->
                                        val searchMatch =
                                                if (query.isBlank()) true
                                                else
                                                        event.title.contains(
                                                                query,
                                                                ignoreCase = true
                                                        ) ||
                                                                event.location.contains(
                                                                        query,
                                                                        ignoreCase = true
                                                                )

                                        val yearMatch =
                                                if (selectedYear == null) true
                                                else dateStr.startsWith(selectedYear)

                                        val monthMatch =
                                                if (selectedMonth == null) true
                                                else {
                                                        val dateParts = dateStr.split("-")
                                                        dateParts.getOrNull(1) == selectedMonth
                                                }

                                        searchMatch && yearMatch && monthMatch
                                }
                                .map { it.first }

                _state.update {
                        it.copy(upcomingEvents = filteredUpcoming, pastEvents = filteredPast)
                }
        }

        private fun updateAvailableMonths(year: String?) {
                val months =
                        listOf(
                                "Todos" to null,
                                "Enero" to 1,
                                "Febrero" to 2,
                                "Marzo" to 3,
                                "Abril" to 4,
                                "Mayo" to 5,
                                "Junio" to 6,
                                "Julio" to 7,
                                "Agosto" to 8,
                                "Septiembre" to 9,
                                "Octubre" to 10,
                                "Noviembre" to 11,
                                "Diciembre" to 12
                        )
                _state.update { it.copy(availableMonths = months) }
        }
        private fun mapDtoToEvent(dto: org.example.project.data.remote.dto.UserHistoryDTO): Event {
                val dateParts = dto.fecha.split(" ", "T")
                val dateStr = dateParts.getOrElse(0) { "" }
                val timeStr = dateParts.getOrElse(1) { "" }

                return Event(
                        id = dto.idEvento.toString(),
                        title = dto.titulo,
                        imagenUrl = "",
                        location = dto.ubicacion ?: "Ubicación desconocida",
                        date =
                                org.example.project.domain.util.DateTimeUtils.formatEventDate(
                                        dateStr,
                                        timeStr
                                ),
                        description = "Asistencia: ${dto.estadoAsistencia ?: "No registrada"}",
                        organizerName = "Muni Arica",
                        isEnrolled = true,
                        enrolledStatus = dto.estadoEvento ?: "INSCRITO",
                        recinto = null
                )
        }
}
