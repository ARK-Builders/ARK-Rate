package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.data.CurrencyAmount
import dev.arkbuilders.rate.data.CurrencyCode
import dev.arkbuilders.rate.data.QuickCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Entity
data class RoomQuickCurrency(
    @PrimaryKey
    val code: CurrencyCode,
    val usedCount: Int
)

@Dao
interface QuickCurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quickCurrency: RoomQuickCurrency)

    @Query("SELECT * FROM RoomQuickCurrency")
    suspend fun getAll(): List<RoomQuickCurrency>

    @Query("SELECT * FROM RoomQuickCurrency")
    fun allFlow(): Flow<List<RoomQuickCurrency>>

    @Query("SELECT * FROM RoomQuickCurrency where code = :code")
    suspend fun getByCode(code: CurrencyCode): RoomQuickCurrency?

    @Query("DELETE FROM RoomQuickCurrency where code = :code")
    suspend fun delete(code: CurrencyCode)
}

@Singleton
class QuickCurrencyRepo @Inject constructor(val dao: QuickCurrencyDao) {

    suspend fun insert(quickCurrency: QuickCurrency) =
        dao.insert(quickCurrency.toRoom())

    suspend fun getAll() = dao.getAll().map { it.toQuickCurrency() }

    suspend fun getByCode(code: CurrencyCode) = dao.getByCode(code)?.toQuickCurrency()

    fun allFlow() =
        dao.allFlow().map { list -> list.map { it.toQuickCurrency() } }

    suspend fun delete(code: CurrencyCode) = dao.delete(code)
}

private fun QuickCurrency.toRoom() = RoomQuickCurrency(code, usedCount)
private fun RoomQuickCurrency.toQuickCurrency() = QuickCurrency(code, usedCount)