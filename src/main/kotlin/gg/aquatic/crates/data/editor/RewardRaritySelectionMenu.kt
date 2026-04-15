package gg.aquatic.crates.data.editor

import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.editor.menu.PagedSelectionMenu
import gg.aquatic.kmenu.inventory.InventoryType
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

object RewardRaritySelectionMenu {

    sealed interface SelectionResult {
        data object Cancelled : SelectionResult
        data class Selected(val rarityId: String) : SelectionResult
        data object PreviousPage : SelectionResult
        data object NextPage : SelectionResult
    }

    private data class RarityOption(
        val id: String,
        val displayName: String,
        val chance: String?,
    )

    suspend fun select(player: Player, context: EditorFieldContext, currentValue: String?): SelectionResult {
        val rarities = parseRarities(context)
        return when (val result = PagedSelectionMenu.select(
            player = player,
            title = "Select Reward Rarity",
            options = rarities,
            currentValue = rarities.firstOrNull { it.id == currentValue },
            inventoryType = InventoryType.GENERIC9X6,
            buildEntry = { option, current -> buildEntry(option, current?.id) },
            cancelDescription = "Keep the current rarity",
            navigationDescription = "Open another page of rarities"
        )) {
            PagedSelectionMenu.Result.Cancelled -> SelectionResult.Cancelled
            is PagedSelectionMenu.Result.Selected -> SelectionResult.Selected(result.value.id)
            PagedSelectionMenu.Result.PreviousPage -> SelectionResult.PreviousPage
            PagedSelectionMenu.Result.NextPage -> SelectionResult.NextPage
        }
    }

    private fun parseRarities(context: EditorFieldContext): List<RarityOption> {
        val root = context.root as? com.charleskorn.kaml.YamlMap ?: return emptyList()
        val rarityRoot = root.get<com.charleskorn.kaml.YamlNode>("rarities") as? com.charleskorn.kaml.YamlMap ?: return emptyList()
        return rarityRoot.entries.entries.sortedBy { it.key.content }.map { (idNode, node) ->
            val id = idNode.content
            val objectNode = node as? com.charleskorn.kaml.YamlMap
            val displayName = objectNode?.get<com.charleskorn.kaml.YamlNode>("displayName")?.stringContentOrNull
                ?: objectNode?.get<com.charleskorn.kaml.YamlNode>("display-name")?.stringContentOrNull
                ?: id
            val chance = objectNode?.get<com.charleskorn.kaml.YamlNode>("chance")?.stringContentOrNull
            RarityOption(id = id, displayName = displayName, chance = chance)
        }
    }

    private fun buildEntry(rarity: RarityOption, currentValue: String?) = stackedItem(Material.NETHER_STAR) {
        displayName = text(rarity.id, NamedTextColor.AQUA)
        lore += text("Display: ${rarity.displayName}", NamedTextColor.GRAY)
        rarity.chance?.let { lore += text("Weight: $it", NamedTextColor.GRAY) }
        if (currentValue == rarity.id) {
            lore += text(" ", NamedTextColor.DARK_GRAY)
            lore += text("Currently selected", NamedTextColor.GREEN)
        }
    }.getItem()

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }
}
