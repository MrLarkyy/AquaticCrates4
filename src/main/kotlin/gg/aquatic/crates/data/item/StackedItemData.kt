package gg.aquatic.crates.data.item

import gg.aquatic.common.toMMComponent
import gg.aquatic.stacked.ItemHandler
import gg.aquatic.stacked.StackedItem
import gg.aquatic.stacked.impl.StackedItemImpl
import gg.aquatic.stacked.option.*
import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import java.util.*

@Serializable
data class StackedItemData(
    val material: String = Material.STONE.name,
    val displayName: String? = null,
    val lore: List<String> = emptyList(),
    val amount: Int = 1,
    val customModelDataLegacy: Int? = null,
    val customModelColors: List<String> = emptyList(),
    val customModelFloats: List<Float> = emptyList(),
    val customModelFlags: List<Boolean> = emptyList(),
    val customModelStrings: List<String> = emptyList(),
    val itemModel: String? = null,
    val damage: Int? = null,
    val maxDamage: Int? = null,
    val maxStackSize: Int? = null,
    val unbreakable: Boolean = false,
    val hideTooltip: Boolean = false,
    val rarity: String? = null,
    val spawnerType: String? = null,
    val tooltipStyle: String? = null,
    val dyeColor: String? = null,
    val enchants: List<String> = emptyList(),
    val flags: List<String> = emptyList(),
) {

    fun asStacked(): StackedItemImpl {
        val baseItem = resolveBaseItem()
        val options = mutableListOf<ItemOptionHandle>()

        options += AmountOptionHandle(amount.coerceIn(1, 64))

        displayName?.let {
            options += DisplayNameOptionHandle(it.toMMComponent().decoration(TextDecoration.ITALIC, false))
        }

        if (lore.isNotEmpty()) {
            options += LoreOptionHandle(lore.map { it.toMMComponent().decoration(TextDecoration.ITALIC, false) })
        }

        customModelDataLegacy?.let {
            options += CustomModelDataLegacyOptionHandle(it)
        }

        val modelColors = customModelColors.mapNotNull(::parseColor)
        if (modelColors.isNotEmpty() || customModelFloats.isNotEmpty() || customModelFlags.isNotEmpty() || customModelStrings.isNotEmpty()) {
            options += CustomModelDataOptionHandle(
                colors = modelColors,
                floats = customModelFloats,
                flags = customModelFlags,
                strings = customModelStrings
            )
        }

        itemModel?.let(::parseKey)?.let {
            options += ItemModelOptionHandle(it)
        }

        damage?.let {
            options += DamageOptionHandle(it)
        }

        maxDamage?.let {
            options += MaxDamageOptionHandle(it)
        }

        maxStackSize?.let {
            options += MaxStackSizeOptionHandle(it)
        }

        if (unbreakable) {
            options += UnbreakableOptionHandle()
        }

        if (hideTooltip) {
            options += HideTooltipOptionHandle(true)
        }

        rarity?.let(::parseRarity)?.let {
            options += RarityOptionHandle(it)
        }

        spawnerType?.let(::parseEntityType)?.let {
            options += SpawnerTypeOptionHandle(it)
        }

        tooltipStyle?.let(::parseKey)?.let {
            options += TooltipStyleOptionHandle(it)
        }

        dyeColor?.let(::parseColor)?.let {
            options += DyeOptionHandle(it)
        }

        val parsedEnchants = enchants.mapNotNull(::parseEnchantLine).toMap()
        if (parsedEnchants.isNotEmpty()) {
            options += EnchantsOptionHandle(parsedEnchants)
        }

        val parsedFlags = flags.mapNotNull(::parseItemFlag)
        if (parsedFlags.isNotEmpty()) {
            options += FlagsOptionHandle(parsedFlags)
        }

        return ItemHandler.Impl.create(baseItem, options)
    }

    private fun resolveBaseItem(): ItemStack {
        val raw = material.trim()
        if (raw.isEmpty()) return ItemStack(Material.STONE)

        parseVanillaMaterial(raw)?.let { vanilla ->
            return ItemStack(vanilla)
        }

        val splitIdx = raw.indexOf(':')
        if (splitIdx <= 0 || splitIdx >= raw.lastIndex) {
            return ItemStack(Material.STONE)
        }

        val factoryId = raw.substring(0, splitIdx).trim()
        val itemId = raw.substring(splitIdx + 1).trim()
        if (factoryId.isEmpty() || itemId.isEmpty()) return ItemStack(Material.STONE)

        return resolveFactoryItem(factoryId, itemId)?.clone() ?: ItemStack(Material.STONE)
    }

    private fun resolveFactoryItem(factory: String, itemId: String): ItemStack? {
        val candidates = linkedSetOf(
            factory,
            factory.lowercase(Locale.ROOT),
            factory.uppercase(Locale.ROOT)
        )

        for (candidate in candidates) {
            val built = StackedItem.ITEM_FACTORIES[candidate]?.create(itemId)
            if (built != null) return built
        }
        return null
    }

    private fun parseVanillaMaterial(raw: String): Material? {
        return Material.matchMaterial(raw)
            ?: Material.matchMaterial(raw.uppercase(Locale.ROOT))
    }

    private fun parseKey(raw: String): Key? {
        return runCatching { Key.key(raw) }.getOrNull()
    }

    private fun parseRarity(raw: String): ItemRarity? {
        return runCatching { ItemRarity.valueOf(raw.uppercase(Locale.ROOT)) }.getOrNull()
    }

    private fun parseEntityType(raw: String): EntityType? {
        return runCatching { EntityType.valueOf(raw.uppercase(Locale.ROOT)) }.getOrNull()
    }

    private fun parseEnchantLine(raw: String): Pair<String, Int>? {
        val value = raw.trim()
        val splitIdx = value.lastIndexOf(':')
        if (splitIdx <= 0 || splitIdx >= value.lastIndex) return null

        val enchant = value.substring(0, splitIdx).trim()
        val level = value.substring(splitIdx + 1).trim().toIntOrNull() ?: return null
        if (enchant.isEmpty()) return null

        return enchant to level
    }

    private fun parseItemFlag(raw: String): ItemFlag? {
        return runCatching { ItemFlag.valueOf(raw.uppercase(Locale.ROOT)) }.getOrNull()
    }

    private fun parseColor(raw: String): Color? {
        val value = raw.trim()
        if (value.isEmpty()) return null

        if (value.startsWith("#") && value.length == 7) {
            return runCatching {
                Color.fromRGB(
                    value.substring(1, 3).toInt(16),
                    value.substring(3, 5).toInt(16),
                    value.substring(5, 7).toInt(16)
                )
            }.getOrNull()
        }

        val split = if (';' in value) value.split(";") else value.split(",")
        if (split.size != 3) return null

        val red = split[0].trim().toIntOrNull() ?: return null
        val green = split[1].trim().toIntOrNull() ?: return null
        val blue = split[2].trim().toIntOrNull() ?: return null
        return runCatching { Color.fromRGB(red, green, blue) }.getOrNull()
    }
}
