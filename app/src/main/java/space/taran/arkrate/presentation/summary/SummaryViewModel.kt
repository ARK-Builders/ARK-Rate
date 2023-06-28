package space.taran.arkrate.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import space.taran.arkrate.data.GeneralCurrencyRepo
import space.taran.arkrate.data.assets.AssetsRepo
import javax.inject.Inject
import javax.inject.Singleton

class SummaryViewModel(
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo
) : ViewModel() {
    var total = MutableStateFlow<Map<String, Double>?>(null)
    var exchange = MutableStateFlow<Map<String, Double>?>(null)

    init {
        viewModelScope.launch {
            calculateTotal()
            calculateExchange()
        }
        assetsRepo.allCurrencyAmountFlow().onEach {
            calculateTotal()
            calculateExchange()
        }.launchIn(viewModelScope)
    }

    private suspend fun calculateTotal() {
        val count = assetsRepo.allCurrencyAmount().map {
            it.code to it.amount
        }.toMap()
        val rates = currencyRepo.getCurrencyRate().associate { it.code to it.rate }
        val result = mutableMapOf<String, Double>()
        var USD = 0.0

        count.forEach {
            USD += it.value * rates[it.key]!!
        }

        count.forEach {
            result[it.key] = USD / rates[it.key]!!
        }

        total.emit(result)
    }

    private suspend fun calculateExchange() {
        val count = assetsRepo.allCurrencyAmount().map {
            it.code to it.amount
        }.toMap()
        val rates = currencyRepo.getCurrencyRate().associate { it.code to it.rate }
        val result = mutableMapOf<String, Double>()

        count.forEach { i ->
            count.forEach { j ->
                if (j.key != i.key) {
                    result["${i.key}/${j.key}"] = rates[i.key]!! / rates[j.key]!!
                }
            }
        }

        exchange.emit(result)
    }
}

@Singleton
class SummaryViewModelFactory @Inject constructor(
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SummaryViewModel(assetsRepo, currencyRepo) as T
    }
}