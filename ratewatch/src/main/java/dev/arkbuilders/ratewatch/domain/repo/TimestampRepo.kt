package dev.arkbuilders.ratewatch.domain.repo

import dev.arkbuilders.ratewatch.domain.model.TimestampType
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface TimestampRepo {
    suspend fun rememberTimestamp(type: TimestampType)

    suspend fun getTimestamp(type: TimestampType): OffsetDateTime?

    fun timestampFlow(type: TimestampType): Flow<OffsetDateTime?>
}
