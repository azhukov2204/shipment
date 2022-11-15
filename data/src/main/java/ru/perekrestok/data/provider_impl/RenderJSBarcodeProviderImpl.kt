package ru.perekrestok.data.provider_impl

import org.apache.commons.text.StringEscapeUtils
import ru.perekrestok.domain.entity.ScanMode
import ru.perekrestok.domain.repository.RenderJSBarcodeProvider

class RenderJSBarcodeProviderImpl : RenderJSBarcodeProvider {

    override fun getRenderBarcodeJs(barcode: String, scanMode: ScanMode): String {

        val escapedBarcode = StringEscapeUtils.escapeEcmaScript(barcode)
        return when (scanMode) {
            ScanMode.TO_ACTIVE_FIELD -> getRenderToActiveFieldJs(escapedBarcode)
            ScanMode.RUN_JS -> getRenderBasicJs(escapedBarcode)
        }
    }

    /**
     * Данный скрипт используется Urovo. Скрипт оставил без изменений, тольок в качестве аргумента
     * сейчас передаю экранированную строку.
     */
    private fun getRenderToActiveFieldJs(escapedBarcode: String): String {
        return "(function(barcode) {\n" +
            "    var ele = document.activeElement;\n" +
            "    var tagName = ele.tagName.toLowerCase();\n" +
            "    if (tagName == 'textarea' || ((tagName == 'input') && (ele.type == 'text' || ele.type == 'search' ||" +
            "           ele.type == 'password' || ele.type == 'tel' || ele.type == 'url'))) {\n" +
            "        var curLength = ele.value.length;\n" +
            "        var maxlength = ele.maxLength;\n" +
            "        var start = ele.selectionStart;\n" +
            "        var end = ele.selectionEnd;\n" +
            "        if (maxlength != -1) {\n" +
            "            var leftLength = curLength - (end - start);\n" +
            "            if (leftLength + barcode.length > maxlength) {\n" +
            "                barcode = barcode.substr(0, maxlength - leftLength);\n" +
            "            }\n" +
            "        }\n" +
            "        ele.value = ele.value.substr(0, start) + barcode + ele.value.substring(end, curLength);\n" +
            "        ele.setSelectionRange(start + barcode.length, start + barcode.length);\n" +
            "    } else if (tagName == 'input' && (ele.type == 'email' || ele.type == 'number')) {\n" +
            "        var maxlength = ele.maxLength;\n" +
            "        if (maxlength != -1) {\n" +
            "            if (barcode.length > maxlength) {\n" +
            "                barcode = barcode.substr(0, maxlength);\n" +
            "            }\n" +
            "        }\n" +
            "        ele.value = barcode;\n" +
            "    }\n" +
            "})('$escapedBarcode');"
    }

    override fun getBackClickJS(): String {
        return "onBackPressed()"
    }

    private fun getRenderBasicJs(escapedBarcode: String): String {
        return "onBarcodeScanned('$escapedBarcode')"
    }
}
