package deng.jitian.raeder.network

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

import java.io.IOException
import java.io.StringReader
import java.util.Date
import java.util.Observable


class XMLParser : Observable() {

    val rss: RSS = RSS()
    private var currentArticle: Article = Article()

    @Throws(XmlPullParserException::class, IOException::class)
    fun parseXML(xml: String) {

        val factory = XmlPullParserFactory.newInstance()

        factory.isNamespaceAware = false
        val xmlPullParser = factory.newPullParser()

        xmlPullParser.setInput(StringReader(xml))
        var insideItem = false
        var eventType = xmlPullParser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {

                xmlPullParser.name.apply {
                    when {
                        equals("item", ignoreCase = true) -> insideItem = true
                        insideItem -> {
                            when {
                                equals("title",ignoreCase = true)->{
                                    currentArticle.title = xmlPullParser.nextText()
                                }
                                equals("link",ignoreCase = true)->{
                                    currentArticle.link = xmlPullParser.nextText()
                                }
                                equals("pubDate",ignoreCase = true)->{
                                    currentArticle.pubDate = xmlPullParser.nextText()
                                }
                                equals("description",ignoreCase = true)->{
                                    currentArticle.description = xmlPullParser.nextText()
                                }
                            }
                        }
                        equals("title",ignoreCase = true)->{
                            rss.name = xmlPullParser.nextText()
                        }
                        equals("link",ignoreCase = true)->{
                            rss.link = xmlPullParser.nextText()
                        }
                    }
                }
            }else if(eventType == XmlPullParser.END_TAG) {
                if(xmlPullParser.name.equals("item",ignoreCase = true)) {
                    insideItem = false
                    rss.articles.add(currentArticle)
                    currentArticle = Article()
                }
            }
            eventType = xmlPullParser.next()
        }
    }
}
