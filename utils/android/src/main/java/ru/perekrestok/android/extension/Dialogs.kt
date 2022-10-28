package ru.perekrestok.android.extension

import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.perekrestok.android.R
import ru.perekrestok.android.databinding.DialogEditTextLayoutBinding
import ru.perekrestok.android.databinding.DialogResultBinding
import ru.perekrestok.android.databinding.IncludeDialogListBinding
import ru.perekrestok.android.databinding.IncludeDialogWaitingBinding
import ru.perekrestok.android.recycler_view.RecyclerViewItem

fun Context.showListDialog(
    adapter: AsyncListDifferDelegationAdapter<RecyclerViewItem>,
    decorator: RecyclerView.ItemDecoration? = null,
    title: String? = null,
    positiveButton: AlertDialogButton? = null,
    negativeButton: AlertDialogButton? = null,
    neutralButton: AlertDialogButton? = null,
    cancelable: Boolean = false
): AlertDialog {
    val layout = IncludeDialogListBinding.inflate(layoutInflater)
    layout.rvItems.adapter = adapter
    layout.rvItems.itemAnimator = null
    if (decorator != null) {
        layout.rvItems.addItemDecoration(decorator)
    }
    return showAlertDialog(
        title = title,
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        neutralButton = neutralButton,
        customView = layout.root,
        cancelable = cancelable
    )
}

fun Context.showWaitingDialog(
    message: String,
    positiveButton: AlertDialogButton? = null,
    cancelable: Boolean = false
): AlertDialog {
    val layout = IncludeDialogWaitingBinding.inflate(layoutInflater)
    layout.tvMessage.text = message
    return showAlertDialog(
        positiveButton = positiveButton,
        customView = layout.root,
        cancelable = cancelable
    )
}

fun Context.showResultDialog(
    message: String,
    positiveButton: AlertDialogButton? = null,
    isSuccess: Boolean,
    cancelable: Boolean = true
): AlertDialog {
    val layout = DialogResultBinding.inflate(layoutInflater)
    val drawableResourceId = if (isSuccess) R.drawable.ic_success else R.drawable.ic_error

    layout.apply {
        ivStatus.setImageDrawable(drawable(drawableResourceId))
        tvMessage.text = message
    }

    return showAlertDialog(
        positiveButton = positiveButton,
        customView = layout.root,
        cancelable = cancelable
    )
}

@Suppress("LongParameterList")
fun Context.showAlertDialog(
    title: String? = null,
    message: CharSequence? = null,
    positiveButton: AlertDialogButton? = null,
    negativeButton: AlertDialogButton? = null,
    neutralButton: AlertDialogButton? = null,
    customView: View? = null,
    @DrawableRes icon: Int? = null,
    cancelable: Boolean = true,
    onDismissListener: () -> Unit = {},
    onShowListener: () -> Unit = {},
): AlertDialog {
    val builder = AlertDialog.Builder(this)
    title?.let {
        builder.setTitle(it)
    }
    message?.let {
        builder.setMessage(it)
    }
    positiveButton?.let { button ->
        builder.setPositiveButton(button.text) { _, _ ->
            button.onClicked()
        }
    }
    negativeButton?.let { button ->
        builder.setNegativeButton(button.text) { _, _ ->
            button.onClicked()
        }
    }
    neutralButton?.let { button ->
        builder.setNeutralButton(button.text) { _, _ ->
            button.onClicked()
        }
    }
    customView?.let { view ->
        builder.setView(view)
    }
    icon?.let { drawableIcon ->
        builder.setIcon(drawableIcon)
    }
    builder.setOnDismissListener {
        onDismissListener()
    }
    builder.setCancelable(cancelable)
    val dialog = builder.create()
    dialog.setOnShowListener { onShowListener() }
    dialog.show()
    return dialog
}

