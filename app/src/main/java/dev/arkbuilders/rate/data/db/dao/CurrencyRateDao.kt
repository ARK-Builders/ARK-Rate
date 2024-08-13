package dev.arkbuilders.rate.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.data.db.entity.RoomCurrencyRate

@Dao
interface CurrencyRateDao {
    @Upsert
    suspend fun insert(currencyRate: List<RoomCurrencyRate>)

    @Query("SELECT * FROM RoomCurrencyRate WHERE currencyType = :currencyType")
    suspend fun getByType(currencyType: String): List<RoomCurrencyRate>

    @Query("SELECT * FROM RoomCurrencyRate")
    suspend fun getAll(): List<RoomCurrencyRate>
}