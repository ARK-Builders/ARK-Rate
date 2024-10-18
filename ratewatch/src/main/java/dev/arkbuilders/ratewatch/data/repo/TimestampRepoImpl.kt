package dev.arkbuilders.ratewatch.data.repo

import dev.arkbuilders.ratewatch.data.db.dao.TimestampDao
import dev.arkbuilders.ratewatch.data.db.entity.RoomFetchTimestamp
import dev.arkbuilders.ratewatch.domain.model.TimestampType
import dev.arkbuilders.ratewatch.domain.repo.TimestampRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton
class TimestampRepoImpl @Inject constructor(private val dao: TimestampDao) :
    TimestampRepo {
        override suspend fun rememberTimestamp(type: TimestampType) =
            dao.insert(RoomFetchTimestamp(type.name, OffsetDateTime.now()))

        override suspend fun getTimestamp(type: TimestampType) =
            dao.getTimestamp(
                type.name,
            )?.timestamp

        override fun timestampFlow(type: TimestampType): Flow<OffsetDateTime?> =
            dao.timestampFlow(type.name).map {
                it?.timestamp
            }
    }
