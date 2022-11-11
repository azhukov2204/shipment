package ru.perekrestok.wms_native_mobile.screens.search_device.delegate

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import ru.perekrestok.android.extension.setDebounceClickListener
import ru.perekrestok.android.recycler_view.RecyclerViewItem
import ru.perekrestok.wms_native_mobile.databinding.ItemSearchDeviceBinding

fun deviceDelegate(onClickListener: (Int) -> Unit): AdapterDelegate<List<RecyclerViewItem>> {
    return adapterDelegateViewBinding<DeviceItem, RecyclerViewItem, ItemSearchDeviceBinding>(
        viewBinding = { layoutInflater, root ->
            ItemSearchDeviceBinding.inflate(
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
                    deviceTv.text = item.name
                }
            }
        }
    )
}
