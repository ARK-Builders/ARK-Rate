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
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.assets.AssetsRepo
import dev.arkbuilders.rate.data.db.QuickBaseCurrencyRepo
import dev.arkbuilders.rate.data.db.QuickCurrencyRepo
import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.CurrencyName
import dev.arkbuilders.rate.data.model.QuickBaseCurrency
import dev.arkbuilders.rate.data.model.QuickCurrency
import dev.arkbuilders.rate.data.preferences.PreferenceKey
import dev.arkbuilders.rate.data.preferences.Preferences
import dev.arkbuilders.rate.data.preferences.QuickScreenShowAs
import dev.arkbuilders.rate.data.preferences.QuickScreenSortedBy
import dev.arkbuilders.rate.presentation.shared.SharedViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar

class CurrencyAttraction(val code: CurrencyCode,
        val name: CurrencyName,
        val attractionRatio: Double)

class QuickViewModel(val sharedViewModel: SharedViewModel,
        val currencyRepo: GeneralCurrencyRepo,
        val assetsRepo: AssetsRepo,
        val quickCurrencyRepo: QuickCurrencyRepo,
        val quickBaseCurrencyRepo: QuickBaseCurrencyRepo,
        val prefs: Preferences) : ViewModel() {
    val currencyAttractionList = mutableStateListOf<CurrencyAttraction>()
    val quickBaseCurrency = mutableStateListOf<QuickBaseCurrency>()
    var selectedCurrency: CurrencyAmount? by mutableStateOf(null)
    val navigateToSummary = MutableSharedFlow<CurrencyAmount>()
    var showAs by mutableStateOf(QuickScreenShowAs.TAG_CLOUD)
    var sortedBy by mutableStateOf(QuickScreenSortedBy.USED_COUNT)
    var sortDialogVisible by mutableStateOf(false)
    lateinit var quickCurrencies: List<QuickCurrency>

    init {
        viewModelScope.launch {
            showAs = QuickScreenShowAs.values()[prefs.get(PreferenceKey.QuickScreenShowAsKey)]

            sortedBy = QuickScreenSortedBy.values()[prefs.get(PreferenceKey.QuickScreenSortedByKey)]

            quickCurrencyRepo.allFlow().onEach { currencies ->
                quickCurrencies = currencies
                calculateAttraction()
            }.launchIn(viewModelScope)

            quickBaseCurrencyRepo.allFlow().onEach {
                quickBaseCurrency.clear()
                quickBaseCurrency.addAll(it)
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
        var usedCount = quickCurrencyRepo.getByCode(selectedCurrency!!.code)?.usedCount
            ?: 0
        quickCurrencyRepo.insert(QuickCurrency(selectedCurrency!!.code,
                                               ++usedCount,
                                               Calendar.getInstance().timeInMillis))
        navigateToSummary.emit(selectedCurrency!!)
    }

    fun onAmountChanged(oldInput: String, newInput: String): String {
        val containsDigitsAndDot = Regex("[0-9]*\\.?[0-9]*")
        if (!containsDigitsAndDot.matches(newInput)) return oldInput

        val containsDigit = Regex(".*[0-9].*")
        if (!containsDigit.matches(newInput)) {
            selectedCurrency = selectedCurrency!!.copy(amount = 0.0)
            return newInput
        }

        selectedCurrency = selectedCurrency!!.copy(amount = newInput.toDouble())

        val leadingZeros = "^0+(?=\\d)".toRegex()

        return newInput.replace(leadingZeros, "")
    }

    fun onRemoveBaseCurrency(code: CurrencyCode) = viewModelScope.launch {
        quickBaseCurrencyRepo.delete(code)
    }

    private suspend fun calculateAttraction() {
        var attraction = when (sortedBy) {
            QuickScreenSortedBy.USED_COUNT -> calcAttractionUsedCount()
            QuickScreenSortedBy.USED_TIME -> calcAttractionUsedTime()
        }

        // most attractive in center
        val attraction1 = attraction.filterIndexed { index, _ -> index % 2 == 0 }
        val attraction2Reversed = attraction.filterIndexed { index, _ -> index % 2 == 1 }.reversed()

        attraction = attraction1 + attraction2Reversed

        currencyAttractionList.clear()
        currencyAttractionList.addAll(attraction)
    }

    private suspend fun calcAttractionUsedCount(): List<CurrencyAttraction> {
        val max = quickCurrencies.maxOfOrNull { it.usedCount }?.toDouble()

        val attraction = quickCurrencies.map {
            CurrencyAttraction(it.code,
                               name = currencyRepo.currencyNameByCode(it.code),
                               attractionRatio = it.usedCount / max!!)
        }.sortedBy { it.attractionRatio }

        return attraction
    }

    private suspend fun calcAttractionUsedTime(): List<CurrencyAttraction> {
        if (quickCurrencies.isEmpty()) {
            return emptyList()
        }

        if (quickCurrencies.size == 1) {
            val quick = quickCurrencies.first()
            return listOf(CurrencyAttraction(quick.code,
                                             currencyRepo.currencyNameByCode(quick.code),
                                             attractionRatio = 1.0))
        }

        val min = quickCurrencies.minOfOrNull { it.usedTime }!!

        val usedTimesReducedByMin = quickCurrencies.map { it to it.usedTime - min }
        val max = usedTimesReducedByMin.maxOfOrNull { (_, time) -> time }!!

        val attraction = usedTimesReducedByMin.map { (quick, time) ->
            CurrencyAttraction(quick.code,
                               name = currencyRepo.currencyNameByCode(quick.code),
                               attractionRatio = time.toDouble() / max)
        }.sortedBy { it.attractionRatio }

        return attraction
    }

    fun onSortedByPick(sortedBy: QuickScreenSortedBy) = viewModelScope.launch {
        prefs.set(PreferenceKey.QuickScreenSortedByKey, sortedBy.ordinal)
        this@QuickViewModel.sortedBy = sortedBy
        calculateAttraction()
    }
}

class QuickViewModelFactory @AssistedInject constructor(
        @Assisted private val sharedViewModel: SharedViewModel,
        private val assetsRepo: AssetsRepo,
        private val quickCurrencyRepo: QuickCurrencyRepo,
        private val currencyRepo: GeneralCurrencyRepo,
        private val quickBaseCurrencyRepo: QuickBaseCurrencyRepo,
        private val prefs: Preferences,
                                                       ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuickViewModel(sharedViewModel,
                              currencyRepo,
                              assetsRepo,
                              quickCurrencyRepo,
                              quickBaseCurrencyRepo,
                              prefs) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
                @Assisted sharedViewModel: SharedViewModel,
                  ): QuickViewModelFactory
    }
}