package dev.arkbuilders.rate.feature.portfolio.presentation.model

import android.os.Parcelable
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import kotlinx.parcelize.Parcelize

@Parcelize
class AddAssetsNavResult(
    val added: Array<NavAsset>,
) : Parcelable

@Parcelize
class NavAsset(
    val id: Long,
    val code: CurrencyCode,
    var value: String,
    val groupId: Long,
) : Parcelable
