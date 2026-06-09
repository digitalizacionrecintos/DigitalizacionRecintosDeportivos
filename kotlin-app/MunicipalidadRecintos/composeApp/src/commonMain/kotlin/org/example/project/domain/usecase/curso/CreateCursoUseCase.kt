package org.example.project.domain.usecase.curso

import org.example.project.core.error.Try
import org.example.project.domain.model.Curso
import org.example.project.domain.repository.CursoRepository

class CreateCursoUseCase(
    private val repository: CursoRepository
) {
    suspend operator fun invoke(
        nombre: String, descripcion: String, fechaInicio: String, fechaFin: String,
        horaInicio: String, horaFin: String, dias: String, cupo: Int,
        maximoPorInscripcion: Int, idRecinto: Int, idEncargado: Int, idCategoria: Int,
        horarios: List<Pair<String, Pair<String, String>>>
    ): Try<Curso> = repository.createCurso(
        nombre, descripcion, fechaInicio, fechaFin, horaInicio, horaFin, dias,
        cupo, maximoPorInscripcion, idRecinto, idEncargado, idCategoria, horarios
    )
}
