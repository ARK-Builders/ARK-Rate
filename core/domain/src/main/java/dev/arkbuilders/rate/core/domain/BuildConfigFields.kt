package dev.arkbuilders.rate.core.domain

data class BuildConfigFields(
    val buildType: String,
    val versionCode: Int,
    val versionName: String,
    val isGooglePlayBuild: Boolean
)

interface BuildConfigFieldsProvider {
    fun init(fields: BuildConfigFields)
    fun provide(): BuildConfigFields
}
