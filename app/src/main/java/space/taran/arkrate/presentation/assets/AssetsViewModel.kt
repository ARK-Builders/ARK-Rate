package space.taran.arkrate.presentation.assets

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import space.taran.arkrate.data.CurrencyAmount
import space.taran.arkrate.data.assets.AssetsRepo
import javax.inject.Inject
import javax.inject.Singleton

class AssetsViewModel(
    private val assetsRepo: AssetsRepo
): ViewModel() {
    var currencyAmountList = mutableStateListOf<CurrencyAmount>()

    init {
        viewModelScope.launch {
            assetsRepo.allCurrencyAmountFlow().collect {
                currencyAmountList.clear()
                currencyAmountList.addAll(it)
            }
        }
    }

    fun onAmountChanged(code: String, oldInput: String, newInput: String): String {
        val containsDigitsAndDot = Regex("[0-9]*\\.?[0-9]*")
        if (!containsDigitsAndDot.matches(newInput))
            return oldInput

        val containsDigit = Regex(".*[0-9].*")
        if (!containsDigit.matches(newInput)) {
            viewModelScope.launch {
                assetsRepo.setCurrencyAmount(code, 0.0)
            }
            return newInput
        }

        viewModelScope.launch {
            assetsRepo.setCurrencyAmount(code, newInput.toDouble())
        }

        val leadingZeros = "^0+(?=\\d)".toRegex()

        return newInput.replace(leadingZeros,"")
    }

    fun onCurrencyRemoved(code: String) = viewModelScope.launch {
        assetsRepo.removeCurrency(code)
    }
}

@Singleton
class AssetsViewModelFactory @Inject constructor(
    private val assetsRepo: AssetsRepo
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AssetsViewModel(assetsRepo) as T
    }
}