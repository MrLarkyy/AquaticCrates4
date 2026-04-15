package gg.aquatic.crates.data.processor.editor

import gg.aquatic.crates.data.processor.*

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicSelectionMenu
import gg.aquatic.kmenu.inventory.InventoryType
import org.bukkit.Material
import org.bukkit.entity.Player

object RewardProcessorTypeSelectionMenu {
    suspend fun select(player: Player): String? {
        return PolymorphicSelectionMenu.selectType(
            player = player,
            title = "Select Reward Processor",
            inventoryType = InventoryType.GENERIC9X3,
            entrySlots = listOf(10, 13, 16),
            cancelSlot = 22,
            definitions = listOf(
                PolymorphicSelectionMenu.Definition(
                    id = RewardProcessorType.BASIC.id,
                    displayName = "Basic",
                    description = listOf(
                        "Rolls rewards and executes them immediately.",
                        "Can optionally open a showcase menu after the win."
                    ),
                    icon = Material.CHEST
                ),
                PolymorphicSelectionMenu.Definition(
                    id = RewardProcessorType.CHOOSE.id,
                    displayName = "Choose",
                    description = listOf(
                        "Rolls reward offers and lets the player choose some of them.",
                        "Supports hidden rewards that reveal on click."
                    ),
                    icon = Material.HOPPER_MINECART
                ),
                PolymorphicSelectionMenu.Definition(
                    id = RewardProcessorType.WORLD_CHOOSE.id,
                    displayName = "World Choose",
                    description = listOf(
                        "Rolls reward offers and shows them around the crate in the world.",
                        "The player scrolls to focus and clicks to confirm choices."
                    ),
                    icon = Material.ENDER_EYE
                )
            )
        )
    }
}


