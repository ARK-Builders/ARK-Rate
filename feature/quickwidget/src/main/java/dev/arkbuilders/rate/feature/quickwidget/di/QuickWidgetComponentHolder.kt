package dev.arkbuilders.rate.feature.quickwidget.di

import android.content.Context
import dev.arkbuilders.rate.feature.quick.di.QuickComponentHolder

object QuickWidgetComponentHolder {
    private var component: QuickWidgetComponent? = null

    fun provide(ctx: Context): QuickWidgetComponent {
        component ?: let {
            val app = ctx.applicationContext
            val quickComponent = QuickComponentHolder.provide(app)
            component = DaggerQuickWidgetComponent.builder().quickComponent(quickComponent).build()
        }
        return component!!
    }
}
