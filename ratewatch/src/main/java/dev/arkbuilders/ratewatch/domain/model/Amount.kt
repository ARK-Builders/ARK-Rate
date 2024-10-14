package dev.arkbuilders.ratewatch.domain.model

import dev.arkbuilders.ratewatch.data.toDoubleSafe

data class Amount(val code: CurrencyCode, val value: Double)

data class AmountStr(val code: CurrencyCode, val value: String)

fun Amount.toStrAmount() = AmountStr(code, value.toString())

fun AmountStr.toDAmount() = Amount(code, value.toDoubleSafe())
