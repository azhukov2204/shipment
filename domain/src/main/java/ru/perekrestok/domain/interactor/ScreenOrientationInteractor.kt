package ru.perekrestok.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.AppSetting.Companion.getScreenOrientation
import ru.perekrestok.domain.entity.SettingKey
import ru.perekrestok.domain.repository.LocalAppSettingsRepository

interface ScreenOrientationInteractor {
    suspend fun setScreenOrientation(screenOrientation: Int)
    fun getScreenOrientationFlow(): Flow<Int>
}

class ScreenOrientationInteractorImpl(
    private val settingsRepository: LocalAppSettingsRepository
) : ScreenOrientationInteractor {

    override suspend fun setScreenOrientation(screenOrientation: Int) {
        settingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.SCREEN_ORIENTATION,
                value = screenOrientation
            )
        )
    }

    override fun getScreenOrientationFlow(): Flow<Int> {
        return settingsRepository.getSettingsFlow().map { settings ->
            settings.getScreenOrientation()
        }
    }
}