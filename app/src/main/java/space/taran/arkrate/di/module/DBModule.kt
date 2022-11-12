package space.taran.arkrate.di.module

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import space.taran.arkrate.data.db.Database
import space.taran.arkrate.data.db.Database.Companion.DB_NAME
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
    fun rateDao(db: Database) = db.rateDao()

    @Provides
    fun fetchTimestampDao(db: Database) = db.fetchTimestampDao()
}