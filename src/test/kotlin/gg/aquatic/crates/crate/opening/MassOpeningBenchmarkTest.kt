package gg.aquatic.crates.crate.opening

import gg.aquatic.common.argument.ArgumentContext
import gg.aquatic.common.argument.ObjectArgument
import gg.aquatic.common.argument.ObjectArguments
import gg.aquatic.crates.crate.Crate
import gg.aquatic.crates.data.interaction.CrateClickMappingData
import gg.aquatic.crates.limit.LimitHandle
import gg.aquatic.crates.milestone.CrateMilestone
import gg.aquatic.crates.milestone.CrateMilestoneManager
import gg.aquatic.crates.open.OpenConditions
import gg.aquatic.crates.reward.Reward
import gg.aquatic.crates.reward.RewardAmountRange
import gg.aquatic.crates.reward.RewardRarity
import gg.aquatic.crates.reward.processor.BasicRewardProcessor
import gg.aquatic.crates.reward.provider.ResolvedRewardProvider
import gg.aquatic.crates.reward.provider.RewardProvider
import gg.aquatic.crates.stats.CrateStats
import gg.aquatic.crates.stats.CrateStatsDatabase
import gg.aquatic.crates.stats.CrateStatsTimeframe
import gg.aquatic.crates.stats.flushPendingOpeningsSync
import gg.aquatic.execute.Action
import gg.aquatic.execute.ActionHandle
import io.mockk.every
import io.mockk.mockk
import java.math.BigInteger
import java.util.UUID
import kotlin.system.measureNanoTime
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertTrue
import net.kyori.adventure.text.Component
import org.junit.jupiter.api.Tag
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Tag("benchmark")
class MassOpeningBenchmarkTest {
    @AfterTest
    fun tearDownStats() {
        CrateStats.shutdown()
    }

    @Test
    fun `benchmark weighted multi reward without stats`() {
        benchmarkScenario(
            name = "weighted_multi_reward_no_stats",
            benchmark = benchmarkCase(
                statsEnabled = false,
                crateLimits = emptyList(),
                rewards = weightedRewards(),
                milestoneManager = emptyMilestones(),
            )
        )
    }

    @Test
    fun `benchmark weighted multi reward with stats`() {
        benchmarkScenario(
            name = "weighted_multi_reward_stats",
            benchmark = benchmarkCase(
                statsEnabled = true,
                crateLimits = emptyList(),
                rewards = weightedRewards(),
                milestoneManager = emptyMilestones(),
            )
        )
    }

    @Test
    fun `benchmark weighted multi reward with reward limits`() {
        benchmarkScenario(
            name = "weighted_multi_reward_limits",
            benchmark = benchmarkCase(
                statsEnabled = true,
                crateLimits = emptyList(),
                rewards = weightedRewards(
                    limitAllTime = 900_000_000
                ),
                milestoneManager = emptyMilestones(),
            )
        )
    }

    @Test
    fun `benchmark weighted multi reward with milestones`() {
        val milestoneReward = fixedReward(id = "milestone", limits = emptyList(), chance = 7.5)
        val manager = CrateMilestoneManager(
            milestones = listOf(CrateMilestone(500_000_000, null, listOf(milestoneReward))),
            repeatableMilestones = listOf(CrateMilestone(10_000_000, null, listOf(milestoneReward)))
        )

        benchmarkScenario(
            name = "weighted_multi_reward_milestones",
            benchmark = benchmarkCase(
                statsEnabled = true,
                crateLimits = emptyList(),
                rewards = weightedRewards(),
                milestoneManager = manager,
            )
        )
    }

    @Test
    fun `benchmark weighted multi reward random count ranges without stats`() {
        benchmarkScenario(
            name = "weighted_multi_reward_random_count_no_stats",
            benchmark = benchmarkCase(
                statsEnabled = false,
                crateLimits = emptyList(),
                rewards = weightedRewards(),
                rewardCountRanges = listOf(
                    RewardAmountRange(min = 1, max = 1, chance = 4.0),
                    RewardAmountRange(min = 2, max = 2, chance = 2.0),
                    RewardAmountRange(min = 3, max = 3, chance = 1.0),
                ),
                milestoneManager = emptyMilestones(),
            )
        )
    }

