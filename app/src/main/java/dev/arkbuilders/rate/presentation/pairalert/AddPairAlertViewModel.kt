package dev.arkbuilders.rate.presentation.pairalert

import android.icu.util.Currency
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.PairAlert
import dev.arkbuilders.rate.data.toDoubleSafe
import dev.arkbuilders.rate.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PairAlertRepo
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
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

data class AddPairAlertScreenState(
    val targetCode: CurrencyCode = "BTC",
    val baseCode: CurrencyCode = "USD",
    val priceOrPercent: Either<String, String> = Either.Left(""),
    val currentPrice: Double = 0.0,
    val aboveNotBelow: Boolean = true,
    val group: String? = null,
    val oneTimeNotRecurrent: Boolean = true,
    val availableGroups: List<String> = emptyList(),
    val finishEnabled: Boolean = true,
    val editExisting: Boolean = false
)


sealed class AddPairAlertScreenEffect {
    data object NavigateBack : AddPairAlertScreenEffect()
    class NotifyPairAdded(val pair: PairAlert) : AddPairAlertScreenEffect()
}

class AddPairAlertViewModel(
    private val pairAlertId: Long?,
    private val currencyRepo: CurrencyRepo,
    private val pairAlertRepo: PairAlertRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), ContainerHost<AddPairAlertScreenState, AddPairAlertScreenEffect> {
    override val container: Container<AddPairAlertScreenState, AddPairAlertScreenEffect> =
        container(AddPairAlertScreenState())

    init {
        analyticsManager.trackScreen("AddPairAlertScreen")

        AppSharedFlow.AddPairAlertTarget.flow.onEach {
            initOnCodeChange(newTarget = it)
        }.launchIn(viewModelScope)

        AppSharedFlow.AddPairAlertBase.flow.onEach {
            initOnCodeChange(newBase = it)
        }.launchIn(viewModelScope)

        pairAlertId?.let {
            setupFromExisting()
            intent {
                checkAboveNotBelow()
            }
        } ?: initOnCodeChange()


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
            val (_, currentPrice) = convertUseCase(
                fromCode = target,
                fromValue = 1.0,
                toCode = base
            )
            val newState = state.copy(
                currentPrice = currentPrice,
                targetCode = target,
                baseCode = base
            )
            val priceOrPercent = calcNewPriceOrPercent(newState)

            reduce {
                newState.copy(priceOrPercent = priceOrPercent)
            }
            checkFinishEnabled()
        }
    }

    fun onPriceOrPercentInputChanged(input: String) = blockingIntent {
        state.priceOrPercent.fold(
            ifLeft = { handlePriceChanged(input) },
            ifRight = { handlePercentChanged(input) }
        )
        checkFinishEnabled()
    }

    fun onPriceOrPercentChanged(priceNotPercent: Boolean) = intent {
        reduce {
            val newState = state.copy(
                priceOrPercent = if (priceNotPercent)
                    Either.Left("")
                else
                    Either.Right("")
            )
            val newPriceOrPercent = calcNewPriceOrPercent(newState)
            newState.copy(priceOrPercent = newPriceOrPercent)
        }
        checkAboveNotBelow()
    }

    fun onIncreaseToggle() = intent {
        if (state.oneTimeNotRecurrent && state.priceOrPercent.isLeft())
            return@intent

        val newPriceOrPercent = state.priceOrPercent.mapLeft {
            if (it.startsWith("-"))
                it.replace("-", "")
            else
                "-$it"
        }.map {
            if (it.startsWith("-"))
                it.replace("-", "")
            else
                "-$it"
        }
        reduce { state.copy(priceOrPercent = newPriceOrPercent) }
        checkAboveNotBelow()
    }

    fun onOneTimeChanged(oneTimeNotRecurrent: Boolean) = intent {
        reduce {
            val newState = state.copy(oneTimeNotRecurrent = oneTimeNotRecurrent)
            val newPriceOrPercent = calcNewPriceOrPercent(newState)
            newState.copy(priceOrPercent = newPriceOrPercent)
        }
        checkAboveNotBelow()
    }

    fun onSaveClick() = intent {
        val targetPrice = state.priceOrPercent.fold(
            ifLeft = { price ->
                if (state.oneTimeNotRecurrent)
                    price.toDoubleSafe()
                else
                    (state.currentPrice + price.toDoubleSafe())
            },
            ifRight = { percent -> (state.currentPrice * (1.0 + percent.toDoubleSafe() / 100)) },
        )

        val percent = state.priceOrPercent.getOrNull()?.toDoubleSafe()

        val id = if (state.editExisting) pairAlertId!! else 0

        val pairAlert = PairAlert(
            id = id,
            targetCode = state.targetCode,
            baseCode = state.baseCode,
            targetPrice = targetPrice,
            startPrice = state.currentPrice,
            percent = percent,
            oneTimeNotRecurrent = state.oneTimeNotRecurrent,
            enabled = true,
            lastDateTriggered = null,
            group = state.group
        )
        pairAlertRepo.insert(pairAlert)
        codeUseStatRepo.codesUsed(pairAlert.baseCode, pairAlert.targetCode)
        postSideEffect(AddPairAlertScreenEffect.NotifyPairAdded(pairAlert))
        postSideEffect(AddPairAlertScreenEffect.NavigateBack)
    }

    fun onGroupSelect(group: String?) = intent {
        reduce { state.copy(group = group) }
    }

    private fun handlePriceChanged(input: String) = blockingIntent {
        val newPrice = state.priceOrPercent
            .mapLeft { oldPrice ->
                if (state.oneTimeNotRecurrent) {
                    CurrUtils.validateInput(
                        oldPrice,
                        input
                    )
                } else {
                    CurrUtils.validateInputWithMinusChar(
                        oldPrice,
                        input
                    )
                }
            }
        reduce {
            state.copy(
                priceOrPercent = newPrice,
            )
        }
        checkAboveNotBelow()
    }

    private fun handlePercentChanged(input: String) = blockingIntent {
        val newPercent = state.priceOrPercent
            .map { oldPercent ->
                CurrUtils.validateInputWithMinusChar(
                    oldPercent,
                    input
                )
            }
        reduce {
            state.copy(
                priceOrPercent = newPercent
            )
        }
        checkAboveNotBelow()
    }

    private fun checkAboveNotBelow() = intent {
        val aboveNotBelow = state.priceOrPercent.fold(
            ifLeft = { price ->
                if (state.oneTimeNotRecurrent) {
                    price.toDoubleSafe() > state.currentPrice
                } else {
                    price.toDoubleSafe() > 0
                }
            },
            ifRight = { percent ->
                percent.toDoubleSafe() > 0
            }
        )
        reduce {
            state.copy(aboveNotBelow = aboveNotBelow)
        }
    }

    private fun calcNewPriceOrPercent(
        state: AddPairAlertScreenState
    ): Either<String, String> {
        return state.priceOrPercent
            .mapLeft { price ->
                if (state.oneTimeNotRecurrent) {
                    CurrUtils.roundOff(state.currentPrice * 1.1)
                } else {
                    CurrUtils.roundOff(state.currentPrice / 10)
                }
            }
            .map { percent ->
                "5"
            }
    }

    private fun setupFromExisting() = intent {
        val pair = pairAlertRepo.getById(pairAlertId!!)!!

        val priceOrPercent = pair.percent?.let { percent ->
            Either.Right(CurrUtils.roundOff(percent))
        } ?: let {
            Either.Left(
                if (pair.oneTimeNotRecurrent)
                    CurrUtils.roundOff(pair.targetPrice)
                else
                    CurrUtils.roundOff(pair.byPriceStep())
            )
        }
        val (_, currentPrice) = convertUseCase(
            fromCode = pair.targetCode,
            fromValue = 1.0,
            toCode = pair.baseCode
        )
        val state = AddPairAlertScreenState(
            targetCode = pair.targetCode,
            baseCode = pair.baseCode,
            priceOrPercent = priceOrPercent,
            currentPrice = currentPrice,
            aboveNotBelow = true,
            group = pair.group,
            oneTimeNotRecurrent = pair.oneTimeNotRecurrent,
            editExisting = true
        )
        reduce { state }
    }

    private fun checkFinishEnabled() = intent {
        var enabled = true

        val priceOrPercentNotSuit = state.priceOrPercent.fold(
            ifLeft = { it.toDoubleSafe() == 0.0 },
            ifRight = { it.toDoubleSafe() == 0.0 }
        )
        if (priceOrPercentNotSuit)
            enabled = false

        if (state.targetCode == state.baseCode)
            enabled = false

        reduce { state.copy(finishEnabled = enabled) }
    }
}

class AddPairAlertViewModelFactory @AssistedInject constructor(
    @Assisted private val pairAlertId: Long?,
    private val currencyRepo: CurrencyRepo,
    private val pairAlertRepo: PairAlertRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddPairAlertViewModel(
            pairAlertId,
            currencyRepo,
            pairAlertRepo,
            codeUseStatRepo,
            convertUseCase,
            analyticsManager,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            pairAlertId: Long?,
        ): AddPairAlertViewModelFactory
    }
}