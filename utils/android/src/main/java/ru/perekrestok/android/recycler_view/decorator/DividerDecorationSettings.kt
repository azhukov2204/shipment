package ru.perekrestok.android.recycler_view.decorator

import android.content.Context
import android.graphics.Rect
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.perekrestok.android.extension.attr
import ru.perekrestok.android.extension.dpToPx

class DividerDecorationSettings private constructor(
    val stroke: DividerDecorationStroke,
    val margin: DividerDecorationMargin,
    val spacing: DividerDecorationSpacing,
    val withLastItem: Boolean
) {
    companion object {
        private const val STROKE_WIDTH = 1f
    }

    data class Builder(
        val context: Context,
        @AttrRes
        val strokeAttr: Int,
        var stroke: DividerDecorationStroke = DividerDecorationStroke(
            context.attr(strokeAttr),
            context.dpToPx(STROKE_WIDTH)
        ),
        var margin: DividerDecorationMargin = DividerDecorationMargin(),
        var spacing: DividerDecorationSpacing = DividerDecorationSpacing(),
        var withLastItem: Boolean = false
    ) {

        fun stroke(stroke: DividerDecorationStroke) = apply { this.stroke = stroke }
        fun margin(margin: DividerDecorationMargin) = apply { this.margin = margin }
        fun spacing(spacing: DividerDecorationSpacing) = apply { this.spacing = spacing }
        fun withLastItem(withLastItem: Boolean) = apply { this.withLastItem = withLastItem }
        fun build() = DividerDecorationSettings(stroke, margin, spacing, withLastItem)
    }
}

class DividerDecorationStroke(
    @ColorInt val color: Int,
    val width: Int
)

class DividerDecorationMargin(
    val start: Float = 0.0F,
    val end: Float = start,
    val top: Float = 0.0F,
    val bottom: Float = top,
)

class DividerDecorationSpacing(
    val horizontal: Int = 0,
    val vertical: Int = horizontal
) {
    fun updateOutRect(outRect: Rect) {
        outRect.set(
            horizontal,
            vertical,
            horizontal,
            vertical
        )
    }
}