package deng.jitian.raeder.database

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable

@Entity(tableName = "SourceData")
data class Source(@PrimaryKey @ColumnInfo(name = "name") var name: String,
                  @ColumnInfo(name = "link") var link: String,
                  @ColumnInfo(name = "tag") var tag: String
)

@Dao
interface SourceDao {
    @Query("SELECT DISTINCT tag FROM SourceData")
    fun getTags(): Flowable<List<String>>

    @Query("SELECT * FROM SourceData")
    fun getAll(): Flowable<List<Source>>

    @Insert(onConflict = REPLACE)
    fun insert(source: Source)

    @Query("DELETE FROM SourceData WHERE name= :name")
    fun delete(name: String)
}