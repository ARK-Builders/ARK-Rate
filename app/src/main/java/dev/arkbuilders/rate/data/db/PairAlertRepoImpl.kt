package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.PairAlert
import dev.arkbuilders.rate.domain.repo.PairAlertRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
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
    val enabled: Boolean,
    val lastDateTriggered: String?,
    val group: String?
)

@Dao
interface PairAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pairAlert: RoomPairAlert): Long

    @Query("SELECT * FROM RoomPairAlert where id = :id")
    suspend fun getById(id: Long): RoomPairAlert?

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
    percent,
    oneTimeNotRecurrent,
    enabled,
    lastDateTriggered?.toString(),
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
    enabled,
    lastDateTriggered?.let { OffsetDateTime.parse(lastDateTriggered) },
    group
)

@Singleton
class PairAlertRepoImpl @Inject constructor(
    private val dao: PairAlertDao
) : PairAlertRepo {
    override suspend fun insert(pairAlert: PairAlert) =
        dao.insert(pairAlert.toRoom())

    override suspend fun getById(id: Long): PairAlert? =
        dao.getById(id)?.toCondition()

    override suspend fun getAll() = dao.getAll().map { it.toCondition() }

    override fun getAllFlow() = dao.getAllFlow().map { it.map { it.toCondition() } }

    override suspend fun delete(id: Long) = dao.delete(id)
}