package deng.jitian.raeder.feedlist

import deng.jitian.raeder.database.FeedCount
import io.reactivex.Flowable

class NewListFragment : ListFragment() {
    override fun getList(): Flowable<List<FeedCount>> {
        return mdb!!.feedsDao().getUnreadCounts()
    }
}

class OldListFragment : ListFragment() {
    override fun getList(): Flowable<List<FeedCount>> {
        return mdb!!.feedsDao().getReadCounts()
    }
}

class StarredListFragment : ListFragment() {
    override fun getList(): Flowable<List<FeedCount>> {
        return mdb!!.feedsDao().getStarredCounts()
    }
}
