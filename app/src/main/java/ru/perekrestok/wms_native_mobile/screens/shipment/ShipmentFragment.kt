package ru.perekrestok.wms_native_mobile.screens.shipment

import android.app.Instrumentation
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.perekrestok.android.extension.FileDirectoryPath
import ru.perekrestok.android.extension.getDirectoryPatch
import ru.perekrestok.android.fragment.BaseFragment
import ru.perekrestok.android.view_model.event.SingleEvent
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.databinding.FragmentShipmentBinding
import ru.perekrestok.wms_native_mobile.screens.shipment.web_view.ShopsChromeWebViewClient

@Suppress("TooManyFunctions")
class ShipmentFragment : BaseFragment<ShipmentViewModel, ShipmentViewState>(R.layout.fragment_shipment) {

    companion object {
        private const val USER_AGENT_NAME = "wms-native-app"
        private const val WEB_VIEW_CACHE_DB_NAME = "wms_native_wv_db"
        private const val JAVASCRIPT_INTERFACE_NAME = "nativeApp"
        private const val ENTER_CLICK_EMULATED_DELAY_MS = 100L
        fun newInstance(): ShipmentFragment = ShipmentFragment()
    }

    private val binding by viewBinding(FragmentShipmentBinding::bind)

    override val viewModel: ShipmentViewModel by viewModel()

    override fun setupUI() = with(binding) {
        initSwipeRefreshLayout()
        initWebView()
    }

    private val instrumentation by lazy { Instrumentation() }

    private fun FragmentShipmentBinding.initSwipeRefreshLayout() = with(shipmentSrl) {
        setOnRefreshListener {
            viewModel.processUiEvent(ShipmentUiEvent.ReloadPage)
        }
        setOnChildScrollUpCallback { _, _ ->
            shipmentWv.scrollY > 0
        }
    }

    private fun FragmentShipmentBinding.initWebView(): Unit = with(shipmentWv) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        webChromeClient = ShopsChromeWebViewClient { progress ->
            viewModel.processUiEvent(ShipmentUiEvent.OnPageLoadingProgressReceived(progress))
        }
        webViewClient = WebViewClient()
        isNestedScrollingEnabled = true

        addJavascriptInterface(viewModel, JAVASCRIPT_INTERFACE_NAME)

        settings.apply {
            userAgentString = USER_AGENT_NAME
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            useWideViewPort = true
            setRenderPriority(WebSettings.RenderPriority.HIGH)
            allowContentAccess = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            setNeedInitialFocus(true)
        }
    }

    override fun observeViewState(newState: ShipmentViewState) = with(binding) {
        if (newState.isDataReady) {
            renderWebView(newState)
            renderProgress(newState)
        }
    }

    override fun observeSingleEvent(event: SingleEvent) = when (event) {
        is ShipmentSingleEvent -> observeShipmentSingleEvent(event)
        else -> {}
    }

    private fun observeShipmentSingleEvent(event: ShipmentSingleEvent) = when (event) {
        ShipmentSingleEvent.ClearCache -> clearWebViewCache()
        ShipmentSingleEvent.ReloadData -> reloadData()
        is ShipmentSingleEvent.SetEnabledSwipeToRefresh -> setEnabledSwipeToRefresh(event)
        is ShipmentSingleEvent.ExecuteJSCommand -> executeJavaScriptCommand(event)
    }

    private fun setEnabledSwipeToRefresh(event: ShipmentSingleEvent.SetEnabledSwipeToRefresh) {
        binding.shipmentSrl.isEnabled = event.isEnabled
    }

    private fun reloadData() {
        binding.shipmentWv.reload()
    }

    private fun FragmentShipmentBinding.renderProgress(newState: ShipmentViewState) {
        shipmentPb.isVisible = newState.isProgressBarVisible
        shipmentPb.progress = newState.loadingProgress
        shipmentSrl.isRefreshing = newState.isProgressBarVisible
    }

    private fun FragmentShipmentBinding.renderWebView(newState: ShipmentViewState) {
        if (!newState.isPageLoaded) {
            setCacheSettings(newState.webViewSettings)
            setCookiesSettings(newState.webViewSettings)
            loadUrl(newState.webViewSettings.shopUrl)
            viewModel.processUiEvent(ShipmentUiEvent.OnPageLoaded)
        }
    }

    @Suppress("NestedBlockDepth")
    private fun FragmentShipmentBinding.setCacheSettings(webViewSettings: WebViewSettings) = with(shipmentWv) {
        settings.apply {
            cacheMode = webViewSettings.webViewCacheMode

            if (webViewSettings.isCacheAllowed) {
                setAppCacheEnabled(true)
                domStorageEnabled = true
                databaseEnabled = true
                setAppCachePath(requireContext().getDirectoryPatch(FileDirectoryPath.WEB_VIEW_CACHE))
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    databasePath = requireActivity().getDatabasePath(WEB_VIEW_CACHE_DB_NAME)?.path
                }
            } else {
                cacheMode = WebSettings.LOAD_NO_CACHE
                viewModel.processUiEvent(ShipmentUiEvent.ClearCache)
            }
        }
    }

    private fun FragmentShipmentBinding.setCookiesSettings(webViewSettings: WebViewSettings) {
        if (webViewSettings.isCookiesAllowed) {
            CookieManager.getInstance().setAcceptCookie(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(shipmentWv, true)
                CookieManager.getInstance().acceptCookie()
            }
        } else {
            viewModel.processUiEvent(ShipmentUiEvent.ClearCookies)
        }
    }

    private fun loadUrl(shopUrl: String) {
        binding.shipmentWv.loadUrl(shopUrl)
    }

    private fun clearWebViewCache() = with(binding) {
        shipmentWv.run {
            clearCache(true)
            clearHistory()
        }
    }

    private fun executeJavaScriptCommand(event: ShipmentSingleEvent.ExecuteJSCommand) {
        binding.shipmentWv.evaluateJavascript(event.renderBarcodeJs) {}
        if (event.isNeedPressEnter) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                delay(ENTER_CLICK_EMULATED_DELAY_MS)
                instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER)
            }
        }
    }
}
