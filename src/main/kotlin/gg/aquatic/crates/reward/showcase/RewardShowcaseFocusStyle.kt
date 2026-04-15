package gg.aquatic.crates.reward.showcase

import gg.aquatic.crates.data.rewardshowcase.GlowColor

data class RewardShowcaseFocusStyle(
    val scaleMultiplier: Double = 1.0,
    val yOffset: Double = 0.0,
    val glowing: Boolean = false,
    val glowColor: GlowColor = GlowColor.WHITE,
) {
    companion object {
        val NONE = RewardShowcaseFocusStyle()
    }
}
