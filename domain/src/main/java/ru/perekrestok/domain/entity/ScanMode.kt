package ru.perekrestok.domain.entity

enum class ScanMode(val id: Int) {
    TO_ACTIVE_FIELD(0),
    RUN_JS(1);

    companion object {
        fun getById(id: Int?): ScanMode {
            return values().find { it.id == id } ?: TO_ACTIVE_FIELD
        }
    }
}
