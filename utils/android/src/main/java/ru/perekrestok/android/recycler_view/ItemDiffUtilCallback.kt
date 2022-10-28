package ru.perekrestok.android.recycler_view

import androidx.recyclerview.widget.DiffUtil

class ItemDiffUtilCallback : DiffUtil.ItemCallback<RecyclerViewItem>() {

    override fun areItemsTheSame(oldItem: RecyclerViewItem, newItem: RecyclerViewItem): Boolean {
        return oldItem.areItemsTheSame(newItem)
    }

    override fun areContentsTheSame(oldItem: RecyclerViewItem, newItem: RecyclerViewItem): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }
}
