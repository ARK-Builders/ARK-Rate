package dev.arkbuilders.rate.core.data.repo

import dev.arkbuilders.rate.core.db.dao.GroupDao
import dev.arkbuilders.rate.core.db.entity.RoomGroup
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import java.time.OffsetDateTime

class GroupRepoImpl(
    private val groupDao: GroupDao,
) : GroupRepo {
    override suspend fun new(
        name: String,
        featureType: GroupFeatureType,
    ): Group {
        val all = groupDao.getAllByFeatureType(featureType)
        val sortIndex = all.maxBy { it.sortIndex }.sortIndex + 1
        val group =
            Group(
                id = 0,
                name = name,
                isDefault = false,
                sortIndex = sortIndex,
                creationTime = OffsetDateTime.now(),
            )
        val id = groupDao.insert(group.toRoom(featureType))
        return group.copy(id = id)
    }

    override suspend fun update(
        updated: Group,
        featureType: GroupFeatureType,
    ) {
        groupDao.insert(updated.toRoom(featureType))
    }

    override suspend fun delete(id: Long) {
        groupDao.delete(id)
    }

    override suspend fun getAllSorted(featureType: GroupFeatureType): List<Group> {
        return groupDao
            .getAllByFeatureType(featureType)
            .map { it.toGroup() }
            .sortedBy { it.sortIndex }
    }

    override suspend fun getDefaultByFeatureType(featureType: GroupFeatureType): Group? {
        return groupDao.getDefaultByFeatureType(featureType)?.toGroup()
    }
}

private fun Group.toRoom(featureType: GroupFeatureType) =
    RoomGroup(id, name, isDefault, sortIndex, creationTime, featureType)

private fun RoomGroup.toGroup() = Group(id, name, isDefault, sortIndex, creationTime)
