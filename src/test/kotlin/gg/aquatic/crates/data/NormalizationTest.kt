package gg.aquatic.crates.data

import gg.aquatic.crates.stats.CrateStatsTimeframe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NormalizationTest {
    @Test
    fun `reward normalization fixes rarity and deduplicates limits by timeframe`() {
        val data = RewardData(
            rarity = "missing",
            limits = listOf(
                LimitData(CrateStatsTimeframe.DAY, 0),
                LimitData(CrateStatsTimeframe.DAY, 5),
                LimitData(CrateStatsTimeframe.WEEK, -2)
            )
        )

        val normalized = data.normalized(
            availableRarities = setOf("default", "rare"),
            fallbackRarityId = "default"
        )

        assertEquals("default", normalized.rarity)
        assertEquals(2, normalized.limits.size)
        assertEquals(1, normalized.limits.first { it.timeframe == CrateStatsTimeframe.DAY }.limit)
        assertEquals(1, normalized.limits.first { it.timeframe == CrateStatsTimeframe.WEEK }.limit)
    }

    @Test
    fun `crate normalization ensures default rarity and sorted milestones`() {
        val data = CrateData(
            rarities = emptyMap(),
            limits = listOf(
                LimitData(CrateStatsTimeframe.MONTH, 0),
                LimitData(CrateStatsTimeframe.MONTH, 5)
            ),
            milestones = listOf(
                MilestoneData(milestone = 5),
                MilestoneData(milestone = 3)
            ),
            repeatableMilestones = listOf(
                MilestoneData(milestone = 10),
                MilestoneData(milestone = 2)
            )
        )

        val normalized = data.normalized(crateId = "test")

        assertTrue(CrateData.DEFAULT_RARITY_ID in normalized.rarities)
        assertEquals(1, normalized.limits.size)
        assertEquals(1, normalized.limits.first().limit)
        assertEquals(listOf(3, 5), normalized.milestones.map { it.milestone })
        assertEquals(listOf(2, 10), normalized.repeatableMilestones.map { it.milestone })
    }
}
