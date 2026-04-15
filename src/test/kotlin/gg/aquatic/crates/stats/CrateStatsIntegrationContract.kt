package gg.aquatic.crates.stats

import gg.aquatic.crates.stats.table.AllTimeCrateStatsTable
import gg.aquatic.crates.stats.table.AllTimeRewardStatsTable
import gg.aquatic.crates.stats.table.CrateOpeningRewardsTable
import gg.aquatic.crates.stats.table.CrateOpeningsTable
import gg.aquatic.crates.stats.table.HourlyCrateStatsTable
import gg.aquatic.crates.stats.table.HourlyRewardStatsTable
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.insert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("integration")
abstract class CrateStatsIntegrationContract {
    protected abstract fun createDatabase(): CrateStatsDatabase

    @AfterEach
    fun tearDownStats() {
        CrateStats.shutdown()
    }

    @Test
    fun `queued openings flush into database and queries return expected values`() = runBlocking {
        connectStats()
        val player = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val now = System.currentTimeMillis()

        CrateStats.logOpening(
            LoggedOpening(
                playerUuid = player,
                crateId = "test",
                openedAtMillis = now,
                rewards = listOf(
                    LoggedRewardWin(rewardId = "example", rarityId = "common", amount = 2),
                    LoggedRewardWin(rewardId = "bonus", rarityId = "rare", amount = 1)
                )
            )
        )
        CrateStats.logOpening(
            LoggedOpening(
                playerUuid = player,
                crateId = "test",
                openedAtMillis = now + 5_000L,
                rewards = listOf(
                    LoggedRewardWin(rewardId = "example", rarityId = "common", amount = 3)
                )
            )
        )

        CrateStats.flushPendingOpeningsSync()

        assertEquals(2L, CrateStats.getCrateOpens("test", CrateStatsTimeframe.ALL_TIME))
        assertEquals(2L, CrateStats.getPlayerCrateOpens(player, "test", CrateStatsTimeframe.ALL_TIME))
        assertEquals(2L, CrateStats.getPlayerRewardWins(player, "test", "example", CrateStatsTimeframe.ALL_TIME))
        assertEquals(
            RewardStatsSnapshot(wins = 2L, amountSum = 5L),
            CrateStats.getRewardStats("test", "example", CrateStatsTimeframe.ALL_TIME)
        )

        val latest = CrateStats.getLatestCrateRewardsCached("test", limit = 3)
        assertEquals(3, latest.size)
        assertEquals("example", latest.first().rewardId)
        assertTrue(latest.any { it.rewardId == "bonus" && it.amount == 1 })
    }

    @Test
    fun `all time hot cache returns player stats before batch flush`() = runBlocking {
        connectStats()
        val player = UUID.fromString("22222222-2222-2222-2222-222222222222")

        CrateStats.logOpening(
            LoggedOpening(
                playerUuid = player,
                crateId = "cached",
                openedAtMillis = System.currentTimeMillis(),
                rewards = listOf(
                    LoggedRewardWin(rewardId = "example", rarityId = "common", amount = 1)
                )
            )
        )

        assertEquals(1L, CrateStats.getPlayerCrateOpens(player, "cached", CrateStatsTimeframe.ALL_TIME))
        assertEquals(1L, CrateStats.getPlayerRewardWins(player, "cached", "example", CrateStatsTimeframe.ALL_TIME))
        assertEquals(0L, CrateStats.getCrateOpens("cached", CrateStatsTimeframe.ALL_TIME))
    }

    @Test
    fun `invalidate removes missing crate data and expired hourly buckets`() = runBlocking {
        connectStats()
        val now = System.currentTimeMillis()
        val expiredBucket = CrateStats.truncateHour(now - (CrateStats.HOURLY_RETENTION_DAYS + 2L) * 24L * CrateStats.HOUR_MILLIS)

        CrateStats.logOpening(
            LoggedOpening(
                playerUuid = UUID.fromString("33333333-3333-3333-3333-333333333333"),
                crateId = "keep",
                openedAtMillis = now,
                rewards = listOf(LoggedRewardWin("example", "common", 1))
            )
        )
        CrateStats.logOpening(
            LoggedOpening(
                playerUuid = UUID.fromString("44444444-4444-4444-4444-444444444444"),
                crateId = "remove",
                openedAtMillis = now,
                rewards = listOf(LoggedRewardWin("old", "common", 2))
            )
        )
        CrateStats.flushPendingOpeningsSync()
        CrateStats.dbQuerySync {
            HourlyCrateStatsTable.insert {
                it[bucketHourMillis] = expiredBucket
                it[crateId] = "keep"
                it[opens] = 9L
            }
            HourlyRewardStatsTable.insert {
                it[bucketHourMillis] = expiredBucket
                it[crateId] = "keep"
                it[rewardId] = "example"
                it[wins] = 9L
                it[amountSum] = 9L
            }
        }

        val result = CrateStats.invalidate(setOf("keep"))

        assertEquals(1, result.deletedOpenings)
        assertEquals(1, result.deletedOpeningRewards)
        assertEquals(1, result.deletedAllTimeCrateRows)
        assertEquals(1, result.deletedAllTimeRewardRows)
        assertEquals(1, result.deletedExpiredHourlyCrateBuckets)
        assertEquals(1, result.deletedExpiredHourlyRewardBuckets)
        assertEquals(1L, CrateStats.getCrateOpens("keep", CrateStatsTimeframe.ALL_TIME))
        assertEquals(0L, CrateStats.getCrateOpens("remove", CrateStatsTimeframe.ALL_TIME))
    }

    private fun connectStats() {
        CrateStats.shutdown()
        CrateStats.configuredEnabled = true
        CrateStats.statsDatabase = createDatabase()
        CrateStats.dbQuerySync {
            CrateOpeningRewardsTable.deleteAll()
            CrateOpeningsTable.deleteAll()
            HourlyRewardStatsTable.deleteAll()
            HourlyCrateStatsTable.deleteAll()
            AllTimeRewardStatsTable.deleteAll()
            AllTimeCrateStatsTable.deleteAll()
        }
    }
}
