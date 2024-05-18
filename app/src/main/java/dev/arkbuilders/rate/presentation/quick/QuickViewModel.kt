package dev.arkbuilders.rate.presentation.quick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.assets.AssetsRepo
import dev.arkbuilders.rate.data.db.QuickRepo
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.QuickPair
import dev.arkbuilders.rate.data.preferences.Preferences
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

data class DisplayQuickPair(
    val pair: QuickPair,
    val to: List<Pair<CurrencyCode, Double>>
)

data class QuickScreenState(
    val groupToQuickPairs: List<Pair<String?, List<DisplayQuickPair>>> = emptyList()
)

sealed class QuickScreenEffect {

}

class QuickViewModel(
    val currencyRepo: GeneralCurrencyRepo,
    val assetsRepo: AssetsRepo,
    val quickRepo: QuickRepo,
    val prefs: Preferences
) : ViewModel(), ContainerHost<QuickScreenState, QuickScreenEffect> {
    override val container: Container<QuickScreenState, QuickScreenEffect> =
        container(QuickScreenState())

    init {
        quickRepo.allFlow().onEach { all ->
            val codeToRate = currencyRepo.getCodeToCurrencyRate()
            val displayList = all.map { pair ->
                val toDisplay = pair.to.map { code ->
                    val rate = codeToRate[pair.from]!!.rate / codeToRate[code]!!.rate
                    val amount = pair.amount * rate
                    code to amount
                }
                DisplayQuickPair(pair, toDisplay)
            }
            val byGroup = displayList.groupBy { it.pair.group }.toList()
            intent {
                reduce { state.copy(groupToQuickPairs = byGroup) }
            }
        }.launchIn(viewModelScope)
    }
}

class QuickViewModelFactory @AssistedInject constructor(
    private val assetsRepo: AssetsRepo,
    private val quickRepo: QuickRepo,
    private val currencyRepo: GeneralCurrencyRepo,
    private val prefs: Preferences,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuickViewModel(
            currencyRepo,
            assetsRepo,
            quickRepo,
            prefs
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(): QuickViewModelFactory
    }
}