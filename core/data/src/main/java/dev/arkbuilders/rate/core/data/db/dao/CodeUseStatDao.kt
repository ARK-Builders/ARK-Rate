package dev.arkbuilders.rate.core.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.data.db.entity.RoomCodeUseStat
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeUseStatDao {
    @Upsert
    suspend fun insert(state: RoomCodeUseStat)

    @Query("SELECT * FROM RoomCodeUseStat WHERE code = :code")
    suspend fun getByCode(code: CurrencyCode): RoomCodeUseStat?

    @Query("SELECT * FROM RoomCodeUseStat")
    suspend fun getAll(): List<RoomCodeUseStat>

    @Query("SELECT * FROM RoomCodeUseStat")
    fun getAllFlow(): Flow<List<RoomCodeUseStat>>
}
