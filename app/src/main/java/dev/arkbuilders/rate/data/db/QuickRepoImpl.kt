package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.domain.repo.QuickRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Entity
data class RoomQuickPair(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val from: CurrencyCode,
    val amount: Double,
    val to: String,
    val group: String?
)

@Dao
interface QuickPairDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quickCurrency: RoomQuickPair)

    @Query("SELECT * FROM RoomQuickPair WHERE id = :id")
    suspend fun getById(id: Long): RoomQuickPair?

    @Query("SELECT * FROM RoomQuickPair")
    suspend fun getAll(): List<RoomQuickPair>

    @Query("SELECT * FROM RoomQuickPair")
    fun allFlow(): Flow<List<RoomQuickPair>>

    @Query("DELETE FROM RoomQuickPair where id = :id")
    suspend fun delete(id: Long)
}

@Singleton
class QuickRepoImpl @Inject constructor(val dao: QuickPairDao): QuickRepo {

    override suspend fun insert(quick: QuickPair) =
        dao.insert(quick.toRoom())

    override suspend fun getById(id: Long): QuickPair? = dao.getById(id)?.toQuickPair()

    override suspend fun getAll() = dao.getAll().map { it.toQuickPair() }

    override fun allFlow() =
        dao.allFlow().map { list -> list.map { it.toQuickPair() } }

    override suspend fun delete(id: Long) = dao.delete(id)
}

private fun QuickPair.toRoom() = RoomQuickPair(id, from, amount, to.joinToString(separator = ","), group)
private fun RoomQuickPair.toQuickPair() = QuickPair(id, from, amount, to.split(","), group)