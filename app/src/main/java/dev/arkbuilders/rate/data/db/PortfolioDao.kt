package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dev.arkbuilders.rate.domain.model.Asset
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import javax.inject.Inject
import javax.inject.Singleton

@Entity
data class RoomAsset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: CurrencyCode,
    val amount: Double,
    val group: String?
)

@Dao
interface PortfolioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: RoomAsset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<RoomAsset>)

    @Query("SELECT * FROM RoomAsset")
    suspend fun getAll(): List<RoomAsset>

    @Query("SELECT * FROM RoomAsset WHERE id = :id")
    suspend fun getById(id: Long): RoomAsset?

    @Query("SELECT * FROM RoomAsset")
    fun allFlow(): Flow<List<RoomAsset>>

    @Query("DELETE FROM RoomAsset where id = :id")
    suspend fun delete(id: Long)
}

@Singleton
class PortfolioRepoImpl @Inject constructor(
    private val dao: PortfolioDao
): PortfolioRepo {
    override suspend fun allAssets(): List<Asset> = dao.getAll()
        .map { it.toAsset() }

    override fun allAssetsFlow(): Flow<List<Asset>> = dao.allFlow()
        .map { list -> list.map { it.toAsset() } }

    override suspend fun getById(id: Long) = dao.getById(id)?.toAsset()

    override suspend fun setAsset(asset: Asset) =
        dao.insert(asset.toRoom())

    override suspend fun setAssetsList(list: List<Asset>) =
        dao.insertList(list.map { it.toRoom() })

    override suspend fun removeAsset(id: Long) =
        dao.delete(id)
}

private fun RoomAsset.toAsset() =
    Asset(id, code, amount, group)

private fun Asset.toRoom() = RoomAsset(id, code, value, group)