package space.taran.arkrate.presentation.addcurrency

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import space.taran.arkrate.data.GeneralCurrencyRepo
import space.taran.arkrate.data.assets.AssetsRepo
import javax.inject.Inject
import javax.inject.Singleton

class AddCurrencyViewModel(
    private val assetsRepo: AssetsRepo,
    private val currencyRepo: GeneralCurrencyRepo
): ViewModel() {
    var codeToCurrency by mutableStateOf<Map<String, String>?>(null)

    init {
        viewModelScope.launch {
            codeToCurrency = currencyRepo.codeToCurrency()
        }
    }

    fun addCurrency(code: String) = viewModelScope.launch {
        assetsRepo.setCurrencyAmount(code, 0.0)
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