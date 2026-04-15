package gg.aquatic.crates.message.storage

import gg.aquatic.klocale.LocaleProvider
import gg.aquatic.klocale.impl.paper.PaperMessage

object MessagesProvider : LocaleProvider<PaperMessage> {
    override suspend fun fetch(): Map<String, Map<String, PaperMessage>> {
        return MessageStorage.loadRuntimeMessages()
    }
}

