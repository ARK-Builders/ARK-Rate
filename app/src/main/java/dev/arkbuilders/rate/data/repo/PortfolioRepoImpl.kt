package dev.arkbuilders.rate.data.repo

import dev.arkbuilders.rate.data.db.dao.PortfolioDao
import dev.arkbuilders.rate.data.db.entity.RoomAsset
import dev.arkbuilders.rate.domain.model.Asset
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepoImpl @Inject constructor(
    private val dao: PortfolioDao,
) : PortfolioRepo {
    override suspend fun allAssets(): List<Asset> =
        dao.getAll()
            .map { it.toAsset() }

    override fun allAssetsFlow(): Flow<List<Asset>> =
        dao.allFlow()
            .map { list -> list.map { it.toAsset() } }

    override suspend fun getById(id: Long) = dao.getById(id)?.toAsset()

    override suspend fun setAsset(asset: Asset) {
        val roomAsset =
            dao.getAllByCode(asset.code).find {
                it.group == asset.group
            }?.toAsset()
        val mergedAsset =
            roomAsset?.let {
                roomAsset.copy(value = asset.value + roomAsset.value)
            } ?: asset
        dao.insert(mergedAsset.toRoom())
    }

    override suspend fun setAssetsList(list: List<Asset>) = list.forEach { setAsset(it) }

    override suspend fun removeAsset(id: Long) = dao.delete(id) > 0
}

private fun RoomAsset.toAsset() = Asset(id, code, amount, group)

private fun Asset.toRoom() = RoomAsset(id, code, value, group)
