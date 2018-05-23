package deng.jitian.backend.backup

import deng.jitian.backend.database.Source
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

val MAGIC = "imamagicnumber"
public fun sourceToString(s: List<Source>): String {
    val o = JSONObject()
    val ss = JSONArray()
    val cs = JSONArray()
    s.forEach {
        ss.put(it.link)
        cs.put(it.tag)
    }
    o.put("magic", MAGIC)
    o.put("source", ss)
    o.put("category", cs)
    return o.toString()
}

public fun stringToSource(s: String): List<SourceBak> {
    val o = JSONObject(s)
    if (o.getString("magic") != MAGIC)
        throw IllegalArgumentException("Magic Number Incorrect!")
    val ss = o.getJSONArray("source")
    val cs = o.getJSONArray("category")
    val r = ArrayList<SourceBak>()
    for (i in 0 until ss.length()) {
        r.add(SourceBak(ss.getString(i), cs.getString(i)))
    }
    return r
}

data class SourceBak(val link: String, val cat: String)