package dev.arkbuilders.rate.feature.pairalert.di

import android.content.Context
import dev.arkbuilders.rate.core.di.CoreComponentProvider

object PairAlertComponentHolder {
    private var component: PairAlertComponent? = null

    fun provide(ctx: Context): PairAlertComponent {
        component ?: let {
            val app = ctx.applicationContext
            val coreComponent = (app as CoreComponentProvider).provideCoreComponent()
            component = DaggerPairAlertComponent.builder().coreComponent(coreComponent)
                .pairAlertModule(PairAlertModule()).build()
        }
        return component!!
    }
}
