package dev.arkbuilders.rate.feature.onboarding.di

import android.content.Context
import dev.arkbuilders.rate.core.di.CoreComponentProvider

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
