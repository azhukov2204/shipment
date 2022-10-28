package ru.perekrestok.domain.provider

interface StringResourceProvider {
    fun getStringResource(stringRes: Int, vararg formatArgs: Any): String
}
