package dev.arkbuilders.rate.core.presentation.utils

import android.content.Context
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.usecase.DefaultGroupNameProvider
import dev.arkbuilders.rate.core.presentation.CoreRString

class DefaultGroupNameProviderImpl(private val ctx: Context) : DefaultGroupNameProvider {
    override fun provide(groupFeatureType: GroupFeatureType): String {
        return ctx.getString(CoreRString.group_default_name)
    }
}
