package dev.arkbuilders.rate.feature.portfolio.domain.usecase

import dev.arkbuilders.rate.feature.portfolio.domain.model.Asset
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo

class AddNewAssetsUseCase(
    private val portfolioRepo: PortfolioRepo,
) {
    suspend operator fun invoke(assets: List<Asset>) {
        val mergedAssets =
            assets.map { asset ->
                val roomAsset =
                    portfolioRepo.getAllByCode(asset.code).find {
                        it.group == asset.group
                    }
                val mergedAsset =
                    roomAsset?.let {
                        roomAsset.copy(value = asset.value + roomAsset.value)
                    } ?: asset
                mergedAsset
            }
        portfolioRepo.setAssetsList(mergedAssets)
    }
}
