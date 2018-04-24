package deng.jitian.raeder.network

import java.util.*


data class RSS @JvmOverloads constructor(
        var name: String = "",
        var link: String = "",
        var articles: MutableList<Article> = ArrayList())

data class Article @JvmOverloads constructor(
        var title: String = "",
        var link: String = "",
        var description: String = "",
        var pubDate: String = "",
        var guid: String = "")
