package org.example.project.presentation.features.events.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.error.Try
import org.example.project.domain.model.Event
import org.example.project.domain.usecase.event.GetEventsUseCase

class EventListViewModel(
    private val getEventsUseCase: GetEventsUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(EventListState())
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

        var filtered = allEvents.filter { event ->
            val matchesQuery = query.isBlank() ||
                    event.title.contains(query, ignoreCase = true) ||
                    event.location.contains(query, ignoreCase = true)

            val matchesCategory = categoryId == null ||
                    event.categoria?.id == categoryId

            val matchesDate = date == null || event.rawDate.startsWith(date)

            matchesQuery && matchesCategory && matchesDate
        }

        filtered = when (sortOrder) {
            DateSortOrder.ASCENDING -> filtered.sortedBy { event ->
                try { event.rawDate } catch (e: Exception) { "9999-12-31" }
            }
            DateSortOrder.DESCENDING -> filtered.sortedByDescending { event ->
                try { event.rawDate } catch (e: Exception) { "0000-01-01" }
            }
            DateSortOrder.NONE -> filtered
        }

        _state.update { it.copy(events = filtered) }
    }

    fun refresh() {
        loadEvents(isRefresh = true)
    }

    private fun loadEvents(isRefresh: Boolean = false) {
        screenModelScope.launch {
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = true) }
            } else {
                _state.update { it.copy(isLoading = true) }
            }

            when (val result = getEventsUseCase()) {
                is Try.Success -> {
                    allEvents = result.value
                    val uniqueCategories = allEvents.mapNotNull { it.categoria }
                        .distinctBy { it.id }
                        .sortedBy { it.nombre }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            events = allEvents,
                            categories = uniqueCategories
                        )
                    }
                }
                is Try.Failure -> {
                    _state.update { it.copy(isLoading = false, isRefreshing = false) }
                }
            }
        }
    }
}
