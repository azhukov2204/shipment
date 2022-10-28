package ru.perekrestok.android.dialogs.edit_text

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.perekrestok.kotlin.StringPatterns

class EditTextDialogScreen(
    private val title: String,
    private val text: String = StringPatterns.EMPTY_SYMBOL,
    private val inputType: Int,
    private val positiveButtonText: String,
    private val negativeButtonText: String,
    private val neutralButtonText: String = StringPatterns.EMPTY_SYMBOL,
    private val positiveButtonAction: (String) -> Unit,
    private val negativeButtonAction: () -> Unit = {},
    private val neutralButtonAction: () -> Unit = {}
) : FragmentScreen {

    override fun createFragment(factory: FragmentFactory): Fragment {
        return EditTextDialogFragment.newInstance(
            title = title,
            text = text,
            inputType = inputType,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            neutralButtonText = neutralButtonText,
            positiveButtonAction = positiveButtonAction,
            negativeButtonAction = negativeButtonAction,
            neutralButtonAction = neutralButtonAction
        )
    }
}
