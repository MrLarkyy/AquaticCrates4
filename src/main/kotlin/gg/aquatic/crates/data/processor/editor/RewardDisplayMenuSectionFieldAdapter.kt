package gg.aquatic.crates.data.processor.editor

import gg.aquatic.crates.data.processor.*

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.CrateDataFormats
import gg.aquatic.crates.data.editor.core.ValueSectionFieldAdapter
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.core.yamlNull
import gg.aquatic.kmenu.inventory.ButtonType
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.FieldEditResult
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object RewardDisplayMenuSectionFieldAdapter : ValueSectionFieldAdapter<RewardDisplayMenuData>(
    serializer = RewardDisplayMenuData.serializer(),
    yaml = CrateDataFormats.yaml,
    schema = RewardDisplayMenuEditorSchema,
    title = Component.text("Result Menu")
) {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val menu = decodeMenu(context)
        return stackedItem(Material.CHEST) {
            displayName = text("Result Menu", NamedTextColor.AQUA)
            if (context.description.isNotEmpty()) {
                lore += text("Description", NamedTextColor.DARK_AQUA)
                lore += context.description.map { text(it, NamedTextColor.GRAY) }
            }
            lore += text("Enabled: ${if (menu != null) "yes" else "no"}", NamedTextColor.WHITE)
            lore += text("Reward Slots: ${menu?.rewardSlots?.size ?: 0}", NamedTextColor.WHITE)
            lore += text("Custom Buttons: ${menu?.customButtons?.size ?: 0}", NamedTextColor.WHITE)
            lore += text("Left Click: Edit result menu", NamedTextColor.GREEN)
            lore += text("Right Click: ${if (menu != null) "Disable" else "Enable"} result menu", NamedTextColor.YELLOW)
        }.getItem()
    }

    override suspend fun edit(player: Player, context: EditorFieldContext, buttonType: ButtonType): FieldEditResult {
        return when (buttonType) {
            ButtonType.LEFT -> editSectionValue(player, context)
            ButtonType.RIGHT -> {
                val menu = decodeMenu(context)
                FieldEditResult.Updated(if (menu == null) RewardDisplayMenuData().toYamlNode() else yamlNull())
            }

            else -> FieldEditResult.NoChange
        }
    }

    override fun loadSection(context: EditorFieldContext): RewardDisplayMenuData? = decodeMenu(context)

    override fun defaultSectionValue(): RewardDisplayMenuData = RewardDisplayMenuData()

    override fun updateValue(edited: RewardDisplayMenuData): YamlNode = edited.toYamlNode()

    private fun decodeMenu(context: EditorFieldContext): RewardDisplayMenuData? {
        return runCatching {
            CrateDataFormats.yaml.decodeFromYamlNode(RewardDisplayMenuData.serializer(), context.value)
        }.getOrNull()
    }

    private fun RewardDisplayMenuData.toYamlNode(): YamlNode {
        return CrateDataFormats.yaml.encodeToNode(RewardDisplayMenuData.serializer(), this)
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }
}


