package gg.aquatic.crates.reward.processor

import gg.aquatic.crates.crate.Crate
import gg.aquatic.crates.crate.CrateHandle
import gg.aquatic.crates.crate.opening.worldchoose.WorldChooseRuntime
import gg.aquatic.crates.data.processor.WorldRewardDisplaySettingsData
import gg.aquatic.crates.reward.RewardAmountRange
import gg.aquatic.crates.reward.provider.ResolvedRewardProvider
import gg.aquatic.crates.util.randomItem
import gg.aquatic.execute.ActionHandle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class WorldChooseRewardProcessor(
    internal val chooseCountRanges: Collection<RewardAmountRange>,
    internal val uniqueRewards: Boolean,
    private val hiddenRewards: Boolean,
    private val onStartActions: Collection<ActionHandle<Player>>,
    private val onSwitchActions: Collection<ActionHandle<Player>>,
    private val onChooseActions: Collection<ActionHandle<Player>>,
    private val onEndActions: Collection<ActionHandle<Player>>,
    private val hiddenItem: ItemStack?,
    private val display: WorldRewardDisplaySettingsData,
) : RewardProcessor {
    override suspend fun process(
        player: Player,
        crate: Crate,
        crateHandle: CrateHandle?,
        provider: ResolvedRewardProvider,
    ): List<RolledReward> {
        val offerCount = if (provider.rewardCountRanges.isEmpty()) 1 else provider.rewardCountRanges.randomItem().roll()
        val offeredRewards = rewardRolls(player, provider, countOverride = offerCount, unique = uniqueRewards)
            .take(MAX_OFFERED_REWARDS)
        if (offeredRewards.isEmpty()) {
            return emptyList()
        }

        val chooseCount = (if (chooseCountRanges.isEmpty()) 1 else chooseCountRanges.randomItem().roll())
            .coerceAtLeast(1)
            .coerceAtMost(offeredRewards.size)

        return WorldChooseRuntime.execute(
            player = player,
            crate = crate,
            crateHandle = crateHandle,
            offeredRewards = offeredRewards,
            chooseCount = chooseCount,
            hiddenRewards = hiddenRewards,
            hiddenItem = hiddenItem,
            onStartActions = onStartActions,
            onSwitchActions = onSwitchActions,
            onChooseActions = onChooseActions,
            onEndActions = onEndActions,
            display = display,
        )
    }

    companion object {
        private const val MAX_OFFERED_REWARDS = 7
    }
}
