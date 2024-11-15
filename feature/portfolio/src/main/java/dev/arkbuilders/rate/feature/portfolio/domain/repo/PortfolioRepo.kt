package dev.arkbuilders.rate.feature.portfolio.domain.repo

import kotlinx.coroutines.flow.Flow

interface PortfolioRepo {
    suspend fun allAssets(): List<dev.arkbuilders.rate.feature.portfolio.domain.model.Asset>

    fun allAssetsFlow(): Flow<List<dev.arkbuilders.rate.feature.portfolio.domain.model.Asset>>

    suspend fun getById(id: Long): dev.arkbuilders.rate.feature.portfolio.domain.model.Asset?

    suspend fun setAsset(asset: dev.arkbuilders.rate.feature.portfolio.domain.model.Asset)

    suspend fun setAssetsList(list: List<dev.arkbuilders.rate.feature.portfolio.domain.model.Asset>)

    suspend fun removeAsset(id: Long): Boolean
}
