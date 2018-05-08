package deng.jitian.raeder.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Feed::class, Source::class], version = 1, exportSchema = false)
abstract class RSSDatabase : RoomDatabase() {
    abstract fun feedsDao(): FeedsDao
    abstract fun sourceDao(): SourceDao

    companion object {
        private var INSTANCE: RSSDatabase? = null

        fun getInstance(context: Context): RSSDatabase? {
            if (INSTANCE == null) {
                synchronized(RSSDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            RSSDatabase::class.java, "rss.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
