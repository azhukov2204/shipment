package ru.perekrestok.wms_native_mobile.screens.nav_drawer_container

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.terrakok.cicerone.NavigatorHolder
import com.google.android.material.navigation.NavigationView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.perekrestok.android.extension.setAttrImageTint
import ru.perekrestok.android.extension.setDebounceClickListener
import ru.perekrestok.android.fragment.BackButtonClickListener
import ru.perekrestok.android.fragment.BaseFragment
import ru.perekrestok.android.view_model.event.SingleEvent
import ru.perekrestok.android.worker.SyncService
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.databinding.FragmentNavDrawerBinding
import ru.perekrestok.wms_native_mobile.databinding.NavDrawerHeaderBinding
import ru.perekrestok.wms_native_mobile.di.DIQualifiers
import ru.perekrestok.wms_native_mobile.di.modules.NAV_DRAWER_QUALIFIER

class NavDrawerFragment :
    BaseFragment<NavDrawerViewModel, NavDrawerViewState>(R.layout.fragment_nav_drawer),
    NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val NAVIGATION_DRAWER_HEADER_INDEX = 0
        fun newInstance(): NavDrawerFragment = NavDrawerFragment()
    }

    private val binding by viewBinding(FragmentNavDrawerBinding::bind)

    private val navigationDrawerHeaderBinding by viewBinding {
        NavDrawerHeaderBinding.bind(binding.navDrawer.getHeaderView(NAVIGATION_DRAWER_HEADER_INDEX))
    }

    override val viewModel: NavDrawerViewModel by viewModel()

    private val syncService: SyncService by inject()

    private val navigator by lazy {
        NavDrawerNavigator(
            activity = requireActivity(),
            fragmentManager = childFragmentManager,
            containerId = R.id.nav_drawer_container
        )
    }

    private val navigatorHolder: NavigatorHolder by inject(
        DIQualifiers.navigationHolderQualifier(moduleQualifier = NAV_DRAWER_QUALIFIER)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(syncService)
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(syncService)
    }

    override fun setupUI() = with(binding) {
        navDrawer.setNavigationItemSelectedListener(this@NavDrawerFragment)
        menuIv.setDebounceClickListener {
            viewModel.processUiEvent(NavDrawerUiEvent.OnMenuButtonClicked)
        }
        navigationDrawerHeaderBinding.aboutIv.setDebounceClickListener {
            viewModel.processUiEvent(NavDrawerUiEvent.OnAboutButtonClicked)
        }
    }

    override fun observeViewState(newState: NavDrawerViewState) = with(binding) {
        scannerIv.setAttrImageTint(newState.scannerIconColorRes)
        navigationDrawerHeaderBinding.scannerIv.setAttrImageTint(newState.scannerIconColorRes)
        printerIv.setAttrImageTint(newState.printerIconColorRes)
        navigationDrawerHeaderBinding.printerIv.setAttrImageTint(newState.printerIconColorRes)
        titleTv.text = newState.toolbarTitle
        toolbar.isVisible = newState.isToolbarVisible
        navigationDrawerHeaderBinding.wmsNameTv.text = newState.shopName
        navigationDrawerHeaderBinding.wmsHostTv.text = newState.shopHost
    }

    override fun observeSingleEvent(event: SingleEvent) = when (event) {
        is NavDrawerSingleEvent -> observeNavDrawerSingleEvent()
        else -> {}
    }

    override fun onBackPressed() {
        val fragment = childFragmentManager.fragments.lastOrNull()

        if (fragment != null && fragment is BackButtonClickListener) {
            fragment.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        viewModel.processUiEvent(NavDrawerUiEvent.OnMenuItemClicked(item.itemId))
        binding.navigationDrawer.closeDrawers()
        return true
    }

    private fun observeNavDrawerSingleEvent() {
        binding.navigationDrawer.openDrawer(GravityCompat.START)
    }
}
