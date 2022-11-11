package ru.perekrestok.domain.interactor

import ru.perekrestok.domain.entity.ScanMode
import ru.perekrestok.domain.repository.RenderJSBarcodeProvider

interface JavaScriptCommandInteractor {
    fun getRenderBarcodeJs(barcode: String, scanMode: ScanMode): String
    fun getBackClickJS(): String
}

class JavaScriptCommandInteractorImpl(
    private val renderJSBarcodeProvider: RenderJSBarcodeProvider
) : JavaScriptCommandInteractor {

    override fun getRenderBarcodeJs(barcode: String, scanMode: ScanMode): String {
        return renderJSBarcodeProvider.getRenderBarcodeJs(barcode, scanMode)
    }

    override fun getBackClickJS(): String {
        return renderJSBarcodeProvider.getBackClickJS()
    }
}
