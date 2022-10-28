package ru.perekrestok.domain.interactor

import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.SettingKey
import ru.perekrestok.domain.exception.AdminModeException
import ru.perekrestok.domain.repository.LocalAppSettingsRepository
import ru.perekrestok.kotlin.currentTimeMillis
import ru.perekrestok.kotlin.toStringDate

interface AdminModeInteractor {
    suspend fun activateAdminMode(password: String): Result<Unit>
    suspend fun resetAdminMode(): Result<Unit>
}

class AdminModeInteractorImpl(
    private val localAppSettingsRepository: LocalAppSettingsRepository
) : AdminModeInteractor {

    companion object {
        private const val ADMIN_MODE_DATE_PATTERN = "M9d"
    }

    override suspend fun activateAdminMode(password: String): Result<Unit> = runCatching {
        // {месяц 1-12}9{день 1-31}
        val secret = currentTimeMillis.toStringDate(ADMIN_MODE_DATE_PATTERN)
        when {
            password.isEmpty() -> throw AdminModeException.PasswordEmpty
            password != secret -> throw AdminModeException.PasswordWrong
            else -> {
                localAppSettingsRepository.saveSetting(
                    AppSetting(
                        key = SettingKey.IS_ADMIN_MODE,
                        value = true
                    )
                )
            }
        }
    }

    override suspend fun resetAdminMode(): Result<Unit> = runCatching {
        localAppSettingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.IS_ADMIN_MODE,
                value = false
            )
        )
    }
}
