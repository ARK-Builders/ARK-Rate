package dev.arkbuilders.rate.feature.pairalert.data.repo

import dev.arkbuilders.rate.core.db.dao.PairAlertDao
import dev.arkbuilders.rate.core.db.entity.RoomPairAlert
import dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert
import dev.arkbuilders.rate.feature.pairalert.domain.repo.PairAlertRepo
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PairAlertRepoImpl @Inject constructor(
    private val dao: PairAlertDao,
) : PairAlertRepo {
    override suspend fun insert(pairAlert: PairAlert) = dao.insert(pairAlert.toRoom())

    override suspend fun getById(id: Long): PairAlert? = dao.getById(id)?.toCondition()

    override suspend fun getAll() = dao.getAll().map { it.toCondition() }

    override fun getAllFlow() = dao.getAllFlow().map { it.map { it.toCondition() } }

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
        group,
    )

private fun RoomPairAlert.toCondition() =
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
