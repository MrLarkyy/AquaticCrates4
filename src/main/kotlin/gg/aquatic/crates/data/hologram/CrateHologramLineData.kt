package gg.aquatic.crates.data.hologram

import gg.aquatic.kholograms.serialize.LineSettings
import gg.aquatic.crates.reward.showcase.RewardShowcase
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
abstract class CrateHologramLineData {
    abstract fun toSettings(rewardEntries: List<RewardHologramEntry> = emptyList()): List<LineSettings>
}

data class RewardHologramEntry(
    val showcase: RewardShowcase,
    val displayName: Component,
)
