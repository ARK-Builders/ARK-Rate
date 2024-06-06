package dev.arkbuilders.rate.presentation.quick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.domain.model.QuickPair
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.domain.repo.Prefs
import dev.arkbuilders.rate.domain.repo.QuickRepo
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

data class QuickDisplayPair(
    val pair: QuickPair,
    val to: List<Amount>
)

data class QuickScreenPage(
    val group: String?,
    val pairs: List<QuickDisplayPair>
)

data class QuickScreenState(
    val pages: List<QuickScreenPage> = emptyList(),
    val initialized: Boolean = false
)

sealed class QuickScreenEffect {

}

class QuickViewModel(
    private val currencyRepo: CurrencyRepo,
    private val assetsRepo: PortfolioRepo,
    private val quickRepo: QuickRepo,
    private val prefs: Prefs,
    private val convertUseCase: ConvertWithRateUseCase
) : ViewModel(), ContainerHost<QuickScreenState, QuickScreenEffect> {
    override val container: Container<QuickScreenState, QuickScreenEffect> =
        container(QuickScreenState())

    init {
        intent {
            if (isRatesAvailable().not())
                return@intent

            quickRepo.allFlow().onEach { all ->
                val codeToRate = currencyRepo.getCodeToCurrencyRate().getOrNull()!!
                val displayList = all.map { pair ->
                    val toDisplay = pair.to.map { code ->
                        val (amount, _) = convertUseCase(
                            Amount(pair.from, pair.amount),
                            toCode = code,
                            codeToRate
                        )
                        amount
                    }
                    QuickDisplayPair(pair, toDisplay)
                }
                val pages = displayList.groupBy { it.pair.group }
                    .map { (group, pairs) -> QuickScreenPage(group, pairs) }
                intent {
                    reduce {
                        state.copy(
                            pages = pages,
                            initialized = true
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun onDelete(pair: QuickPair) = intent {
        quickRepo.delete(pair.id)
    }

    private suspend fun isRatesAvailable() = currencyRepo.getCurrencyRate().isRight()
}

class QuickViewModelFactory @AssistedInject constructor(
    private val assetsRepo: PortfolioRepo,
    private val quickRepo: QuickRepo,
    private val currencyRepo: CurrencyRepo,
    private val prefs: Prefs,
    private val convertUseCase: ConvertWithRateUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuickViewModel(
            currencyRepo,
            assetsRepo,
            quickRepo,
            prefs,
            convertUseCase
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(): QuickViewModelFactory
    }
}