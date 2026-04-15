package gg.aquatic.crates.data.rewardshowcase.editor

import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.editor.core.yamlScalar
import gg.aquatic.crates.data.rewardshowcase.GlowColor
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.meta.EditorFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.FieldEditResult
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object GlowColorFieldAdapter : EditorFieldAdapter {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val current = GlowColor.of(context.value.stringContentOrNull) ?: GlowColor.WHITE
        return stackedItem(current.material) {
            displayName = text(context.label, NamedTextColor.AQUA)
            if (context.description.isNotEmpty()) {
                lore += text("Description", NamedTextColor.DARK_AQUA)
                lore += context.description.map { text(it, NamedTextColor.GRAY) }
            }
            lore += text("Selected: ${current.displayName}", NamedTextColor.WHITE)
            lore += text("Click to choose a supported MC glow color.", NamedTextColor.GRAY)
        }.getItem()
    }

    override suspend fun edit(player: Player, context: EditorFieldContext): FieldEditResult {
        return when (val result = GlowColorSelectionMenu.select(player, context.value.stringContentOrNull ?: GlowColor.WHITE.id)) {
            GlowColorSelectionMenu.SelectionResult.Cancelled -> FieldEditResult.NoChange
            is GlowColorSelectionMenu.SelectionResult.Selected -> FieldEditResult.Updated(yamlScalar(result.colorId))
            GlowColorSelectionMenu.SelectionResult.NextPage,
            GlowColorSelectionMenu.SelectionResult.PreviousPage -> FieldEditResult.NoChange
        }
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color).decoration(TextDecoration.ITALIC, false)
    }
}
