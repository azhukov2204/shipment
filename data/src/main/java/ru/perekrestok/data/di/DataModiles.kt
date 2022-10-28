package ru.perekrestok.data.di

import ru.perekrestok.data.di.modules.apiModule
import ru.perekrestok.data.di.modules.databaseModule
import ru.perekrestok.data.di.modules.interceptorModule
import ru.perekrestok.data.di.modules.providerModule
import ru.perekrestok.data.di.modules.repositoryModule
import ru.perekrestok.data.di.modules.retrofitModule
import ru.perekrestok.data.di.modules.workersModule

val dataModules = listOf(
    apiModule,
    databaseModule,
    interceptorModule,
    providerModule,
    repositoryModule,
    retrofitModule,
    workersModule
)