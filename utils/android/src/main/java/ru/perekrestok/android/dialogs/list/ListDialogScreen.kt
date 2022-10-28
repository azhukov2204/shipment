package ru.perekrestok.android.dialogs.list

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.perekrestok.android.dialogs.list.delegate.ListDialogItems

class ListDialogScreen(
    private val title: String,
    private val listDialogItems: ListDialogItems,
    private val onItemClicked: (Int) -> Unit
) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return ListDialogFragment.newInstance(
            title = title,
            listDialogItems = listDialogItems,
            onItemClicked = onItemClicked
        )
    }
}
