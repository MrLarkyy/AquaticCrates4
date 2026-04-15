package gg.aquatic.crates.message

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.message.storage.MessageStorage
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageStorageTest {
    @Test
    fun `mergeWithDefaultsOrFallback keeps user values and adds missing defaults`() {
        val defaults = MessagesFileData()
        val defaultNode = MessagesFormats.yaml.encodeToNode(MessagesFileData.serializer(), defaults)
        val currentText = """
            plugin-reloaded:
              lines:
                - components:
                    - text: "<red>Custom reload"
        """.trimIndent()

        val mergedNode = MessageStorage.mergeWithDefaultsOrFallback(currentText, defaultNode)
        val decoded = MessageStorage.decodeOrFallback(mergedNode, defaults)

        assertEquals("<red>Custom reload", decoded.pluginReloaded.lines.first().components.first().text)
        assertEquals("<yellow>Reloading...", decoded.pluginReloading.lines.first().components.first().text)
    }

    @Test
    fun `mergeWithDefaultsOrFallback restores null sections from defaults`() {
        val defaults = MessagesFileData()
        val defaultNode = MessagesFormats.yaml.encodeToNode(MessagesFileData.serializer(), defaults)
        val currentText = """
            plugin-reloaded: null
        """.trimIndent()

        val mergedNode = MessageStorage.mergeWithDefaultsOrFallback(currentText, defaultNode)
        val decoded = MessageStorage.decodeOrFallback(mergedNode, defaults)

        assertEquals("<green>Reloaded!", decoded.pluginReloaded.lines.first().components.first().text)
    }

    @Test
    fun `mergeWithDefaultsOrFallback falls back to defaults when yaml is invalid`() {
        val defaults = MessagesFileData()
        val defaultNode = MessagesFormats.yaml.encodeToNode(MessagesFileData.serializer(), defaults)

        val mergedNode = MessageStorage.mergeWithDefaultsOrFallback("plugin-reloaded: [", defaultNode)
        val decoded = MessageStorage.decodeOrFallback(mergedNode, defaults)

        assertEquals(defaultNode, mergedNode)
        assertEquals(defaults, decoded)
    }

    @Test
    fun `decodeOrFallback returns defaults when merged node cannot decode`() {
        val defaults = MessagesFileData()
        val invalidNode = MessagesFormats.yaml.parseToYamlNode(
            """
            plugin-reloaded:
              lines: "not-a-list"
            """.trimIndent()
        )

        val decoded = MessageStorage.decodeOrFallback(invalidNode, defaults)

        assertEquals(defaults, decoded)
    }
}
