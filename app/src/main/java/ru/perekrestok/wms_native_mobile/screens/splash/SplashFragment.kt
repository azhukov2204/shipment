package ru.perekrestok.wms_native_mobile.screens.splash

import android.Manifest
import android.view.animation.AnimationUtils
import by.kirich1409.viewbindingdelegate.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.perekrestok.android.extension.checkPermissions
import ru.perekrestok.android.fragment.BaseFragment
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.databinding.FragmentSplashBinding

class SplashFragment : BaseFragment<SplashViewModel, SplashViewState>(R.layout.fragment_splash) {

    companion object {
        fun newInstance(): SplashFragment = SplashFragment()
    }

    override val viewModel: SplashViewModel by viewModel()

    private val binding: FragmentSplashBinding by viewBinding(FragmentSplashBinding::bind)

    override fun setupUI() {
        startLogoAnimation()
        checkPermissions(
            permissions = arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            onPermissionChecked = {
                viewModel.processUiEvent(SplashUiEvent.OnPermissionsChecked)
            }
        )
    }

    private fun startLogoAnimation() {
        binding.ivLogo.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.login_circle_logo
            )
        )
    }

    override fun observeViewState(newState: SplashViewState) {
        // nothing
    }
}
