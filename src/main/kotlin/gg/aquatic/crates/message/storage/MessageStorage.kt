package gg.aquatic.crates.message.storage

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.CratesPlugin
import gg.aquatic.crates.Messages
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.debug.CratesLogger
import gg.aquatic.crates.message.MessagesFileData
import gg.aquatic.crates.message.MessagesFormats
import gg.aquatic.klocale.impl.paper.PaperMessage
import gg.aquatic.crates.yaml.encodeCompactString
import gg.aquatic.crates.yaml.mergeMissing
import gg.aquatic.crates.yaml.parseCompactNode
import java.io.File

object MessageStorage {
    val file: File
        get() = File(CratesPlugin.dataFolder, "messages.yml")

    fun loadData(): MessagesFileData {
        val defaults = loadDefaultData()
        val defaultNode = MessagesFormats.yaml.encodeToNode(MessagesFileData.serializer(), defaults)
        val target = file
        if (!target.exists()) {
            saveData(defaults)
            return defaults
        }

        val currentText = target.readText(Charsets.UTF_8)
        val mergedNode = mergeWithDefaultsOrFallback(currentText, defaultNode)
        val decoded = decodeOrFallback(mergedNode, defaults)

        val mergedText = MessagesFormats.yaml.encodeToString(YamlNode.serializer(), mergedNode)
        if (mergedText != currentText) {
            target.writeText(mergedText, Charsets.UTF_8)
        }

        return decoded
    }

    fun saveData(data: MessagesFileData) {
        val target = file
        target.parentFile?.mkdirs()
        target.writeText(MessagesFormats.yaml.encodeCompactString(MessagesFileData.serializer(), data), Charsets.UTF_8)
    }

    fun loadRuntimeMessages(): Map<String, Map<String, PaperMessage>> {
        val data = loadData()
        return mapOf(
            "en" to mapOf(
                Messages.HELP.path to data.help.toPaperMessage(),
                Messages.CRATE_GIVEN.path to data.crateGiven.toPaperMessage(),
                Messages.CRATE_OPEN_SELF_REQUIRES_PLAYER.path to data.crateOpenSelfRequiresPlayer.toPaperMessage(),
                Messages.CRATE_OPENED_SELF.path to data.crateOpenedSelf.toPaperMessage(),
                Messages.CRATE_OPENED_TARGET.path to data.crateOpenedTarget.toPaperMessage(),
                Messages.CRATE_OPENED_SENDER.path to data.crateOpenedSender.toPaperMessage(),
                Messages.CRATE_OPEN_KEY_REQUIRED.path to data.crateOpenKeyRequired.toPaperMessage(),
                Messages.CRATE_OPEN_FAILED.path to data.crateOpenFailed.toPaperMessage(),
                Messages.PLAYER_ONLY_COMMAND.path to data.playerOnlyCommand.toPaperMessage(),
                Messages.PLAYER_NOT_FOUND.path to data.playerNotFound.toPaperMessage(),
                Messages.KEYS_SELF_REQUIRES_PLAYER.path to data.keysSelfRequiresPlayer.toPaperMessage(),
                Messages.KEYS_GIVEN_SELF.path to data.keysGivenSelf.toPaperMessage(),
                Messages.KEYS_GIVEN_TARGET.path to data.keysGivenTarget.toPaperMessage(),
                Messages.KEYS_GIVEN_SENDER.path to data.keysGivenSender.toPaperMessage(),
                Messages.KEYS_GIVEN_ALL_SELF.path to data.keysGivenAllSelf.toPaperMessage(),
                Messages.KEYS_GIVEN_ALL_TARGET.path to data.keysGivenAllTarget.toPaperMessage(),
                Messages.KEYS_GIVEN_ALL_SENDER.path to data.keysGivenAllSender.toPaperMessage(),
                Messages.KEYS_OFFLINE_REQUIRES_VIRTUAL.path to data.keysOfflineRequiresVirtual.toPaperMessage(),
                Messages.KEY_BANK.path to data.keyBank.toPaperMessage(),
                Messages.KEY_BANK_EMPTY.path to data.keyBankEmpty.toPaperMessage(),
                Messages.NO_PERMISSION.path to data.noPermission.toPaperMessage(),
                Messages.PLUGIN_RELOADING.path to data.pluginReloading.toPaperMessage(),
                Messages.PLUGIN_RELOADED.path to data.pluginReloaded.toPaperMessage(),
                Messages.STATS_INVALIDATED.path to data.statsInvalidated.toPaperMessage(),
                Messages.CRATE_PLACED.path to data.cratePlaced.toPaperMessage(),
                Messages.CRATE_DESTROYED.path to data.crateDestroyed.toPaperMessage(),
                Messages.CRATE_SAVED.path to data.crateSaved.toPaperMessage(),
                Messages.CRATE_CREATE_PROMPT.path to data.crateCreatePrompt.toPaperMessage(),
                Messages.CRATE_INVALID_ID.path to data.crateInvalidId.toPaperMessage(),
                Messages.CRATE_NOT_FOUND.path to data.crateNotFound.toPaperMessage(),
                Messages.CRATE_ALREADY_EXISTS.path to data.crateAlreadyExists.toPaperMessage(),
                Messages.CRATE_EDITOR_OPEN_FAILED.path to data.crateEditorOpenFailed.toPaperMessage(),
            )
        )
    }

    private fun loadDefaultData(): MessagesFileData {
        val defaultText = CratesPlugin.getResource("messages.yml")
            ?.bufferedReader(Charsets.UTF_8)
            ?.use { it.readText() }
            ?: return MessagesFileData()

        return runCatching {
            MessagesFormats.yaml.decodeFromYamlNode(MessagesFileData.serializer(), MessagesFormats.yaml.parseCompactNode(defaultText))
        }.getOrElse { exception ->
            CratesLogger.warning(
                "Failed to load default messages.yml, falling back to hardcoded defaults: ${exception.message ?: exception.javaClass.simpleName}"
            )
            MessagesFileData()
        }
    }

    internal fun mergeWithDefaultsOrFallback(currentText: String, defaultNode: YamlNode): YamlNode {
        return runCatching {
            MessagesFormats.yaml.parseCompactNode(currentText).mergeMissing(defaultNode)
        }.getOrElse { exception ->
            CratesLogger.warning(
                "Failed to load messages.yml, falling back to defaults: ${exception.message ?: exception.javaClass.simpleName}"
            )
            defaultNode
        }
    }

    internal fun decodeOrFallback(node: YamlNode, defaults: MessagesFileData): MessagesFileData {
        return runCatching {
            MessagesFormats.yaml.decodeFromYamlNode(MessagesFileData.serializer(), node)
        }.getOrElse { exception ->
            CratesLogger.warning(
                "Failed to decode merged messages.yml, falling back to defaults: ${exception.message ?: exception.javaClass.simpleName}"
            )
            defaults
        }
    }
}

