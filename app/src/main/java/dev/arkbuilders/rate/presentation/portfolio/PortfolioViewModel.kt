package dev.arkbuilders.rate.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.domain.model.Amount
import dev.arkbuilders.rate.domain.model.Asset
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.domain.repo.PreferenceKey
import dev.arkbuilders.rate.domain.repo.Prefs
import dev.arkbuilders.rate.domain.usecase.ConvertWithRateUseCase
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import javax.inject.Singleton

data class PortfolioScreenState(
    val baseCode: CurrencyCode = "USD",
    val groupToPortfolioAmount: Map<String?, List<PortfolioDisplayAsset>> = emptyMap(),
    val initialized: Boolean = false
)

class PortfolioDisplayAsset(
    val asset: Asset,
    val baseAmount: Amount,
    val ratioToBase: Double
)

sealed class PortfolioScreenEffect

class PortfolioViewModel(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val prefs: Prefs,
    private val convertUseCase: ConvertWithRateUseCase
) : ViewModel(), ContainerHost<PortfolioScreenState, PortfolioScreenEffect> {

    override val container: Container<PortfolioScreenState, PortfolioScreenEffect> =
        container(PortfolioScreenState())

    init {
        intent {
            if (isRatesAvailable().not())
                return@intent

            init()

            prefs.flow(PreferenceKey.BaseCurrencyCode).drop(1).onEach {
                init()
            }.launchIn(viewModelScope)

            assetsRepo.allAssetsFlow().drop(1).onEach {
                init()
            }.launchIn(viewModelScope)
        }
    }

    fun onAssetRemove(amount: Asset) = intent {
        assetsRepo.removeAsset(amount.id)
    }

    private fun init() = intent {
        val baseCode = prefs.get(PreferenceKey.BaseCurrencyCode)
        val list = assetsRepo.allAssets()
        val groups = list.groupBy(keySelector = { it.group })
        val groupToPortfolioAmount = groups.mapValues {
            assetToPortfolioDisplayAmount(
                baseCode,
                it.value
            )
        }
        reduce {
            state.copy(baseCode, groupToPortfolioAmount, initialized = true)
        }
    }

    private suspend fun assetToPortfolioDisplayAmount(
        baseCode: CurrencyCode,
        list: List<Asset>
    ): List<PortfolioDisplayAsset> {
        val rates = currencyRepo.getCodeToCurrencyRate().getOrNull()!!
        return list.map { asset ->
            val (baseAmount, rate) = convertUseCase(
                asset.code,
                asset.value,
                toCode = baseCode,
                rates
            )
            PortfolioDisplayAsset(asset, baseAmount, rate)
        }
    }

    private suspend fun isRatesAvailable() = currencyRepo.getCurrencyRate().isRight()
}

@Singleton
class PortfolioViewModelFactory @Inject constructor(
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val prefs: Prefs,
    private val convertUseCase: ConvertWithRateUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PortfolioViewModel(assetsRepo, currencyRepo, prefs, convertUseCase) as T
    }
}