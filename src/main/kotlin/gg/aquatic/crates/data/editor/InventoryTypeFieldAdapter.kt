package gg.aquatic.crates.data.editor

import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.editor.core.yamlScalar
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.meta.EditorFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.FieldEditResult
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object InventoryTypeFieldAdapter : EditorFieldAdapter {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        return stackedItem(Material.CHEST) {
            displayName = text(context.label, NamedTextColor.AQUA)
            if (context.description.isNotEmpty()) {
                lore += text("Description", NamedTextColor.DARK_AQUA)
                lore += context.description.map { text(it, NamedTextColor.GRAY) }
            }
            lore += text("Selected: ${currentValue(context)}", NamedTextColor.WHITE)
            lore += text("Click to choose a registered UI layout.", NamedTextColor.GRAY)
        }.getItem()
    }

    override suspend fun edit(player: Player, context: EditorFieldContext): FieldEditResult {
        return when (val result = InventoryTypeSelectionMenu.select(player, currentValue(context))) {
            InventoryTypeSelectionMenu.SelectionResult.Cancelled -> FieldEditResult.NoChange
            is InventoryTypeSelectionMenu.SelectionResult.Selected -> FieldEditResult.Updated(yamlScalar(result.inventoryType))
            InventoryTypeSelectionMenu.SelectionResult.NextPage,
            InventoryTypeSelectionMenu.SelectionResult.PreviousPage -> FieldEditResult.NoChange
        }
    }

    private fun currentValue(context: EditorFieldContext): String {
        return context.value.stringContentOrNull ?: ""
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }
}
