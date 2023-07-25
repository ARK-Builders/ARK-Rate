package dev.arkbuilders.rate.data.db

import androidx.room.AutoMigration
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@androidx.room.Database(
    entities = [
        RoomCurrencyAmount::class,
        RoomCurrencyRate::class,
        RoomFetchTimestamp::class,
        RoomPairAlertCondition::class
    ],
    version = 3,
    exportSchema = true,
)
abstract class Database : RoomDatabase() {
    abstract fun assetsDao(): AssetsDao
    abstract fun rateDao(): CurrencyRateDao
    abstract fun fetchTimestampDao(): FetchTimestampDao
    abstract fun pairAlertDao(): PairAlertConditionDao

    companion object {
        const val DB_NAME = "arkrate.db"
    }
}