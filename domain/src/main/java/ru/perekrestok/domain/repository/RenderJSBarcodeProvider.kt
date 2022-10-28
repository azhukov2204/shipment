package ru.perekrestok.domain.repository

import ru.perekrestok.domain.entity.ScanMode

interface RenderJSBarcodeProvider {
    fun getRenderBarcodeJs(barcode: String, scanMode: ScanMode): String
    fun getBackClickJS(): String
}
