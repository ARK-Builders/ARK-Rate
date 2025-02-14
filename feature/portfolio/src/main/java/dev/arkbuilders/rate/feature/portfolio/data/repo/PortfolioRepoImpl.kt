package dev.arkbuilders.rate.feature.portfolio.data.repo

import dev.arkbuilders.rate.core.db.dao.PortfolioDao
import dev.arkbuilders.rate.core.db.entity.RoomAsset
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.feature.portfolio.domain.model.Asset
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepoImpl @Inject constructor(
    private val dao: PortfolioDao,
    private val groupRepo: GroupRepo,
) : PortfolioRepo {
    override suspend fun allAssets(): List<Asset> {
        val allGroups = groupRepo.getAllSorted(GroupFeatureType.Portfolio)
        return dao.getAll()
            .map { asset ->
                val group = allGroups.find { it.id == asset.groupId }!!
                asset.toAsset(group)
            }
    }

    override fun allAssetsFlow(): Flow<List<Asset>> =
        dao.allFlow()
            .map { list ->
                val allGroups = groupRepo.getAllSorted(GroupFeatureType.Portfolio)
                list.map { asset ->
                    val group = allGroups.find { it.id == asset.groupId }!!
                    asset.toAsset(group)
                }
            }

    override suspend fun getById(id: Long) = dao.getById(id)?.toAsset(groupRepo)

    override suspend fun getAllByCode(code: CurrencyCode): List<Asset> {
        val allGroups = groupRepo.getAllSorted(GroupFeatureType.Portfolio)
        return dao.getAllByCode(code)
            .map { asset ->
                val group = allGroups.find { it.id == asset.groupId }!!
                asset.toAsset(group)
            }
    }

    override suspend fun setAsset(asset: Asset) = dao.insert(asset.toRoom())

    override suspend fun setAssetsList(list: List<Asset>) = dao.insertList(list.map { it.toRoom() })

    override suspend fun removeAsset(id: Long) = dao.delete(id) > 0
}

private fun RoomAsset.toAsset(group: Group) = Asset(id, code, amount, group)

private suspend fun RoomAsset.toAsset(groupRepo: GroupRepo) =
    Asset(id, code, amount, groupRepo.getById(groupId))

private fun Asset.toRoom() = RoomAsset(id, code, value, group.id)
