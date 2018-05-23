package deng.jitian.backend.database

import android.content.Context

public fun getFeedsDao(c: Context): FeedsDao? = RSSDatabase.getInstance(c)?.feedsDao()

public fun getSourceDao(c: Context): SourceDao? = RSSDatabase.getInstance(c)?.sourceDao()

public fun updateFeed(c: Context, feed: Feed) {
    val dao = getFeedsDao(c) ?: return
    dao.updateFeed(feed)
}
