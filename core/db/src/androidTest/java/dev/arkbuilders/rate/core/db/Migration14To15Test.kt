@file:Suppress("ktlint")

package dev.arkbuilders.rate.core.db

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.arkbuilders.rate.core.db.migration.MIGRATION_14_15
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class Migration14To15Test {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        Database::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate14To15() {
        var db = helper.createDatabase(TEST_DB, 14)
        fillDBWithTestData(db)
        db.close()

        db = helper.runMigrationsAndValidate(TEST_DB, 15, true, MIGRATION_14_15)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.

        checkGroupTable(db)

        checkTableData(
            db = db,
            tableName = "RoomQuickPair",
            keyColumn = "`from`",
            featureType = GroupFeatureType.Quick.name,
            expectedData = listOf(
                "USD" to "Default",
                "EUR" to "GroupQuickPair",
                "GBP" to "GroupQuickPair",
                "JPY" to "Default"
            )
        )

        checkTableData(
            db = db,
            tableName = "RoomPairAlert",
            keyColumn = "targetCode",
            featureType = GroupFeatureType.PairAlert.name,
            expectedData = listOf(
                "EUR" to "Default",
                "GBP" to "GroupPairAlert",
                "USD" to "GroupPairAlert",
                "CHF" to "Default"
            )
        )

        checkTableData(
            db = db,
            tableName = "RoomAsset",
            keyColumn = "code",
            featureType = GroupFeatureType.Portfolio.name,
            expectedData = listOf(
                "BTC" to "Default",
                "ETH" to "GroupAsset",
                "LTC" to "GroupAsset",
                "XRP" to "Default"
            )
        )
    }

    private fun fillDBWithTestData(db: SupportSQLiteDatabase) {
        db.execSQL(
            "INSERT INTO RoomQuickPair (id, `from`, amount, `to`, calculatedDate, pinnedDate, `group`) VALUES " +
                "(1, 'USD', '100.0', '[{\"code\":\"AAVE\",\"value\":\"0.58551437437789097722349083670004098600620645236800\"}]', '2024-03-15T12:00:00Z', NULL, NULL)," +
                "(2, 'EUR', '200.0', '[{\"code\":\"BTC\",\"value\":\"0.004512378912340987623450123450987654321\"}]', '2024-03-16T12:00:00Z', '2024-03-17T08:00:00Z', 'GroupQuickPair')," +
                "(3, 'GBP', '300.0', '[{\"code\":\"ETH\",\"value\":\"0.052341234123412341234\"}]', '2024-03-18T12:00:00Z', NULL, 'GroupQuickPair')," +
                "(4, 'JPY', '4000.0', '[{\"code\":\"DOGE\",\"value\":\"126.45\"}]', '2024-03-19T12:00:00Z', NULL, NULL)"
        )

        db.execSQL(
            "INSERT INTO RoomPairAlert (id, targetCode, baseCode, targetPrice, startPrice, alertPercent, oneTimeNotRecurrent, enabled, lastDateTriggered, `group`) VALUES " +
                "(1, 'EUR', 'USD', '1.1', '1.0', 10.0, 1, 1, '2024-03-16T10:00:00Z', NULL)," +
                "(2, 'GBP', 'EUR', '0.85', '0.8', NULL, 0, 1, NULL, 'GroupPairAlert')," +
                "(3, 'USD', 'GBP', '1.3', '1.2', 5.0, 1, 1, NULL, 'GroupPairAlert')," +
                "(4, 'CHF', 'USD', '0.92', '0.95', 3.0, 0, 1, NULL, NULL)"
        )

        db.execSQL(
            "INSERT INTO RoomAsset (id, code, amount, `group`) VALUES " +
                "(1, 'BTC', '0.005', NULL)," +
                "(2, 'ETH', '2.0', 'GroupAsset')," +
                "(3, 'LTC', '5.5', 'GroupAsset')," +
                "(4, 'XRP', '1000.0', NULL)"
        )
    }

    private fun checkGroupTable(db: SupportSQLiteDatabase) {
        val expectedGroupsToFeatureType = listOf(
            "GroupQuickPair" to GroupFeatureType.Quick.name,
            "GroupPairAlert" to GroupFeatureType.PairAlert.name,
            "GroupAsset" to GroupFeatureType.Portfolio.name,
            "Default" to GroupFeatureType.Quick.name,
            "Default" to GroupFeatureType.PairAlert.name,
            "Default" to GroupFeatureType.Portfolio.name,
        )

        val foundGroupsWithFeatureType = mutableSetOf<Pair<String, String>>()

        val groupsCursor = db.query("SELECT name, featureType FROM RoomGroup")
        while (groupsCursor.moveToNext()) {
            val name = groupsCursor.getString(groupsCursor.getColumnIndexOrThrow("name"))
            val featureType =
                groupsCursor.getString(groupsCursor.getColumnIndexOrThrow("featureType"))

            val groupToFeatureType = name to featureType

            assertTrue(
                "Unexpected group found: name = $name, featureType = $featureType",
                expectedGroupsToFeatureType.contains(groupToFeatureType),
            )

            assertTrue(
                "Duplicate group found: name = $name, featureType = $featureType",
                foundGroupsWithFeatureType.add(groupToFeatureType),
            )
        }
        groupsCursor.close()

        assertEquals(
            "The number of groups in the database does not match the expected count",
            expectedGroupsToFeatureType.size,
            foundGroupsWithFeatureType.size,
        )
    }

    private fun checkTableData(
        db: SupportSQLiteDatabase,
        tableName: String,
        keyColumn: String,
        featureType: String,
        expectedData: List<Pair<String, String>> // key, groupName
    ) {
        val cursor = db.query(
            """
                SELECT $keyColumn,
                               (SELECT name FROM RoomGroup WHERE id = $tableName.groupId AND featureType = ?)
                        FROM $tableName
            """.trimIndent(),
            arrayOf(featureType)
        )
        val foundData = mutableListOf<Pair<String, String>>()

        while (cursor.moveToNext()) {
            val key = cursor.getString(0)
            val groupName = cursor.getString(1)
            foundData.add(key to groupName)
        }
        cursor.close()

        assertEquals(
            "$tableName: Number of records doesn't match the expected count",
            expectedData.size,
            foundData.size,
        )

        expectedData.forEach { expected ->
            assertTrue(
                "$tableName: Missing record $expected",
                foundData.contains(expected)
            )
        }
    }
}
