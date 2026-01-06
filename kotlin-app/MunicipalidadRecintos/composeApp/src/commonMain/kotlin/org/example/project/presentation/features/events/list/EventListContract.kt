package org.example.project.presentation.features.events.list

import org.example.project.domain.model.Categoria
import org.example.project.domain.model.Event

enum class DateSortOrder {
    NONE,
    ASCENDING,
    DESCENDING
}

data class EventListState(
        val events: List<Event> = emptyList(),
        val categories: List<Categoria> = emptyList(),
        val selectedCategoryId: Int? = null,
        val selectedDate: String? = null,
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val selectedSortOrder: DateSortOrder = DateSortOrder.NONE
)

sealed interface EventListEvent {
    data class OnSearchQueryChange(val query: String) : EventListEvent
    data class OnEventClick(val eventId: String) : EventListEvent
    data class OnCategorySelect(val categoryId: Int?) : EventListEvent
    data class OnDateSelect(val date: String?) : EventListEvent
    data class OnSortOrderChange(val sortOrder: DateSortOrder) : EventListEvent
}
