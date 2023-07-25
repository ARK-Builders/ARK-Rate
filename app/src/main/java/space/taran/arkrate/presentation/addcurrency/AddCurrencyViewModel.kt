package dev.arkbuilders.rate.presentation.addcurrency

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import dev.arkbuilders.rate.data.CurrencyAmount
import dev.arkbuilders.rate.data.CurrencyName
import dev.arkbuilders.rate.data.GeneralCurrencyRepo
import dev.arkbuilders.rate.data.assets.AssetsRepo
import dev.arkbuilders.rate.data.CurrencyCode
import javax.inject.Inject
import javax.inject.Singleton

class AddCurrencyViewModel(
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo
): ViewModel() {
    var currencyNameList by mutableStateOf<List<CurrencyName>?>(null)

    init {
        viewModelScope.launch {
            currencyNameList = currencyRepo.getCurrencyName()
        }
    }

    fun addCurrency(code: CurrencyCode) = viewModelScope.launch {
        assetsRepo.findByCode(code) ?: let {
            assetsRepo.setCurrencyAmount(CurrencyAmount(code = code, amount = 0.0))
        }
    }
}

@Singleton
class AddCurrencyViewModelFactory @Inject constructor(
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  AddCurrencyViewModel(assetsRepo, currencyRepo) as T
    }
}