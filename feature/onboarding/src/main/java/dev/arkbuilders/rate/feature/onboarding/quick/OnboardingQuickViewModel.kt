package dev.arkbuilders.rate.feature.onboarding.quick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.feature.onboarding.di.OnboardingScope
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

enum class OnboardingQuickStep {
    Calculate,
    Portfolio,
//    PairAlerts,
}

data class OnboardingQuickState(
    val stepIndex: Int = 0,
)

sealed class OnboardingQuickEffect {
    data object NavBack : OnboardingQuickEffect()

    data object Finish : OnboardingQuickEffect()
}

class OnboardingQuickViewModel(
    private val prefs: Prefs,
) : ViewModel(), ContainerHost<OnboardingQuickState, OnboardingQuickEffect> {
    override val container: Container<OnboardingQuickState, OnboardingQuickEffect> =
        container(OnboardingQuickState())

    fun onNext() =
        intent {
            val nextIndex = state.stepIndex + 1

            if (nextIndex == OnboardingQuickStep.entries.lastIndex + 1) {
                finish()
                return@intent
            }

            reduce {
                state.copy(stepIndex = nextIndex)
            }
        }

    fun onBack() =
        intent {
            val prevIndex = state.stepIndex - 1
            if (prevIndex < 0) {
                postSideEffect(OnboardingQuickEffect.NavBack)
                return@intent
            }

            reduce {
                state.copy(stepIndex = prevIndex)
            }
        }

    private fun finish() =
        intent {
            prefs.set(PreferenceKey.IsOnboardingCompleted, true)
            postSideEffect(OnboardingQuickEffect.Finish)
        }
}

@OnboardingScope
class OnboardingQuickViewModelFactory @Inject constructor(
    private val prefs: Prefs,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnboardingQuickViewModel(
            prefs,
        ) as T
    }
}
