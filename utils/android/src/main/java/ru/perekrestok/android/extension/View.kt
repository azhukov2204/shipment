package ru.perekrestok.android.extension

import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes

private const val DEFAULT_THROTTLE_DELAY = 400L

fun View.setDebounceClickListener(delay: Number = DEFAULT_THROTTLE_DELAY, onClick: (View) -> Unit) {
    setOnClickListener {
        debounce(delay.toLong()) {
            onClick(it)
        }
    }
}

private var lastClickTimestamp = 0L
fun View.debounce(delay: Long = DEFAULT_THROTTLE_DELAY, action: () -> Unit): Boolean {
    val currentTimestamp = System.currentTimeMillis()
    val delta = currentTimestamp - lastClickTimestamp
    if (delta !in 0L..delay) {
        lastClickTimestamp = currentTimestamp
        action()
        return true
    }
    return false
}

@AttrRes
fun View.resolveThemeColor(@AttrRes attrResColor: Int): Int {
    return context.resolveThemeColor(attrResColor)
}

fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                context.inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        // It’s very important to remove this listener once we are done.
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}

fun View.hideSoftInputFromWindow() {
    context.inputMethodManager.hideSoftInputFromWindow(
        windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}