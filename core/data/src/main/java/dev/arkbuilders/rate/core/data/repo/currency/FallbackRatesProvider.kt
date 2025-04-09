package dev.arkbuilders.rate.core.data.repo.currency

import android.content.Context
import com.google.gson.Gson
import dev.arkbuilders.rate.core.data.mapper.CryptoRateResponseMapper
import dev.arkbuilders.rate.core.data.mapper.FiatRateResponseMapper
import dev.arkbuilders.rate.core.data.network.dto.CryptoRateResponse
import dev.arkbuilders.rate.core.data.network.dto.FiatRateResponse
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FallbackRatesProvider @Inject constructor(
    private val ctx: Context,
    private val fiatRateResponseMapper: FiatRateResponseMapper,
    private val cryptoRateResponseMapper: CryptoRateResponseMapper,
) {
    suspend fun provideRatesAndFetchDate(): Pair<List<CurrencyRate>, OffsetDateTime> =
        withContext(Dispatchers.IO) {
            val fiatJson =
                ctx.assets.open(FIAT_RATES_FILE).bufferedReader().use {
                    it.readText()
                }
            val fiatResp = Gson().fromJson(fiatJson, FiatRateResponse::class.java)
            val fiatRates = fiatRateResponseMapper.map(fiatResp)

            val cryptoJson =
                ctx.assets.open(CRYPTO_RATES_FILE).bufferedReader().use {
                    it.readText()
                }
            val cryptoResp = Gson().fromJson(cryptoJson, Array<CryptoRateResponse>::class.java)
            val cryptoRates = cryptoRateResponseMapper.map(cryptoResp.toList())

            val fetchDate =
                OffsetDateTime.ofInstant(
                    Instant.ofEpochSecond(fiatResp.timestamp),
                    ZoneOffset.UTC,
                )
            val rates = cryptoRates + fiatRates
            return@withContext rates to fetchDate
        }

    companion object {
        private const val CRYPTO_RATES_FILE = "crypto-rates.json"
        private const val FIAT_RATES_FILE = "fiat-rates.json"
    }
}
