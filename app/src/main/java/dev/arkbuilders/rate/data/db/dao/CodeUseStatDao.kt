package dev.arkbuilders.rate.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.data.db.entity.RoomCodeUseStat
import dev.arkbuilders.rate.domain.model.CurrencyCode

@Dao
interface CodeUseStatDao {
    @Upsert
    suspend fun insert(state: RoomCodeUseStat)

    @Query("SELECT * FROM RoomCodeUseStat WHERE code = :code")
    suspend fun getByCode(code: CurrencyCode): RoomCodeUseStat?

    @Query("SELECT * FROM RoomCodeUseStat")
    suspend fun getAll(): List<RoomCodeUseStat>
}