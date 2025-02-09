package dev.arkbuilders.rate.core.domain.usecase

import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import java.time.OffsetDateTime

class CheckAndPrepopulateDefaultGroupUseCase(
    private val groupRepo: GroupRepo,
) {
    suspend operator fun invoke() {
        val features =
            listOf(GroupFeatureType.Quick, GroupFeatureType.Portfolio, GroupFeatureType.PairAlert)

        features.forEach {
            checkAndPrepopulateDefault(it)
        }
    }

    private suspend fun checkAndPrepopulateDefault(featureType: GroupFeatureType) {
        val roomDefault = groupRepo.getDefaultByFeatureType(featureType)
        roomDefault ?: let {
            val default = Group(0, null, isDefault = true, 0, OffsetDateTime.now())
            groupRepo.update(default, featureType)
        }
    }
}
