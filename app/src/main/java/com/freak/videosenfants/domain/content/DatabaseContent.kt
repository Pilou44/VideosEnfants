package com.freak.videosenfants.domain.content

import android.arch.persistence.room.*
import com.freak.videosenfants.domain.bean.DlnaElement
import com.freak.videosenfants.domain.bean.VideoElement
import io.reactivex.Single

@Database(entities = arrayOf(DlnaElement::class, VideoElement::class), version = 1, exportSchema = false)
abstract class DatabaseContent : RoomDatabase() {
    abstract fun dlnaDao(): DlnaDao
    abstract fun localDao(): LocalDao

    @Dao
    interface DlnaDao {
        @Query("SELECT * FROM dlna_roots")
        fun getAll(): Single<List<DlnaElement>>

        @Query("SELECT * FROM dlna_roots WHERE id LIKE :id LIMIT 1")
        fun findById(id: Long): Single<DlnaElement>

        @Insert
        fun insertAll(vararg dlnas: DlnaElement): Array<Long>

        @Delete
        fun delete(dlna: DlnaElement) : Int
    }

    @Dao
    interface LocalDao {
        @Query("SELECT * FROM local_roots")
        fun getAll(): Single<List<VideoElement>>

        @Query("SELECT * FROM local_roots WHERE id LIKE :id LIMIT 1")
        fun findById(id: Long): Single<VideoElement>

        @Insert
        fun insertAll(vararg locals: VideoElement): Array<Long>

        @Insert
        fun insert(local: VideoElement): Long

        @Delete
        fun delete(local: VideoElement) : Int
    }
}