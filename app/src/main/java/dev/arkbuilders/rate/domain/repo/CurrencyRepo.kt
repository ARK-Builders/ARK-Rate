package dev.arkbuilders.rate.domain.repo

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate

interface CurrencyRepo {
    suspend fun getCurrencyRate(): Either<Throwable, List<CurrencyRate>>

    suspend fun getCurrencyName(): Either<Throwable, List<CurrencyName>>

    suspend fun isRatesAvailable(): Boolean

    suspend fun getCurrencyNameUnsafe() = getCurrencyName().getOrNull()!!

    suspend fun nameByCode(code: CurrencyCode): Either<Throwable, CurrencyName> =
        getCurrencyName().fold(
            ifLeft = {
                it.left()
            },
            ifRight = { names ->
                val name = names.find { name -> name.code == code }
                return name?.right() ?: IllegalStateException().left()
            }
        )

    suspend fun nameByCodeUnsafe(code: CurrencyCode) = nameByCode(code).getOrNull()!!

    suspend fun rateByCode(code: CurrencyCode): Either<Throwable, CurrencyRate> =
        getCodeToCurrencyRate().map { codeToRate -> codeToRate[code]!! }

    suspend fun rateByCodeUnsafe(code: CurrencyCode) = rateByCode(code).getOrNull()!!

    suspend fun getCodeToCurrencyRate(): Either<Throwable, Map<CurrencyCode, CurrencyRate>> =
        getCurrencyRate().map { rates -> rates.associateBy { it.code } }
}