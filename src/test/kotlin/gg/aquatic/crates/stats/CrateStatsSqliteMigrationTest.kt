package gg.aquatic.crates.stats

import java.nio.file.Files
import java.sql.DriverManager
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("integration")
class CrateStatsSqliteMigrationTest {
    private val tempDbFile = Files.createTempFile("aqcrates-stats-migration-", ".db").toFile().apply {
        deleteOnExit()
    }

    @AfterEach
    fun tearDownStats() {
        CrateStats.shutdown()
        tempDbFile.delete()
    }

    @Test
    fun `legacy sqlite schema is migrated to add reward win count column`() = runBlocking {
        createLegacySchema()

        CrateStats.shutdown()
        CrateStats.configuredEnabled = true
        CrateStats.statsDatabase = CrateStatsDatabase.connect(
            url = "jdbc:sqlite:${tempDbFile.absolutePath.replace('\\', '/')}",
            driver = "org.sqlite.JDBC",
            user = "",
            password = ""
        )

        val player = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        val opens = CrateStats.getPlayerCrateOpens(player, "test", CrateStatsTimeframe.ALL_TIME)
        val wins = CrateStats.getPlayerRewardWins(player, "test", "example", CrateStatsTimeframe.ALL_TIME)
        assertEquals(1L, opens)
        assertEquals(1L, wins)
    }

    private fun createLegacySchema() {
        DriverManager.getConnection("jdbc:sqlite:${tempDbFile.absolutePath.replace('\\', '/')}").use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(
                    """
                    CREATE TABLE acrates_openings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        player_uuid VARCHAR(36) NOT NULL,
                        crate_id VARCHAR(64) NOT NULL,
                        opened_at_millis BIGINT NOT NULL
                    )
                    """.trimIndent()
                )
                statement.executeUpdate(
                    """
                    CREATE TABLE acrates_opening_rewards (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        opening_id BIGINT NOT NULL,
                        player_uuid VARCHAR(36) NOT NULL,
                        crate_id VARCHAR(64) NOT NULL,
                        reward_id VARCHAR(64) NOT NULL,
                        rarity_id VARCHAR(64),
                        amount INTEGER NOT NULL,
                        won_at_millis BIGINT NOT NULL
                    )
                    """.trimIndent()
                )
                statement.executeUpdate(
                    """
                    INSERT INTO acrates_openings (
                        id, player_uuid, crate_id, opened_at_millis
                    ) VALUES (
                        1, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'test', 123456789
                    )
                    """.trimIndent()
                )
                statement.executeUpdate(
                    """
                    INSERT INTO acrates_opening_rewards (
                        opening_id, player_uuid, crate_id, reward_id, rarity_id, amount, won_at_millis
                    ) VALUES (
                        1, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'test', 'example', 'common', 5, 123456789
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
