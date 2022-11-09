package ru.perekrestok.android.dialogs.message

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import ru.perekrestok.android.extension.AlertDialogButton
import ru.perekrestok.android.extension.requireParameter
import ru.perekrestok.android.extension.showAlertDialog

class MessageDialogFragment : DialogFragment() {

    companion object {
        private const val MESSAGE_KEY = "message_key"
        private const val BUTTON_TEXT_KEY = "button_text_key"

        fun newInstance(
            message: String,
            buttonText: String
        ): MessageDialogFragment = MessageDialogFragment().apply {
            arguments = bundleOf(
                MESSAGE_KEY to message,
                BUTTON_TEXT_KEY to buttonText
            )

            isCancelable = false
        }
    }

    private val message: String by requireParameter(MESSAGE_KEY)
    private val buttonText: String by requireParameter(BUTTON_TEXT_KEY)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().showAlertDialog(
            message = message,
            positiveButton = AlertDialogButton(buttonText)
        )
    }
}
