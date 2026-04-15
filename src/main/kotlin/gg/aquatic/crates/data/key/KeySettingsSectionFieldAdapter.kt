package gg.aquatic.crates.data.key

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.CrateData
import gg.aquatic.crates.data.CrateDataFormats
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.core.RootSectionFieldAdapter
import gg.aquatic.crates.data.editor.core.withMapValue
import gg.aquatic.crates.data.editor.core.yamlScalar
import gg.aquatic.crates.data.interaction.CrateClickMappingData
import gg.aquatic.crates.data.item.StackedItemData
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object KeySettingsSectionFieldAdapter : RootSectionFieldAdapter<KeySettingsData>(
    serializer = KeySettingsData.serializer(),
    yaml = CrateDataFormats.yaml,
    schema = KeySettingsEditorSchema,
    title = Component.text("Key Settings")
) {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val settings = decodeSettings(context)
        val keyMaterial = settings?.keyItem?.material ?: "unknown"
        val mustBeHeld = if (settings?.keyMustBeHeld == true) "yes" else "no"

        return stackedItem(Material.TRIPWIRE_HOOK) {
            displayName = text("Key Settings", NamedTextColor.AQUA)
            if (context.description.isNotEmpty()) {
                lore += text("Description", NamedTextColor.DARK_AQUA)
                lore += context.description.map { text(it, NamedTextColor.GRAY) }
            }
            lore += text("Key Material: $keyMaterial", NamedTextColor.WHITE)
            lore += text("Must Be Held: $mustBeHeld", NamedTextColor.WHITE)
            lore += text("Left Click: Edit key settings", NamedTextColor.GREEN)
        }.getItem()
    }

    override fun loadSection(context: EditorFieldContext): KeySettingsData? = decodeSettings(context)

    override fun defaultSectionValue(): KeySettingsData = KeySettingsData()

    private fun decodeSettings(context: EditorFieldContext): KeySettingsData? {
        val crateData = runCatching {
            CrateDataFormats.yaml.decodeFromYamlNode(CrateData.serializer(), context.root)
        }.getOrNull() ?: return null
        return KeySettingsData.from(crateData)
    }

    override fun updateRoot(context: EditorFieldContext, root: YamlNode, edited: KeySettingsData): YamlNode {
        return root
            .withMapValue("keyItem", CrateDataFormats.yaml.encodeToNode(StackedItemData.serializer(), edited.keyItem))
            .withMapValue("keyMustBeHeld", yamlScalar(edited.keyMustBeHeld))
            .withMapValue("keyClickMapping", CrateDataFormats.yaml.encodeToNode(CrateClickMappingData.serializer(), edited.keyClickMapping))
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }
}
