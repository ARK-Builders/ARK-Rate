package dev.arkbuilders.rate.feature.quick.data

import dev.arkbuilders.rate.core.db.dao.QuickPairDao
import dev.arkbuilders.rate.core.db.entity.RoomQuickPair
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickRepoImpl @Inject constructor(
    private val dao: QuickPairDao,
    private val groupRepo: GroupRepo,
) : QuickRepo {
    override suspend fun insert(quick: QuickPair) = dao.insert(quick.toRoom())

    override suspend fun getById(id: Long): QuickPair? = dao.getById(id)?.toQuickPair(groupRepo)

    override suspend fun getAll(): List<QuickPair> {
        val groups = groupRepo.getAllSorted(GroupFeatureType.Quick)
        return dao.getAll().map { roomPair ->
            val group = groups.find { it.id == roomPair.groupId }!!
            roomPair.toQuickPair(group)
        }
    }

    override fun allFlow() =
        dao.allFlow().map { list ->
            val groups = groupRepo.getAllSorted(GroupFeatureType.Quick)
            list.map { roomPair ->
                val group = groups.find { it.id == roomPair.groupId }!!
                roomPair.toQuickPair(group)
            }
        }

    override suspend fun delete(id: Long) = dao.delete(id) > 0
}

private fun QuickPair.toRoom() =
    RoomQuickPair(
        id,
        from,
        amount,
        to,
        calculatedDate,
        pinnedDate,
        group.id,
    )

private suspend fun RoomQuickPair.toQuickPair(group: Group) =
    QuickPair(id, from, amount, to, calculatedDate, pinnedDate, group)

private suspend fun RoomQuickPair.toQuickPair(groupRepo: GroupRepo) =
    QuickPair(id, from, amount, to, calculatedDate, pinnedDate, groupRepo.getById(groupId))
