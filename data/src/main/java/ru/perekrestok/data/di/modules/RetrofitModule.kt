package ru.perekrestok.data.di.modules

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.perekrestok.data.BuildConfig
import ru.perekrestok.data.remote.okhttp.HttpLogger
import ru.perekrestok.domain.Production

internal val retrofitModule = module {

    single<Gson> {
        GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
    }

    single<Converter.Factory> { GsonConverterFactory.create(get()) }

    single<Retrofit> {
        val baseUrl = Production.url
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(get())
            .addConverterFactory(get())
            .build()
    }

    single<OkHttpClient> {
        val okhttpBuilder = OkHttpClient
            .Builder()

        if (BuildConfig.DEBUG) {
            okhttpBuilder
                .addInterceptor(get<Interceptor>(named(CHUCKER_INTERCEPTOR)))
                .addNetworkInterceptor(
                    HttpLoggingInterceptor(HttpLogger(gson = get())).setLevel(
                        HttpLoggingInterceptor.Level.BODY
                    )
                )
        }
        okhttpBuilder
            .build()
    }
}
