package dev.arkbuilders.ratewatch.di.module

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dev.arkbuilders.ratewatch.data.db.Database
import dev.arkbuilders.ratewatch.data.db.Database.Companion.DB_NAME
import javax.inject.Singleton

@Module
class DBModule {
    @Singleton
    @Provides
    fun database(app: Application): Database {
        return Room.databaseBuilder(app, Database::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
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
}
