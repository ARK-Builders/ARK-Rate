package dev.arkbuilders.rate.core.data.repo

import dev.arkbuilders.rate.core.db.dao.GroupDao
import dev.arkbuilders.rate.core.db.entity.RoomGroup
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

class GroupRepoImpl(
    private val groupDao: GroupDao,
) : GroupRepo {
    override suspend fun getByNameOrCreateNew(
        name: String,
        featureType: GroupFeatureType,
    ): Group {
        val exists = getByName(name, featureType)
        exists?.let {
            return exists
        }

        val all = groupDao.getAllByFeatureType(featureType)
        val sortIndex = all.maxOf { it.orderIndex } + 1
        val group =
            Group(
                id = 0,
                name = name,
                orderIndex = sortIndex,
                creationTime = OffsetDateTime.now(),
            )
        val id = groupDao.insert(group.toRoom(featureType))
        return group.copy(id = id)
    }

    override suspend fun update(
        updated: Group,
        featureType: GroupFeatureType,
    ): Long {
        return groupDao.insert(updated.toRoom(featureType))
    }

    override suspend fun update(
        updated: List<Group>,
        featureType: GroupFeatureType,
    ) {
        groupDao.insert(updated.map { it.toRoom(featureType) })
    }

    override suspend fun getById(id: Long): Group {
        return groupDao.getById(id).toGroup()
    }

    override suspend fun getByName(
        name: String,
        featureType: GroupFeatureType,
    ): Group? {
        return groupDao.getByName(name, featureType)?.toGroup()
    }

    override suspend fun delete(id: Long) {
        groupDao.delete(id)
    }

    override fun allFlow(featureType: GroupFeatureType): Flow<List<Group>> {
        return groupDao
            .allFlow(featureType)
            .distinctUntilChanged()
            .map { list -> list.map { it.toGroup() } }
    }

    override suspend fun getAllSorted(featureType: GroupFeatureType): List<Group> {
        return groupDao
            .getAllByFeatureType(featureType)
            .map { it.toGroup() }
            .sortedBy { it.orderIndex }
    }
}

private fun Group.toRoom(featureType: GroupFeatureType) =
    RoomGroup(id, name, orderIndex, creationTime, featureType)

private fun RoomGroup.toGroup() = Group(id, name, orderIndex, creationTime)
