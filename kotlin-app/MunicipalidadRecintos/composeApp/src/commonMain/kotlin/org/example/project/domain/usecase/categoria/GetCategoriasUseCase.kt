package org.example.project.domain.usecase.categoria

import org.example.project.core.error.Try
import org.example.project.domain.model.Categoria
import org.example.project.domain.repository.CategoriaRepository

class GetCategoriasUseCase(
    private val repository: CategoriaRepository
) {
    suspend operator fun invoke(): Try<List<Categoria>> = repository.getAll()
}
