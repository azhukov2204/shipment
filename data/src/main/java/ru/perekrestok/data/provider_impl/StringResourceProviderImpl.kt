package ru.perekrestok.data.provider_impl

import android.content.Context
import androidx.annotation.StringRes
import ru.perekrestok.domain.provider.StringResourceProvider

class StringResourceProviderImpl(private val context: Context) : StringResourceProvider {
    override fun getStringResource(@StringRes stringRes: Int, vararg formatArgs: Any): String {
        return if (formatArgs.isEmpty()) {
            context.getString(stringRes)
        } else {
            context.getString(stringRes, *formatArgs)
        }
    }
}