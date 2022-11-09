package ru.perekrestok.domain

interface ApiStand {
    val id: Int
    val url: String
    val shortName: String
}

object Production : ApiStand {
    override val id: Int = 0
    override val url: String = "https://tsom.vprok.ru"
    override val shortName: String = "PRODUCTION"
}
