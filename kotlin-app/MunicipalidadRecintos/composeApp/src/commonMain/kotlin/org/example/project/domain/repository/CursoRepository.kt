package org.example.project.domain.repository

import org.example.project.core.error.Try
import org.example.project.domain.model.Curso

interface CursoRepository {
    suspend fun getCursos(): Try<List<Curso>>
    suspend fun getAvailable(): Try<List<Curso>>
    suspend fun getCursoDetail(id: Int): Try<Curso>
    suspend fun createCurso(
        nombre: String,
        descripcion: String,
        fechaInicio: String,
        fechaFin: String,
        horaInicio: String,
        horaFin: String,
        dias: String,
        cupo: Int,
        maximoPorInscripcion: Int,
        idRecinto: Int,
        idEncargado: Int,
        idCategoria: Int,
        horarios: List<Pair<String, Pair<String, String>>>
    ): Try<Curso>
    suspend fun updateCurso(
        id: Int,
        nombre: String,
        descripcion: String,
        fechaInicio: String,
        fechaFin: String,
        horaInicio: String,
        horaFin: String,
        dias: String,
        cupo: Int,
        maximoPorInscripcion: Int,
        idRecinto: Int,
        idEncargado: Int,
        idCategoria: Int,
        horarios: List<Pair<String, Pair<String, String>>>
    ): Try<Curso>
    suspend fun deleteCurso(id: Int): Try<Unit>
    suspend fun publicarCurso(id: Int): Try<Curso>
    suspend fun cancelarCurso(id: Int): Try<Curso>
    suspend fun checkEnrollment(cursoId: Int, userId: Int): CursoEnrollmentStatus
    suspend fun enrollUser(cursoId: Int, userId: Int, participantes: List<ParticipanteInfo>): Try<String>
}

data class CursoEnrollmentStatus(
    val inscrito: Boolean,
    val sesiones: List<org.example.project.domain.model.Sesion> = emptyList()
)

data class ParticipanteInfo(
    val nombre: String,
    val apellido: String,
    val edad: Int
)
