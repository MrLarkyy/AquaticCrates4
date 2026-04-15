package gg.aquatic.crates.message

import gg.aquatic.common.argument.ObjectArguments
import gg.aquatic.common.toMMComponent
import gg.aquatic.crates.data.action.RewardActionData
import gg.aquatic.crates.data.action.editor.RewardActionSelectionMenu
import gg.aquatic.crates.data.action.editor.defineRewardActionEditor
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.message.editor.defineMessageLineEditor
import gg.aquatic.klocale.impl.paper.PaperMessage
import gg.aquatic.crates.message.runtime.EditableMessageRuntimeFactory
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class EditableMessageData(
    val lines: List<EditableMessageLineData> = listOf(EditableMessageLineData()),
    val pagination: EditableMessagePaginationData? = null,
    val actions: List<RewardActionData> = emptyList(),
) {
    fun toPaperMessage(): PaperMessage {
        return EditableMessageRuntimeFactory.create(this)
    }

    fun toPaperMessage(
        renderedLines: Iterable<net.kyori.adventure.text.Component>,
        paginationReplacements: Map<String, String> = emptyMap(),
    ): PaperMessage {
        return EditableMessageRuntimeFactory.create(this, renderedLines, paginationReplacements)
    }

    companion object {
        fun lines(vararg value: String): EditableMessageData {
            return EditableMessageData(
                value.map { EditableMessageLineData(components = listOf(MessageComponentData(text = it))) }
            )
        }

        fun TypedNestedSchemaBuilder<EditableMessageData>.defineEditor() {
            list(
                EditableMessageData::lines,
                displayName = "Lines",
                iconMaterial = Material.WRITABLE_BOOK,
                description = listOf(
                    "Each entry is one rendered line of this message.",
                    "Each line is built from one or more components."
                ),
                newValueFactory = gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories.text(
                    prompt = "Enter default line text:",
                    transform = {
                        MessagesFormats.yaml.encodeToNode(
                            EditableMessageLineData.serializer(),
                            EditableMessageLineData(
                                components = listOf(MessageComponentData(text = it))
                            )
                        )
                    }
                )
            ) {
                defineMessageLineEditor()
            }
            list(
                EditableMessageData::actions,
                displayName = "Send Actions",
                iconMaterial = Material.BLAZE_POWDER,
                description = listOf(
                    "Actions executed when this message is sent to a player.",
                    "These do not run for console senders."
                ),
                newValueFactory = RewardActionSelectionMenu.entryFactory
            ) {
                defineRewardActionEditor()
            }
            field(
                EditableMessageData::pagination,
                displayName = "Pagination",
                iconMaterial = Material.BOOKSHELF,
                description = listOf(
                    "Optional pagination settings for this message.",
                    "When enabled, the message is split into pages with header/footer support."
                )
            )
            optionalGroup(EditableMessageData::pagination) {
                with(EditableMessagePaginationData) {
                    defineEditor()
                }
            }
        }
    }
}

