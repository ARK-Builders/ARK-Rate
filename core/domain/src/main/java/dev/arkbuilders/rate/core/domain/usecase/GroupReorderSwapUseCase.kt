package dev.arkbuilders.rate.core.domain.usecase

import android.util.Log
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.GroupRepo

class GroupReorderSwapUseCase(
    private val groupRepo: GroupRepo,
) {
    suspend operator fun invoke(
        groups: List<Group>,
        from: Int,
        to: Int,
        groupFeatureType: GroupFeatureType,
    ): List<Group> {
        val newGroups =
            groups.toMutableList()
                .apply { add(to, removeAt(from)) }
                .mapIndexed { index, group ->
                    group.copy(orderIndex = index)
                }
        groupRepo.update(newGroups, groupFeatureType)
        return newGroups
    }
}
