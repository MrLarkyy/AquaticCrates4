package gg.aquatic.crates.data.editor.core

import gg.aquatic.kmenu.inventory.ButtonType
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

abstract class SwitchingSectionFieldAdapter(
    private val sectionName: String,
    private val iconMaterial: Material,
    private val defaultType: String,
    private val editHint: String,
    private val changeHint: String,
) : EditorFieldAdapter {

    protected abstract fun currentType(context: EditorFieldContext): String?
    protected abstract suspend fun selectType(player: Player): String?
    protected abstract fun updateType(context: EditorFieldContext, selected: String): FieldEditResult

    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val currentType = currentType(context) ?: defaultType
        return stackedItem(iconMaterial) {
            displayName = text(sectionName, NamedTextColor.AQUA)
            if (context.description.isNotEmpty()) {
                lore += text("Description", NamedTextColor.DARK_AQUA)
                lore += context.description.map { text(it, NamedTextColor.GRAY) }
            }
            lore += text("Current Type: $currentType", NamedTextColor.WHITE)
            lore += text("Left Click: $editHint", NamedTextColor.GREEN)
            lore += text("Right Click: $changeHint", NamedTextColor.YELLOW)
        }.getItem()
    }

    override suspend fun edit(player: Player, context: EditorFieldContext, buttonType: ButtonType): FieldEditResult {
        return when (buttonType) {
            ButtonType.LEFT -> FieldEditResult.PassThrough
            ButtonType.RIGHT -> {
                val selected = selectType(player) ?: return FieldEditResult.NoChange
                updateType(context, selected)
            }
            else -> FieldEditResult.NoChange
        }
    }

    protected fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }
}
