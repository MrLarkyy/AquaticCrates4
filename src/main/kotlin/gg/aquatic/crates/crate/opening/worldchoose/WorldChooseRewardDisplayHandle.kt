package gg.aquatic.crates.crate.opening.worldchoose

import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.crates.data.CrateHologramData
import gg.aquatic.crates.data.hologram.RewardHologramEntry
import gg.aquatic.crates.reward.processor.RolledReward
import gg.aquatic.crates.reward.showcase.RewardShowcase
import gg.aquatic.crates.reward.showcase.RewardShowcaseFocusStyle
import gg.aquatic.crates.reward.showcase.RewardShowcaseHandle
import gg.aquatic.kholograms.Hologram
import gg.aquatic.replace.Placeholder
import gg.aquatic.replace.PlaceholderContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Location
import org.bukkit.entity.Player

class WorldChooseRewardDisplayHandle(
    private val showcaseHandle: RewardShowcaseHandle,
    private val hologram: Hologram?,
    private val hologramYOffset: Double,
) : RewardShowcaseHandle {

    override fun setFocusStyle(focusStyle: RewardShowcaseFocusStyle) {
        showcaseHandle.setFocusStyle(focusStyle)
    }

    override fun move(location: Location) {
        showcaseHandle.move(location)
        hologram?.let { active ->
            VirtualsCtx {
                active.teleport(location.clone().add(0.0, hologramYOffset, 0.0))
            }
        }
    }

    override fun destroy() {
        showcaseHandle.destroy()
        hologram?.let { active ->
            VirtualsCtx {
                active.destroy()
            }
        }
    }

    companion object {
        fun createRewardHologram(
            player: Player,
            rewardHologram: CrateHologramData,
            rewardHologramYOffset: Double,
            showcaseLocation: Location,
            rolledReward: RolledReward,
            rewardShowcase: RewardShowcase,
            rewardDisplayName: Component,
        ): Hologram? {
            val settings = rewardHologram.toSettings(
                listOf(
                    RewardHologramEntry(
                        showcase = rewardShowcase,
                        displayName = rewardDisplayName,
                    )
                )
            ) ?: return null

            val location = showcaseLocation.clone().add(0.0, rewardHologramYOffset + rewardHologram.yOffset, 0.0)
            return settings.create(
                location = location,
                placeholderContext = { rewardPlaceholderContext(rolledReward) },
                filter = { it.uniqueId == player.uniqueId },
            )
        }

        private fun rewardPlaceholderContext(rolledReward: RolledReward): PlaceholderContext<Player> {
            val reward = rolledReward.reward
            val amount = rolledReward.amount.toString()
            val plainDisplayName = PlainTextComponentSerializer.plainText().serialize(reward.displayName)
            val plainRarityName = PlainTextComponentSerializer.plainText().serialize(reward.rarity.displayName)

            return PlaceholderContext(
                PlaceholderContext.player.placeholders.values,
                PlaceholderContext.player.maxUpdateInterval
            ).apply {
                addPlaceholders(
                    Placeholder.Literal("crate_id", true) { _, _ -> reward.crateId },
                    Placeholder.Literal("crate-id", true) { _, _ -> reward.crateId },
                    Placeholder.Literal("reward_id", true) { _, _ -> reward.id },
                    Placeholder.Literal("reward-id", true) { _, _ -> reward.id },
                    Placeholder.Literal("reward_name", false) { player, _ -> reward.updatePlaceholders(plainDisplayName, player, rolledReward.amount) },
                    Placeholder.Literal("reward-name", false) { player, _ -> reward.updatePlaceholders(plainDisplayName, player, rolledReward.amount) },
                    Placeholder.Literal("reward_rarity_id", true) { _, _ -> reward.rarity.id },
                    Placeholder.Literal("reward-rarity-id", true) { _, _ -> reward.rarity.id },
                    Placeholder.Literal("reward_rarity_name", true) { _, _ -> plainRarityName },
                    Placeholder.Literal("reward-rarity-name", true) { _, _ -> plainRarityName },
                    Placeholder.Literal("amount", true) { _, _ -> amount },
                    Placeholder.Literal("random_amount", true) { _, _ -> amount },
                    Placeholder.Literal("random-amount", true) { _, _ -> amount },
                    Placeholder.Literal("reward_total_amount", true) { _, _ -> amount },
                    Placeholder.Literal("reward-total-amount", true) { _, _ -> amount },
                )
            }
        }
    }
}
