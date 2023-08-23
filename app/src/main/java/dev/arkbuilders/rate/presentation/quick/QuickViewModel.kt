package dev.arkbuilders.rate.presentation.quick

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.data.CurrencyAmount
import dev.arkbuilders.rate.data.CurrencyCode
import dev.arkbuilders.rate.data.QuickCurrency
import dev.arkbuilders.rate.data.assets.AssetsRepo
import dev.arkbuilders.rate.data.db.QuickCurrencyRepo
import dev.arkbuilders.rate.presentation.addcurrency.AddCurrencyEvent
import dev.arkbuilders.rate.presentation.shared.SharedViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class QuickViewModel(
    val sharedViewModel: SharedViewModel,
    val assetsRepo: AssetsRepo,
    val quickCurrencyRepo: QuickCurrencyRepo,
) : ViewModel() {
    val quickCurrencyList = mutableStateListOf<CurrencyCode>()
    var selectedCurrency: CurrencyAmount? by mutableStateOf(null)
    val navigateToSummary = MutableSharedFlow<CurrencyAmount>()

    init {
        quickCurrencyRepo.allFlow().onEach { currencies ->
            quickCurrencyList.clear()
            quickCurrencyList.addAll(currencies.map { it.code })
        }.launchIn(viewModelScope)

        sharedViewModel.quickCurrencyPickedFlow.onEach { code ->
            selectedCurrency = CurrencyAmount(code = code, amount = 0.0)
        }.launchIn(viewModelScope)
    }

    fun onCurrencySelected(code: CurrencyCode) {
        selectedCurrency = CurrencyAmount(code = code, amount = 0.0)
    }

    fun onExchange() = viewModelScope.launch {
        var usedCount = quickCurrencyRepo.getByCode(selectedCurrency!!.code)?.usedCount
            ?: 0
        quickCurrencyRepo.insert(QuickCurrency(selectedCurrency!!.code, ++usedCount))
        navigateToSummary.emit(selectedCurrency!!)
    }

    fun onAmountChanged(
        oldInput: String,
        newInput: String
    ): String {
        val containsDigitsAndDot = Regex("[0-9]*\\.?[0-9]*")
        if (!containsDigitsAndDot.matches(newInput))
            return oldInput

        val containsDigit = Regex(".*[0-9].*")
        if (!containsDigit.matches(newInput)) {
            selectedCurrency = selectedCurrency!!.copy(amount = 0.0)
            return newInput
        }

        selectedCurrency = selectedCurrency!!.copy(amount = newInput.toDouble())

        val leadingZeros = "^0+(?=\\d)".toRegex()

        return newInput.replace(leadingZeros, "")
    }
}

class QuickViewModelFactory @AssistedInject constructor(
    @Assisted private val sharedViewModel: SharedViewModel,
    private val assetsRepo: AssetsRepo,
    private val quickCurrencyRepo: QuickCurrencyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuickViewModel(sharedViewModel, assetsRepo, quickCurrencyRepo) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted sharedViewModel: SharedViewModel,
        ): QuickViewModelFactory
    }
}