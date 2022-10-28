package ru.perekrestok.android.recycler_view.decorator

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.graphics.withTranslation
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.dsl.AdapterDelegateLayoutContainerViewHolder

interface BlockingViewHolder

abstract class DividerItemDecoration(
    private val settings: DividerDecorationSettings

) : RecyclerView.ItemDecoration() {

    protected val dividerPaint = Paint().apply {
        color = settings.stroke.color
        flags = Paint.ANTI_ALIAS_FLAG
        strokeWidth = this@DividerItemDecoration.settings.stroke.width.toFloat()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        settings.spacing.updateOutRect(outRect)
    }

    abstract fun drawDivider(canvas: Canvas, child: View)

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.children.forEach { child ->
            val viewHolder = parent.getChildViewHolder(child)
            if (child.isNotLastItem(parent, state.itemCount) &&
                !(viewHolder is AdapterDelegateLayoutContainerViewHolder<*> && viewHolder.item is BlockingViewHolder)
            ) {
                canvas.withTranslation(
                    x = 0.0F,
                    y = settings.spacing.vertical.toFloat()
                ) {
                    drawDivider(this, child)
                }
            }
        }
    }

    private fun View.isNotLastItem(parent: RecyclerView, itemCount: Int): Boolean {
        return settings.withLastItem || parent.getChildAdapterPosition(this) < itemCount - 1
    }
}
