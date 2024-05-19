package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.data.model.CurrencyCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Entity
data class RoomCurrencyAmount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: CurrencyCode,
    val amount: Double,
    val group: String?
)

@Dao
interface AssetsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currencyAmount: RoomCurrencyAmount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<RoomCurrencyAmount>)

    @Query("SELECT * FROM RoomCurrencyAmount")
    suspend fun getAll(): List<RoomCurrencyAmount>

    @Query("SELECT * FROM RoomCurrencyAmount WHERE code = :code")
    suspend fun getByCode(code: CurrencyCode): RoomCurrencyAmount?

    @Query("SELECT * FROM RoomCurrencyAmount")
    fun allFlow(): Flow<List<RoomCurrencyAmount>>

    @Query("DELETE FROM RoomCurrencyAmount where id = :id")
    suspend fun delete(id: Long)
}

@Singleton
class AssetsRepo @Inject constructor(
    private val dao: AssetsDao
) {
    suspend fun allCurrencyAmount(): List<CurrencyAmount> = dao.getAll()
        .map { it.toCurrencyAmount() }

    fun allCurrencyAmountFlow(): Flow<List<CurrencyAmount>> = dao.allFlow()
        .map { list -> list.map { it.toCurrencyAmount() } }

    suspend fun setCurrencyAmount(amount: CurrencyAmount) =
        dao.insert(amount.toRoom())

    suspend fun setCurrencyAmountList(list: List<CurrencyAmount>) =
        dao.insertList(list.map { it.toRoom() })

    suspend fun removeCurrency(id: Long) =
        dao.delete(id)
}

private fun RoomCurrencyAmount.toCurrencyAmount() =
    CurrencyAmount(id, code, amount, group)

private fun CurrencyAmount.toRoom() = RoomCurrencyAmount(id, code, amount, group)