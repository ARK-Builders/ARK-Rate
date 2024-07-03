package dev.arkbuilders.rate.presentation.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.data.CurrUtils
import dev.arkbuilders.rate.domain.model.Asset
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.data.toDoubleSafe
import dev.arkbuilders.rate.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.domain.repo.PreferenceKey
import dev.arkbuilders.rate.domain.repo.Prefs
import dev.arkbuilders.rate.presentation.shared.AppSharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

data class EditAssetScreenState(
    val asset: Asset = Asset.EMPTY,
    val name: CurrencyName = CurrencyName.EMPTY,
    val value: String = "",
    val initialized: Boolean = false
)

sealed class EditAssetScreenEffect

private val PERSIST_AMOUNT_DEBOUNCE = 300L

class EditAssetViewModel(
    private val assetId: Long,
    private val currencyRepo: CurrencyRepo,
    private val assetsRepo: PortfolioRepo,
    private val prefs: Prefs
): ViewModel(), ContainerHost<EditAssetScreenState, EditAssetScreenEffect> {

    override val container: Container<EditAssetScreenState, EditAssetScreenEffect> =
        container(EditAssetScreenState())

    private val inputFlow = MutableSharedFlow<String>()

    init {
        intent {
            val asset = assetsRepo.getById(assetId)
            val name = currencyRepo.currencyNameByCode(asset!!.code)

            inputFlow.debounce(PERSIST_AMOUNT_DEBOUNCE).onEach {
                assetsRepo.setAsset(asset.copy(value = it.toDoubleSafe()))
            }.launchIn(viewModelScope)

            reduce {
                state.copy(asset, name, asset.value.toString(), initialized = true)
            }
        }

        AppSharedFlow.PickBaseCurrency.flow.onEach {
            prefs.set(PreferenceKey.BaseCurrencyCode, it)
        }.launchIn(viewModelScope)
    }

    fun onValueChange(input: String) = blockingIntent {
        val validated = CurrUtils.validateInput(state.value, input)
        inputFlow.emit(validated)
        reduce {
            state.copy(value = validated)
        }
    }
}

class EditAssetViewModelFactory @AssistedInject constructor(
    @Assisted private val amountId: Long,
    private val currencyRepo: CurrencyRepo,
    private val assetsRepo: PortfolioRepo,
    private val prefs: Prefs
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditAssetViewModel(amountId, currencyRepo, assetsRepo, prefs) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(amountId: Long): EditAssetViewModelFactory
    }
}