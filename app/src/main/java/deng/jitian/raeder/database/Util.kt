package deng.jitian.raeder.database

import android.content.Context

fun getFeedsDao(c: Context): FeedsDao? = RSSDatabase.getInstance(c)?.feedsDao()

fun getSourceDao(c: Context): SourceDao? = RSSDatabase.getInstance(c)?.sourceDao()

fun updateFeed(c: Context, feed: Feed) {
    val dao = getFeedsDao(c) ?: return
    dao.updateFeed(feed)
}
