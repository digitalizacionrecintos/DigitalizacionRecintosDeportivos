package org.example.project.domain.usecase.recinto

import org.example.project.core.error.Try
import org.example.project.domain.model.Recinto
import org.example.project.domain.repository.RecintoRepository

class GetRecintosUseCase(
    private val repository: RecintoRepository
) {
    suspend operator fun invoke(): Try<List<Recinto>> = repository.getAll()
}
