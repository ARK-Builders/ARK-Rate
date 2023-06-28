package space.taran.arkrate.data.assets

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import space.taran.arkrate.data.CurrencyAmount
import space.taran.arkrate.data.db.AssetsLocalDataSource
import space.taran.arkrate.utils.replace
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetsRepo @Inject constructor(
    private val local: AssetsLocalDataSource
) {
    private var currencyAmountList = listOf<CurrencyAmount>()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            currencyAmountList = local.getAll()
        }
    }

    fun allCurrencyAmount(): List<CurrencyAmount> = currencyAmountList

    fun allCurrencyAmountFlow(): Flow<List<CurrencyAmount>> = local.allFlow()

    suspend fun setCurrencyAmount(code: String, amount: Double) =
        withContext(Dispatchers.IO) {
            currencyAmountList.find {
                it.code == code
            }?.let { currencyAmount ->
                currencyAmountList = currencyAmountList.replace(
                    currencyAmount,
                    currencyAmount.copy(amount = amount)
                )
            } ?: let {
                currencyAmountList =
                    currencyAmountList + CurrencyAmount(code, amount)
            }
            local.insert(CurrencyAmount(code, amount))
        }

    suspend fun removeCurrency(code: String) = withContext(Dispatchers.IO) {
        currencyAmountList.find { it.code == code }?.let {
            currencyAmountList = currencyAmountList - it
        }
        local.delete(code)
    }
}