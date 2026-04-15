package gg.aquatic.crates.data.interaction.editor

import gg.aquatic.crates.data.interaction.*

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.CrateData
import gg.aquatic.crates.data.CrateDataFormats
import gg.aquatic.crates.data.LimitData
import gg.aquatic.crates.data.condition.PlayerConditionData
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.core.RootSectionFieldAdapter
import gg.aquatic.crates.data.editor.core.withMapValue
import gg.aquatic.crates.data.editor.core.yamlScalar
import gg.aquatic.crates.data.interactable.CrateInteractableData
import gg.aquatic.crates.data.price.OpenPriceGroupData
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object InteractionSettingsSectionFieldAdapter : RootSectionFieldAdapter<InteractionSettingsData>(
    serializer = InteractionSettingsData.serializer(),
    yaml = CrateDataFormats.yaml,
    schema = InteractionSettingsEditorSchema,
    title = Component.text("Interaction Settings")
) {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val settings = decodeSettings(context)
        return stackedItem(Material.ARMOR_STAND) {
            displayName = text("Interaction Settings", NamedTextColor.AQUA)
            if (context.description.isNotEmpty()) {
                lore += text("Description", NamedTextColor.DARK_AQUA)
                lore += context.description.map { text(it, NamedTextColor.GRAY) }
            }
            lore += text("Interactables: ${settings?.interactables?.size ?: 0}", NamedTextColor.WHITE)
            lore += text("Open Conditions: ${settings?.openConditions?.size ?: 0}", NamedTextColor.WHITE)
            lore += text("Price Groups: ${settings?.priceGroups?.size ?: 0}", NamedTextColor.WHITE)
            lore += text("Limits: ${settings?.limits?.size ?: 0}", NamedTextColor.WHITE)
            lore += text("Left Click: Edit interaction settings", NamedTextColor.GREEN)
        }.getItem()
    }

    override fun loadSection(context: EditorFieldContext): InteractionSettingsData? = decodeSettings(context)

    override fun defaultSectionValue(): InteractionSettingsData = InteractionSettingsData()

    private fun decodeSettings(context: EditorFieldContext): InteractionSettingsData? {
        val crateData = runCatching {
            CrateDataFormats.yaml.decodeFromYamlNode(CrateData.serializer(), context.root)
        }.getOrNull() ?: return null
        return InteractionSettingsData.from(crateData)
    }

    override fun updateRoot(context: EditorFieldContext, root: YamlNode, edited: InteractionSettingsData): YamlNode {
        return root
            .withMapValue(
                "interactables",
                CrateDataFormats.yaml.encodeToNode(
                    kotlinx.serialization.builtins.ListSerializer(CrateInteractableData.serializer()),
                    edited.interactables
                )
            )
            .withMapValue(
                "openConditions",
                CrateDataFormats.yaml.encodeToNode(
                    kotlinx.serialization.builtins.ListSerializer(PlayerConditionData.serializer()),
                    edited.openConditions
                )
            )
            .withMapValue("disableOpenStats", yamlScalar(edited.disableOpenStats))
            .withMapValue(
                "limits",
                CrateDataFormats.yaml.encodeToNode(
                    kotlinx.serialization.builtins.ListSerializer(LimitData.serializer()),
                    edited.limits
                )
            )
            .withMapValue(
                "priceGroups",
                CrateDataFormats.yaml.encodeToNode(
                    kotlinx.serialization.builtins.ListSerializer(OpenPriceGroupData.serializer()),
                    edited.priceGroups
                )
            )
            .withMapValue(
                "crateClickMapping",
                CrateDataFormats.yaml.encodeToNode(CrateClickMappingData.serializer(), edited.crateClickMapping)
            )
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }
}


