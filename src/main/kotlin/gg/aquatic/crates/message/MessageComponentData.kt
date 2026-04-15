package gg.aquatic.crates.message

import gg.aquatic.crates.message.condition.MessageConditionData
import gg.aquatic.crates.message.condition.editor.MessageConditionSelectionMenu
import gg.aquatic.crates.message.condition.editor.defineMessageConditionEditor
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class MessageComponentData(
    val text: String = "<gray>Component",
    val hover: List<String> = emptyList(),
    val click: MessageClickActionData? = null,
    val visibilityConditions: List<MessageConditionData> = emptyList(),
) {
    companion object {
        fun TypedNestedSchemaBuilder<MessageComponentData>.defineEditor() {
            field(
                MessageComponentData::text,
                TextFieldAdapter,
                TextFieldConfig(
                    prompt = "Enter component text:",
                    showFormattedPreview = true
                ),
                displayName = "Text",
                iconMaterial = Material.PAPER,
                description = listOf("MiniMessage text rendered for this component.")
            )
            list(
                MessageComponentData::hover,
                displayName = "Hover Text",
                iconMaterial = Material.FEATHER,
                description = listOf("Hover lines shown when the player hovers this component."),
                newValueFactory = EditorEntryFactories.text("Enter hover line:")
            )
            field(
                MessageComponentData::click,
                displayName = "Click Action",
                iconMaterial = Material.LEVER,
                description = listOf(
                    "Optional click action for this component.",
                    "Press Q to clear it back to null."
                )
            )
            optionalGroup(MessageComponentData::click) {
                with(MessageClickActionData) {
                    defineEditor()
                }
            }
            list(
                MessageComponentData::visibilityConditions,
                displayName = "Visibility Conditions",
                iconMaterial = Material.COMPARATOR,
                description = listOf(
                    "Optional conditions that must all pass for this component to be visible.",
                    "Useful for paginated messages like Previous and Next buttons."
                ),
                newValueFactory = MessageConditionSelectionMenu.entryFactory
            ) {
                defineMessageConditionEditor()
            }
        }
    }
}

