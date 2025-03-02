package dev.arkbuilders.rate.feature.pairalert.data.repo

import dev.arkbuilders.rate.core.db.dao.PairAlertDao
import dev.arkbuilders.rate.core.db.entity.RoomPairAlert
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert
import dev.arkbuilders.rate.feature.pairalert.domain.repo.PairAlertRepo
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PairAlertRepoImpl @Inject constructor(
    private val dao: PairAlertDao,
    private val groupRepo: GroupRepo,
) : PairAlertRepo {
    override suspend fun insert(pairAlert: PairAlert) = dao.insert(pairAlert.toRoom())

    override suspend fun getById(id: Long): PairAlert? = dao.getById(id)?.toCondition(groupRepo)

    override suspend fun getAll(): List<PairAlert> {
        val groups = groupRepo.getAllSorted(GroupFeatureType.PairAlert)
        return dao.getAll().map { roomPair ->
            val group = groups.find { it.id == roomPair.groupId }!!
            roomPair.toCondition(group)
        }
    }

    override fun getAllFlow() =
        dao.getAllFlow().map { list ->
            val groups = groupRepo.getAllSorted(GroupFeatureType.PairAlert)
            list.map { roomPair ->
                val group = groups.find { it.id == roomPair.groupId }!!
                roomPair.toCondition(group)
            }
        }

    override suspend fun delete(id: Long) = dao.delete(id) > 0
}

private fun PairAlert.toRoom() =
    RoomPairAlert(
        id,
        targetCode,
        baseCode,
        targetPrice,
        startPrice,
        percent,
        oneTimeNotRecurrent,
        enabled,
        lastDateTriggered,
        group.id,
    )

private suspend fun RoomPairAlert.toCondition(groupRepo: GroupRepo) =
    PairAlert(
        id,
        targetCode,
        baseCode,
        targetPrice,
        startPrice,
        alertPercent,
        oneTimeNotRecurrent,
        enabled,
        lastDateTriggered,
        groupRepo.getById(groupId),
    )

private suspend fun RoomPairAlert.toCondition(group: Group) =
    PairAlert(
        id,
        targetCode,
        baseCode,
        targetPrice,
        startPrice,
        alertPercent,
        oneTimeNotRecurrent,
        enabled,
        lastDateTriggered,
        group,
    )
