package org.example.project.presentation.features.cursos.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.error.Try
import org.example.project.core.error.displayMessage
import org.example.project.domain.manager.SessionManager
import org.example.project.domain.repository.CursoRepository
import org.example.project.domain.repository.ParticipanteInfo
import org.example.project.domain.usecase.curso.CheckCursoEnrollmentUseCase
import org.example.project.domain.usecase.curso.EnrollCursoUseCase
import org.example.project.domain.usecase.curso.GetCursoDetailUseCase

class CursoDetailViewModel(
    private val getCursoDetailUseCase: GetCursoDetailUseCase,
    private val enrollCursoUseCase: EnrollCursoUseCase,
    private val checkEnrollmentUseCase: CheckCursoEnrollmentUseCase,
    private val cursoRepository: CursoRepository
) : ScreenModel {
    private val _state = MutableStateFlow(CursoDetailState())
    val state = _state.asStateFlow()

    fun loadCurso(id: Int) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getCursoDetailUseCase(id)) {
                is Try.Success -> {
                    _state.update { it.copy(curso = result.value, isLoading = false) }
                    checkEnrollment(id)
                }
                is Try.Failure -> {
                    _state.update { it.copy(isLoading = false, error = result.error.displayMessage()) }
                }
            }
        }
    }

    private suspend fun checkEnrollment(cursoId: Int) {
        val user = SessionManager.getCurrentUser()
        println("=== DEBUG CursoDetailViewModel.checkEnrollment: cursoId=$cursoId, user=$user ===")
        val userId = user?.idUsuario
        if (userId == null) {
            println("=== DEBUG CursoDetailViewModel.checkEnrollment: NO USER LOGGED IN, skipping ===")
            return
        }
        _state.update { it.copy(isCheckingEnrollment = true) }
        val status = checkEnrollmentUseCase(cursoId, userId)
        println("=== DEBUG CursoDetailViewModel.checkEnrollment: status.inscrito=${status.inscrito} ===")
        val participantesRegistrados = if (status.inscrito) {
            status.sesiones
                .flatMap { it.inscripciones }
                .distinctBy { "${it.nombre}|${it.apellido}" }
                .map { ParticipanteUI(nombre = it.nombre ?: "", apellido = it.apellido ?: "", edad = it.edad?.toString() ?: "") }
        } else {
            emptyList()
        }
        println("=== DEBUG CursoDetailViewModel: participantesRegistrados=${participantesRegistrados.size} ===")
        _state.update { it.copy(enrollmentStatus = status, isCheckingEnrollment = false, participantesRegistrados = participantesRegistrados) }
    }

    fun onEvent(event: CursoDetailEvent) {
        when (event) {
            is CursoDetailEvent.AddParticipante -> {
                val list = _state.value.participantes.toMutableList()
                list.add(ParticipanteUI())
                _state.update { it.copy(participantes = list) }
            }
            is CursoDetailEvent.RemoveParticipante -> {
                val list = _state.value.participantes.toMutableList()
                if (list.size > 1) list.removeAt(event.index)
                _state.update { it.copy(participantes = list) }
            }
            is CursoDetailEvent.UpdateParticipanteNombre -> {
                val list = _state.value.participantes.toMutableList()
                list[event.index] = list[event.index].copy(nombre = event.nombre)
                _state.update { it.copy(participantes = list) }
            }
            is CursoDetailEvent.UpdateParticipanteApellido -> {
                val list = _state.value.participantes.toMutableList()
                list[event.index] = list[event.index].copy(apellido = event.apellido)
                _state.update { it.copy(participantes = list) }
            }
            is CursoDetailEvent.UpdateParticipanteEdad -> {
                val list = _state.value.participantes.toMutableList()
                list[event.index] = list[event.index].copy(edad = event.edad)
                _state.update { it.copy(participantes = list) }
            }
            is CursoDetailEvent.Enroll -> enroll()
            is CursoDetailEvent.ClearMessage -> {
                _state.update { it.copy(enrollmentMessage = null) }
            }
        }
    }

    private fun enroll() {
        val curso = _state.value.curso ?: return
        val userId = SessionManager.getCurrentUser()?.idUsuario ?: return
        val participantes = _state.value.participantes.map {
            ParticipanteInfo(nombre = it.nombre, apellido = it.apellido, edad = it.edad.toIntOrNull() ?: 0)
        }
        println("=== DEBUG CursoDetailViewModel.enroll: cursoId=${curso.idCurso}, userId=$userId, participantes=$participantes ===")

        screenModelScope.launch {
            _state.update { it.copy(isEnrolling = true) }
            println("=== DEBUG CursoDetailViewModel.enroll: calling enrollCursoUseCase... ===")
            when (val result = enrollCursoUseCase(curso.idCurso, userId, participantes)) {
                is Try.Success -> {
                    println("=== DEBUG CursoDetailViewModel.enroll SUCCESS: ${result.value} ===")
                    _state.update { it.copy(isEnrolling = false, enrollmentMessage = "Inscripción exitosa") }
                    checkEnrollment(curso.idCurso)
                }
                is Try.Failure -> {
                    println("=== DEBUG CursoDetailViewModel.enroll FAILURE: ${result.error.displayMessage()} ===")
                    _state.update { it.copy(isEnrolling = false, enrollmentMessage = result.error.displayMessage()) }
                }
            }
        }
    }
}
