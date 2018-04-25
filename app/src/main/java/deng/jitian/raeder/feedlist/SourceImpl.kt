package deng.jitian.raeder.feedlist

import android.util.Log
import android.widget.Toast
import deng.jitian.raeder.database.Feed
import deng.jitian.raeder.database.FeedCount
import deng.jitian.raeder.database.getFeedsDao
import io.reactivex.Flowable
import io.reactivex.Maybe

class NewListFragment : ListFragment() {
    override fun getList(): Flowable<List<FeedCount>> {
        return mdb!!.feedsDao().getUnreadCounts()
    }

    override fun getFeedIn(s: String): Maybe<List<Feed>> {
        val feedsDao = getFeedsDao(activity!!)
        if (feedsDao == null) {
            Toast.makeText(activity, "Load database failed!", Toast.LENGTH_SHORT).show()
            Log.e("Main", "getFeedsDao return null!")
            return Maybe.just(listOf())
        }
        return feedsDao.getFeedsIn(s).map{it.filter { f -> !f.read }}
    }

    override fun getFeedInTag(tag: String): Maybe<List<Feed>> {
        val feedsDao = getFeedsDao(activity!!)
        if (feedsDao == null) {
            Toast.makeText(activity, "Load database failed!", Toast.LENGTH_SHORT).show()
            Log.e("Main", "getFeedsDao return null!")
            return Maybe.just(listOf())
        }
        return feedsDao.getFeedsInTag(tag).map{it.filter { f -> !f.read }}
    }
}

class OldListFragment : ListFragment() {
    override fun getList(): Flowable<List<FeedCount>> {
        return mdb!!.feedsDao().getReadCounts()
    }

    override fun getFeedIn(s: String): Maybe<List<Feed>> {
        val feedsDao = getFeedsDao(activity!!)
        if (feedsDao == null) {
            Toast.makeText(activity, "Load database failed!", Toast.LENGTH_SHORT).show()
            Log.e("Main", "getFeedsDao return null!")
            return Maybe.just(listOf())
        }
        return feedsDao.getFeedsIn(s).map{it.filter { f -> f.read }}
    }

    override fun getFeedInTag(tag: String): Maybe<List<Feed>> {
        val feedsDao = getFeedsDao(activity!!)
        if (feedsDao == null) {
            Toast.makeText(activity, "Load database failed!", Toast.LENGTH_SHORT).show()
            Log.e("Main", "getFeedsDao return null!")
            return Maybe.just(listOf())
        }
        return feedsDao.getFeedsInTag(tag).map{it.filter { f -> f.read }}
    }
}

class StarredListFragment : ListFragment() {
    override fun getList(): Flowable<List<FeedCount>> {
        return mdb!!.feedsDao().getStarredCounts()
    }

    override fun getFeedIn(s: String): Maybe<List<Feed>> {
        val feedsDao = getFeedsDao(activity!!)
        if (feedsDao == null) {
            Toast.makeText(activity, "Load database failed!", Toast.LENGTH_SHORT).show()
            Log.e("Main", "getFeedsDao return null!")
            return Maybe.just(listOf())
        }
        return feedsDao.getFeedsIn(s).map{it.filter { f -> f.starred }}
    }

    override fun getFeedInTag(tag: String): Maybe<List<Feed>> {
        val feedsDao = getFeedsDao(activity!!)
        if (feedsDao == null) {
            Toast.makeText(activity, "Load database failed!", Toast.LENGTH_SHORT).show()
            Log.e("Main", "getFeedsDao return null!")
            return Maybe.just(listOf())
        }
        return feedsDao.getFeedsInTag(tag).map{it.filter { f -> f.starred }}
    }
}
