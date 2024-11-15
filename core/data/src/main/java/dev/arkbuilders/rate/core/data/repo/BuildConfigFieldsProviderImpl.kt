package dev.arkbuilders.rate.core.data.repo

import dev.arkbuilders.rate.core.domain.BuildConfigFields
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider

class BuildConfigFieldsProviderImpl: BuildConfigFieldsProvider {
    private var fields: BuildConfigFields? = null

    @Synchronized
    override fun init(fields: BuildConfigFields) {
        this.fields = fields
    }

    @Synchronized
    override fun provide(): BuildConfigFields {
        return fields ?: error("BuildConfigFields not initialized")
    }
}
