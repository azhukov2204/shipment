package ru.perekrestok.android.dialogs.waiting

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class WaitingDialogScreen(
    private val message: String,
    private val closeButtonText: String,
    private val onCancelAction: () -> Unit
) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return WaitingDialogFragment.newInstance(
            message = message,
            closeButtonText = closeButtonText,
            onCancelAction = onCancelAction
        )
    }
}
