package dev.arkbuilders.rate.core.domain

import android.icu.util.Currency
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrUtils {
    fun validateInput(
        oldInput: String,
        newInput: String,
    ): String {
        val containsDigitsAndDot = Regex("[0-9]*\\.?[0-9]*")
        if (!containsDigitsAndDot.matches(newInput))
            return oldInput

        val leadingZeros = "^0+(?=\\d)".toRegex()

        return newInput.replace(leadingZeros, "")
    }

    fun validateInputWithMinusChar(
        oldInput: String,
        newInput: String,
    ): String {
        val containsDigitsAndDot = Regex("-?[0-9]*\\.?[0-9]*")
        if (!containsDigitsAndDot.matches(newInput))
            return oldInput

        val leadingZeros = "^0+(?=\\d)".toRegex()

        return newInput.replace(leadingZeros, "")
    }

    fun prepareToDisplay(value: BigDecimal): String {
        var fractionSize = if (value.toDouble() > 10) 2 else 8

        val fractionalPart = value.remainder(BigDecimal.ONE)
        if (fractionalPart == BigDecimal.ZERO) {
            fractionSize = 0
        }
        val fractionPattern =
            if (fractionSize == 0)
                ""
            else
                "." + "#".repeat(fractionSize)

        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
        formatSymbols.decimalSeparator = '.'
        formatSymbols.groupingSeparator = ','
        val numberFormatter = DecimalFormat("###,###$fractionPattern", formatSymbols)
        return numberFormatter.format(value)
    }

    fun roundOff(number: BigDecimal): String {
        val fractionSize =
            if (number.toDouble() > 10) 2 else 8

        val df =
            DecimalFormat(
                "#." + "#".repeat(fractionSize),
                DecimalFormatSymbols(Locale.ENGLISH),
            )
        return df.format(number)
    }

    fun getSymbolOrCode(code: CurrencyCode): String {
        return try {
            Currency.getInstance(code).symbol
        } catch (e: Throwable) {
            code
        }
    }
}

fun BigDecimal.divideArk(divisor: BigDecimal) = this.divide(divisor, 50, RoundingMode.HALF_EVEN)

fun String.toBigDecimalArk() =
    when {
        this == "" -> BigDecimal.ZERO
        this == "-" -> BigDecimal.ZERO
        this == "." -> BigDecimal.ZERO
        else -> this.toBigDecimal()
    }

fun String.toDoubleArk() =
    when {
        this == "" -> 0.0
        this == "-" -> 0.0
        this == "." -> 0.0
        else -> this.toDouble()
    }
