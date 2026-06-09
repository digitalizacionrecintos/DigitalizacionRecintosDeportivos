package org.example.project.domain.repository

import org.example.project.core.error.Try
import org.example.project.domain.model.CursoHistorial
import org.example.project.domain.model.Event
import org.example.project.domain.model.Encargado

interface UserRepository {
    suspend fun getUserEventHistory(userId: Int): Try<List<Event>>
    suspend fun getUserCourseHistory(userId: Int): Try<List<CursoHistorial>>
    suspend fun updateProfile(
        userId: Int,
        nombre: String,
        apellido: String,
        correo: String,
        telefono: String,
        informacion: String
    ): Try<Encargado>
}
