package dev.arkbuilders.rate.domain.repo

import arrow.core.Either
import dev.arkbuilders.rate.domain.model.CurrencyCode
import dev.arkbuilders.rate.domain.model.CurrencyName
import dev.arkbuilders.rate.domain.model.CurrencyRate

interface CurrencyRepo {
    suspend fun nameByCode(code: CurrencyCode): Either<Throwable, CurrencyName>

    suspend fun nameByCodeUnsafe(code: CurrencyCode) = nameByCode(code).getOrNull()!!

    suspend fun rateByCode(code: CurrencyCode): Either<Throwable, CurrencyRate>

    suspend fun rateByCodeUnsafe(code: CurrencyCode) = rateByCode(code).getOrNull()!!

    suspend fun getCodeToCurrencyRate(): Either<Throwable, Map<CurrencyCode, CurrencyRate>>

    suspend fun getCurrencyRate(): Either<Throwable, List<CurrencyRate>>

    suspend fun getCurrencyName(): Either<Throwable, List<CurrencyName>>

    suspend fun getCurrencyNameUnsafe() = getCurrencyName().getOrNull()!!
}