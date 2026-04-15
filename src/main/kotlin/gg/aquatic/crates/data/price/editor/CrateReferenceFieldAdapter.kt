package gg.aquatic.crates.data.price.editor

import gg.aquatic.crates.data.price.*

import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.editor.core.yamlNull
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

object CrateReferenceFieldAdapter : EditorFieldAdapter {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val rawValue = context.value.stringContentOrNull.orEmpty()
        val selected = rawValue.takeIf { it.isNotBlank() && it != "null" } ?: "Current crate"

        return stackedItem(Material.CHEST) {
            displayName = text(context.label, NamedTextColor.AQUA)
            if (context.description.isNotEmpty()) {
                lore += text("Description", NamedTextColor.DARK_AQUA)
                lore += context.description.map { text(it, NamedTextColor.GRAY) }
            }
            lore += text("Selected: $selected", NamedTextColor.WHITE)
            lore += text("Click to choose from registered crates.", NamedTextColor.GRAY)
        }.getItem()
    }

    override suspend fun edit(player: Player, context: EditorFieldContext): FieldEditResult {
        return when (val result = CrateReferenceSelectionMenu.select(player, currentValue(context))) {
            CrateReferenceSelectionMenu.SelectionResult.Cancelled -> FieldEditResult.NoChange
            is CrateReferenceSelectionMenu.SelectionResult.Selected -> {
                val value = result.crateId?.let(::yamlScalar) ?: yamlNull()
                FieldEditResult.Updated(value)
            }
            CrateReferenceSelectionMenu.SelectionResult.NextPage,
            CrateReferenceSelectionMenu.SelectionResult.PreviousPage -> FieldEditResult.NoChange
        }
    }

    private fun currentValue(context: EditorFieldContext): String? {
        val rawValue = context.value.stringContentOrNull.orEmpty()
        return rawValue.takeIf { it.isNotBlank() && it != "null" }
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }
}


