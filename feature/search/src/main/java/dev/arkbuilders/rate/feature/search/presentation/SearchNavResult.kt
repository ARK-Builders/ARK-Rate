package dev.arkbuilders.rate.feature.search.presentation

import android.os.Parcelable
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchNavResult(
    val key: String?,
    val pos: Int?,
    val code: CurrencyCode,
) : Parcelable
