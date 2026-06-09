package org.example.project.domain.usecase.encargado

import org.example.project.core.error.Try
import org.example.project.domain.model.Encargado
import org.example.project.domain.repository.EncargadoRepository

class GetEncargadosUseCase(
    private val repository: EncargadoRepository
) {
    suspend operator fun invoke(): Try<List<Encargado>> = repository.getAll()
}
