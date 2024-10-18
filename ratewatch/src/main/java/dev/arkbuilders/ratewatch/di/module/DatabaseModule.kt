package dev.arkbuilders.ratewatch.di.module

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.arkbuilders.ratewatch.data.db.Database
import dev.arkbuilders.ratewatch.data.db.Database.Companion.DB_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun database(app: Application): Database {
        return Room.databaseBuilder(app, Database::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun assetsDao(db: Database) = db.assetsDao()

    @Provides
    @Singleton
    fun quickDao(db: Database) = db.quickDao()

    @Provides
    @Singleton
    fun rateDao(db: Database) = db.rateDao()

    @Provides
    @Singleton
    fun pairAlertDao(db: Database) = db.pairAlertDao()

    @Provides
    @Singleton
    fun fetchTimestampDao(db: Database) = db.fetchTimestampDao()

    @Provides
    @Singleton
    fun codeUseStatDao(db: Database) = db.codeUseStatDao()
}
