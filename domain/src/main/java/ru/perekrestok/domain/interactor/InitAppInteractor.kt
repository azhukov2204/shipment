package ru.perekrestok.domain.interactor

import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.AppSetting.Companion.getDateInitApp
import ru.perekrestok.domain.entity.SettingKey
import ru.perekrestok.domain.repository.LocalAppSettingsRepository
import ru.perekrestok.kotlin.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.Locale

interface InitAppInteractor {
    suspend fun checkInitDate()
}

class InitAppInteractorImpl(
    private val localAppSettingsRepository: LocalAppSettingsRepository
) : InitAppInteractor {

    companion object {
        private const val DATE_FORMAT = "dd.MM HH:mm"
    }

    override suspend fun checkInitDate() {
        localAppSettingsRepository.getSettings().getDateInitApp() ?: run {
            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val initDate = dateFormat.format(currentTimeMillis)
            localAppSettingsRepository.saveSetting(
                AppSetting(
                    key = SettingKey.DATE_INIT,
                    value = initDate
                )
            )
        }
    }
}
