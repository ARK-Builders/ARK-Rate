package dev.arkbuilders.rate.domain.usecase

import dev.arkbuilders.rate.domain.model.Asset
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddNewAssetsUseCase @Inject constructor(
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
