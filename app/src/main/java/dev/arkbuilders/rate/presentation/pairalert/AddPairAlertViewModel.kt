package dev.arkbuilders.rate.presentation.pairalert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.db.PairAlertRepo
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.PairAlert
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import javax.inject.Singleton

data class AddPairAlertScreenState(
    val targetCode: CurrencyCode,
    val baseCode: CurrencyCode,
    val priceOrPercent: Either<Double, Double>,
    val currentPrice: Double,
    val oneTime: Boolean,
    val aboveNotBelow: Boolean,
    val group: String? = null,
    val availableGroups: List<String> = emptyList()
)


sealed class AddPairAlertScreenEffect {
    data object NavigateBack : AddPairAlertScreenEffect()
}

class AddPairAlertViewModel(
    private val currencyRepo: GeneralCurrencyRepo,
    private val pairAlertRepo: PairAlertRepo
) : ViewModel(), ContainerHost<AddPairAlertScreenState, AddPairAlertScreenEffect> {
    override val container: Container<AddPairAlertScreenState, AddPairAlertScreenEffect> =
        container(
            AddPairAlertScreenState(
                targetCode = "BTC",
                baseCode = "USD",
                priceOrPercent = Either.Left(0.0),
                currentPrice = 0.0,
                oneTime = true,
                aboveNotBelow = true
            )
        )

    init {
        AppSharedFlow.AddPairAlertTarget.flow.onEach {
            initOnCodeChange(newTarget = it)
        }.launchIn(viewModelScope)

        AppSharedFlow.AddPairAlertBase.flow.onEach {
            initOnCodeChange(newBase = it)
        }.launchIn(viewModelScope)

        initOnCodeChange()

        intent {
            val groups =
                pairAlertRepo.getAll().mapNotNull { it.group }.distinct()
            reduce {
                state.copy(availableGroups = groups)
            }
        }
    }

    private fun initOnCodeChange(
        newTarget: CurrencyCode? = null,
        newBase: CurrencyCode? = null
    ) {
        intent {
            val target = newTarget ?: state.targetCode
            val base = newBase ?: state.baseCode
            val baseRate =
                currencyRepo.getCurrencyRate().find { it.code == base }
            val targetRate =
                currencyRepo.getCurrencyRate().find { it.code == target }

            reduce {
                state.copy(
                    currentPrice = CurrUtils.roundOff(targetRate!!.rate / baseRate!!.rate),
                    priceOrPercent = Either.Left(CurrUtils.roundOff(targetRate.rate * 1.1)),
                    targetCode = target,
                    baseCode = base
                )
            }
        }
    }

    fun onPriceOrPercentChanged(input: String) = blockingIntent {
        state.priceOrPercent.fold(
            ifLeft = { handlePriceChanged(input) },
            ifRight = { handlePercentChanged(input) }
        )
    }

    fun onSaveClick() = intent {
        val (price, percent) = state.priceOrPercent.fold(
            ifLeft = { price -> price to (state.currentPrice / price) - 1.0 },
            ifRight = { percent -> state.currentPrice * (1.0 + percent) to percent },
        )

        val pairAlert = PairAlert(
            id = 0,
            targetCode = state.targetCode,
            baseCode = state.baseCode,
            targetPrice = price,
            startPrice = state.currentPrice,
            alertPercent = percent,
            oneTimeNotRecurrent = true,
            priceNotPercent = true,
            triggered = false,
            group = state.group
        )
        pairAlertRepo.insert(pairAlert)
        postSideEffect(AddPairAlertScreenEffect.NavigateBack)
    }

    fun onGroupSelect(group: String?) = intent {
        reduce { state.copy(group = group) }
    }

    private fun handlePriceChanged(input: String) = blockingIntent {
        val newPrice = state.priceOrPercent
            .mapLeft { left ->
                CurrUtils.validateInput(
                    left.toString(),
                    input
                ).toDouble()
            }
        reduce {
            state.copy(
                priceOrPercent = newPrice,
                aboveNotBelow = newPrice.leftOrNull()!! > state.currentPrice
            )
        }
    }

    private fun handlePercentChanged(input: String) = blockingIntent {
        val newPercent = state.priceOrPercent
            .map { right ->
                right
            }
        reduce {
            state.copy(
                priceOrPercent = newPercent,
                aboveNotBelow = newPercent.getOrNull()!! > 0
            )
        }
    }
}
@Singleton
class AddPairAlertViewModelFactory @Inject constructor(
    private val currencyRepo: GeneralCurrencyRepo,
    private val pairAlertRepo: PairAlertRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddPairAlertViewModel(
            currencyRepo,
            pairAlertRepo
        ) as T
    }
}