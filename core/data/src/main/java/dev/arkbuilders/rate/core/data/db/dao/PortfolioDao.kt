package dev.arkbuilders.rate.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.core.data.db.entity.RoomAsset
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {
    @Upsert
    suspend fun insert(asset: RoomAsset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<RoomAsset>)

    @Query("SELECT * FROM RoomAsset")
    suspend fun getAll(): List<RoomAsset>

    @Query("SELECT * FROM RoomAsset WHERE id = :id")
    suspend fun getById(id: Long): RoomAsset?

    @Query("SELECT * FROM RoomAsset")
    fun allFlow(): Flow<List<RoomAsset>>

    @Query("DELETE FROM RoomAsset WHERE id = :id")
    suspend fun delete(id: Long): Int
}
