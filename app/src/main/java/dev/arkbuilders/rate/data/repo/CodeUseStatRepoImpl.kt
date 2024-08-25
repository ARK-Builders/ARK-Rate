package dev.arkbuilders.rate.data.repo

import dev.arkbuilders.rate.data.db.dao.CodeUseStatDao
import dev.arkbuilders.rate.data.db.entity.RoomCodeUseStat
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.stats.CodeUseStat
import dev.arkbuilders.rate.domain.repo.CodeUseStatRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CodeUseStatRepoImpl @Inject constructor(
    private val dao: CodeUseStatDao,
) : CodeUseStatRepo {
    override suspend fun codesUsed(vararg codes: CurrencyCode) {
        codes.forEach { code ->
            val old = dao.getByCode(code)?.toCodeUseStat()
            val new =
                old?.copy(count = old.count.inc(), lastUsedDate = OffsetDateTime.now())
                    ?: CodeUseStat(code, 1, OffsetDateTime.now())

            dao.insert(new.toRoom())
        }
    }

    override suspend fun getAll(): List<CodeUseStat> = dao.getAll().map { it.toCodeUseStat() }

    override fun getAllFlow(): Flow<List<CodeUseStat>> =
        dao.getAllFlow().map { list ->
            list.map { it.toCodeUseStat() }
        }
}

private fun RoomCodeUseStat.toCodeUseStat() = CodeUseStat(code, count, lastUsedDate)

private fun CodeUseStat.toRoom() = RoomCodeUseStat(code, count, lastUsedDate)
