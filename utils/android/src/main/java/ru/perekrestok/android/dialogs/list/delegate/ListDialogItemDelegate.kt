package ru.perekrestok.android.dialogs.list.delegate

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.perekrestok.android.databinding.ItemListDialogBinding
import ru.perekrestok.android.recycler_view.RecyclerViewItem

fun listDialogItemDelegate(
    onItemClickListener: (item: ListDialogItem) -> Unit
): AdapterDelegate<List<RecyclerViewItem>> {
    return adapterDelegateViewBinding<ListDialogItem, RecyclerViewItem, ItemListDialogBinding>(
        viewBinding = { layoutInflater, root ->
            ItemListDialogBinding.inflate(layoutInflater, root, false)
        },
        block = {
            binding.root.setOnClickListener {
                onItemClickListener(item)
            }

            bind {
                with(binding) {
                    rbChoose.isChecked = item.isSelected
                    tvMessage.text = item.name
                }
            }
        }
    )
}
