package org.example.project.presentation.di

import org.example.project.domain.usecase.auth.LoginUseCase
import org.example.project.domain.usecase.auth.RegisterUseCase
import org.example.project.domain.usecase.categoria.GetCategoriasUseCase
import org.example.project.domain.usecase.curso.*
import org.example.project.domain.usecase.encargado.GetAvailableManagersUseCase
import org.example.project.domain.usecase.encargado.GetEncargadosUseCase
import org.example.project.domain.usecase.estadisticas.GetEstadisticasUseCase
import org.example.project.domain.usecase.event.*
import org.example.project.domain.usecase.notificacion.GetNotificacionesUseCase
import org.example.project.domain.usecase.notificacion.MarkNotificacionReadUseCase
import org.example.project.domain.usecase.recinto.GetRecintosUseCase
import org.example.project.domain.usecase.user.GetUserHistoryUseCase
import org.example.project.domain.usecase.user.UpdateProfileUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetEventsUseCase(get()) }
    factory { GetEventDetailUseCase(get()) }
    factory { EnrollUserUseCase(get()) }
    factory { GetManagerEventsUseCase(get()) }
    factory { ChangeEventStatusUseCase(get()) }
    factory { UpdateAttendanceUseCase(get()) }
    factory { GetUserHistoryUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }

    factory { GetCursosUseCase(get()) }
    factory { GetCursoDetailUseCase(get()) }
    factory { GetAvailableCursosUseCase(get()) }
    factory { EnrollCursoUseCase(get()) }
    factory { CheckCursoEnrollmentUseCase(get()) }
    factory { PublicarCursoUseCase(get()) }
    factory { CancelarCursoUseCase(get()) }
    factory { DeleteCursoUseCase(get()) }
    factory { CreateCursoUseCase(get()) }

    factory { GetRecintosUseCase(get()) }
    factory { GetCategoriasUseCase(get()) }
    factory { GetEncargadosUseCase(get()) }
    factory { GetAvailableManagersUseCase(get()) }
    factory { GetEstadisticasUseCase(get()) }
    factory { GetNotificacionesUseCase(get()) }
    factory { MarkNotificacionReadUseCase(get()) }
}
