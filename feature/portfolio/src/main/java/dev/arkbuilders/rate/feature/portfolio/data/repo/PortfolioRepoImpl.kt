package dev.arkbuilders.rate.feature.portfolio.data.repo

import dev.arkbuilders.rate.core.db.dao.PortfolioDao
import dev.arkbuilders.rate.core.db.entity.RoomAsset
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.feature.portfolio.domain.model.Asset
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo
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

    override suspend fun getAllByCode(code: CurrencyCode) =
        dao.getAllByCode(code).map { it.toAsset() }

    override suspend fun setAsset(asset: Asset) = dao.insert(asset.toRoom())

    override suspend fun setAssetsList(list: List<Asset>) = dao.insertList(list.map { it.toRoom() })

    override suspend fun removeAsset(id: Long) = dao.delete(id) > 0
}

private fun RoomAsset.toAsset() = Asset(id, code, amount, group)

private fun Asset.toRoom() = RoomAsset(id, code, value, group)
