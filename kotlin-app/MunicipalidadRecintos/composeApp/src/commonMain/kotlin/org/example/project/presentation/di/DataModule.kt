package org.example.project.presentation.di

import org.example.project.data.remote.AuthRemoteDataSource
import org.example.project.data.remote.CategoriaRemoteDataSource
import org.example.project.data.remote.CursoRemoteDataSource
import org.example.project.data.remote.EncargadoRemoteDataSource
import org.example.project.data.remote.EstadisticasRemoteDataSource
import org.example.project.data.remote.EventRemoteDataSource
import org.example.project.data.remote.NotificacionRemoteDataSource
import org.example.project.data.remote.RecintoRemoteDataSource
import org.example.project.data.remote.StorageRemoteDataSource
import org.example.project.data.remote.UserRemoteDataSource
import org.example.project.data.repository.AuthRepositoryImpl
import org.example.project.data.repository.CategoriaRepositoryImpl
import org.example.project.data.repository.CursoRepositoryImpl
import org.example.project.data.repository.EncargadoRepositoryImpl
import org.example.project.data.repository.EstadisticasRepositoryImpl
import org.example.project.data.repository.EventRepositoryImpl
import org.example.project.data.repository.NotificacionRepositoryImpl
import org.example.project.data.repository.RecintoRepositoryImpl
import org.example.project.data.repository.UserRepositoryImpl
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.CategoriaRepository
import org.example.project.domain.repository.CursoRepository
import org.example.project.domain.repository.EncargadoRepository
import org.example.project.domain.repository.EstadisticasRepository
import org.example.project.domain.repository.EventRepository
import org.example.project.domain.repository.NotificacionRepository
import org.example.project.domain.repository.RecintoRepository
import org.example.project.domain.repository.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    single { AuthRemoteDataSource() }
    single { EventRemoteDataSource() }
    single { UserRemoteDataSource() }
    single { CursoRemoteDataSource() }
    single { RecintoRemoteDataSource() }
    single { CategoriaRemoteDataSource() }
    single { EncargadoRemoteDataSource() }
    single { EstadisticasRemoteDataSource() }
    single { NotificacionRemoteDataSource() }
    single { StorageRemoteDataSource() }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<EventRepository> { EventRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<CursoRepository> { CursoRepositoryImpl(get()) }
    single<RecintoRepository> { RecintoRepositoryImpl(get()) }
    single<CategoriaRepository> { CategoriaRepositoryImpl(get()) }
    single<EncargadoRepository> { EncargadoRepositoryImpl(get()) }
    single<EstadisticasRepository> { EstadisticasRepositoryImpl(get()) }
    single<NotificacionRepository> { NotificacionRepositoryImpl(get()) }
}
