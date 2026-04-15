package gg.aquatic.crates.data.milestone

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.CrateData
import gg.aquatic.crates.data.CrateDataFormats
import gg.aquatic.crates.data.MilestoneData
import gg.aquatic.crates.data.editor.core.RootSectionFieldAdapter
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.core.withMapValue
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import kotlinx.serialization.builtins.ListSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object MilestoneSettingsSectionFieldAdapter : RootSectionFieldAdapter<MilestoneSettingsData>(
    serializer = MilestoneSettingsData.serializer(),
    yaml = CrateDataFormats.yaml,
    schema = MilestoneSettingsEditorSchema,
    title = Component.text("Milestones")
) {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val settings = decodeSettings(context)
        return stackedItem(Material.DIAMOND) {
            displayName = text("Milestones", NamedTextColor.AQUA)
            if (context.description.isNotEmpty()) {
                lore += text("Description", NamedTextColor.DARK_AQUA)
                lore += context.description.map { text(it, NamedTextColor.GRAY) }
            }
            lore += text("Milestones: ${settings?.milestones?.size ?: 0}", NamedTextColor.WHITE)
            lore += text("Repeatable: ${settings?.repeatableMilestones?.size ?: 0}", NamedTextColor.WHITE)
            lore += text("Left Click: Edit milestone settings", NamedTextColor.GREEN)
        }.getItem()
    }

    override fun loadSection(context: EditorFieldContext): MilestoneSettingsData? = decodeSettings(context)

    override fun defaultSectionValue(): MilestoneSettingsData = MilestoneSettingsData()

    private fun decodeSettings(context: EditorFieldContext): MilestoneSettingsData? {
        val crateData = runCatching {
            CrateDataFormats.yaml.decodeFromYamlNode(CrateData.serializer(), context.root)
        }.getOrNull() ?: return null
        return MilestoneSettingsData.from(crateData)
    }

    override fun updateRoot(context: EditorFieldContext, root: YamlNode, edited: MilestoneSettingsData): YamlNode {
        return root
            .withMapValue(
                "milestones",
                CrateDataFormats.yaml.encodeToNode(
                    ListSerializer(MilestoneData.serializer()),
                    edited.milestones
                )
            )
            .withMapValue(
                "repeatableMilestones",
                CrateDataFormats.yaml.encodeToNode(
                    ListSerializer(MilestoneData.serializer()),
                    edited.repeatableMilestones
                )
            )
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color).decoration(TextDecoration.ITALIC, false)
    }
}
