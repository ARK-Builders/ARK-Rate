package dev.arkbuilders.ratewatch.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.ratewatch.data.db.entity.RoomQuickPair
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickPairDao {
    @Upsert
    suspend fun insert(quickCurrency: RoomQuickPair)

    @Query("SELECT * FROM RoomQuickPair WHERE id = :id")
    suspend fun getById(id: Long): RoomQuickPair?

    @Query("SELECT * FROM RoomQuickPair")
    suspend fun getAll(): List<RoomQuickPair>

    @Query("SELECT * FROM RoomQuickPair")
    fun allFlow(): Flow<List<RoomQuickPair>>

    @Query("DELETE FROM RoomQuickPair WHERE id = :id")
    suspend fun delete(id: Long): Int
}
