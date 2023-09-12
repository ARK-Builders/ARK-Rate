package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.QuickConvertToCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Entity
data class RoomQuickConvertToCurrency(
    @PrimaryKey
    val code: CurrencyCode
)

@Dao
interface QuickConvertToCurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quickCurrency: RoomQuickConvertToCurrency)

    @Query("SELECT * FROM RoomQuickConvertToCurrency")
    suspend fun getAll(): List<RoomQuickConvertToCurrency>

    @Query("SELECT * FROM RoomQuickConvertToCurrency")
    fun allFlow(): Flow<List<RoomQuickConvertToCurrency>>

    @Query("DELETE FROM RoomQuickConvertToCurrency where code = :code")
    suspend fun delete(code: CurrencyCode)
}

@Singleton
class QuickConvertToCurrencyRepo @Inject constructor(
    private val dao: QuickConvertToCurrencyDao
) {
    suspend fun insert(code: CurrencyCode) =
        dao.insert(RoomQuickConvertToCurrency(code = code))

    suspend fun getAll() = dao.getAll().map { it.toQuickCurrency() }

    fun allFlow() =
        dao.allFlow().map { list -> list.map { it.toQuickCurrency() } }

    suspend fun delete(code: CurrencyCode) = dao.delete(code)
}

private fun RoomQuickConvertToCurrency.toQuickCurrency() =
    QuickConvertToCurrency(code)