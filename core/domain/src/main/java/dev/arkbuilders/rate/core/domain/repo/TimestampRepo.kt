package dev.arkbuilders.rate.core.domain.repo

import dev.arkbuilders.rate.core.domain.model.TimestampType
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

interface TimestampRepo {
    suspend fun rememberTimestamp(type: TimestampType)

    suspend fun rememberTimestamp(
        type: TimestampType,
        manualDate: OffsetDateTime,
    )

    suspend fun getTimestamp(type: TimestampType): OffsetDateTime?

    fun timestampFlow(type: TimestampType): Flow<OffsetDateTime?>
}
