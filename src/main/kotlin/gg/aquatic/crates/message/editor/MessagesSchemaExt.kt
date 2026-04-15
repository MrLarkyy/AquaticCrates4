package gg.aquatic.crates.message.editor

import gg.aquatic.crates.Messages
import gg.aquatic.crates.message.EditableMessageData
import gg.aquatic.crates.message.editor.MessageEntryFieldAdapter
import gg.aquatic.crates.message.MessagesFileData
import gg.aquatic.waves.serialization.editor.meta.TypedEditorSchemaBuilder
import kotlin.reflect.KProperty1

internal fun TypedEditorSchemaBuilder<MessagesFileData>.messageField(
    property: KProperty1<MessagesFileData, EditableMessageData>,
    metadata: Messages,
) {
    field(
        property,
        displayName = metadata.displayName,
        iconMaterial = metadata.icon,
        adapter = MessageEntryFieldAdapter,
        description = buildList {
            addAll(metadata.description)
            if (metadata.placeholders.isEmpty()) {
                add("Available placeholders: none.")
            } else {
                add("Available placeholders:")
                addAll(metadata.placeholders.map { "- $it" })
            }
        }
    )
    group(property) {
        with(EditableMessageData) {
            defineEditor()
        }
    }
}


