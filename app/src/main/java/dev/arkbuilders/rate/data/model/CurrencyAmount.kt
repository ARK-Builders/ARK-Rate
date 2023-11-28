package dev.arkbuilders.rate.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrencyAmount(val id: Long = 0, val code: CurrencyCode, var amount: Double) : Parcelable