package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.data.model.PairAlertCondition
import javax.inject.Inject
import javax.inject.Singleton

@Entity
data class RoomPairAlertCondition(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val numeratorCode: String,
    val denominatorCode: String,
    val ratio: Float,
    val moreNotLess: Boolean
)

@Dao
interface PairAlertConditionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pairAlert: RoomPairAlertCondition): Long

    @Query("SELECT * FROM RoomPairAlertCondition")
    suspend fun getAll(): List<RoomPairAlertCondition>

    @Query("DELETE FROM RoomPairAlertCondition where id = :id")
    suspend fun delete(id: Long)
}

private fun PairAlertCondition.toRoom() = RoomPairAlertCondition(
    id, numeratorCode, denominatorCode, ratio, moreNotLess
)

private fun RoomPairAlertCondition.toCondition() = PairAlertCondition(
    id, numeratorCode, denominatorCode, ratio, moreNotLess
)

@Singleton
class PairAlertConditionRepo @Inject constructor(
    private val dao: PairAlertConditionDao
) {
    suspend fun insert(pairAlertCondition: PairAlertCondition) =
        dao.insert(pairAlertCondition.toRoom())

    suspend fun getAll() = dao.getAll().map { it.toCondition() }

    suspend fun delete(id: Long) = dao.delete(id)
}