package org.example.project.domain.model

data class Curso(
    val idCurso: Int,
    val nombre: String,
    val descripcion: String,
    val fechaInicio: String,
    val fechaFin: String,
    val cupo: Int? = null,
    val maximoPorInscripcion: Int? = null,
    val estado: String,
    val recinto: Recinto? = null,
    val encargado: Encargado? = null,
    val categoria: Categoria? = null,
    val horarios: List<Horario> = emptyList(),
    val sesiones: List<Event> = emptyList(),
    val cantidadSesiones: Int = 0,
    val inscripciones: List<Inscripcion> = emptyList()
)

data class Horario(
    val dia: String,
    val horaInicio: String,
    val horaFin: String
)

data class Inscripcion(
    val idInscripcion: Int,
    val nombre: String? = null,
    val apellido: String? = null,
    val edad: Int? = null,
    val estadoAsistencia: String = "PENDIENTE"
)

data class Sesion(
    val tituloEvento: String,
    val fechaInicio: String,
    val horaInicio: String,
    val horaFin: String,
    val inscripciones: List<Inscripcion> = emptyList()
)

data class EstadisticasGenerales(
    val totalEventos: Long,
    val porcentajeAsistencia: Double,
    val tasaAusentismo: Double,
    val promedioEventosMensual: Double,
    val eventosPorCategoria: List<EstadisticaItem>,
    val eventosPorRecinto: List<EstadisticaItem>,
    val eventosPorMes: Map<String, Long>,
    val eventosPorAnio: Map<String, Long>,
    val eventosPorDia: Map<String, Long>
)

data class EstadisticasCursos(
    val totalCursos: Int,
    val totalInscritos: Int,
    val promedioInscritosPorCurso: Double,
    val cursosPopulares: List<CursoPopularItem>,
    val ocupacionLlenos: Int,
    val ocupacionAlta: Int,
    val ocupacionBaja: Int,
    val porCategoria: List<CategoriaCursoItem>,
    val tendenciaMensual: List<EstadisticaItem>
)

data class CursoPopularItem(
    val nombre: String,
    val inscritos: Int,
    val cupoMaximo: Int,
    val porcentajeOcupacion: Double,
    val categoria: String
)

data class CategoriaCursoItem(
    val categoria: String,
    val inscritos: Int,
    val cursos: Int
)

data class EstadisticaItem(
    val label: String,
    val cantidad: Int
)

data class Notificacion(
    val idNotificacion: Int,
    val titulo: String,
    val mensaje: String,
    val leida: Boolean,
    val fechaCreacion: String? = null,
    val tipo: String? = null,
    val idReferencia: Int? = null
)

data class CursoHistorial(
    val nombreCurso: String,
    val participantes: List<ParticipanteHistorial> = emptyList()
)

data class ParticipanteHistorial(
    val nombre: String,
    val apellido: String,
    val asistencias: List<String> = emptyList()
) {
    val totalSesiones: Int get() = asistencias.size
    val presentes: Int get() = asistencias.count { it == "PRESENTE" }
    val ausentes: Int get() = asistencias.count { it == "AUSENTE" }
    val pendientes: Int get() = asistencias.count { it == "PENDIENTE" }
}
