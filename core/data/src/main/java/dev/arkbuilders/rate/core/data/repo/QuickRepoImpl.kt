package dev.arkbuilders.rate.core.data.repo

import dev.arkbuilders.rate.core.data.db.dao.QuickPairDao
import dev.arkbuilders.rate.core.data.db.entity.RoomQuickPair
import dev.arkbuilders.rate.core.domain.model.QuickPair
import dev.arkbuilders.rate.core.domain.repo.QuickRepo
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickRepoImpl @Inject constructor(val dao: QuickPairDao) : QuickRepo {
    override suspend fun insert(quick: QuickPair) = dao.insert(quick.toRoom())

    override suspend fun getById(id: Long): QuickPair? = dao.getById(id)?.toQuickPair()

    override suspend fun getAll() = dao.getAll().map { it.toQuickPair() }

    override fun allFlow() = dao.allFlow().map { list -> list.map { it.toQuickPair() } }

    override suspend fun delete(id: Long) = dao.delete(id) > 0
}

private fun QuickPair.toRoom() =
    RoomQuickPair(id, from, amount, to, calculatedDate, pinnedDate, group)

private fun RoomQuickPair.toQuickPair() =
    QuickPair(id, from, amount, to, calculatedDate, pinnedDate, group)
