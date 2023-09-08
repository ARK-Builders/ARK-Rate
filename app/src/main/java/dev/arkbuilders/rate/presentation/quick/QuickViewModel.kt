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
import dev.arkbuilders.rate.data.preferences.PreferenceKey
import dev.arkbuilders.rate.data.preferences.Preferences
import dev.arkbuilders.rate.presentation.shared.SharedViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CurrencyAttraction(
    val code: CurrencyCode,
    val attractionRatio: Double
)

class QuickViewModel(
    val sharedViewModel: SharedViewModel,
    val assetsRepo: AssetsRepo,
    val quickCurrencyRepo: QuickCurrencyRepo,
    val prefs: Preferences
) : ViewModel() {
    val currencyAttractionList = mutableStateListOf<CurrencyAttraction>()
    var selectedCurrency: CurrencyAmount? by mutableStateOf(null)
    val navigateToSummary = MutableSharedFlow<CurrencyAmount>()
    var showAsTagCloud by mutableStateOf(true)

    init {
        viewModelScope.launch {
            showAsTagCloud = prefs.get(PreferenceKey.QuickScreenTagCloud)

            quickCurrencyRepo.allFlow().onEach { currencies ->
                currencyAttractionList.clear()
                currencyAttractionList.addAll(calculateAttraction(currencies))
            }.launchIn(viewModelScope)

            sharedViewModel.quickCurrencyPickedFlow.onEach { code ->
                selectedCurrency = CurrencyAmount(code = code, amount = 0.0)
            }.launchIn(viewModelScope)
        }
    }

    fun onCurrencySelected(code: CurrencyCode) {
        selectedCurrency = CurrencyAmount(code = code, amount = 0.0)
    }

    fun onExchange() = viewModelScope.launch {
        var usedCount =
            quickCurrencyRepo.getByCode(selectedCurrency!!.code)?.usedCount
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

    private fun calculateAttraction(
        quick: List<QuickCurrency>
    ): List<CurrencyAttraction> {
        val max = quick.maxOfOrNull { it.usedCount }?.toDouble()

        var attraction = quick.map {
            CurrencyAttraction(it.code, attractionRatio = it.usedCount / max!! )
        }.sortedBy { it.attractionRatio }

        // most attractive in center
        val attraction1 = attraction.filterIndexed { index, _ -> index % 2 == 0 }
        val attraction2Reversed = attraction
            .filterIndexed { index, _ -> index % 2 == 1 }
            .reversed()

        attraction = attraction1 + attraction2Reversed

        return attraction
    }
}

class QuickViewModelFactory @AssistedInject constructor(
    @Assisted private val sharedViewModel: SharedViewModel,
    private val assetsRepo: AssetsRepo,
    private val quickCurrencyRepo: QuickCurrencyRepo,
    private val prefs: Preferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuickViewModel(sharedViewModel, assetsRepo, quickCurrencyRepo, prefs) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted sharedViewModel: SharedViewModel,
        ): QuickViewModelFactory
    }
}