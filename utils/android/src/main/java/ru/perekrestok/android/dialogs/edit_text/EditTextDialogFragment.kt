package ru.perekrestok.android.dialogs.edit_text

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import ru.perekrestok.android.extension.hideSoftInputFromWindow
import ru.perekrestok.android.extension.requireParameter
import ru.perekrestok.android.extension.showEditTextDialog

class EditTextDialogFragment : DialogFragment() {

    companion object {
        private const val TITLE_KEY = "title_key"
        private const val TEXT_KEY = "text_key"
        private const val INPUT_TYPE_KEY = "input_type_key"
        private const val POSITIVE_BUTTON_TEXT_KEY = "positive_button_text_key"
        private const val NEGATIVE_BUTTON_TEXT_KEY = "negative_button_text_key"
        private const val NEUTRAL_BUTTON_TEXT_KEY = "neutral_button_text_key"

        @Suppress("LongParameterList")
        fun newInstance(
            title: String,
            text: String,
            inputType: Int,
            positiveButtonText: String,
            negativeButtonText: String,
            neutralButtonText: String,
            positiveButtonAction: (String) -> Unit,
            negativeButtonAction: () -> Unit,
            neutralButtonAction: () -> Unit
        ): EditTextDialogFragment = EditTextDialogFragment().apply {
            arguments = bundleOf(
                TITLE_KEY to title,
                TEXT_KEY to text,
                INPUT_TYPE_KEY to inputType,
                POSITIVE_BUTTON_TEXT_KEY to positiveButtonText,
                NEGATIVE_BUTTON_TEXT_KEY to negativeButtonText,
                NEUTRAL_BUTTON_TEXT_KEY to neutralButtonText
            )

            this.positiveButtonAction = positiveButtonAction
            this.negativeButtonAction = negativeButtonAction
            this.neutralButtonAction = neutralButtonAction
            isCancelable = false
        }
    }

    private val title: String by requireParameter(TITLE_KEY)
    private val text: String by requireParameter(TEXT_KEY)
    private val inputType: Int by requireParameter(INPUT_TYPE_KEY)
    private val positiveButtonText: String by requireParameter(POSITIVE_BUTTON_TEXT_KEY)
    private val negativeButtonText: String by requireParameter(NEGATIVE_BUTTON_TEXT_KEY)
    private val neutralButtonText: String by requireParameter(NEUTRAL_BUTTON_TEXT_KEY)

    var positiveButtonAction: (String) -> Unit = {}
    var negativeButtonAction: () -> Unit = {}
    var neutralButtonAction: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().showEditTextDialog(
            title = title,
            text = text,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            neutralButtonText = neutralButtonText,
            inputType = inputType,
            positiveButtonAction = { value -> positiveButtonAction(value) },
            negativeButtonAction = { negativeButtonAction() },
            neutralButtonAction = { neutralButtonAction() },
            onDismissListener = { requireDialog().currentFocus?.hideSoftInputFromWindow() }
        )
    }
}
