package dev.arkbuilders.rate.domain.repo

import dev.arkbuilders.rate.domain.model.TimestampType
import java.time.OffsetDateTime

interface TimestampRepo {
    suspend fun rememberTimestamp(type: TimestampType)
    suspend fun getTimestamp(type: TimestampType): OffsetDateTime?
}