package org.example.project.core.error

sealed interface AppError {
    data class Network(val message: String, val cause: Throwable? = null) : AppError
    data class Server(val code: Int, val message: String) : AppError
    data object Unauthorized : AppError
    data object NotFound : AppError
    data class Unknown(val message: String, val cause: Throwable? = null) : AppError
}

fun AppError.displayMessage(): String = when (this) {
    is AppError.Network -> message
    is AppError.Server -> message
    is AppError.Unauthorized -> "No autorizado"
    is AppError.NotFound -> "No encontrado"
    is AppError.Unknown -> message
}

sealed interface Try<out T> {
    data class Success<T>(val value: T) : Try<T>
    data class Failure(val error: AppError) : Try<Nothing>
}