    @Test
    fun `benchmark random multi reward counts and random amounts without stats`() {
        benchmarkScenario(
            name = "random_multi_reward_no_stats",
            benchmark = benchmarkCase(
                statsEnabled = false,
                crateLimits = emptyList(),
                rewards = randomAmountRewards(),
                rewardCountRanges = listOf(
                    RewardAmountRange(min = 1, max = 1, chance = 3.0),
                    RewardAmountRange(min = 2, max = 2, chance = 2.0),
                    RewardAmountRange(min = 3, max = 3, chance = 1.0),
                ),
                milestoneManager = emptyMilestones(),
            )
        )
    }

    @Test
    fun `benchmark random multi reward counts and random amounts with stats`() {
        benchmarkScenario(
            name = "random_multi_reward_stats",
            benchmark = benchmarkCase(
                statsEnabled = true,
                crateLimits = emptyList(),
                rewards = randomAmountRewards(),
                rewardCountRanges = listOf(
                    RewardAmountRange(min = 1, max = 1, chance = 3.0),
                    RewardAmountRange(min = 2, max = 2, chance = 2.0),
                    RewardAmountRange(min = 3, max = 3, chance = 1.0),
                ),
                milestoneManager = emptyMilestones(),
            )
        )
    }

    @Test
    fun `benchmark random multi reward counts and random amounts with reward limits`() {
        benchmarkScenario(
            name = "random_multi_reward_limits",
            benchmark = benchmarkCase(
                statsEnabled = true,
                crateLimits = emptyList(),
                rewards = randomAmountRewards(limitAllTime = 900_000_000),
                rewardCountRanges = listOf(
                    RewardAmountRange(min = 1, max = 1, chance = 3.0),
                    RewardAmountRange(min = 2, max = 2, chance = 2.0),
                    RewardAmountRange(min = 3, max = 3, chance = 1.0),
                ),
                milestoneManager = emptyMilestones(),
            )
        )
    }

    private fun benchmarkScenario(name: String, benchmark: suspend (Int) -> Unit) {
        listOf(100_000_000, 1_000_000_000).forEach { amount ->
            val elapsedNanos = measureNanoTime {
                kotlinx.coroutines.runBlocking {
                    benchmark(amount)
                }
            }
            val elapsedMillis = elapsedNanos / 1_000_000.0
            println("BENCHMARK $name amount=$amount elapsedMs=$elapsedMillis")
        }
    }

    private fun benchmarkCase(
        statsEnabled: Boolean,
        crateLimits: Collection<LimitHandle>,
        rewards: List<Reward>,
        rewardCountRanges: List<RewardAmountRange> = emptyList(),
        milestoneManager: CrateMilestoneManager,
    ): suspend (Int) -> Unit = { amount ->
        if (statsEnabled) {
            connectStats()
        } else {
            CrateStats.shutdown()
        }

        val player = mockPlayer()
        val crate = crate(
            crateId = "bench",
            rewards = rewards,
            rewardCountRanges = rewardCountRanges,
            crateLimits = crateLimits,
            milestoneManager = milestoneManager
        )
        val session = OpeningSession(player = player, crate = crate)

        val success = CrateOpeningService.executeOpening(session, crateHandle = null, amount = BigInteger.valueOf(amount.toLong()))
        if (statsEnabled) {
            CrateStats.flushPendingOpeningsSync()
        }

        assertTrue(success)
    }

    private fun connectStats() {
        CrateStats.shutdown()
        CrateStats.configuredEnabled = true
        CrateStats.statsDatabase = CrateStatsDatabase.connect(
            url = "jdbc:sqlite:file:${UUID.randomUUID()}?mode=memory&cache=shared",
            driver = "org.sqlite.JDBC",
            user = "",
            password = ""
        )
    }

    private fun mockPlayer(): Player = mockk {
        every { uniqueId } returns UUID.randomUUID()
    }

