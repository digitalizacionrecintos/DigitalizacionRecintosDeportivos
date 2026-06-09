package org.example.project.data.mapper

import org.example.project.data.remote.dto.CursoDTO
import org.example.project.data.remote.dto.CategoriaCursosDTO
import org.example.project.data.remote.dto.CursoPopularDTO
import org.example.project.data.remote.dto.EstadisticasCursosDTO
import org.example.project.data.remote.dto.EstadisticasResponseDTO
import org.example.project.domain.model.CategoriaCursoItem
import org.example.project.domain.model.CursoPopularItem
import org.example.project.data.remote.dto.HorarioDTO
import org.example.project.data.remote.dto.InscripcionDTO
import org.example.project.data.remote.dto.ManagerEventDTO
import org.example.project.data.remote.dto.NotificacionDTO
import org.example.project.data.remote.dto.SesionDTO
import org.example.project.domain.model.Categoria
import org.example.project.domain.model.Curso
import org.example.project.domain.model.EstadisticaItem
import org.example.project.domain.model.EstadisticasCursos
import org.example.project.domain.model.EstadisticasGenerales
import org.example.project.domain.model.Horario
import org.example.project.domain.model.Inscripcion
import org.example.project.domain.model.Notificacion
import org.example.project.domain.model.Recinto
import org.example.project.domain.model.Sesion

fun CursoDTO.toDomain(): Curso {
    val recintoDomain = recinto?.toDomain()
    val encargadoDomain = encargado?.let {
        org.example.project.domain.model.Encargado(
            idUsuario = it.idUsuario ?: it.id ?: 0,
            nombre = it.nombre,
            apellido = it.apellido,
            correo = it.correo,
            rol = it.rol
        )
    }
    val categoriaDomain = categoria?.let {
        Categoria(id = it.id, nombre = it.nombre, descripcion = it.descripcion)
    }
    return Curso(
        idCurso = idCurso ?: 0,
        nombre = nombre,
        descripcion = descripcion,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        cupo = cupo,
        maximoPorInscripcion = maximoPorInscripcion,
        estado = estado,
        recinto = recintoDomain,
        encargado = encargadoDomain,
        categoria = categoriaDomain,
        horarios = horarios.map { it.toDomain() },
        sesiones = sesiones.map { it.toCursoEvent() },
        cantidadSesiones = cantidadSesiones
    )
}

fun HorarioDTO.toDomain(): Horario = Horario(
    dia = dia,
    horaInicio = horaInicio,
    horaFin = horaFin
)

fun ManagerEventDTO.toCursoEvent(): org.example.project.domain.model.Event {
    val recintoDomain = recinto?.toDomain()
    return org.example.project.domain.model.Event(
        id = id?.toString() ?: "",
        title = titulo,
        imagenUrl = imagenUrl?.takeIf { it.isNotBlank() } ?: recintoDomain?.imagenUrl ?: "",
        location = recintoDomain?.nombre ?: "Sin Recinto",
        date = "$fechaInicio $horaInicio",
        description = descripcion,
        organizerName = "Encargado ${encargadoId ?: "?"}",
        recinto = recintoDomain,
        cupoMaximo = cupoMaximo
    )
}

fun InscripcionDTO.toDomain(): Inscripcion = Inscripcion(
    idInscripcion = idInscripcion,
    nombre = nombre,
    apellido = apellido,
    edad = edad,
    estadoAsistencia = estadoAsistencia
)

fun SesionDTO.toDomain(): Sesion = Sesion(
    tituloEvento = tituloEvento,
    fechaInicio = fechaInicio,
    horaInicio = horaInicio,
    horaFin = horaFin,
    inscripciones = inscripciones.map { it.toDomain() }
)

fun EstadisticasResponseDTO.toDomain(): EstadisticasGenerales = EstadisticasGenerales(
    totalEventos = resumen?.totalEventos ?: 0,
    porcentajeAsistencia = resumen?.porcentajeAsistencia ?: 0.0,
    tasaAusentismo = resumen?.tasaAusentismo ?: 0.0,
    promedioEventosMensual = resumen?.promedioEventosMensual ?: 0.0,
    eventosPorCategoria = categorias.map { EstadisticaItem(it.nombre, it.cantidad.toInt()) },
    eventosPorRecinto = recintos.map { EstadisticaItem(it.nombre, it.cantidad.toInt()) },
    eventosPorMes = distribucionTemporal?.porMes?.mapKeys { ensureSpanishMonth(it.key) } ?: emptyMap(),
    eventosPorAnio = distribucionTemporal?.porAnio ?: emptyMap(),
    eventosPorDia = distribucionTemporal?.porDia ?: emptyMap()
)

fun EstadisticasCursosDTO.toDomain(): EstadisticasCursos = EstadisticasCursos(
    totalCursos = resumen?.totalCursos ?: 0,
    totalInscritos = resumen?.totalInscritos ?: 0,
    promedioInscritosPorCurso = resumen?.promedioInscritosPorCurso ?: 0.0,
    cursosPopulares = cursosPopulares.map { it.toDomain() },
    ocupacionLlenos = ocupacion?.llenos ?: 0,
    ocupacionAlta = ocupacion?.altaOcupacion ?: 0,
    ocupacionBaja = ocupacion?.bajaOcupacion ?: 0,
    porCategoria = porCategoria.map { it.toDomain() },
    tendenciaMensual = tendenciaMensual.map { EstadisticaItem(ensureSpanishMonth(it.mes), it.inscritos) }
)

fun CursoPopularDTO.toDomain(): CursoPopularItem = CursoPopularItem(
    nombre = nombre,
    inscritos = inscritos,
    cupoMaximo = cupoMaximo,
    porcentajeOcupacion = porcentajeOcupacion,
    categoria = categoria
)

fun CategoriaCursosDTO.toDomain(): CategoriaCursoItem = CategoriaCursoItem(
    categoria = categoria,
    inscritos = inscritos,
    cursos = cursos
)

private val englishToSpanishMonth = mapOf(
    "January" to "Enero", "February" to "Febrero", "March" to "Marzo",
    "April" to "Abril", "May" to "Mayo", "June" to "Junio",
    "July" to "Julio", "August" to "Agosto", "September" to "Septiembre",
    "October" to "Octubre", "November" to "Noviembre", "December" to "Diciembre"
)

private fun ensureSpanishMonth(name: String): String =
    englishToSpanishMonth[name] ?: name

fun NotificacionDTO.toDomain(): Notificacion = Notificacion(
    idNotificacion = idNotificacion,
    titulo = titulo,
    mensaje = mensaje,
    leida = leida,
    fechaCreacion = fechaCreacion,
    tipo = tipo,
    idReferencia = idReferencia
)
