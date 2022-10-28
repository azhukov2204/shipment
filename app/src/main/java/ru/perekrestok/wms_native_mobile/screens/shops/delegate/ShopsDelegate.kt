package ru.perekrestok.wms_native_mobile.screens.shops.delegate

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.perekrestok.android.extension.setDebounceClickListener
import ru.perekrestok.android.recycler_view.RecyclerViewItem
import ru.perekrestok.wms_native_mobile.databinding.ItemShopBinding

fun shopsDelegate(onClickListener: (Int) -> Unit): AdapterDelegate<List<RecyclerViewItem>> {
    return adapterDelegateViewBinding<ShopItem, RecyclerViewItem, ItemShopBinding>(
        viewBinding = { layoutInflater, root ->
            ItemShopBinding.inflate(
                layoutInflater,
                root,
                false
            )
        },
        block = {
            with(binding) {

                root.setDebounceClickListener {
                    onClickListener(item.id)
                }

                bind {
                    tvShopName.text = item.title
                }
            }
        }
    )
}
