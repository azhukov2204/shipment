package ru.perekrestok.domain.entity

import kotlinx.serialization.Serializable
import ru.perekrestok.kotlin.StringPatterns

@Serializable
data class Device(
    val name: String,
    val address: String
) {
    override fun toString(): String {
        return buildString {
            append(name)
            append(StringPatterns.WHITE_SPACE)
            append(address)
        }
    }
}
