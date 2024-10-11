package dev.arkbuilders.rate.domain.model

import dev.arkbuilders.rate.data.toBigDecimalArk
import java.math.BigDecimal

data class Amount(val code: CurrencyCode, val value: BigDecimal)

data class AmountStr(val code: CurrencyCode, val value: String)

fun Amount.toStrAmount() = AmountStr(code, value.toPlainString())

fun AmountStr.toAmount() = Amount(code, value.toBigDecimalArk())
