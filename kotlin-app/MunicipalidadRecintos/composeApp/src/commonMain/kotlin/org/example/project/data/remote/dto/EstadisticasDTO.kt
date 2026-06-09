package org.example.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EstadisticasResponseDTO(
    val resumen: ResumenEstadisticasDTO? = null,
    val categorias: List<CategoriaStatsDTO> = emptyList(),
    val recintos: List<RecintoStatsDTO> = emptyList(),
    val distribucionTemporal: DistribucionTemporalDTO? = null
)

@Serializable
data class ResumenEstadisticasDTO(
    val totalEventos: Long = 0,
    val porcentajeAsistencia: Double = 0.0,
    val tasaAusentismo: Double = 0.0,
    val promedioEventosMensual: Double = 0.0
)

@Serializable
data class CategoriaStatsDTO(
    val nombre: String = "",
    val cantidad: Long = 0
)

@Serializable
data class RecintoStatsDTO(
    val nombre: String = "",
    val cantidad: Long = 0
)

@Serializable
data class DistribucionTemporalDTO(
    val porMes: Map<String, Long> = emptyMap(),
    val porAnio: Map<String, Long> = emptyMap(),
    val porDia: Map<String, Long> = emptyMap()
)

@Serializable
data class EstadisticasCursosDTO(
    val resumen: ResumenCursosDTO? = null,
    val cursosPopulares: List<CursoPopularDTO> = emptyList(),
    val ocupacion: OcupacionCursosDTO? = null,
    val porCategoria: List<CategoriaCursosDTO> = emptyList(),
    val tendenciaMensual: List<TendenciaMensualDTO> = emptyList()
)

@Serializable
data class ResumenCursosDTO(
    val totalCursos: Int = 0,
    val totalInscritos: Int = 0,
    val promedioInscritosPorCurso: Double = 0.0
)

@Serializable
data class CursoPopularDTO(
    val nombre: String = "",
    val inscritos: Int = 0,
    val cupoMaximo: Int = 0,
    val porcentajeOcupacion: Double = 0.0,
    val categoria: String = ""
)

@Serializable
data class OcupacionCursosDTO(
    val llenos: Int = 0,
    val altaOcupacion: Int = 0,
    val bajaOcupacion: Int = 0
)

@Serializable
data class CategoriaCursosDTO(
    val categoria: String = "",
    val inscritos: Int = 0,
    val cursos: Int = 0
)

@Serializable
data class TendenciaMensualDTO(
    val mes: String = "",
    val inscritos: Int = 0
)
