package ru.perekrestok.wms_native_mobile.screens.settings

import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.perekrestok.android.extension.setDebounceClickListener
import ru.perekrestok.android.fragment.BaseFragment
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment<SettingsViewModel, SettingsViewState>(R.layout.fragment_settings) {

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }

    override val viewModel: SettingsViewModel by viewModel()

    private val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)

    override fun setupUI() = with(binding) {
        initItems()
    }

    private fun FragmentSettingsBinding.initItems() {
        adminModeLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnAdminModeItemClicked)
        }
        selectShopLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnSelectShopItemClicked)
        }
        selectPrinterLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnSelectPrinterItemClicked)
        }
        selectScannerLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnSelectScannerItemClicked)
        }
        scanModeLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnScanModeItemClicked)
        }
        selectOrientationLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnSelectOrientationItemClicked)
        }
        cookiesAllowedLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnCookiesAllowedItemClicked)
        }
        cacheAllowedLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnCacheAllowedItemClicked)
        }
        cacheModeLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnCacheModeItemClicked)
        }
        clearCacheLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnClearCacheItemClicked)
        }
        clearCookiesLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnClearCookiesItemClicked)
        }
        customEndpointLayout.setDebounceClickListener {
            viewModel.processUiEvent(SettingsUiEvent.OnCustomEndpointItemClicked)
        }
    }

    override fun observeViewState(newState: SettingsViewState) = with(binding) {
        adminModeSwitch.isChecked = newState.isAdminModeEnabled

        selectShopLayout.isVisible = newState.isAdminModeEnabled
        shopSubtitleTv.text = newState.shopName

        printerSubtitleTv.text = newState.printerName
        scannerSubtitleTv.text = newState.scannerName
        scanModeSubtitleTv.text = newState.scanModeDescription

        selectOrientationLayout.isVisible = newState.isAdminModeEnabled
        orientationSubtitleTv.text = newState.screenOrientationName

        cookiesAllowedLayout.isVisible = newState.isAdminModeEnabled
        cookiesAllowedSwitch.isChecked = newState.isCookiesAllowed

        cacheAllowedLayout.isVisible = newState.isAdminModeEnabled
        cacheAllowedSwitch.isChecked = newState.isCacheAllowed

        cacheModeLayout.isVisible = newState.isCacheAllowed && newState.isAdminModeEnabled
        cacheModeSubtitleTv.text = newState.cachedModeName

        customEndpointLayout.isVisible = newState.isAdminModeEnabled
        customEndpointSubtitleTv.text = newState.customUrl
    }
}
