package deng.jitian.raeder.database

import android.arch.persistence.room.*
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import io.reactivex.Flowable
import io.reactivex.Maybe

@Parcelize
@Entity(tableName = "FeedsData")
data class Feed(@ColumnInfo(name = "title") var title: String,
                @PrimaryKey @ColumnInfo(name = "link") var link: String,
                @ColumnInfo(name = "sourceName") var sourceName: String,
                @ColumnInfo(name = "starred") var starred: Boolean,
                @ColumnInfo(name = "read") var read: Boolean,
                @ColumnInfo(name = "pubDate") var pubDate: String,
                @ColumnInfo(name = "description") var description: String): Parcelable {
    @Ignore
    constructor() : this( "", "", "",
            false, false, "", "")
}

data class FeedCount(@ColumnInfo(name = "sourceName") var source: String,
                     @ColumnInfo(name = "COUNT(*)") var count: Int,
                     @ColumnInfo(name = "tag") var tag: String)

@Dao
interface FeedsDao {
    @Query("SELECT f.sourceName,COUNT(*),s.tag FROM FeedsData f JOIN SourceData s" +
            " WHERE f.read = 0 and f.sourceName = s.name GROUP BY f.sourceName")
    fun getUnreadCounts(): Flowable<List<FeedCount>>

    @Query("SELECT f.sourceName,COUNT(*),s.tag FROM FeedsData f JOIN SourceData s" +
            " WHERE f.read = 1 and f.sourceName = s.name GROUP BY f.sourceName")
    fun getReadCounts(): Flowable<List<FeedCount>>

    @Query("SELECT f.sourceName,COUNT(*),s.tag FROM FeedsData f JOIN SourceData s" +
            " WHERE f.starred = 1 and f.sourceName = s.name GROUP BY f.sourceName")
    fun getStarredCounts(): Flowable<List<FeedCount>>

    @Query("SELECT * FROM FeedsData WHERE sourceName = :source")
    fun getFeedsIn(source: String): Maybe<List<Feed>>

    @Query("SELECT * FROM FeedsData WHERE sourceName IN" +
            "(SELECT name FROM SourceData WHERE tag = :tag)")
    fun getFeedsInTag(tag: String): Maybe<List<Feed>>

    @Insert(onConflict = IGNORE)
    fun insertFeed(feed: Feed)

    @Update
    fun updateFeed(feed: Feed)
}
