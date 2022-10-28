package ru.perekrestok.data.di.modules

import org.koin.dsl.module
import retrofit2.Retrofit
import ru.perekrestok.data.remote.api.ShopsApi

internal val apiModule = module {

    single<ShopsApi> {
        get<Retrofit>().create(ShopsApi::class.java)
    }

}