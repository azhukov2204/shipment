package ru.perekrestok.data.di.modules

import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.Interceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val CHUCKER_INTERCEPTOR = "chucker_interceptor"

internal val interceptorModule = module {

    single<Interceptor>(named(CHUCKER_INTERCEPTOR)) {
        ChuckerInterceptor.Builder(get()).build()
    }
}
