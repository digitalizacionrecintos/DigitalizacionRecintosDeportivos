package org.example.project.presentation.features.estadisticas

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.error.Try
import org.example.project.core.error.displayMessage
import org.example.project.domain.usecase.estadisticas.GetEstadisticasUseCase

class EstadisticasViewModel(
    private val getEstadisticasUseCase: GetEstadisticasUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(EstadisticasState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun onEvent(event: EstadisticasEvent) {
        when (event) {
            is EstadisticasEvent.SelectYear -> {
                _state.update { it.copy(selectedYear = event.year) }
                load(event.year)
            }
        }
    }

    private fun load(anio: Int? = null) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getEstadisticasUseCase(anio)) {
                is Try.Success -> {
                    _state.update { it.copy(generales = result.value, isLoading = false) }
                }
                is Try.Failure -> {
                    _state.update { it.copy(isLoading = false, error = result.error.displayMessage()) }
                }
            }
        }
    }
}
