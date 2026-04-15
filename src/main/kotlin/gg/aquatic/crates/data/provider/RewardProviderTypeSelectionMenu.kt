package gg.aquatic.crates.data.provider

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicSelectionMenu
import gg.aquatic.kmenu.inventory.InventoryType
import org.bukkit.Material
import org.bukkit.entity.Player

object RewardProviderTypeSelectionMenu {
    suspend fun select(player: Player): String? {
        return PolymorphicSelectionMenu.selectType(
            player = player,
            title = "Select Reward Provider",
            inventoryType = InventoryType.GENERIC9X3,
            entrySlots = listOf(11, 15),
            cancelSlot = 22,
            definitions = listOf(
                PolymorphicSelectionMenu.Definition(
                    id = RewardProviderType.SIMPLE.id,
                    displayName = "Simple",
                    description = listOf(
                        "Uses one standard reward table.",
                        "This matches the classic crate setup."
                    ),
                    icon = Material.CHEST
                ),
                PolymorphicSelectionMenu.Definition(
                    id = RewardProviderType.CONDITIONAL_POOLS.id,
                    displayName = "Conditional Pools",
                    description = listOf(
                        "Lets you define multiple reward pools.",
                        "Each pool can have its own conditions and rewards."
                    ),
                    icon = Material.BOOKSHELF
                )
            )
        )
    }
}
