package dev.arkbuilders.rate.core.domain.usecase

import dev.arkbuilders.rate.core.domain.model.GroupFeatureType

interface DefaultGroupNameProvider {
    fun provide(groupFeatureType: GroupFeatureType): String
}
