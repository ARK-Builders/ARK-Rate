package dev.arkbuilders.rate.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.data.db.entity.RoomPairAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface PairAlertDao {
    @Upsert
    suspend fun insert(pairAlert: RoomPairAlert): Long

    @Query("SELECT * FROM RoomPairAlert WHERE id = :id")
    suspend fun getById(id: Long): RoomPairAlert?

    @Query("SELECT * FROM RoomPairAlert")
    suspend fun getAll(): List<RoomPairAlert>

    @Query("SELECT * FROM RoomPairAlert")
    fun getAllFlow(): Flow<List<RoomPairAlert>>

    @Query("DELETE FROM RoomPairAlert WHERE id = :id")
    suspend fun delete(id: Long): Int
}
