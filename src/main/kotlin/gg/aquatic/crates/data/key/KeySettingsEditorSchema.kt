package gg.aquatic.crates.data.key

import gg.aquatic.crates.data.interaction.CrateClickMappingData
import gg.aquatic.crates.data.item.StackedItemData
import gg.aquatic.crates.data.item.StackedItemDataEditor
import gg.aquatic.crates.data.resolveCrateDataDescriptor
import gg.aquatic.waves.serialization.editor.meta.EditableModel
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.TypedEditorSchemaBuilder
import org.bukkit.Material

object KeySettingsEditorSchema : EditableModel<KeySettingsData>(KeySettingsData.serializer()) {
    override fun resolveDescriptor(context: EditorFieldContext) = resolveCrateDataDescriptor(context)

    override fun TypedEditorSchemaBuilder<KeySettingsData>.define() {
        group(KeySettingsData::keyItem) {
            with(StackedItemDataEditor) {
                defineFullEditor(
                    materialLabel = "Key Material",
                    materialPrompt = "Enter material or Factory:ItemId:",
                    nameLabel = "Key Name",
                    namePrompt = "Enter key display name:",
                    loreLabel = "Key Lore"
                )
            }
        }
        field(
            KeySettingsData::keyMustBeHeld,
            displayName = "Key Must Be Held",
            searchTags = listOf("must be held", "hold key", "hand", "require held key", "key requirement"),
            prompt = "Enter true or false:",
            iconMaterial = Material.TRIPWIRE_HOOK,
            description = listOf("If enabled, players must hold this crate key in hand when opening the crate directly in the world.")
        )
        group(KeySettingsData::keyClickMapping) {
            with(CrateClickMappingData) { defineEditor("Key Click", allowDestroy = false) }
        }
    }
}
