package space.taran.arkrate.data.assets

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import space.taran.arkrate.data.CurrencyAmount
import space.taran.arkrate.data.db.AssetsDao
import space.taran.arkrate.data.db.AssetsLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetsRepo @Inject constructor(
    private val local: AssetsLocalDataSource
) {
    private val codeToAmount = mutableListOf<CurrencyAmount>()
    private val codeToAmountFlow = MutableSharedFlow<List<CurrencyAmount>>()

    suspend fun init() = withContext(Dispatchers.IO) {
        codeToAmount.addAll(local.getAll())
        codeToAmountFlow.emit(codeToAmount)
    }

    fun allCurrencyAmount() = codeToAmount

    fun allCurrencyAmountFlow(): SharedFlow<List<CurrencyAmount>?> = codeToAmountFlow

    suspend fun setCurrencyAmount(code: String, amount: Double) {
        codeToAmount.find {
            it.code == code
        }?.let {
            it.amount = amount
        } ?: let {
            codeToAmount.add(CurrencyAmount(code, amount))
        }
        codeToAmountFlow.emit(codeToAmount)
        local.insert(CurrencyAmount(code, amount))
    }

    suspend fun removeCurrency(code: String) {
        codeToAmount.find { it.code == code }?.let {
            codeToAmount.remove(it)
        }
        codeToAmountFlow.emit(codeToAmount)
        local.delete(code)
    }
}