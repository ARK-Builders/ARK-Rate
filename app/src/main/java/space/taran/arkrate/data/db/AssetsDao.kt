package space.taran.arkrate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import space.taran.arkrate.data.CurrencyAmount
import javax.inject.Inject

@Entity
data class RoomCurrencyAmount(
    @PrimaryKey
    val code: String,
    val amount: Double
)

@Dao
interface AssetsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currencyAmount: RoomCurrencyAmount)

    @Query("SELECT * FROM RoomCurrencyAmount")
    suspend fun getAll(): List<RoomCurrencyAmount>

    @Query("DELETE FROM RoomCurrencyAmount where code = :code")
    suspend fun delete(code: String)
}

class AssetsLocalDataSource @Inject constructor(val dao: AssetsDao) {
    suspend fun insert(currencyAmount: CurrencyAmount) =
        dao.insert(currencyAmount.toRoom())

    suspend fun getAll() = dao.getAll().map { it.toCurrencyAmount() }

    suspend fun delete(code: String) = dao.delete(code)
}

private fun RoomCurrencyAmount.toCurrencyAmount() = CurrencyAmount(code, amount)
private fun CurrencyAmount.toRoom() = RoomCurrencyAmount(code, amount)