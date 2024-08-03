package dev.arkbuilders.rate.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.data.db.entity.RoomFetchTimestamp
import kotlinx.coroutines.flow.Flow

@Dao
interface TimestampDao {
    @Upsert
    suspend fun insert(fetchTimestamp: RoomFetchTimestamp)

    @Query("SELECT * FROM RoomFetchTimestamp WHERE type = :type")
    suspend fun getTimestamp(type: String): RoomFetchTimestamp?

    @Query("SELECT * FROM RoomFetchTimestamp WHERE type = :type")
    fun timestampFlow(type: String): Flow<RoomFetchTimestamp?>
}