package ru.perekrestok.android.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import ru.perekrestok.android.fragment.BackButtonClickListener
import ru.perekrestok.android.view_model.BaseViewModel
import ru.perekrestok.android.view_model.event.SingleEvent

abstract class BaseActivity<VIEW_MODEL : BaseViewModel<VIEW_STATE>, VIEW_STATE>(@LayoutRes layoutRes: Int) :
    AppCompatActivity(layoutRes) {

    protected abstract val viewModel: VIEW_MODEL

    protected abstract fun observeViewState(newState: VIEW_STATE)

    protected abstract fun setupUI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObserve()
        setupUI()
    }

    private fun setupObserve() {
        viewModel.viewState.observe(this) { newViewState ->
            if (newViewState != null) {
                observeViewState(newViewState)
            }
        }
        viewModel.singleEvent.observe(this) { newEvent ->
            if (newEvent != null) {
                observeSingleEvent(newEvent)
            }
        }
    }

    protected open fun observeSingleEvent(event: SingleEvent) {
        // nothing
    }

    override fun onBackPressed() {

        val fragment = supportFragmentManager
            .fragments
            .firstOrNull()

        if (fragment != null && fragment is BackButtonClickListener) {
            fragment.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }
}
