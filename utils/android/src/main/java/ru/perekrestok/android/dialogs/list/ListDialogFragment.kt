package ru.perekrestok.android.dialogs.list

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.perekrestok.android.R
import ru.perekrestok.android.dialogs.list.delegate.ListDialogItem
import ru.perekrestok.android.dialogs.list.delegate.ListDialogItems
import ru.perekrestok.android.dialogs.list.delegate.listDialogItemDelegate
import ru.perekrestok.android.extension.AlertDialogButton
import ru.perekrestok.android.extension.requireParameter
import ru.perekrestok.android.extension.showListDialog
import ru.perekrestok.android.extension.viewLifecycleAware
import ru.perekrestok.android.recycler_view.ItemDiffUtilCallback
import ru.perekrestok.android.recycler_view.decorator.DividerDecorationSettings
import ru.perekrestok.android.recycler_view.decorator.VerticalDividerItemDecoration

class ListDialogFragment : DialogFragment() {
    companion object {
        private const val TITLE_KEY = "title_key"
        private const val LIST_DIALOG_ITEMS_KEY = "list_dialog_items_key"

        fun newInstance(
            title: String,
            listDialogItems: ListDialogItems,
            onItemClicked: (Int) -> Unit
        ): ListDialogFragment = ListDialogFragment().apply {
            arguments = bundleOf(
                TITLE_KEY to title,
                LIST_DIALOG_ITEMS_KEY to listDialogItems
            )

            this.onItemClicked = onItemClicked
            isCancelable = false
        }
    }

    private val title: String by requireParameter(TITLE_KEY)
    private val listDialogItems: ListDialogItems by requireParameter(LIST_DIALOG_ITEMS_KEY)

    var onItemClicked: (Int) -> Unit = {}

    private val listAdapter by viewLifecycleAware {
        AsyncListDifferDelegationAdapter(
            ItemDiffUtilCallback(),
            listDialogItemDelegate { selectedItem ->
                handleSelectItem(selectedItem)
            }
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().showListDialog(
            adapter = listAdapter,
            decorator = VerticalDividerItemDecoration(
                DividerDecorationSettings
                    .Builder(requireContext(), R.attr.colorBorderDivider)
                    .build()
            ),
            title = title,
            negativeButton = AlertDialogButton(getString(R.string.text_cancel))
        ).also {
            listAdapter.items = listDialogItems.listDialogItems
        }
    }

    private fun handleSelectItem(selectedItem: ListDialogItem) {
        val newListDialogItems = listDialogItems.listDialogItems.map { currentItem ->
            currentItem.copy(isSelected = currentItem.id == selectedItem.id)
        }
        listAdapter.items = newListDialogItems
        onItemClicked(selectedItem.id)
        dismiss()
    }
}
