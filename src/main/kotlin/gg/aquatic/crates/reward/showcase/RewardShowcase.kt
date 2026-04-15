package gg.aquatic.crates.reward.showcase

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface RewardShowcase {
    fun previewItem(): ItemStack
    fun displayNameYOffset(): Double

    fun createHandle(
        player: Player,
        location: Location,
        focusStyle: RewardShowcaseFocusStyle,
    ): RewardShowcaseHandle
}
