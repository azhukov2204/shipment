package ru.perekrestok.android.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import ru.perekrestok.android.view_model.BaseViewModel
import ru.perekrestok.android.view_model.event.OnButtonBackClicked
import ru.perekrestok.android.view_model.event.SingleEvent

abstract class BaseFragment<VIEW_MODEL : BaseViewModel<VIEW_STATE>, VIEW_STATE>(@LayoutRes layoutRes: Int) :
    Fragment(layoutRes),
    BackButtonClickListener {

    protected abstract val viewModel: VIEW_MODEL

    protected abstract fun observeViewState(newState: VIEW_STATE)

    protected abstract fun setupUI()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObserve()
        setupUI()
    }

    private fun setupObserve() {
        viewModel.viewState.observe(viewLifecycleOwner) { newViewState ->
            if (newViewState != null) {
                observeViewState(newViewState)
            }
        }
        viewModel.singleEvent.observe(viewLifecycleOwner) { newEvent ->
            if (newEvent != null) {
                observeSingleEvent(newEvent)
            }
        }
    }

    protected open fun observeSingleEvent(event: SingleEvent) {
        // nothing
    }

    override fun onBackPressed() {
        viewModel.processUiEvent(OnButtonBackClicked)
    }
}
