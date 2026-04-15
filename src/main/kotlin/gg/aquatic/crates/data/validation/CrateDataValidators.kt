package gg.aquatic.crates.data.validation

import gg.aquatic.blokk.factory.BlockFactory
import com.ticxo.modelengine.api.ModelEngineAPI
import kr.toxicity.model.api.BetterModel
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.configuration.MemoryConfiguration
import java.util.Locale

object CrateDataValidators {

    val crateIdRegex = Regex("[a-zA-Z0-9_-]+")
    private val namespacedKeyRegex = Regex("[a-z0-9_.-]+:[a-z0-9_/.-]+")

    fun validateNamespacedKey(raw: String): String? {
        return if (namespacedKeyRegex.matches(raw.trim())) null else "Invalid namespaced key."
    }

    fun validateEnchantLine(raw: String): String? {
        val idx = raw.lastIndexOf(':')
        if (idx <= 0 || idx >= raw.lastIndex) return "Use enchant:level format."
        if (raw.substring(idx + 1).trim().toIntOrNull() == null) return "Invalid enchant level."
        return null
    }

    fun validateInventoryType(raw: String): String? {
        return runCatching {
            gg.aquatic.kmenu.inventory.InventoryType.valueOf(raw.trim())
        }.exceptionOrNull()?.let { "Invalid inventory type." }
    }

    fun validateBlockMaterialLike(raw: String): String? {
        val value = raw.trim()
        if (value.isEmpty()) return "Block material cannot be empty."

        if (Material.matchMaterial(value) != null) {
            return null
        }

        val split = value.indexOf(':')
        if (split <= 0 || split >= value.lastIndex) {
            return "Use a vanilla block material or factory:id."
        }

        val factoryId = value.substring(0, split)
        val blockId = value.substring(split + 1)
        val factory = sequenceOf(
            factoryId,
            factoryId.lowercase(Locale.ROOT),
            factoryId.uppercase(Locale.ROOT)
        ).mapNotNull { BlockFactory.REGISTRY[it] }.firstOrNull()
            ?: return "Unknown block factory '$factoryId'."
        val probe = MemoryConfiguration().apply {
            set("material", value)
        }
        return if (factory.load(probe, blockId) != null) null else "Unknown block id '$blockId' for factory '$factoryId'."
    }

    fun validateBlockFace(raw: String): String? {
        return runCatching {
            BlockFace.valueOf(raw.trim().uppercase())
        }.exceptionOrNull()?.let { "Invalid block face." }
    }

    fun validateModelEngineModel(raw: String): String? {
        val value = raw.trim()
        if (value.isEmpty()) return "Model id cannot be empty."

        if (Bukkit.getPluginManager().getPlugin("ModelEngine") == null) {
            return "ModelEngine is not installed."
        }

        val blueprint = runCatching { ModelEngineAPI.getBlueprint(value) }.getOrNull()
        return if (blueprint != null) null else "Unknown ModelEngine model '$value'."
    }

    fun validateBetterModelModel(raw: String): String? {
        val value = raw.trim()
        if (value.isEmpty()) return "Model id cannot be empty."

        if (Bukkit.getPluginManager().getPlugin("BetterModel") == null) {
            return "BetterModel is not installed."
        }

        return if (BetterModel.model(value).isPresent) null else "Unknown BetterModel model '$value'."
    }

    fun isValidColor(raw: String): Boolean {
        val value = raw.trim()
        if (value.matches(Regex("#[0-9a-fA-F]{6}"))) return true
        val split = if (';' in value) value.split(';') else value.split(',')
        return split.size == 3 && split.all { it.trim().toIntOrNull() != null }
    }
}
