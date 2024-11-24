package dev.arkbuilders.rate.feature.search.di

import android.content.Context
import dev.arkbuilders.rate.core.di.CoreComponentProvider

object SearchComponentHolder {
    private var component: SearchComponent? = null

    fun provide(ctx: Context): SearchComponent {
        component ?: let {
            val app = ctx.applicationContext
            val coreComponent = (app as CoreComponentProvider).provideCoreComponent()
            component = DaggerSearchComponent.builder().coreComponent(coreComponent).build()
        }
        return component!!
    }
}
