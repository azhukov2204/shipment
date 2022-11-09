package ru.perekrestok.android.recycler_view.decorator

import android.graphics.Canvas
import android.view.View

class VerticalDividerItemDecoration(
    private val settings: DividerDecorationSettings
) : DividerItemDecoration(settings) {

    override fun drawDivider(canvas: Canvas, child: View) {
        val lineY = child.bottom - settings.stroke.width / 2.0F
        canvas.drawLine(
            child.left + settings.margin.start,
            lineY,
            child.right - settings.margin.end,
            lineY,
            dividerPaint
        )
    }
}
