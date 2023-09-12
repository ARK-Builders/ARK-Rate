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
import dev.arkbuilders.rate.data.db.QuickConvertToCurrencyRepo
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.QuickConvertToCurrency
import dev.arkbuilders.rate.data.preferences.PreferenceKey
import dev.arkbuilders.rate.data.preferences.Preferences
import java.text.DecimalFormat

class SummaryViewModel(
    private val selectedAmount: CurrencyAmount?,
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo,
    private val quickConvertToCurrencyRepo: QuickConvertToCurrencyRepo,
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
        val convertToList = quickConvertToCurrencyRepo.getAll().map { it.code }
        val rates = currencyRepo
            .getCurrencyRate()
            .associate { it.code to it.rate }
        val result = mutableMapOf<String, Double>()
        val USD = selectedAmount!!.amount * rates[selectedAmount.code]!!
        result[selectedAmount.code] = selectedAmount.amount

        convertToList.forEach {
            result[it] = USD / rates[it]!!
        }


        total.emit(result)
    }

    private suspend fun calculateSelectedExchange() {
        val convertToList =
            quickConvertToCurrencyRepo.getAll().map { it.code }.toMutableList()
        val rates = currencyRepo
            .getCurrencyRate()
            .associateBy { it.code }
            .filter { it.key in convertToList }
        val result = mutableMapOf<String, String>()

        convertToList.find {
            it == selectedAmount!!.code
        }?.let { duplicate ->
            convertToList.remove(duplicate)
        }

        convertToList.add(0, selectedAmount!!.code)

        convertToList.forEach { iter1 ->
            convertToList.forEach { iter2 ->
                if (iter1 == iter2) {
                    return@forEach
                }

                if (iter1 == selectedAmount.code ||
                    iter2 == selectedAmount.code
                ) {
                    val rate1 = rates[iter1]!!
                    val rate2 = rates[iter2]!!

                    val exchangeRate = rate1.rate / rate2.rate
                    result["${iter1}/${iter2}"] =
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
    private val quickConvertToCurrencyRepo: QuickConvertToCurrencyRepo,
    private val prefs: Preferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SummaryViewModel(
            amount,
            assetsRepo,
            currencyRepo,
            quickConvertToCurrencyRepo,
            prefs
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted amount: CurrencyAmount?,
        ): SummaryViewModelFactory
    }
}