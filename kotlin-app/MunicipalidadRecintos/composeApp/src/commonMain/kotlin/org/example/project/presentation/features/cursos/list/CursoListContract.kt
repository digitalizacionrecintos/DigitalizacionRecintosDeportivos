package org.example.project.presentation.features.cursos.list

import org.example.project.domain.model.Curso

data class CursoListState(
    val cursos: List<Curso> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

sealed interface CursoListEvent {
    data class OnSearchQueryChange(val query: String) : CursoListEvent
    data class OnCursoClick(val cursoId: Int) : CursoListEvent
}
