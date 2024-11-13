package dev.arkbuilders.rate.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.core.db.entity.RoomFetchTimestamp
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
