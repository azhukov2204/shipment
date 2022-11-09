package ru.perekrestok.android.extension

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import ru.perekrestok.android.R
import ru.perekrestok.kotlin.StringPatterns
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T> Fragment.viewLifecycleAware(
    onDestroyView: () -> Unit = {},
    propertyInit: () -> T,
) = object : ReadOnlyProperty<Fragment, T> {

    private var viewLifecycleAwareProperty: T? = null

    init {
        viewLifecycleOwnerLiveData.observe(this@viewLifecycleAware) { viewLifecycleOwner ->
            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)

                    onDestroyView()

                    viewLifecycleAwareProperty = null
                }
            })
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T = viewLifecycleAwareProperty ?: run {
        val newProperty = propertyInit()

        viewLifecycleAwareProperty = newProperty

        newProperty
    }
}

fun Fragment.showSnackBar(text: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(requireView(), text, duration).show()
}

fun Fragment.showErrorDetailsSnackBar(
    errorText: String,
    errorDetails: String = StringPatterns.EMPTY_SYMBOL,
    duration: Int = Snackbar.LENGTH_LONG
) {
    val snackBar = Snackbar.make(requireView(), errorText, duration)
    val group = snackBar.view as ViewGroup
    group.setBackgroundColor(group.resolveThemeColor(R.attr.ErrorDetailsSnackBarBackground))
    snackBar.setActionTextColor(group.resolveThemeColor(R.attr.ErrorDetailsSnackBarActionTextColor))

    errorDetails.takeIf { it.isNotBlank() }?.let { errorMessageNotBlank ->
        snackBar.setAction(resources.getString(R.string.text_details)) {
            requireContext().showAlertDialog(
                message = errorMessageNotBlank,
                negativeButton = AlertDialogButton(text = resources.getString(R.string.text_close)),
            )
        }
    }

    snackBar.show()
}

inline fun <reified T> Fragment.requireParameter(key: String): Lazy<T> = lazy(mode = LazyThreadSafetyMode.NONE) {

    val argumentClass = T::class.java

    val param = when {

        argumentClass == String::class.java -> requireArguments().getString(key, null)
            ?: throw IllegalArgumentException("Wrong type String for $key key.")

        argumentClass == Integer::class.java -> requireArguments().getInt(key)

        argumentClass == Boolean::class.java -> requireArguments().getBoolean(key)

        argumentClass == java.lang.Boolean::class.java -> requireArguments().getBoolean(key)

        argumentClass == Long::class.java -> requireArguments().getLong(key)

        argumentClass == Short::class.java -> requireArguments().getShort(key)

        argumentClass == Byte::class.java -> requireArguments().getByte(key)

        argumentClass.interfaces.contains(Class.forName("android.os.Parcelable")) ->
            requireArguments().getParcelable(key) ?: throw IllegalArgumentException(
                "Wrong type Parcelable for $key key."
            )

        else -> throw IllegalArgumentException("Parameter with key = $key is not passed")
    }

    param as T
}
