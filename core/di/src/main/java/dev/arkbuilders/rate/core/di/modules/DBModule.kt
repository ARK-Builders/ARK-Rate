package dev.arkbuilders.rate.core.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.db.Database
import javax.inject.Singleton

@Module
class DBModule {
    @Singleton
    @Provides
    fun database(app: Application): Database {
        return Database.build(app)
    }

    @Provides
    fun assetsDao(db: Database) = db.assetsDao()

    @Provides
    fun quickDao(db: Database) = db.quickDao()

    @Provides
    fun rateDao(db: Database) = db.rateDao()

    @Provides
    fun pairAlertDao(db: Database) = db.pairAlertDao()

    @Provides
    fun fetchTimestampDao(db: Database) = db.fetchTimestampDao()

    @Provides
    fun codeUseStatDao(db: Database) = db.codeUseStatDao()

    @Provides
    fun groupDao(db: Database) = db.groupDao()
}
