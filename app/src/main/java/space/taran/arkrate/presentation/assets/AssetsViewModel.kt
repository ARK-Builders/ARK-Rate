package space.taran.arkrate.presentation.assets

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var codeToAmount by mutableStateOf<List<CurrencyAmount>?>(null)

    init {
        viewModelScope.launch {
            assetsRepo.init()
            codeToAmount = assetsRepo.allCurrencyAmount()
            assetsRepo.allCurrencyAmountFlow().collect {
                codeToAmount = it!!
            }
        }
    }

    fun onAmountChanged(code: String, amount: Double) = viewModelScope.launch {
        assetsRepo.setCurrencyAmount(code, amount)
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