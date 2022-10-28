package ru.perekrestok.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.perekrestok.domain.entity.AppSetting
import ru.perekrestok.domain.entity.ScanMode
import ru.perekrestok.domain.entity.SettingKey
import ru.perekrestok.domain.provider.BluetoothScannerProvider
import ru.perekrestok.domain.repository.LocalAppSettingsRepository
import ru.perekrestok.domain.repository.RenderJSBarcodeProvider

interface ScannerInteractor {
    val scanResultFlow: Flow<String>
    fun getRenderBarcodeJs(barcode: String, scanMode: ScanMode): String
    fun getBackClickJS(): String
    suspend fun setIsInternalScannerEnabled(isEnabled: Boolean)
    suspend fun setScanMode(scanMode: ScanMode)
}

class ScannerInteractorImpl(
    private val localAppSettingsRepository: LocalAppSettingsRepository,
    bluetoothScannerProvider: BluetoothScannerProvider,
    private val renderJSBarcodeProvider: RenderJSBarcodeProvider
) : ScannerInteractor {

    override val scanResultFlow: Flow<String> = bluetoothScannerProvider.scanResultFlow

    override suspend fun setIsInternalScannerEnabled(isEnabled: Boolean) {
        localAppSettingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.IS_USE_INTERNAL_SCANNER,
                value = isEnabled
            )
        )
    }

    override suspend fun setScanMode(scanMode: ScanMode) {
        localAppSettingsRepository.saveSetting(
            AppSetting(
                key = SettingKey.SCAN_MODE_ID,
                value = scanMode.id
            )
        )
    }

    override fun getRenderBarcodeJs(barcode: String, scanMode: ScanMode): String {
        return renderJSBarcodeProvider.getRenderBarcodeJs(barcode, scanMode)
    }

    override fun getBackClickJS(): String {
        return renderJSBarcodeProvider.getBackClickJS()
    }
}