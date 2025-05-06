package dev.arkbuilders.rate.feature.onboarding.mock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.arkbuilders.rate.feature.onboarding.di.OnboardingScope
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

enum class OnboardingQuickStep {
    CALCULATE,
    PORTFOLIO,
    PAIRALERTS
}

data class OnboardingQuickState(
    val stepIndex: Int = 0,
)

class OnboardingQuickViewModel(

) : ViewModel(), ContainerHost<OnboardingQuickState, Unit> {

    override val container: Container<OnboardingQuickState, Unit> =
        container(OnboardingQuickState())

    init {

    }

    fun onNext() = intent {
        val nextIndex = state.stepIndex + 1

        if (nextIndex == OnboardingQuickStep.entries.size) {
            return@intent
        }

        reduce {
            state.copy(stepIndex = nextIndex)
        }
    }
}

@OnboardingScope
class OnboardingQuickViewModelFactory @Inject constructor(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnboardingQuickViewModel() as T
    }
}
