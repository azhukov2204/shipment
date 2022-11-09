package ru.perekrestok.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class MapPosition(
    val latitude: Double,
    val longitude: Double,
) {
    override fun toString(): String {
        return "$latitude,$longitude"
    }
}
