package dev.arkbuilders.rate.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.data.CurrencyAmount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.assets.AssetsRepo

class SummaryViewModel(
    private val selectedAmount: CurrencyAmount?,
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo
) : ViewModel() {
    var total = MutableStateFlow<Map<String, Double>?>(null)
    var exchange = MutableStateFlow<Map<String, Double>?>(null)

    init {
        if (selectedAmount == null) {
            viewModelScope.launch {
                calculateTotal()
                calculateExchange()
            }
            assetsRepo.allCurrencyAmountFlow().onEach {
                calculateTotal()
                calculateExchange()
            }.launchIn(viewModelScope)
        } else {
            viewModelScope.launch {
                calculateSelectedTotal()
                calculateSelectedExchange()
            }
        }
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

    private suspend fun calculateSelectedTotal() {
        val count = assetsRepo.allCurrencyAmount().map {
            it.code to it.amount
        }.toMap()
        val rates = currencyRepo.getCurrencyRate().associate { it.code to it.rate }
        val result = mutableMapOf<String, Double>()
        val USD = selectedAmount!!.amount * rates[selectedAmount.code]!!
        result[selectedAmount.code] = selectedAmount.amount

        count.forEach {
            result[it.key] = USD / rates[it.key]!!
        }


        total.emit(result)
    }

    private suspend fun calculateSelectedExchange() {
        val amountsList = assetsRepo.allCurrencyAmount().toMutableList()
        val rates =
            currencyRepo.getCurrencyRate().associate { it.code to it.rate }
        val result = mutableMapOf<String, Double>()

        amountsList.find {
            it.code == selectedAmount!!.code
        }?.let { duplicate ->
            amountsList.remove(duplicate)
        }

        amountsList.add(0, selectedAmount!!)

        amountsList.forEach { iterAmount1 ->
            amountsList.forEach { iterAmount2 ->
                if (iterAmount1 == iterAmount2) {
                    return@forEach
                }

                if (iterAmount1.code == selectedAmount.code ||
                    iterAmount2.code == selectedAmount.code
                ) {
                    result["${iterAmount1.code}/${iterAmount2.code}"] =
                        rates[iterAmount1.code]!! / rates[iterAmount2.code]!!
                }
            }
        }

        exchange.emit(result)
    }
}

class SummaryViewModelFactory @AssistedInject constructor(
    @Assisted private val amount: CurrencyAmount?,
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SummaryViewModel(amount, assetsRepo, currencyRepo) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted amount: CurrencyAmount?,
        ): SummaryViewModelFactory
    }
}