package ru.perekrestok.domain.entity

data class CustomSetting(
    val key: String,
    val type: String,
    val value: String
) {
    companion object {
        fun List<CustomSetting>.getValueByKey(key: String): String? {
            return find { it.key.equals(key, ignoreCase = true) }?.value
        }
    }
}