    private fun crate(
        crateId: String,
        rewards: List<Reward>,
        rewardCountRanges: List<RewardAmountRange>,
        crateLimits: Collection<LimitHandle>,
        milestoneManager: CrateMilestoneManager,
    ): Crate {
        val provider = object : RewardProvider {
            override fun allRewards(): Collection<Reward> = rewards

            override suspend fun resolve(player: Player): ResolvedRewardProvider {
                return ResolvedRewardProvider(
                    rewards = rewards,
                    rewardCountRanges = rewardCountRanges
                )
            }
        }

        return Crate(
            id = crateId,
            keyItemSupplier = { mockk(relaxed = true) },
            keyMustBeHeld = false,
            crateClickMapping = CrateClickMappingData(),
            keyClickMapping = CrateClickMappingData(),
            displayName = Component.text(crateId),
            hologramSupplier = { null },
            hologramYOffset = 0.0,
            priceGroupsSupplier = { emptyList() },
            openConditionsSupplier = { OpenConditions.DUMMY },
            interactables = emptyList(),
            disableOpenStats = false,
            limits = crateLimits,
            milestoneManagerSupplier = { milestoneManager },
            rewardProviderSupplier = { provider },
            rewardProcessorSupplier = { BasicRewardProcessor(null) },
            previewSupplier = { null },
        )
    }

    private fun fixedReward(
        id: String,
        limits: Collection<LimitHandle>,
        chance: Double = 1.0,
    ): Reward {
        val noOpAction = ActionHandle(NoOpPlayerAction, ObjectArguments(emptyMap()))
        return Reward(
            id = id,
            crateId = "bench",
            displayName = Component.text(id),
            previewItem = { ItemStack(Material.STONE) },
            fallbackItem = null,
            winActions = emptyList(),
            massWinActions = listOf(noOpAction),
            conditions = emptyList(),
            purchaseManager = null,
            amountRanges = listOf(RewardAmountRange(min = 1, max = 1, chance = 1.0)),
            clickHandler = { _, _, _ -> },
            rarity = RewardRarity(id = "common", displayName = Component.text("Common"), chance = 1.0),
            limits = limits,
            chance = chance
        )
    }

    private fun weightedRewards(limitAllTime: Int? = null): List<Reward> {
        val limits = limitAllTime?.let { listOf(LimitHandle(CrateStatsTimeframe.ALL_TIME, it)) } ?: emptyList()
        return listOf(
            fixedReward(id = "common", limits = limits, chance = 60.0),
            fixedReward(id = "uncommon", limits = limits, chance = 25.0),
            fixedReward(id = "rare", limits = limits, chance = 10.0),
            fixedReward(id = "legendary", limits = limits, chance = 5.0),
        )
    }

    private fun randomAmountRewards(limitAllTime: Int? = null): List<Reward> {
        val limits = limitAllTime?.let { listOf(LimitHandle(CrateStatsTimeframe.ALL_TIME, it)) } ?: emptyList()
        return listOf(
            rewardWithAmountRanges(id = "common", chance = 55.0, ranges = listOf(1 to 3, 4 to 6), limits = limits),
            rewardWithAmountRanges(id = "uncommon", chance = 25.0, ranges = listOf(1 to 2, 3 to 5), limits = limits),
            rewardWithAmountRanges(id = "rare", chance = 15.0, ranges = listOf(1 to 1, 2 to 4), limits = limits),
            rewardWithAmountRanges(id = "legendary", chance = 5.0, ranges = listOf(2 to 3, 4 to 8), limits = limits),
        )
    }

    private fun rewardWithAmountRanges(
        id: String,
        chance: Double,
        ranges: List<Pair<Int, Int>>,
        limits: Collection<LimitHandle>,
    ): Reward {
        val noOpAction = ActionHandle(NoOpPlayerAction, ObjectArguments(emptyMap()))
        return Reward(
            id = id,
            crateId = "bench",
            displayName = Component.text(id),
            previewItem = { ItemStack(Material.STONE) },
            fallbackItem = null,
            winActions = emptyList(),
            massWinActions = listOf(noOpAction),
            conditions = emptyList(),
            purchaseManager = null,
            amountRanges = ranges.mapIndexed { index, range ->
                RewardAmountRange(min = range.first, max = range.second, chance = (ranges.size - index).toDouble())
            },
            clickHandler = { _, _, _ -> },
            rarity = RewardRarity(id = "common", displayName = Component.text("Common"), chance = 1.0),
            limits = limits,
            chance = chance
        )
    }

    private fun emptyMilestones(): CrateMilestoneManager {
        return CrateMilestoneManager(
            milestones = emptyList(),
            repeatableMilestones = emptyList()
        )
    }

    private object NoOpPlayerAction : Action<Player> {
        override val binder: Class<out Player> = Player::class.java
        override val arguments: List<ObjectArgument<*>> = emptyList()

        override suspend fun execute(binder: Player, args: ArgumentContext<Player>) {
        }
    }
}
