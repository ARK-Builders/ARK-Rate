package dev.arkbuilders.rate.core.domain.usecase

import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import java.time.OffsetDateTime

class GetGroupByIdOrCreateDefaultUseCase(
    private val groupRepo: GroupRepo,
    private val defaultGroupNameProvider: DefaultGroupNameProvider,
) {
    suspend operator fun invoke(
        groupId: Long?,
        groupFeatureType: GroupFeatureType,
    ): Group {
        if (groupId != null)
            return groupRepo.getById(groupId)

        val all = groupRepo.getAllSorted(groupFeatureType)
        if (all.isNotEmpty())
            return all.first()

        val default =
            Group(
                0,
                defaultGroupNameProvider.provide(groupFeatureType),
                0,
                OffsetDateTime.now(),
            )
        val id = groupRepo.update(default, groupFeatureType)
        return default.copy(id = id)
    }
}
