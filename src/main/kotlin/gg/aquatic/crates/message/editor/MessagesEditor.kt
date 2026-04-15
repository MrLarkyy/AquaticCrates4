package gg.aquatic.crates.message.editor

import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.crates.Messages
import gg.aquatic.crates.message.MessagesFileData
import gg.aquatic.crates.message.MessagesFormats
import gg.aquatic.crates.message.storage.MessageStorage
import gg.aquatic.waves.serialization.editor.SerializableEditor
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object MessagesEditor {

    fun open(player: Player) {
        SerializableEditor.startEditing(
            player = player,
            title = Component.text("Editing messages"),
            serializer = MessagesFileData.serializer(),
            yaml = MessagesFormats.yaml,
            schema = MessagesLocaleEditorSchema,
            loadFresh = {
                MessageStorage.loadData()
            },
            onSave = { data ->
                MessageStorage.saveData(data)
                VirtualsCtx {
                    Messages.load()
                }
            }
        )
    }
}

