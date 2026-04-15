package gg.aquatic.crates.data.item

import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.crates.data.editor.ItemFlagFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.ColorFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.ColorFieldConfig
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.EnumFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EnumFieldConfig
import gg.aquatic.waves.serialization.editor.meta.IntFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.IntFieldConfig
import gg.aquatic.waves.serialization.editor.meta.MaterialLikeFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.MaterialLikeFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemRarity

object StackedItemDataEditor {
    fun TypedNestedSchemaBuilder<StackedItemData>.defineBasicEditor(
        materialLabel: String,
        nameLabel: String,
        namePrompt: String,
        loreLabel: String,
        amountLabel: String,
        materialPrompt: String = "Enter material, Factory:ItemId or 'hand':",
    ) {
        field(
            StackedItemData::material,
            MaterialLikeFieldAdapter,
            MaterialLikeFieldConfig(prompt = materialPrompt, allowHandShortcut = true),
            displayName = materialLabel,
            iconMaterial = Material.BRICKS,
            description = listOf("Base item material or Factory:ItemId used for this item.", "Type 'hand' to copy the item currently held in your main hand as a base64 item.")
        )
        field(
            StackedItemData::displayName,
            TextFieldAdapter,
            TextFieldConfig(prompt = namePrompt, showFormattedPreview = true),
            displayName = nameLabel,
            iconMaterial = Material.NAME_TAG,
            description = listOf("Custom item name shown to players.")
        )
        list(
            StackedItemData::lore,
            loreLabel,
            iconMaterial = Material.WRITABLE_BOOK,
            description = listOf("Lore lines shown under the item name."),
            newValueFactory = EditorEntryFactories.text("Enter lore line:")
        )
        field(
            StackedItemData::amount,
            IntFieldAdapter,
            IntFieldConfig(prompt = "Enter amount:", min = 1, max = 64),
            displayName = amountLabel,
            iconMaterial = Material.COPPER_INGOT,
            description = listOf("Item stack size given or shown in menus.")
        )
    }

    fun TypedNestedSchemaBuilder<StackedItemData>.defineFullEditor(
        materialLabel: String,
        materialPrompt: String,
        nameLabel: String,
        namePrompt: String,
        loreLabel: String,
    ) {
        defineBasicEditor(
            materialLabel = materialLabel,
            materialPrompt = materialPrompt,
            nameLabel = nameLabel,
            namePrompt = namePrompt,
            loreLabel = loreLabel,
            amountLabel = "Amount"
        )
        field(
            StackedItemData::customModelDataLegacy,
            IntFieldAdapter,
            IntFieldConfig(prompt = "Enter legacy custom model data:"),
            displayName = "Legacy CMD",
            iconMaterial = Material.REDSTONE,
            description = listOf("Legacy CustomModelData integer value.")
        )
        list(
            StackedItemData::customModelColors,
            "Model Colors",
            iconMaterial = Material.MAGENTA_DYE,
            description = listOf("Color entries used by modern item model data."),
            newValueFactory = EditorEntryFactories.text(
                prompt = "Enter color (#RRGGBB or r;g;b):",
                validator = { if (CrateDataValidators.isValidColor(it)) null else "Invalid color." }
            )
        )
        list(
            StackedItemData::customModelFloats,
            "Model Floats",
            iconMaterial = Material.AMETHYST_SHARD,
            description = listOf("Float entries used by modern item model data."),
            newValueFactory = EditorEntryFactories.float("Enter float value:")
        )
        list(
            StackedItemData::customModelFlags,
            "Model Flags",
            iconMaterial = Material.LEVER,
            description = listOf("Boolean flags used by modern item model data."),
            newValueFactory = EditorEntryFactories.boolean("Enter boolean value (true/false):")
        )
        list(
            StackedItemData::customModelStrings,
            "Model Strings",
            iconMaterial = Material.PAPER,
            description = listOf("String entries used by modern item model data."),
            newValueFactory = EditorEntryFactories.text("Enter model string:")
        )
        field(
            StackedItemData::itemModel,
            TextFieldAdapter,
            TextFieldConfig(
                prompt = "Enter item model key (namespace:key):",
                validator = CrateDataValidators::validateNamespacedKey
            ),
            displayName = "Item Model",
            iconMaterial = Material.ITEM_FRAME,
            description = listOf("Namespaced item model key used by the client.")
        )
        field(
            StackedItemData::damage,
            IntFieldAdapter,
            IntFieldConfig(prompt = "Enter damage:", min = 0),
            displayName = "Damage",
            iconMaterial = Material.IRON_SWORD,
            description = listOf("Current durability damage applied to the item.")
        )
        field(
            StackedItemData::maxDamage,
            IntFieldAdapter,
            IntFieldConfig(prompt = "Enter max damage:", min = 1),
            displayName = "Max Damage",
            iconMaterial = Material.ANVIL,
            description = listOf("Overrides the maximum durability of the item.")
        )
        field(
            StackedItemData::maxStackSize,
            IntFieldAdapter,
            IntFieldConfig(prompt = "Enter max stack size:", min = 1, max = 99),
            displayName = "Max Stack Size",
            iconMaterial = Material.CHEST,
            description = listOf("Overrides how many items can stack together.")
        )
        field(
            StackedItemData::rarity,
            EnumFieldAdapter,
            EnumFieldConfig(
                prompt = "Enter item rarity:",
                values = { ItemRarity.entries.map { it.name } }
            ),
            displayName = "Rarity",
            iconMaterial = Material.NETHER_STAR,
            description = listOf("Vanilla item rarity shown by the client.")
        )
        field(
            StackedItemData::spawnerType,
            EnumFieldAdapter,
            EnumFieldConfig(
                prompt = "Enter entity type:",
                values = { EntityType.entries.map { it.name } }
            ),
            displayName = "Spawner Type",
            iconMaterial = Material.SPAWNER,
            description = listOf("Entity type stored in the spawner item.")
        )
        field(
            StackedItemData::tooltipStyle,
            TextFieldAdapter,
            TextFieldConfig(
                prompt = "Enter tooltip style key (namespace:key):",
                validator = CrateDataValidators::validateNamespacedKey
            ),
            displayName = "Tooltip Style",
            iconMaterial = Material.PAINTING,
            description = listOf("Namespaced tooltip style key used by the client.")
        )
        field(
            StackedItemData::dyeColor,
            ColorFieldAdapter,
            ColorFieldConfig(),
            displayName = "Dye Color",
            iconMaterial = Material.MAGENTA_DYE,
            description = listOf("Leather armor or dyed item color.")
        )
        list(
            StackedItemData::enchants,
            "Enchants",
            iconMaterial = Material.ENCHANTING_TABLE,
            description = listOf("Enchant entries in format namespace:enchant:level."),
            newValueFactory = EditorEntryFactories.text("Enter enchant in format enchant:level:", CrateDataValidators::validateEnchantLine)
        )
        list(
            StackedItemData::flags,
            displayName = "Flags",
            iconMaterial = Material.BOOK,
            description = listOf("Vanilla item flags hidden on the tooltip."),
            adapter = ItemFlagFieldAdapter
        )
    }
}
