package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.PairAlert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Entity
data class RoomPairAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val targetCode: CurrencyCode,
    val baseCode: CurrencyCode,
    val targetPrice: Double,
    val startPrice: Double,
    val alertPercent: Double?,
    val oneTimeNotRecurrent: Boolean,
    val priceNotPercent: Boolean,
    val triggered: Boolean,
    val group: String?
)

@Dao
interface PairAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pairAlert: RoomPairAlert): Long

    @Query("SELECT * FROM RoomPairAlert")
    suspend fun getAll(): List<RoomPairAlert>

    @Query("SELECT * FROM RoomPairAlert")
    fun getAllFlow(): Flow<List<RoomPairAlert>>

    @Query("DELETE FROM RoomPairAlert where id = :id")
    suspend fun delete(id: Long)
}

private fun PairAlert.toRoom() = RoomPairAlert(
    id,
    targetCode,
    baseCode,
    targetPrice,
    startPrice,
    alertPercent,
    oneTimeNotRecurrent,
    priceNotPercent,
    triggered,
    group
)

private fun RoomPairAlert.toCondition() = PairAlert(
    id,
    targetCode,
    baseCode,
    targetPrice,
    startPrice,
    alertPercent,
    oneTimeNotRecurrent,
    priceNotPercent,
    triggered,
    group
)

@Singleton
class PairAlertRepo @Inject constructor(
    private val dao: PairAlertDao
) {
    suspend fun insert(pairAlert: PairAlert) =
        dao.insert(pairAlert.toRoom())

    suspend fun getAll() = dao.getAll().map { it.toCondition() }

    fun getAllFlow() = dao.getAllFlow().map { it.map { it.toCondition() } }

    suspend fun delete(id: Long) = dao.delete(id)
}