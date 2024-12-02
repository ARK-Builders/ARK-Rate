package dev.arkbuilders.rate.feature.portfolio.di

import android.content.Context
import dev.arkbuilders.rate.core.di.CoreComponentProvider

object PortfolioComponentHolder {
    private var component: PortfolioComponent? = null

    fun provide(ctx: Context): PortfolioComponent {
        component ?: let {
            val app = ctx.applicationContext
            val coreComponent = (app as CoreComponentProvider).provideCoreComponent()
            component =
                DaggerPortfolioComponent.builder().coreComponent(coreComponent)
                    .portfolioModule(PortfolioModule()).build()
        }
        return component!!
    }
}
