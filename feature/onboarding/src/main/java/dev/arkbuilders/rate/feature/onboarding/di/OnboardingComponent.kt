package dev.arkbuilders.rate.feature.onboarding.di

import dagger.Component
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.feature.onboarding.quick.OnboardingQuickViewModelFactory
import dev.arkbuilders.rate.feature.onboarding.quickpair.OnboardingQuickPairViewModelFactory

@OnboardingScope
@Component(dependencies = [CoreComponent::class], modules = [])
interface OnboardingComponent {
    fun onboardingQuickViewModelFactory(): OnboardingQuickViewModelFactory

    fun onboardingQuickPairViewModelFactory(): OnboardingQuickPairViewModelFactory.Factory
}
