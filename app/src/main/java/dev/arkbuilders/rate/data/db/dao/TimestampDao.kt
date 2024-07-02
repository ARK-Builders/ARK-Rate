package dev.arkbuilders.rate.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.data.db.entity.RoomFetchTimestamp

@Dao
interface TimestampDao {
    @Upsert
    suspend fun insert(fetchTimestamp: RoomFetchTimestamp)

    @Query("SELECT * FROM RoomFetchTimestamp WHERE currencyType = :currencyType")
    suspend fun getTimestamp(currencyType: String): RoomFetchTimestamp?
}