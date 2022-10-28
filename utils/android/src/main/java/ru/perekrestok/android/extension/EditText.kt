package ru.perekrestok.android.extension

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

fun EditText.setOnTextChangeListenerByLifecycle(
    lifecycleOwner: LifecycleOwner,
    onBeforeTextChanged: (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit = { _, _, _, _ -> },
    onTextChanged: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit = { _, _, _, _ -> },
    onAfterTextChanged: (s: Editable?) -> Unit = { _ -> }
) {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            onBeforeTextChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s, start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            onAfterTextChanged(s)
        }
    }

    lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onResume(owner: LifecycleOwner) {
            this@setOnTextChangeListenerByLifecycle.addTextChangedListener(textWatcher)
        }

        override fun onPause(owner: LifecycleOwner) {
            this@setOnTextChangeListenerByLifecycle.removeTextChangedListener(textWatcher)
        }
    })
}
