package ru.perekrestok.data.di.modules

import androidx.room.Room
import org.koin.dsl.module
import ru.perekrestok.data.local.database.AppDatabase

internal val databaseModule = module {
    single(createdAtStart = true) {
        Room.databaseBuilder(get(), AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .build()
    }

    single { get<AppDatabase>().shopsDao() }
    single { get<AppDatabase>().appSettingsDao() }
    single { get<AppDatabase>().customSettingsDao() }
}
