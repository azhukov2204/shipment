package ru.perekrestok.android.dialogs.alert

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import ru.perekrestok.android.R
import ru.perekrestok.android.extension.AlertDialogButton
import ru.perekrestok.android.extension.requireParameter
import ru.perekrestok.android.extension.showAlertDialog

class AlertDialogFragment : DialogFragment() {

    companion object {
        private const val TITLE_KEY = "title_key"
        private const val MESSAGE_KEY = "message_key"

        fun newInstance(
            title: String,
            message: String,
            onConfirmAction: () -> Unit
        ): AlertDialogFragment = AlertDialogFragment().apply {
            arguments = bundleOf(
                TITLE_KEY to title,
                MESSAGE_KEY to message
            )

            this.onConfirmAction = onConfirmAction
            isCancelable = false
        }
    }

    private val title: String by requireParameter(TITLE_KEY)
    private val message: String by requireParameter(MESSAGE_KEY)

    var onConfirmAction: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().showAlertDialog(
            title = title,
            message = message,
            positiveButton = AlertDialogButton(getString(R.string.text_yes)) {
                onConfirmAction()
            },
            negativeButton = AlertDialogButton(getString(R.string.text_no))
        )
    }
}