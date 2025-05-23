package dev.arkbuilders.rate.core.domain.model

import java.time.OffsetDateTime

enum class GroupFeatureType {
    Quick,
    Portfolio,
    PairAlert,
}

data class Group(
    val id: Long,
    val name: String,
    val orderIndex: Int,
    val creationTime: OffsetDateTime,
) {
    companion object {
        fun empty(name: String = "") = Group(0, name, 0, OffsetDateTime.now())
    }
}
