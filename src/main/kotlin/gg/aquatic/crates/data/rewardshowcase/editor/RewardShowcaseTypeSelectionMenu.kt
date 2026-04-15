package gg.aquatic.crates.data.rewardshowcase.editor

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicSelectionMenu
import gg.aquatic.crates.data.rewardshowcase.RewardShowcaseType
import gg.aquatic.kmenu.inventory.InventoryType
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

object RewardShowcaseTypeSelectionMenu {
    suspend fun select(player: Player): String? {
        return PolymorphicSelectionMenu.selectType(
            player = player,
            title = "Select Reward Showcase",
            inventoryType = InventoryType.GENERIC9X3,
            entrySlots = listOf(10, 13, 16),
            cancelSlot = 22,
            definitions = listOf(
                PolymorphicSelectionMenu.Definition(
                    id = RewardShowcaseType.ITEM_DISPLAY.id,
                    displayName = "Item Display",
                    description = listOf(
                        "Shows the reward as an item display in world-based presentations.",
                        "Uses the reward preview item as the rendered showcase."
                    ),
                    icon = Material.ITEM_FRAME
                ),
                PolymorphicSelectionMenu.Definition(
                    id = RewardShowcaseType.MODEL_ENGINE.id,
                    displayName = "ModelEngine",
                    description = listOf(
                        "Shows the reward as a ModelEngine model in world-based presentations."
                    ),
                    icon = Material.ARMOR_STAND,
                    availability = {
                        if (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) {
                            PolymorphicSelectionMenu.Availability(true)
                        } else {
                            PolymorphicSelectionMenu.Availability(
                                available = false,
                                lockedDescription = listOf("Requires ModelEngine on the server"),
                                deniedMessage = "ModelEngine is not installed, so this reward showcase type is unavailable."
                            )
                        }
                    }
                ),
                PolymorphicSelectionMenu.Definition(
                    id = RewardShowcaseType.BETTER_MODEL.id,
                    displayName = "BetterModel",
                    description = listOf(
                        "Shows the reward as a BetterModel model in world-based presentations."
                    ),
                    icon = Material.GLOW_ITEM_FRAME,
                    availability = {
                        if (Bukkit.getPluginManager().getPlugin("BetterModel") != null) {
                            PolymorphicSelectionMenu.Availability(true)
                        } else {
                            PolymorphicSelectionMenu.Availability(
                                available = false,
                                lockedDescription = listOf("Requires BetterModel on the server"),
                                deniedMessage = "BetterModel is not installed, so this reward showcase type is unavailable."
                            )
                        }
                    }
                )
            )
        )
    }
}
