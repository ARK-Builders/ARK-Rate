package dev.arkbuilders.rate.domain.repo

import dev.arkbuilders.rate.domain.model.Asset
import dev.arkbuilders.rate.domain.model.CurrencyCode
import kotlinx.coroutines.flow.Flow

interface PortfolioRepo {
    suspend fun allAssets(): List<Asset>

    fun allAssetsFlow(): Flow<List<Asset>>

    suspend fun getById(id: Long): Asset?

    suspend fun getAllByCode(code: CurrencyCode): List<Asset>

    suspend fun setAsset(asset: Asset)

    suspend fun setAssetsList(list: List<Asset>)

    suspend fun removeAsset(id: Long): Boolean
}
