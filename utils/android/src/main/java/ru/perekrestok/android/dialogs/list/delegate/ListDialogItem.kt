package ru.perekrestok.android.dialogs.list.delegate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.perekrestok.android.recycler_view.RecyclerViewItem

@Parcelize
data class ListDialogItem(
    override val id: Int,
    val name: String,
    val isSelected: Boolean = false
) : RecyclerViewItem, Parcelable

@JvmInline
@Parcelize
value class ListDialogItems(val listDialogItems: List<ListDialogItem>) : Parcelable
