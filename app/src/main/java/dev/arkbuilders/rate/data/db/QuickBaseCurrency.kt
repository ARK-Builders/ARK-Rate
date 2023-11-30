package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.QuickBaseCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Entity
data class RoomQuickBaseCurrency(
    @PrimaryKey
    val code: CurrencyCode
)

@Dao
interface QuickBaseCurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quickCurrency: RoomQuickBaseCurrency)

    @Query("SELECT * FROM RoomQuickBaseCurrency")
    suspend fun getAll(): List<RoomQuickBaseCurrency>

    @Query("SELECT * FROM RoomQuickBaseCurrency")
    fun allFlow(): Flow<List<RoomQuickBaseCurrency>>

    @Query("DELETE FROM RoomQuickBaseCurrency where code = :code")
    suspend fun delete(code: CurrencyCode)
}

@Singleton
class QuickBaseCurrencyRepo @Inject constructor(
    private val dao: QuickBaseCurrencyDao
) {
    suspend fun insert(code: CurrencyCode) =
        dao.insert(RoomQuickBaseCurrency(code = code))

    suspend fun getAll() = dao.getAll().map { it.toQuickCurrency() }

    fun allFlow() =
        dao.allFlow().map { list -> list.map { it.toQuickCurrency() } }

    suspend fun delete(code: CurrencyCode) = dao.delete(code)
}

private fun RoomQuickBaseCurrency.toQuickCurrency() =
    QuickBaseCurrency(code)