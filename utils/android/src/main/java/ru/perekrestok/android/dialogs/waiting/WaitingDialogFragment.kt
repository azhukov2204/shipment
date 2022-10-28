package ru.perekrestok.android.dialogs.waiting

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import ru.perekrestok.android.extension.AlertDialogButton
import ru.perekrestok.android.extension.requireParameter
import ru.perekrestok.android.extension.showWaitingDialog

class WaitingDialogFragment : DialogFragment() {

    companion object {
        private const val MESSAGE_KEY = "message_key"
        private const val CLOSE_BUTTON_TEXT_KEY = "close_button_text_key"

        fun newInstance(
            message: String,
            closeButtonText: String,
            onCancelAction: () -> Unit
        ): WaitingDialogFragment =
            WaitingDialogFragment().apply {
                arguments = bundleOf(
                    MESSAGE_KEY to message,
                    CLOSE_BUTTON_TEXT_KEY to closeButtonText
                )
                this.onCancelAction = onCancelAction
            }
    }

    private val message: String by requireParameter(MESSAGE_KEY)
    private val closeButtonText: String by requireParameter(CLOSE_BUTTON_TEXT_KEY)
    private var onCancelAction: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().showWaitingDialog(
            message = message,
            positiveButton = AlertDialogButton(text = closeButtonText) {
                onCancelAction()
            }
        ).also {
            isCancelable = false
        }
    }
}
