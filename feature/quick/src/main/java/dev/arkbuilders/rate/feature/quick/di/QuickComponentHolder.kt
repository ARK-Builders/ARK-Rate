package dev.arkbuilders.rate.feature.quick.di

import android.content.Context
import dev.arkbuilders.rate.core.di.CoreComponentProvider

object QuickComponentHolder {
    private var component: QuickComponent? = null

    fun provide(ctx: Context): QuickComponent {
        component ?: let {
            val app = ctx.applicationContext
            val coreComponent = (app as CoreComponentProvider).provideCoreComponent()
            component =
                DaggerQuickComponent.builder().coreComponent(coreComponent)
                    .quickModule(QuickModule()).build()
        }
        return component!!
    }
}
