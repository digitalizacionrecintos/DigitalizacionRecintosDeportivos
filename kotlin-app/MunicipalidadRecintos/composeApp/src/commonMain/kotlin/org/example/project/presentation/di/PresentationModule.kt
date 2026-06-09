package org.example.project.presentation.di

import org.example.project.presentation.features.auth.login.LoginViewModel
import org.example.project.presentation.features.cursos.list.CursoListViewModel
import org.example.project.presentation.features.cursos.detail.CursoDetailViewModel
import org.example.project.presentation.features.estadisticas.EstadisticasViewModel
import org.example.project.presentation.features.events.detail.EventDetailViewModel
import org.example.project.presentation.features.events.list.EventListViewModel
import org.example.project.presentation.features.history.HistoryViewModel
import org.example.project.presentation.features.manager.events.ManagerEventsViewModel
import org.example.project.presentation.features.notificaciones.NotificacionesViewModel
import org.example.project.presentation.features.profile.ProfileViewModel
import org.koin.dsl.module

val presentationModule = module {
    factory { LoginViewModel(get()) }
    factory { EventListViewModel(get()) }
    factory { EventDetailViewModel(get(), get(), get()) }
    factory { HistoryViewModel(get()) }
    factory { ProfileViewModel(get()) }
    factory { ManagerEventsViewModel(get()) }

    factory { CursoListViewModel(get()) }
    factory { CursoDetailViewModel(get(), get(), get(), get()) }
    factory { EstadisticasViewModel(get()) }
    factory { NotificacionesViewModel(get(), get()) }
}
