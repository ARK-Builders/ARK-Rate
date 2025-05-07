package dev.arkbuilders.rate.feature.onboarding.di

import android.content.Context
import dev.arkbuilders.rate.core.di.CoreComponentProvider
import dev.arkbuilders.rate.feature.quick.di.DaggerQuickComponent
import dev.arkbuilders.rate.feature.quick.di.QuickComponent
import dev.arkbuilders.rate.feature.quick.di.QuickModule

object OnboardingComponentHolder {
    private var component: OnboardingComponent? = null

    fun provide(ctx: Context): OnboardingComponent {
        component ?: let {
            val app = ctx.applicationContext
            val coreComponent = (app as CoreComponentProvider).provideCoreComponent()
            component =
                DaggerOnboardingComponent.builder().coreComponent(coreComponent).build()
        }
        return component!!
    }
}
