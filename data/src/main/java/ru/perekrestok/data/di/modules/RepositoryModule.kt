package ru.perekrestok.data.di.modules

import org.koin.dsl.module
import ru.perekrestok.data.local.repository_impl.LocalAppSettingsRepositoryImpl
import ru.perekrestok.data.local.repository_impl.LocalCustomSettingsRepositoryImpl
import ru.perekrestok.data.local.repository_impl.LocalShopsRepositoryImpl
import ru.perekrestok.data.remote.repository_impl.RemoteShopsRepositoryImpl
import ru.perekrestok.domain.repository.LocalAppSettingsRepository
import ru.perekrestok.domain.repository.LocalCustomSettingsRepository
import ru.perekrestok.domain.repository.LocalShopsRepository
import ru.perekrestok.domain.repository.RemoteShopsRepository

internal val repositoryModule = module {
    single<RemoteShopsRepository> {
        RemoteShopsRepositoryImpl(
            shopsApi = get(),
            systemActionInteractor = get(),
            settingsInteractor = get()
        )
    }

    single<LocalShopsRepository> {
        LocalShopsRepositoryImpl(
            shopsDao = get()
        )
    }

    single<LocalAppSettingsRepository> {
        LocalAppSettingsRepositoryImpl(
            appSettingsDao = get()
        )
    }

    single<LocalCustomSettingsRepository> {
        LocalCustomSettingsRepositoryImpl(
            customSettingsDao = get()
        )
    }
}