package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.data.CurrencyRate
import dev.arkbuilders.rate.data.CurrencyType
import dev.arkbuilders.rate.data.CurrencyCode
import javax.inject.Inject

@Entity
data class RoomCurrencyRate(
    @PrimaryKey
    val code: CurrencyCode,
    val currencyType: String,
    val rate: Double
)

@Dao
interface CurrencyRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currencyRate: List<RoomCurrencyRate>)

    @Query("SELECT * FROM RoomCurrencyRate where currencyType = :currencyType")
    suspend fun getByType(currencyType: String): List<RoomCurrencyRate>
}

class CurrencyRateLocalDataSource @Inject constructor(val dao: CurrencyRateDao) {
    suspend fun insert(
        currencyRate: List<CurrencyRate>,
        currencyType: CurrencyType
    ) = dao.insert(currencyRate.map { it.toRoom(currencyType) })

    suspend fun getByType(currencyType: CurrencyType) =
        dao.getByType(currencyType.name).map { it.toCurrencyRate() }
}

private fun RoomCurrencyRate.toCurrencyRate() =
    CurrencyRate(CurrencyType.valueOf(currencyType), code, rate)

private fun CurrencyRate.toRoom(currencyType: CurrencyType) =
    RoomCurrencyRate(code, currencyType.name, rate)