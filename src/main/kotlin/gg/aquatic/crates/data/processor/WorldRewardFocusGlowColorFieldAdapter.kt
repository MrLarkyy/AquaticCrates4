package gg.aquatic.crates.data.processor

import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.rewardshowcase.GlowColor
import gg.aquatic.crates.data.rewardshowcase.editor.GlowColorSelectionMenu
import gg.aquatic.kmenu.inventory.ButtonType
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.meta.EditorFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.FieldEditResult
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object WorldRewardFocusGlowColorFieldAdapter : EditorFieldAdapter {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val current = GlowColor.of(context.value.stringContentOrNull) ?: GlowColor.WHITE
        return stackedItem(current.material) {
            displayName = text(context.label, NamedTextColor.AQUA)
            lore += text("Current Color: ${current.displayName}", NamedTextColor.GRAY)
            lore += text("Left Click: Select glow color", NamedTextColor.GREEN)
        }.getItem()
    }

    override suspend fun edit(player: Player, context: EditorFieldContext, buttonType: ButtonType): FieldEditResult {
        if (buttonType != ButtonType.LEFT) return FieldEditResult.NoChange
        return when (val selected = GlowColorSelectionMenu.select(player, context.value.stringContentOrNull)) {
            is GlowColorSelectionMenu.SelectionResult.Selected ->
                FieldEditResult.Updated(gg.aquatic.crates.data.editor.core.yamlScalar(selected.colorId))
            else -> FieldEditResult.NoChange
        }
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color).decoration(TextDecoration.ITALIC, false)
    }
}
