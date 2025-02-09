package dev.arkbuilders.rate.core.domain.model

import java.time.OffsetDateTime

enum class GroupFeatureType {
    Quick,
    Portfolio,
    PairAlert,
}

data class Group(
    val id: Long,
    val name: String?,
    val isDefault: Boolean,
    val sortIndex: Int,
    val creationTime: OffsetDateTime,
)
