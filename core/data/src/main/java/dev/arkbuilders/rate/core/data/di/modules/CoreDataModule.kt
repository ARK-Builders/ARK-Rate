package dev.arkbuilders.rate.core.data.di.modules

import dagger.Module

@Module(
    includes = [
        ApiModule::class,
        DBModule::class,
        RepoModule::class,
        UseCaseModule::class
    ]
)
class CoreDataModule
