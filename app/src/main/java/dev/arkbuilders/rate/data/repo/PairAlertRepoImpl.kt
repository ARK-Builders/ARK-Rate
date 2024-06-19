package dev.arkbuilders.rate.data.repo

import dev.arkbuilders.rate.data.db.dao.PairAlertDao
import dev.arkbuilders.rate.data.db.entity.RoomPairAlert
import dev.arkbuilders.rate.domain.model.PairAlert
import dev.arkbuilders.rate.domain.repo.PairAlertRepo
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PairAlertRepoImpl @Inject constructor(
    private val dao: PairAlertDao
) : PairAlertRepo {
    override suspend fun insert(pairAlert: PairAlert) =
        dao.insert(pairAlert.toRoom())

    override suspend fun getById(id: Long): PairAlert? =
        dao.getById(id)?.toCondition()

    override suspend fun getAll() = dao.getAll().map { it.toCondition() }

    override fun getAllFlow() = dao.getAllFlow().map { it.map { it.toCondition() } }

    override suspend fun delete(id: Long) = dao.delete(id)
}

private fun PairAlert.toRoom() = RoomPairAlert(
    id,
    targetCode,
    baseCode,
    targetPrice,
    startPrice,
    percent,
    oneTimeNotRecurrent,
    enabled,
    lastDateTriggered?.toString(),
    group
)

private fun RoomPairAlert.toCondition() = PairAlert(
    id,
    targetCode,
    baseCode,
    targetPrice,
    startPrice,
    alertPercent,
    oneTimeNotRecurrent,
    enabled,
    lastDateTriggered?.let { OffsetDateTime.parse(lastDateTriggered) },
    group
)