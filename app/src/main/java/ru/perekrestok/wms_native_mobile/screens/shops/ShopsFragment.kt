package ru.perekrestok.wms_native_mobile.screens.shops

import android.os.Parcelable
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import kotlinx.parcelize.Parcelize
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.perekrestok.android.extension.hideKeyboard
import ru.perekrestok.android.extension.requireParameter
import ru.perekrestok.android.extension.setDebounceClickListener
import ru.perekrestok.android.extension.setOnTextChangeListenerByLifecycle
import ru.perekrestok.android.extension.viewLifecycleAware
import ru.perekrestok.android.fragment.BaseFragment
import ru.perekrestok.android.recycler_view.ItemDiffUtilCallback
import ru.perekrestok.android.recycler_view.RecyclerViewItem
import ru.perekrestok.android.view_model.event.SingleEvent
import ru.perekrestok.wms_native_mobile.R
import ru.perekrestok.wms_native_mobile.databinding.FragmentShopsBinding
import ru.perekrestok.wms_native_mobile.screens.shops.delegate.shopsDelegate

class ShopsFragment : BaseFragment<ShopsViewModel, ShopsViewState>(R.layout.fragment_shops) {

    companion object {

        @Parcelize
        enum class AfterChoseShopRouting: Parcelable {
            NEW_ROOT_SCREEN,
            EXIT
        }

        private const val AFTER_CHOSE_SHOP_ROUTING_KEY = "after_chose_shop_routing_key"

        fun newInstance(afterChoseShoppRouting: AfterChoseShopRouting): ShopsFragment = ShopsFragment().apply {
            arguments = bundleOf(
                AFTER_CHOSE_SHOP_ROUTING_KEY to afterChoseShoppRouting
            )
        }
    }

    private val afterChoseShopRouting: AfterChoseShopRouting by requireParameter(AFTER_CHOSE_SHOP_ROUTING_KEY)

    override val viewModel: ShopsViewModel by viewModel {
        parametersOf(afterChoseShopRouting)
    }

    private val binding: FragmentShopsBinding by viewBinding(FragmentShopsBinding::bind)

    private val shopsAdapter: AsyncListDifferDelegationAdapter<RecyclerViewItem> by viewLifecycleAware {
        AsyncListDifferDelegationAdapter(
            ItemDiffUtilCallback(),
            shopsDelegate { shopId ->
                viewModel.processUiEvent(
                    ShopsUiEvent.OnShopClicked(shopId)
                )
            }
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().hideKeyboard()
    }


    override fun observeViewState(newState: ShopsViewState) = with(binding) {
        shopsAdapter.items = newState.shopItems
        ivClose.isVisible = newState.isCurrentShopNotEmpty
        tvCurrentShop.isInvisible = !newState.isCurrentShopNotEmpty
        tvCurrentShop.text = String.format(
            getString(R.string.text_template_current_shop),
            newState.currentShop?.name.orEmpty()
        )
        renderLogoAnimation(newState.isAnimationLoading)

    }

    private fun FragmentShopsBinding.renderLogoAnimation(isAnimationLoading: Boolean) {
        if (isAnimationLoading) {
            ivLogo.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.login_circle_logo
                )
            )
        } else {
            ivLogo.clearAnimation()
        }
    }

    override fun setupUI() = with(binding) {
        initEditTextShopSearch()
        initRecyclerView()
        initSwipeContainer()
        initCloseButton()
    }

    override fun observeSingleEvent(event: SingleEvent) = when (event) {
        is ShopsSingleEvent -> dispatchShopsSingleEvent(event)
        else -> {}
    }

    private fun dispatchShopsSingleEvent(event: ShopsSingleEvent) = when (event) {
        ShopsSingleEvent.ClearSearchFieldFocus -> clearSearchFieldFocus()
    }

    private fun FragmentShopsBinding.initEditTextShopSearch() {
        etShopSearch.setOnTextChangeListenerByLifecycle(
            lifecycleOwner = viewLifecycleOwner,
            onAfterTextChanged = { searchQuery ->
                viewModel.processUiEvent(
                    ShopsUiEvent.OnSearchQueryFilled(
                        searchQuery = searchQuery.toString()
                    )
                )
            })

        etShopSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.processUiEvent(ShopsUiEvent.OnImeSearchClicked)
            }
            true
        }
    }

    private fun clearSearchFieldFocus() {
        requireActivity().hideKeyboard()
        binding.etShopSearch.clearFocus()
    }

    private fun FragmentShopsBinding.initRecyclerView() {
        recycleView.adapter = shopsAdapter
    }

    private fun FragmentShopsBinding.initSwipeContainer() {
        swipeContainer.setOnRefreshListener {
            viewModel.processUiEvent(ShopsUiEvent.RefreshShopList)
            swipeContainer.isRefreshing = false
        }
    }

    private fun FragmentShopsBinding.initCloseButton() {
        ivClose.setDebounceClickListener {
            viewModel.processUiEvent(ShopsUiEvent.OnCloseButtonClicked)
        }
    }
}