package dev.arkbuilders.rate.data.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import dev.arkbuilders.rate.data.model.CurrencyType
import javax.inject.Inject

@Entity
data class RoomFetchTimestamp(@PrimaryKey val currencyType: String, val timestamp: Long)

@Dao
interface FetchTimestampDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fetchTimestamp: RoomFetchTimestamp)

    @Query("SELECT * FROM RoomFetchTimestamp where currencyType = :currencyType")
    suspend fun getTimestamp(currencyType: String): RoomFetchTimestamp?
}

class FetchTimestampDataSource @Inject constructor(private val dao: FetchTimestampDao) {
    suspend fun rememberTimestamp(currencyType: CurrencyType) =
        dao.insert(RoomFetchTimestamp(currencyType.name, System.currentTimeMillis()))

    suspend fun getTimestamp(currencyType: CurrencyType) =
        dao.getTimestamp(currencyType.name)?.timestamp
}

