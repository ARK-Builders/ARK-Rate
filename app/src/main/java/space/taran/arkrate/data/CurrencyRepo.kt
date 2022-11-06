package space.taran.arkrate.data

import kotlinx.coroutines.flow.StateFlow

interface CurrencyRepo {

    // ETH -> 1500.0
    suspend fun getCurrencyRate(): List<CurrencyRate>

    // ETH -> Ethereum
    suspend fun getCurrencyName(): List<CurrencyName>
}