package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dev.arkbuilders.rate.data.CurrencyAmount
import dev.arkbuilders.rate.data.CurrencyCode
import javax.inject.Inject

@Entity
data class RoomCurrencyAmount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: CurrencyCode,
    val amount: Double
)

@Dao
interface AssetsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currencyAmount: RoomCurrencyAmount)

    @Query("SELECT * FROM RoomCurrencyAmount")
    suspend fun getAll(): List<RoomCurrencyAmount>

    @Query("SELECT * FROM RoomCurrencyAmount WHERE code = :code")
    suspend fun getByCode(code: CurrencyCode): RoomCurrencyAmount?

    @Query("SELECT * FROM RoomCurrencyAmount")
    fun allFlow(): Flow<List<RoomCurrencyAmount>>

    @Query("DELETE FROM RoomCurrencyAmount where code = :code")
    suspend fun delete(code: CurrencyCode)
}

class AssetsLocalDataSource @Inject constructor(val dao: AssetsDao) {
    suspend fun insert(currencyAmount: CurrencyAmount) =
        dao.insert(currencyAmount.toRoom())

    suspend fun getAll() = dao.getAll().map { it.toCurrencyAmount() }

    suspend fun getByCode(code: CurrencyCode) =
        dao.getByCode(code)?.toCurrencyAmount()

    fun allFlow() =
        dao.allFlow().map { list -> list.map { it.toCurrencyAmount() } }

    suspend fun delete(code: String) = dao.delete(code)
}

private fun RoomCurrencyAmount.toCurrencyAmount() = CurrencyAmount(id, code, amount)
private fun CurrencyAmount.toRoom() = RoomCurrencyAmount(id, code, amount)