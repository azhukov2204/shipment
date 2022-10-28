package ru.perekrestok.android.recycler_view

interface RecyclerViewItem {

    val id: Int

    fun areItemsTheSame(comparableItem: RecyclerViewItem): Boolean = this.id == comparableItem.id

    fun areContentsTheSame(comparableItem: RecyclerViewItem): Boolean = this == comparableItem
}
