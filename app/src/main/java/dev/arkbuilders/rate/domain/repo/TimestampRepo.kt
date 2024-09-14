package dev.arkbuilders.rate.domain.repo

import dev.arkbuilders.rate.domain.model.TimestampType
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface TimestampRepo {
    suspend fun rememberTimestamp(type: TimestampType)

    suspend fun getTimestamp(type: TimestampType): OffsetDateTime?

    fun timestampFlow(type: TimestampType): Flow<OffsetDateTime?>
}
