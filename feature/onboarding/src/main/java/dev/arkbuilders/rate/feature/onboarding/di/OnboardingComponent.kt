package dev.arkbuilders.rate.feature.onboarding.di

import dagger.Component
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.feature.onboarding.quick.OnboardingQuickViewModelFactory

@OnboardingScope
@Component(dependencies = [CoreComponent::class], modules = [])
interface OnboardingComponent {
    fun onboardingQuickViewModelFactory(): OnboardingQuickViewModelFactory
}
