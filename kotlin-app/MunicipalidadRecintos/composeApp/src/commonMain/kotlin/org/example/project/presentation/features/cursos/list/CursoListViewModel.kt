package org.example.project.presentation.features.cursos.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.error.Try
import org.example.project.core.error.displayMessage
import org.example.project.domain.usecase.curso.GetAvailableCursosUseCase

class CursoListViewModel(
    private val getAvailableCursosUseCase: GetAvailableCursosUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(CursoListState())
    val state = _state.asStateFlow()

    private var allCursos: List<org.example.project.domain.model.Curso> = emptyList()

    init {
        loadCursos()
    }

    fun onEvent(event: CursoListEvent) {
        when (event) {
            is CursoListEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
                applyFilter()
            }
            is CursoListEvent.OnCursoClick -> {}
        }
    }

    fun refresh() {
        loadCursos(isRefresh = true)
    }

    private fun loadCursos(isRefresh: Boolean = false) {
        screenModelScope.launch {
            if (isRefresh) _state.update { it.copy(isRefreshing = true) }
            else _state.update { it.copy(isLoading = true, error = null) }

            when (val result = getAvailableCursosUseCase()) {
                is Try.Success -> {
                    allCursos = result.value
                    _state.update {
                        it.copy(
                            cursos = result.value,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
                is Try.Failure -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.error.displayMessage()
                        )
                    }
                }
            }
        }
    }

    private fun applyFilter() {
        val query = _state.value.searchQuery
        val filtered = if (query.isBlank()) allCursos
        else allCursos.filter {
            it.nombre.contains(query, ignoreCase = true) ||
            it.descripcion.contains(query, ignoreCase = true)
        }
        _state.update { it.copy(cursos = filtered) }
    }
}
