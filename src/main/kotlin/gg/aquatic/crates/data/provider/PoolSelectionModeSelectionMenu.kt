package gg.aquatic.crates.data.provider

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicSelectionMenu
import gg.aquatic.kmenu.inventory.InventoryType
import org.bukkit.Material
import org.bukkit.entity.Player

object PoolSelectionModeSelectionMenu {
    suspend fun select(player: Player): String? {
        return PolymorphicSelectionMenu.selectType(
            player = player,
            title = "Select Pool Mode",
            inventoryType = InventoryType.GENERIC9X3,
            entrySlots = listOf(11, 15),
            cancelSlot = 22,
            definitions = listOf(
                PolymorphicSelectionMenu.Definition(
                    id = PoolSelectionMode.FIRST_MATCH.id,
                    displayName = "First Match",
                    description = listOf(
                        "Uses the first pool whose conditions pass.",
                        "Good when pools are ordered by priority."
                    ),
                    icon = Material.TARGET
                ),
                PolymorphicSelectionMenu.Definition(
                    id = PoolSelectionMode.MERGE_ALL.id,
                    displayName = "Merge All",
                    description = listOf(
                        "Combines rewards from all matching pools.",
                        "Good for layered bonus pools."
                    ),
                    icon = Material.CRAFTER
                )
            )
        )
    }
}
