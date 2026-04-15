package gg.aquatic.crates.reward.processor

import gg.aquatic.crates.crate.Crate
import gg.aquatic.crates.crate.CrateHandle
import gg.aquatic.crates.reward.provider.ResolvedRewardProvider
import gg.aquatic.crates.util.randomItem
import gg.aquatic.crates.util.randomItemIndex
import org.bukkit.entity.Player

interface RewardProcessor {
    suspend fun process(
        player: Player,
        crate: Crate,
        crateHandle: CrateHandle?,
        provider: ResolvedRewardProvider,
    ): List<RolledReward>

    suspend fun rewardRolls(
        player: Player,
        provider: ResolvedRewardProvider,
        countOverride: Int? = null,
        unique: Boolean = false,
    ): List<RolledReward> {
        val availableRewards = provider.rewards.filter { it.canWin(player) }
        if (availableRewards.isEmpty()) {
            return emptyList()
        }

        val targetCount = (countOverride ?: if (provider.rewardCountRanges.isEmpty()) 1 else provider.rewardCountRanges.randomItem().roll())
            .coerceAtLeast(1)
        val rolledRewards = ArrayList<RolledReward>(targetCount)

        if (unique) {
            val pool = availableRewards.toMutableList()
            repeat(targetCount.coerceAtMost(pool.size)) {
                if (pool.isEmpty()) return@repeat
                val picked = pool.randomItemIndex()
                val reward = pool.removeAt(picked)
                rolledRewards += RolledReward(reward, reward.rollAmount())
            }
            return rolledRewards
        }

        repeat(targetCount) {
            val picked = availableRewards.randomItem()
            rolledRewards += RolledReward(picked, picked.rollAmount())
        }

        return rolledRewards
    }
}
