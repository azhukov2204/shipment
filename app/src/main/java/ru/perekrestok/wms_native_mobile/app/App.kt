package ru.perekrestok.wms_native_mobile.app

import org.koin.core.module.Module
import ru.perekrestok.android.app.BaseApp
import ru.perekrestok.data.di.dataModules
import ru.perekrestok.domain.di.domainModules
import ru.perekrestok.kotlin.addToList
import ru.perekrestok.wms_native_mobile.di.appModules

class App : BaseApp() {
    override fun predefineKoinModules(): List<Module> {
        return buildList<Module> {
            dataModules.addToList(this@buildList)
            appModules.addToList(this@buildList)
            domainModules.addToList(this@buildList)
        }
    }
}
