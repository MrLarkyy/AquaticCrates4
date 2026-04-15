package gg.aquatic.crates.reward.processor

import gg.aquatic.crates.crate.Crate
import gg.aquatic.crates.crate.CrateHandle
import gg.aquatic.crates.data.processor.RewardDisplayMenuSettings
import gg.aquatic.crates.reward.provider.ResolvedRewardProvider
import org.bukkit.entity.Player

class BasicRewardProcessor(
    private val resultMenu: RewardDisplayMenuSettings?,
) : RewardProcessor {
    override suspend fun process(
        player: Player,
        crate: Crate,
        crateHandle: CrateHandle?,
        provider: ResolvedRewardProvider,
    ): List<RolledReward> {
        val rolledRewards = rewardRolls(player, provider)
        if (rolledRewards.isEmpty()) {
            return emptyList()
        }

        val grantedRewards = buildList {
            for (rolled in rolledRewards) {
                if (rolled.reward.tryWin(player, rolled.amount)) {
                    add(rolled)
                }
            }
        }

        resultMenu?.let { menuSettings ->
            RewardShowcaseMenu.create(player, rolledRewards, menuSettings).open()
        }

        return grantedRewards
    }
}
