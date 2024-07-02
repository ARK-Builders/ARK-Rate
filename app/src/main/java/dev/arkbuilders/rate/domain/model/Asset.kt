package dev.arkbuilders.rate.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Asset(
    val id: Long = 0,
    val code: CurrencyCode,
    var value: Double,
    val group: String? = null
): Parcelable {
    companion object {
        val EMPTY = Asset(0, "", 0.0)
    }
}