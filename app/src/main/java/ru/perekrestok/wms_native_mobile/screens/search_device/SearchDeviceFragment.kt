package ru.perekrestok.wms_native_mobile.screens.search_device

import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.perekrestok.android.extension.requireParameter
import ru.perekrestok.android.extension.setDebounceClickListener
import ru.perekrestok.android.extension.viewLifecycleAware
import ru.perekrestok.android.fragment.BaseFragment
import ru.perekrestok.android.recycler_view.ItemDiffUtilCallback
import ru.perekrestok.android.recycler_view.RecyclerViewItem
import ru.perekrestok.android.recycler_view.loadingDelegate
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.databinding.FragmentSearchDeviceBinding
import ru.perekrestok.wms_native_mobile.screens.search_device.delegate.deviceDelegate

class SearchDeviceFragment :
    BaseFragment<SearchDeviceViewModel, SearchDeviceViewState>(R.layout.fragment_search_device) {

    companion object {
        private const val DEVICE_TYPE_KEY = "device_type_key"

        fun newInstance(deviceType: DeviceType): SearchDeviceFragment {
            return SearchDeviceFragment().apply {
                arguments = bundleOf(
                    DEVICE_TYPE_KEY to deviceType
                )
            }
        }
    }

    private val binding: FragmentSearchDeviceBinding by viewBinding(FragmentSearchDeviceBinding::bind)

    private val devicesAdapter: AsyncListDifferDelegationAdapter<RecyclerViewItem> by viewLifecycleAware {
        AsyncListDifferDelegationAdapter(
            ItemDiffUtilCallback(),
            loadingDelegate(),
            deviceDelegate { deviceHashCode ->
                viewModel.processUiEvent(SearchDeviceUiEvent.OnDeviceItemClicked(deviceHashCode))
            }
        )
    }

    private val deviceType: DeviceType by requireParameter(DEVICE_TYPE_KEY)

    override val viewModel: SearchDeviceViewModel by viewModel { parametersOf(deviceType) }

    override fun setupUI() = with(binding) {
        searchDeviceRv.adapter = devicesAdapter
        closeIv.setDebounceClickListener {
            viewModel.processUiEvent(SearchDeviceUiEvent.OnCloseButtonClicked)
        }
    }

    override fun observeViewState(newState: SearchDeviceViewState) = with(binding) {
        devicesAdapter.items = newState.deviceItems
        renderLogoAnimation(newState.isAnimationLoading)
        searchDeviceInfoTv.isVisible = newState.hasError.not()
        currentDeviceTv.text = newState.currentDeviceText
        searchDeviceErrorTv.isVisible = newState.hasError
        searchDeviceErrorTv.text = getString(R.string.error_message, newState.errorMessage)
    }

    private fun FragmentSearchDeviceBinding.renderLogoAnimation(isAnimationLoading: Boolean) {
        if (isAnimationLoading) {
            logoIv.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.login_circle_logo
                )
            )
        } else {
            logoIv.clearAnimation()
        }
    }
}
