package deng.jitian.raeder.network

import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

fun getAllfeeds(sources: List<String>): List<RSS?> = sources.map { getFeeds(it) }

fun getFeeds(s: String): RSS? {
    var source = s
    // Check if prefixed with protocol
    // Try to use http if not found
    if (!(source.startsWith("http://") || source.startsWith("https://"))) {
        source = "http://$s"
    }
    Log.d("http", "getting $source")
    val (_, _, result) = source.httpGet().responseString()
    return when (result) {
        is Result.Failure -> null
        is Result.Success -> {
            val parser = XMLParser()
            parser.parseXML(result.value)
            parser.rss.link = source
            parser.rss
        }
    }
}
