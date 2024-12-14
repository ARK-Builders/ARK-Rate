package dev.arkbuilders.rate.core.di

import dagger.Module
import dev.arkbuilders.rate.core.di.modules.ApiModule
import dev.arkbuilders.rate.core.di.modules.DBModule
import dev.arkbuilders.rate.core.di.modules.RepoModule
import dev.arkbuilders.rate.core.di.modules.UseCaseModule

@Module(
    includes = [
        ApiModule::class,
        DBModule::class,
        RepoModule::class,
        UseCaseModule::class,
    ],
)
class CoreDataModule
