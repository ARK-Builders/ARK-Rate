package dev.arkbuilders.rate.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.data.model.CurrencyAmount
import dev.arkbuilders.rate.data.model.CurrencyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.assets.AssetsRepo
import dev.arkbuilders.rate.data.preferences.PreferenceKey
import dev.arkbuilders.rate.data.preferences.Preferences
import java.text.DecimalFormat

class SummaryViewModel(
    private val selectedAmount: CurrencyAmount?,
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo,
    private val prefs: Preferences,
) : ViewModel() {
    var total = MutableStateFlow<Map<String, Double>?>(null)
    var exchange = MutableStateFlow<Map<String, String>?>(null)
    private lateinit var fiatFiatFormat: DecimalFormat
    private lateinit var cryptoCryptoFormat: DecimalFormat
    private lateinit var fiatCryptoFormat: DecimalFormat

    init {
        viewModelScope.launch {
            prefs.get(PreferenceKey.FiatFiatRateRound).let {
                fiatFiatFormat = createFormat(it)
            }
            prefs.get(PreferenceKey.CryptoCryptoRateRound).let {
                cryptoCryptoFormat = createFormat(it)
            }
            prefs.get(PreferenceKey.FiatCryptoRateRound).let {
                fiatCryptoFormat = createFormat(it)
            }
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
        val count = assetsRepo.allCurrencyAmount().associate {
            it.code to it.amount
        }
        val rates = currencyRepo.getCurrencyRate().associateBy { it.code }
        val result = mutableMapOf<String, String>()

        count.forEach { i ->
            count.forEach { j ->
                if (j.key != i.key) {
                    val rate1 = rates[i.key]!!
                    val rate2 = rates[j.key]!!
                    val exchangeRate = rate1.rate / rate2.rate
                    result["${i.key}/${j.key}"] =
                        pickFormatter(rate1.type, rate2.type)
                            .format(exchangeRate)
                }
            }
        }

        exchange.emit(result)
    }

    private suspend fun calculateSelectedTotal() {
        val count = assetsRepo.allCurrencyAmount().associate {
            it.code to it.amount
        }
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
            currencyRepo.getCurrencyRate().associateBy { it.code }
        val result = mutableMapOf<String, String>()

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
                    val rate1 = rates[iterAmount1.code]!!
                    val rate2 = rates[iterAmount2.code]!!

                    val exchangeRate = rate1.rate / rate2.rate
                    result["${iterAmount1.code}/${iterAmount2.code}"] =
                        pickFormatter(rate1.type, rate2.type).format(exchangeRate)
                }
            }
        }

        exchange.emit(result)
    }

    private fun createFormat(n: Int): DecimalFormat {
        return if (n == 0)
            DecimalFormat("0")
        else
            DecimalFormat("0." + "#".repeat(n))
    }

    private fun pickFormatter(
        type1: CurrencyType,
        type2: CurrencyType
    ): DecimalFormat {
        if (type1 == CurrencyType.FIAT && type2 == CurrencyType.FIAT)
            return fiatFiatFormat

        if (type1 == CurrencyType.CRYPTO && type2 == CurrencyType.CRYPTO)
            return cryptoCryptoFormat

        return fiatCryptoFormat
    }
}

class SummaryViewModelFactory @AssistedInject constructor(
    @Assisted private val amount: CurrencyAmount?,
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo,
    private val prefs: Preferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SummaryViewModel(amount, assetsRepo, currencyRepo, prefs) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted amount: CurrencyAmount?,
        ): SummaryViewModelFactory
    }
}