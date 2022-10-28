package ru.perekrestok.wms_native_mobile.activity

import android.view.WindowManager
import com.github.terrakok.cicerone.NavigatorHolder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.perekrestok.android.activity.BaseActivity
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.di.DIQualifiers.navigationHolderQualifier
import ru.perekrestok.wms_native_mobile.di.modules.MAIN_ACTIVITY_QUALIFIER

class MainActivity : BaseActivity<MainActivityViewModel, MainActivityViewState>(R.layout.activity_main) {

    private val navigator by lazy {
        MainActivityNavigator(this, supportFragmentManager, R.id.fragmentContainer)
    }

    override val viewModel: MainActivityViewModel by viewModel()

    override fun setupUI() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun observeViewState(newState: MainActivityViewState) {
        renderScreenOrientation(newState.screenOrientation)
    }

    private fun renderScreenOrientation(screenOrientation: Int) {
        if (requestedOrientation != screenOrientation) {
            requestedOrientation =  screenOrientation
        }
    }

    private val navigatorHolder: NavigatorHolder by inject(
        navigationHolderQualifier(
            MAIN_ACTIVITY_QUALIFIER
        )
    )

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }
}
