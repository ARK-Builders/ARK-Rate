package dev.arkbuilders.rate.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class Asset(
    val id: Long = 0,
    val code: CurrencyCode,
    var value: BigDecimal,
    val group: String? = null,
) : Parcelable {
    companion object {
        val EMPTY = Asset(0, "", BigDecimal.ZERO)
    }
}
