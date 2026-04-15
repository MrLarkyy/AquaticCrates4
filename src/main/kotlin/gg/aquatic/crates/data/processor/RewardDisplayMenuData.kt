package gg.aquatic.crates.data.processor

import gg.aquatic.common.toMMComponent
import gg.aquatic.crates.data.CrateDataFormats
import gg.aquatic.crates.data.PreviewButtonData
import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.resolveCrateDataDescriptor
import gg.aquatic.crates.data.menu.AnvilMenuRuntimeSettings
import gg.aquatic.crates.data.menu.MenuInventoryData
import gg.aquatic.kmenu.menu.settings.PrivateMenuSettings
import gg.aquatic.waves.serialization.editor.meta.EditableModel
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import gg.aquatic.waves.serialization.editor.meta.TypedEditorSchemaBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bukkit.Material

@Serializable
data class RewardDisplayMenuData(
    val inventory: MenuInventoryData = MenuInventoryData(),
    val title: String = "<yellow>Rewards",
    val rewardSlots: List<Int> = listOf(10, 11, 12, 13, 14, 15, 16),
    val customButtons: Map<String, PreviewButtonData> = emptyMap(),
) {
    fun toMenuSettings(): RewardDisplayMenuSettings {
        val inventorySettings = inventory.toRuntimeSettings()

        val components = hashMapOf<String, gg.aquatic.kmenu.menu.settings.IButtonSettings>()
        customButtons.forEach { (id, button) ->
            components[id] = button.toButtonSettings(id)
        }

        return RewardDisplayMenuSettings(
            rewardSlots = rewardSlots.distinct(),
            invSettings = PrivateMenuSettings(
                inventorySettings.inventoryType,
                title.toMMComponent(),
                components
            ),
            anvilSettings = inventorySettings.anvil
        )
    }

    companion object {
        private val schemaJson = Json { encodeDefaults = true }

        fun TypedNestedSchemaBuilder<RewardDisplayMenuData>.defineEditor() {
            group(RewardDisplayMenuData::inventory) {
                with(MenuInventoryData) {
                    defineEditor()
                }
            }
            field(
                RewardDisplayMenuData::title,
                TextFieldAdapter,
                TextFieldConfig(prompt = "Enter menu title:", showFormattedPreview = true),
                displayName = "Title",
                searchTags = listOf("title", "menu title", "result title", "reward menu title", "inventory title"),
                iconMaterial = Material.NAME_TAG,
                description = listOf("Menu title shown at the top of this reward menu.")
            )
            list(
                RewardDisplayMenuData::rewardSlots,
                displayName = "Reward Slots",
                searchTags = listOf("reward slots", "slots", "won rewards", "result slots", "menu slots"),
                iconMaterial = Material.HOPPER,
                description = listOf(
                    "Slots where rolled reward items should appear.",
                    "These slots control the actual reward placement in the menu."
                ),
                newValueFactory = EditorEntryFactories.int("Enter reward slot or range (e.g. 10-16 or 10,12,14):", unique = true)
            )
            map(
                RewardDisplayMenuData::customButtons,
                displayName = "Custom Buttons",
                searchTags = listOf("buttons", "custom buttons", "result buttons", "pagination", "next page", "prev page"),
                iconMaterial = Material.STONE_BUTTON,
                description = listOf(
                    "Additional buttons shown in this reward menu.",
                    "Use ID 'next-page' or 'prev-page' to add pagination buttons when needed."
                ),
                mapKeyPrompt = "Enter custom button ID:",
                newMapEntryFactory = EditorEntryFactories.map(
                    keyPrompt = "Enter custom button ID:",
                    keyValidator = { if (CrateDataValidators.crateIdRegex.matches(it)) null else "Use only letters, numbers, '_' or '-'." },
                    valueFactory = { buttonId ->
                        CrateDataFormats.yaml.encodeToNode(
                            PreviewButtonData.serializer(),
                            PreviewButtonData(
                                item = gg.aquatic.crates.data.item.StackedItemData(
                                    material = Material.PAPER.name,
                                    displayName = "<yellow>$buttonId"
                                )
                            )
                        )
                    }
                )
            ) {
                with(PreviewButtonData) {
                    defineEditor()
                }
            }
        }
    }
}

object RewardDisplayMenuEditorSchema : EditableModel<RewardDisplayMenuData>(RewardDisplayMenuData.serializer()) {
    override fun resolveDescriptor(context: EditorFieldContext) = resolveCrateDataDescriptor(context)

    override fun TypedEditorSchemaBuilder<RewardDisplayMenuData>.define() {
        include<RewardDisplayMenuData> {
            with(RewardDisplayMenuData) { defineEditor() }
        }
    }
}

data class RewardDisplayMenuSettings(
    val rewardSlots: Collection<Int>,
    val invSettings: PrivateMenuSettings,
    val anvilSettings: AnvilMenuRuntimeSettings?,
)
