package ru.perekrestok.android.dialogs.message

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

class MessageDialogScreen(private val message: String, private val buttonText: String) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return MessageDialogFragment.newInstance(message = message, buttonText = buttonText)
    }
}
