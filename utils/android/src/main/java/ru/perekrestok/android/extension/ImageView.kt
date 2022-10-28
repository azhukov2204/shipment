package ru.perekrestok.android.extension

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.core.widget.ImageViewCompat

fun ImageView.setAttrImageTint(@AttrRes attrResColor: Int?) {
    ImageViewCompat.setImageTintList(
        this,
        if (attrResColor != null) {
            ColorStateList.valueOf(resolveThemeColor(attrResColor))
        } else {
            null
        }
    )
}
