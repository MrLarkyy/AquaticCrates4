package gg.aquatic.crates.data.processor.editor

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.CrateDataFormats
import gg.aquatic.crates.data.CrateHologramData
import gg.aquatic.crates.data.editor.core.ValueSectionFieldAdapter
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.core.yamlNull
import gg.aquatic.crates.data.hologram.TextCrateHologramLineData
import gg.aquatic.crates.data.hologram.editor.HologramSettingsEditorSchema
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

object WorldChooseRewardHologramSectionFieldAdapter : ValueSectionFieldAdapter<CrateHologramData>(
    serializer = CrateHologramData.serializer(),
    yaml = CrateDataFormats.yaml,
    schema = HologramSettingsEditorSchema,
    title = Component.text("Reward Hologram")
) {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val hologram = decodeHologram(context)
        return stackedItem(Material.END_CRYSTAL) {
            displayName = text("Reward Hologram", NamedTextColor.AQUA)
            if (context.description.isNotEmpty()) {
                lore += text("Description", NamedTextColor.DARK_AQUA)
                lore += context.description.map { text(it, NamedTextColor.GRAY) }
            }
            lore += text("Enabled: ${if (hologram != null) "yes" else "no"}", NamedTextColor.WHITE)
            lore += text("Lines: ${hologram?.lines?.size ?: 0}", NamedTextColor.WHITE)
            lore += text("Left Click: Edit reward hologram", NamedTextColor.GREEN)
            lore += text("Right Click: ${if (hologram != null) "Disable" else "Enable"} reward hologram", NamedTextColor.YELLOW)
        }.getItem()
    }

    override suspend fun edit(player: Player, context: EditorFieldContext, buttonType: ButtonType): FieldEditResult {
        return when (buttonType) {
            ButtonType.LEFT -> editSectionValue(player, context)
            ButtonType.RIGHT -> {
                val hologram = decodeHologram(context)
                FieldEditResult.Updated(if (hologram == null) defaultSectionValue().toYamlNode() else yamlNull())
            }
            else -> FieldEditResult.NoChange
        }
    }

    override fun loadSection(context: EditorFieldContext): CrateHologramData? = decodeHologram(context)

    override fun defaultSectionValue(): CrateHologramData {
        return CrateHologramData(
            lines = listOf(
                TextCrateHologramLineData(
                    text = "<white>%reward-name%</white>"
                )
            )
        )
    }

    override fun updateValue(edited: CrateHologramData): YamlNode = edited.toYamlNode()

    private fun decodeHologram(context: EditorFieldContext): CrateHologramData? {
        return runCatching {
            CrateDataFormats.yaml.decodeFromYamlNode(CrateHologramData.serializer(), context.value)
        }.getOrNull()
    }

    private fun CrateHologramData.toYamlNode(): YamlNode {
        return CrateDataFormats.yaml.encodeToNode(CrateHologramData.serializer(), this)
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }
}
