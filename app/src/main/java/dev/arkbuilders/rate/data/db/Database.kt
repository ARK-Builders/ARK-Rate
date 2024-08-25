package dev.arkbuilders.rate.data.db

import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.arkbuilders.rate.data.db.dao.CodeUseStatDao
import dev.arkbuilders.rate.data.db.dao.CurrencyRateDao
import dev.arkbuilders.rate.data.db.dao.PairAlertDao
import dev.arkbuilders.rate.data.db.dao.PortfolioDao
import dev.arkbuilders.rate.data.db.dao.QuickPairDao
import dev.arkbuilders.rate.data.db.dao.TimestampDao
import dev.arkbuilders.rate.data.db.entity.RoomAsset
import dev.arkbuilders.rate.data.db.entity.RoomCodeUseStat
import dev.arkbuilders.rate.data.db.entity.RoomCurrencyRate
import dev.arkbuilders.rate.data.db.entity.RoomFetchTimestamp
import dev.arkbuilders.rate.data.db.entity.RoomPairAlert
import dev.arkbuilders.rate.data.db.entity.RoomQuickPair
import dev.arkbuilders.rate.data.db.typeconverters.ListAmountTypeConverter
import dev.arkbuilders.rate.data.db.typeconverters.OffsetDateTimeTypeConverter

@androidx.room.Database(
    entities = [
        RoomAsset::class,
        RoomCurrencyRate::class,
        RoomFetchTimestamp::class,
        RoomPairAlert::class,
        RoomQuickPair::class,
        RoomCodeUseStat::class,
    ],
    version = 13,
    exportSchema = true,
)
@TypeConverters(ListAmountTypeConverter::class, OffsetDateTimeTypeConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun assetsDao(): PortfolioDao

    abstract fun rateDao(): CurrencyRateDao

    abstract fun fetchTimestampDao(): TimestampDao

    abstract fun pairAlertDao(): PairAlertDao

    abstract fun quickDao(): QuickPairDao

    abstract fun codeUseStatDao(): CodeUseStatDao

    companion object {
        const val DB_NAME = "arkrate.db"
    }
}
