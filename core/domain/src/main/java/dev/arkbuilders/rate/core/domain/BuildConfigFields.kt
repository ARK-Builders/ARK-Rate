package dev.arkbuilders.rate.core.domain

import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import java.time.OffsetDateTime

data class BuildConfigFields(
    val buildType: String,
    val versionCode: Int,
    val versionName: String,
    val isGooglePlayBuild: Boolean,
    val fallbackCryptoRatesFetchDate: OffsetDateTime,
    val fallbackFiatRatesFetchDate: OffsetDateTime,
    val availableIconCodes: Set<CurrencyCode>,
)

interface BuildConfigFieldsProvider {
    fun init(fields: BuildConfigFields)

    fun provide(): BuildConfigFields
}
