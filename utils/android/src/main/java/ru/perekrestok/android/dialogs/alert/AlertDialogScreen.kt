package ru.perekrestok.android.dialogs.alert

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class AlertDialogScreen(
    private val title: String,
    private val message: String,
    private val onConfirmAction: () -> Unit
) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return AlertDialogFragment.newInstance(
            title = title,
            message = message,
            onConfirmAction = onConfirmAction
        )
    }
}
