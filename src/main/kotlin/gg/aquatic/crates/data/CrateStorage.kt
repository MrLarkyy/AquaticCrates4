package gg.aquatic.crates.data

import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.core.mapValue
import gg.aquatic.crates.data.editor.core.withMapValue
import gg.aquatic.crates.data.processor.BasicRewardProcessorData
import gg.aquatic.crates.data.processor.ChooseRewardProcessorData
import gg.aquatic.crates.data.processor.RewardProcessorData
import gg.aquatic.crates.data.processor.RewardProcessorType
import gg.aquatic.crates.data.processor.WorldChooseRewardProcessorData
import gg.aquatic.crates.data.provider.ConditionalPoolsRewardProviderData
import gg.aquatic.crates.data.provider.RewardProviderData
import gg.aquatic.crates.data.provider.RewardProviderType
import gg.aquatic.crates.data.provider.SimpleRewardProviderData
import gg.aquatic.crates.CratesPlugin
import gg.aquatic.crates.crate.Crate
import gg.aquatic.crates.yaml.encodeCompactString
import gg.aquatic.crates.yaml.parseCompactNode
import java.io.File

object CrateStorage {

    private val yaml = CrateDataFormats.yaml

    private val cratesDirectory: File
        get() = File(CratesPlugin.dataFolder, "crates")

    fun ensureDefaults() {
        cratesDirectory.mkdirs()
        if (availableIds().isNotEmpty()) return

        save("test", CrateData.createDefault(displayName = "<yellow>Test Crate"))
    }

    fun availableIds(): List<String> {
        if (!cratesDirectory.exists()) return emptyList()

        return cratesDirectory.listFiles { file ->
            file.isFile && (
                file.extension.equals("yml", true) ||
                    file.extension.equals("yaml", true)
                )
        }
            ?.map { it.nameWithoutExtension }
            ?.sorted()
            ?.distinct()
            ?: emptyList()
    }

    fun loadData(id: String): CrateData {
        val yamlFile = yamlFileFor(id)
        if (!yamlFile.exists()) {
            return CrateData.createDefault()
        }

        val content = yamlFile.readText()
        val normalized = normalizeCrateData(id, decodeCrateData(content))
        rewriteNormalizedCrateFileIfNeeded(yamlFile, content, normalized)
        return normalized
    }

    fun loadAllCrates(): Map<String, Crate> {
        ensureDefaults()
        return availableIds().associateWith { id ->
            loadData(id).toCrate(id)
        }
    }

    fun save(id: String, crateData: CrateData) {
        cratesDirectory.mkdirs()
        yamlFileFor(id).writeText(encodeCompactYaml(normalizeCrateData(id, crateData, includeCurrentId = true)))
    }

    fun exists(id: String): Boolean {
        return yamlFileFor(id).exists() || yamlAltFileFor(id).exists()
    }

    fun delete(id: String): Boolean {
        val deletedPrimary = yamlFileFor(id).delete()
        val deletedAlt = yamlAltFileFor(id).delete()
        return deletedPrimary || deletedAlt
    }

    private fun yamlFileFor(id: String): File {
        return File(cratesDirectory, "$id.yml")
    }

    private fun yamlAltFileFor(id: String): File {
        return File(cratesDirectory, "$id.yaml")
    }

    private fun decodeCrateData(content: String): CrateData {
        val parsed = yaml.parseCompactNode(content)
        return yaml.decodeFromYamlNode(CrateData.serializer(), migrateLegacyCrateNode(parsed))
    }

    private fun normalizeCrateData(id: String, crateData: CrateData, includeCurrentId: Boolean = false): CrateData {
        val existingIds = availableIds().toMutableSet()
        if (includeCurrentId) {
            existingIds += id
        }
        return crateData.normalized(id, existingIds)
    }

    private fun rewriteNormalizedCrateFileIfNeeded(yamlFile: File, currentContent: String, crateData: CrateData) {
        val normalizedYaml = encodeCompactYaml(crateData)
        if (!sameYaml(currentContent, normalizedYaml)) {
            yamlFile.writeText(normalizedYaml)
        }
    }

    private fun encodeCompactYaml(crateData: CrateData): String {
        return yaml.encodeCompactString(CrateData.serializer(), crateData)
    }

    private fun sameYaml(current: String, normalized: String): Boolean {
        return current
            .replace("\r\n", "\n")
            .trim() == normalized
            .replace("\r\n", "\n")
            .trim()
    }

    private fun migrateLegacyCrateNode(root: YamlNode): YamlNode {
        if (root !is YamlMap) {
            return root
        }

        return root
            .migrateLegacyRewardProvider()
            .migrateLegacyRewardProcessor()
    }

    private fun YamlNode.migrateLegacyRewardProvider(): YamlNode {
        if (mapValue("rewardProvider") != null) {
            return this
        }

        val type = mapValue("rewardProviderType")?.let {
            RewardProviderType.of(yaml.decodeFromYamlNode(kotlinx.serialization.serializer<String>(), it)).id
        } ?: return this

        val data = when (RewardProviderType.of(type)) {
            RewardProviderType.SIMPLE -> {
                val legacy = mapValue("simpleProvider")
                if (legacy != null) yaml.decodeFromYamlNode(SimpleRewardProviderData.serializer(), legacy) else SimpleRewardProviderData()
            }
            RewardProviderType.CONDITIONAL_POOLS -> {
                val legacy = mapValue("conditionalPoolsProvider")
                if (legacy != null) yaml.decodeFromYamlNode(ConditionalPoolsRewardProviderData.serializer(), legacy) else ConditionalPoolsRewardProviderData()
            }
        }

        return withMapValue(
            "rewardProvider",
            yaml.encodeToNode(RewardProviderData.serializer(), data)
        )
    }

    private fun YamlNode.migrateLegacyRewardProcessor(): YamlNode {
        if (mapValue("rewardProcessor") != null) {
            return this
        }

        val type = mapValue("rewardProcessorType")?.let {
            RewardProcessorType.of(yaml.decodeFromYamlNode(kotlinx.serialization.serializer<String>(), it)).id
        } ?: return this

        val data = when (RewardProcessorType.of(type)) {
            RewardProcessorType.BASIC -> {
                val legacy = mapValue("basicProcessor")
                if (legacy != null) yaml.decodeFromYamlNode(BasicRewardProcessorData.serializer(), legacy) else BasicRewardProcessorData()
            }
            RewardProcessorType.CHOOSE -> {
                val legacy = mapValue("chooseProcessor")
                if (legacy != null) yaml.decodeFromYamlNode(ChooseRewardProcessorData.serializer(), legacy) else ChooseRewardProcessorData()
            }
            RewardProcessorType.WORLD_CHOOSE -> {
                val legacy = mapValue("worldChooseProcessor")
                if (legacy != null) yaml.decodeFromYamlNode(WorldChooseRewardProcessorData.serializer(), legacy) else WorldChooseRewardProcessorData()
            }
        }

        return withMapValue(
            "rewardProcessor",
            yaml.encodeToNode(RewardProcessorData.serializer(), data)
        )
    }
}
