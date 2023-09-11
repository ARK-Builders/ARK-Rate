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
        RoomPairAlertCondition::class,
        RoomQuickCurrency::class
    ],
    version = 5,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 3, to = 5)
    ]
)
abstract class Database : RoomDatabase() {
    abstract fun assetsDao(): AssetsDao
    abstract fun rateDao(): CurrencyRateDao
    abstract fun fetchTimestampDao(): FetchTimestampDao
    abstract fun pairAlertDao(): PairAlertConditionDao
    abstract fun quickDao(): QuickCurrencyDao

    companion object {
        const val DB_NAME = "arkrate.db"
    }
}