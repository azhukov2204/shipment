package ru.perekrestok.wms_native_mobile.screens

import android.webkit.WebSettings
import ru.perekrestok.domain.entity.CacheMode

val WEB_VIEW_CACHE_MODES = listOf(
    CacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK, "LOAD_CACHE_ELSE_NETWORK", "CACHE_ELSE_NETWORK"),
    CacheMode(WebSettings.LOAD_CACHE_ONLY, "LOAD_CACHE_ONLY", "CACHE_ONLY"),
    CacheMode(WebSettings.LOAD_DEFAULT, "LOAD_DEFAULT", "DEFAULT"),
    CacheMode(WebSettings.LOAD_NORMAL, "LOAD_NORMAL", "NORMAL"),
    CacheMode(WebSettings.LOAD_NO_CACHE, "LOAD_NO_CACHE", "NO_CACHE")
)
