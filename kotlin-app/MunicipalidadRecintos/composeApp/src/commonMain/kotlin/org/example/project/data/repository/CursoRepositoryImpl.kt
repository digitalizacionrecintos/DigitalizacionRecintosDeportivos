package org.example.project.data.repository

import org.example.project.core.error.AppError
import org.example.project.core.error.Try
import org.example.project.data.mapper.toDomain
import org.example.project.data.remote.CursoRemoteDataSource
import org.example.project.data.remote.dto.CreateCursoRequest
import org.example.project.data.remote.dto.CursoInscripcionItemDTO
import org.example.project.data.remote.dto.HorarioDTO
import org.example.project.data.remote.dto.RegisterCourseInscriptionRequest
import org.example.project.domain.model.Curso
import org.example.project.domain.repository.CursoEnrollmentStatus
import org.example.project.domain.repository.CursoRepository
import org.example.project.domain.repository.ParticipanteInfo

class CursoRepositoryImpl(
    private val remoteDataSource: CursoRemoteDataSource
) : CursoRepository {

    override suspend fun getCursos(): Try<List<Curso>> {
        return try {
            val cursos = remoteDataSource.getAll().map { it.toDomain() }
            Try.Success(cursos)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar cursos", e))
        }
    }

    override suspend fun getAvailable(): Try<List<Curso>> {
        return try {
            val cursos = remoteDataSource.getAvailable().map { it.toDomain() }
            Try.Success(cursos)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar cursos disponibles", e))
        }
    }

    override suspend fun getCursoDetail(id: Int): Try<Curso> {
        return try {
            val curso = remoteDataSource.getById(id).toDomain()
            Try.Success(curso)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cargar curso", e))
        }
    }

    override suspend fun createCurso(
        nombre: String, descripcion: String, fechaInicio: String, fechaFin: String,
        horaInicio: String, horaFin: String, dias: String, cupo: Int,
        maximoPorInscripcion: Int, idRecinto: Int, idEncargado: Int, idCategoria: Int,
        horarios: List<Pair<String, Pair<String, String>>>
    ): Try<Curso> {
        return try {
            val request = CreateCursoRequest(
                nombre = nombre, descripcion = descripcion,
                fechaInicio = fechaInicio, fechaFin = fechaFin,
                horaInicio = horaInicio, horaFin = horaFin,
                dias = dias, cupo = cupo, maximoPorInscripcion = maximoPorInscripcion,
                idRecinto = idRecinto, idEncargado = idEncargado, idCategoria = idCategoria,
                horarios = horarios.map { (dia, times) ->
                    HorarioDTO(dia = dia, horaInicio = times.first, horaFin = times.second)
                }
            )
            Try.Success(remoteDataSource.create(request).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al crear curso", e))
        }
    }

    override suspend fun updateCurso(
        id: Int, nombre: String, descripcion: String, fechaInicio: String, fechaFin: String,
        horaInicio: String, horaFin: String, dias: String, cupo: Int,
        maximoPorInscripcion: Int, idRecinto: Int, idEncargado: Int, idCategoria: Int,
        horarios: List<Pair<String, Pair<String, String>>>
    ): Try<Curso> {
        return try {
            val request = CreateCursoRequest(
                nombre = nombre, descripcion = descripcion,
                fechaInicio = fechaInicio, fechaFin = fechaFin,
                horaInicio = horaInicio, horaFin = horaFin,
                dias = dias, cupo = cupo, maximoPorInscripcion = maximoPorInscripcion,
                idRecinto = idRecinto, idEncargado = idEncargado, idCategoria = idCategoria,
                horarios = horarios.map { (dia, times) ->
                    HorarioDTO(dia = dia, horaInicio = times.first, horaFin = times.second)
                }
            )
            Try.Success(remoteDataSource.update(id, request).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al actualizar curso", e))
        }
    }

    override suspend fun deleteCurso(id: Int): Try<Unit> {
        return try {
            remoteDataSource.delete(id)
            Try.Success(Unit)
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al eliminar curso", e))
        }
    }

    override suspend fun publicarCurso(id: Int): Try<Curso> {
        return try {
            Try.Success(remoteDataSource.publicar(id).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al publicar curso", e))
        }
    }

    override suspend fun cancelarCurso(id: Int): Try<Curso> {
        return try {
            Try.Success(remoteDataSource.cancelar(id).toDomain())
        } catch (e: Exception) {
            Try.Failure(AppError.Network(e.message ?: "Error al cancelar curso", e))
        }
    }

    override suspend fun checkEnrollment(cursoId: Int, userId: Int): CursoEnrollmentStatus {
        return try {
            val status = remoteDataSource.checkEnrollment(cursoId, userId)
            println("=== DEBUG checkEnrollment: inscrito=${status.inscrito}, sesiones=${status.sesiones.size} ===")
            CursoEnrollmentStatus(
                inscrito = status.inscrito,
                sesiones = status.sesiones.map { it.toDomain() }
            )
        } catch (e: Exception) {
            println("=== DEBUG checkEnrollment ERROR: ${e.message} ===")
            e.printStackTrace()
            CursoEnrollmentStatus(inscrito = false)
        }
    }

    override suspend fun enrollUser(
        cursoId: Int, userId: Int, participantes: List<ParticipanteInfo>
    ): Try<String> {
        println("=== DEBUG enrollUser: cursoId=$cursoId, userId=$userId, participantes=$participantes ===")
        return try {
            val request = RegisterCourseInscriptionRequest(
                idUsuario = userId, idCurso = cursoId,
                listaInscripcion = participantes.map {
                    CursoInscripcionItemDTO(nombre = it.nombre, apellido = it.apellido, edad = it.edad)
                }
            )
            val result = remoteDataSource.registerInscription(request)
            println("=== DEBUG enrollUser SUCCESS: $result ===")
            Try.Success(result)
        } catch (e: Exception) {
            println("=== DEBUG enrollUser ERROR: ${e.message} ===")
            e.printStackTrace()
            Try.Failure(AppError.Network(e.message ?: "Error al inscribir en curso", e))
        }
    }
}
