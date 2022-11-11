package ru.perekrestok.android.recycler_view

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.perekrestok.android.databinding.ItemLoadingBinding

fun loadingDelegate(): AdapterDelegate<List<RecyclerViewItem>> {
    return adapterDelegateViewBinding<LoadingItem, RecyclerViewItem, ItemLoadingBinding>(
        viewBinding = { layoutInflater, root ->
            ItemLoadingBinding.inflate(layoutInflater, root, false)
        },
        block = {}
    )
}

object LoadingItem : RecyclerViewItem {
    override val id = -1
}
