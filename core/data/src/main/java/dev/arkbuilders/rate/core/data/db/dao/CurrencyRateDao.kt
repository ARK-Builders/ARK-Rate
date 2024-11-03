package dev.arkbuilders.rate.core.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.core.data.db.entity.RoomCurrencyRate

@Dao
interface CurrencyRateDao {
    @Upsert
    suspend fun insert(currencyRate: List<RoomCurrencyRate>)

    @Query("SELECT * FROM RoomCurrencyRate WHERE currencyType = :currencyType")
    suspend fun getByType(currencyType: String): List<RoomCurrencyRate>

    @Query("SELECT * FROM RoomCurrencyRate")
    suspend fun getAll(): List<RoomCurrencyRate>
}
