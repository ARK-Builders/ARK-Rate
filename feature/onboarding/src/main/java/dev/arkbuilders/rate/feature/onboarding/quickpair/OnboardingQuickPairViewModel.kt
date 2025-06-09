package dev.arkbuilders.rate.feature.onboarding.quickpair

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.model.CurrencyName
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.core.domain.usecase.GetTopResultUseCase
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

enum class OnboardingQuickPairStep {
    PairSwipeToRight,
    PairSwipeToLeft,
    PinnedSwipeToRight,
    PairMenu,
}

data class OnboardingQuickPairState(
    val pair: QuickPair = QuickPair.empty(),
    val stepIndex: Int = 0,
    val currencies: List<CurrencyName> = emptyList(),
    val initialized: Boolean = false,
)

sealed class OnboardingQuickPairEffect {
    data object NavBack : OnboardingQuickPairEffect()

    data object Finish : OnboardingQuickPairEffect()
}

class OnboardingQuickPairViewModel(
    private val quickRepo: QuickRepo,
    private val prefs: Prefs,
    private val getTopResultUseCase: GetTopResultUseCase,
) : ViewModel(), ContainerHost<OnboardingQuickPairState, OnboardingQuickPairEffect> {
    override val container: Container<OnboardingQuickPairState, OnboardingQuickPairEffect> =
        container(OnboardingQuickPairState())

    init {
        intent {
            val pair = quickRepo.getAll().first()
            val currencies = getTopResultUseCase.invoke()
            reduce {
                state.copy(
                    pair = pair,
                    currencies = currencies,
                    initialized = true,
                )
            }
        }
    }

    fun onNext() =
        intent {
            val nextIndex = state.stepIndex + 1

            if (nextIndex == OnboardingQuickPairStep.entries.lastIndex + 1) {
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
                postSideEffect(OnboardingQuickPairEffect.NavBack)
                return@intent
            }

            reduce {
                state.copy(stepIndex = prevIndex)
            }
        }

    fun onSkip() {
        finish()
    }

    private fun finish() =
        intent {
            prefs.set(PreferenceKey.IsOnboardingQuickPairCompleted, true)
            postSideEffect(OnboardingQuickPairEffect.Finish)
        }
}

class OnboardingQuickPairViewModelFactory @AssistedInject constructor(
    @Assisted private val quickRepo: QuickRepo,
    private val prefs: Prefs,
    private val getTopResultUseCase: GetTopResultUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnboardingQuickPairViewModel(
            quickRepo,
            prefs,
            getTopResultUseCase,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted quickRepo: QuickRepo,
        ): OnboardingQuickPairViewModelFactory
    }
}