@Suppress("LongParameterList")
fun Context.showEditTextDialog(
    title: String? = null,
    message: String? = null,
    text: String? = null,
    positiveButtonText: String,
    negativeButtonText: String,
    neutralButtonText: String,
    isKeyboardRequired: Boolean = false,
    @DrawableRes icon: Int? = null,
    @DrawableRes endIconDrawable: Int? = null,
    endIconMode: Int = TextInputLayout.END_ICON_NONE,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    prefix: String? = null,
    maxLength: Int? = null,
    positiveButtonAction: (String) -> Unit,
    negativeButtonAction: () -> Unit,
    neutralButtonAction: () -> Unit,
    onDismissListener: () -> Unit = {},
    onAfterTextChangeListener: (String) -> Unit = {}
): AlertDialog {
    val editTextBinding = DialogEditTextLayoutBinding.inflate(layoutInflater)

    with(editTextBinding) {
        etDialog.apply {
            doAfterTextChanged { changedText ->
                changedText?.let { onAfterTextChangeListener(changedText.toString()) }
            }
            maxLength?.let { filters = arrayOf(InputFilter.LengthFilter(maxLength)) }
            text?.let { setText(text) }
        }

        tilDialog.apply {
            this.endIconMode = endIconMode
            endIconDrawable?.let { this.endIconDrawable = drawable(endIconDrawable) }
            setEndIconTintList(ColorStateList.valueOf(resolveThemeColor(R.attr.colorAccent)))
        }

        prefix?.let { tvPrefix.text = prefix }
    }

    if (isKeyboardRequired) {
        editTextBinding.etDialog.focusAndShowKeyboard()
    }

    return showAlertDialog(
        title = title,
        message = message,
        customView = editTextBinding.root,
        icon = icon,
        positiveButton = positiveButtonText.takeIf { it.isNotBlank() }?.let {
            AlertDialogButton(positiveButtonText) {
                val value = editTextBinding.etDialog.text.toString()
                positiveButtonAction(value)
            }
        },
        negativeButton = negativeButtonText.takeIf { it.isNotBlank() }?.let {
            AlertDialogButton(negativeButtonText) {
                negativeButtonAction()
            }
        },
        neutralButton = neutralButtonText.takeIf { it.isNotBlank() }?.let {
            AlertDialogButton(neutralButtonText) {
                neutralButtonAction()
            }
        },
        onDismissListener = onDismissListener,
        onShowListener = {
            editTextBinding.etDialog.inputType = inputType
            editTextBinding.etDialog.requestFocus()

            editTextBinding.root.post {
                inputMethodManager.showSoftInput(
                    editTextBinding.etDialog,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }
        }
    )
}

private const val NO_ITEM_CHECKED = -1

@Suppress("LongParameterList")
fun <T> Fragment.showSingleChoiceDialog(
    title: String,
    elements: List<T>,
    itemCheckedPosition: Int? = null,
    onItemTextTransform: (T) -> String,
    onChoiceListener: (T) -> Unit = {},
    positiveConfirmation: Pair<String, (DialogInterface, Int) -> Unit>? = null,
    negativeConfirmation: Pair<String, (DialogInterface, Int) -> Unit>? = null,
) {
    val textItems = elements.map(onItemTextTransform).toTypedArray()

    var selectedPosition: Int? = null

    val builder = AlertDialog
        .Builder(requireContext())
        .setTitle(title)
        .setSingleChoiceItems(textItems, itemCheckedPosition ?: NO_ITEM_CHECKED) { dialog, which ->

            selectedPosition = which

            onChoiceListener(elements[which])

            if (positiveConfirmation == null) {
                dialog.dismiss()
            }
        }

    if (positiveConfirmation != null) {
        builder.setPositiveButton(positiveConfirmation.first) { dialog: DialogInterface, _: Int ->
            positiveConfirmation.second(dialog, selectedPosition ?: -1)
        }
    }

    if (negativeConfirmation != null) {
        builder.setNegativeButton(negativeConfirmation.first, negativeConfirmation.second)
    }

    val dialog = builder.create()

    dialog.show()
}