package gg.aquatic.crates.data

import gg.aquatic.crates.data.menu.MenuInventoryData
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.IntFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.IntFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import org.bukkit.Material
import kotlin.reflect.KProperty1

internal fun <T> TypedNestedSchemaBuilder<T>.definePreviewInventorySection(
    inventory: KProperty1<T, MenuInventoryData>,
    title: KProperty1<T, String>,
    titleLabel: String,
    titlePrompt: String,
    rewardSlots: KProperty1<T, List<Int>>,
    randomRewardSlots: KProperty1<T, List<Int>>,
    randomRewardSwitchTicks: KProperty1<T, Int>,
    randomRewardUnique: KProperty1<T, Boolean>,
    rewardLore: KProperty1<T, List<String>>,
    customButtons: KProperty1<T, Map<String, PreviewButtonData>>,
    inventoryTypeLabel: String,
    visibleWhen: (gg.aquatic.waves.serialization.editor.meta.EditorFieldContext) -> Boolean = { true },
) {
    group(inventory) {
        with(MenuInventoryData) {
            defineEditor(typeLabel = inventoryTypeLabel)
        }
    }
    field(
        title,
        TextFieldAdapter,
        TextFieldConfig(prompt = titlePrompt, showFormattedPreview = true),
        displayName = titleLabel,
        searchTags = listOf("title", "preview title", "menu title", "inventory title"),
        iconMaterial = Material.NAME_TAG,
        description = listOf("Menu title shown at the top of the preview inventory."),
        visibleWhen = visibleWhen
    )
    list(
        rewardSlots,
        "Reward Slots",
        searchTags = listOf("reward slots", "slots", "static reward slots", "preview slots"),
        iconMaterial = Material.HOPPER,
        description = listOf("Slots where reward icons can appear in the preview menu."),
        newValueFactory = EditorEntryFactories.int("Enter reward slot or range (e.g. 10-16 or 10,12,14):", unique = true),
        visibleWhen = visibleWhen
    )
    list(
        randomRewardSlots,
        "Random Reward Slots",
        searchTags = listOf("random slots", "random reward slots", "rolling rewards", "random preview"),
        iconMaterial = Material.CHEST,
        description = listOf("Slots where randomly selected reward icons can appear in the preview menu."),
        newValueFactory = EditorEntryFactories.int("Enter random reward slot or range (e.g. 19-25):", unique = true),
        visibleWhen = visibleWhen
    )
    field(
        randomRewardSwitchTicks,
        IntFieldAdapter,
        IntFieldConfig(prompt = "Enter random reward switch ticks:", min = 1),
        displayName = "Random Reward Switch Ticks",
        searchTags = listOf("switch ticks", "reroll", "refresh interval", "random reward speed"),
        iconMaterial = Material.CLOCK,
        description = listOf("How often random reward slots reroll to a new reward."),
        visibleWhen = visibleWhen
    )
    field(
        randomRewardUnique,
        displayName = "Random Reward Unique",
        searchTags = listOf("unique", "unique rewards", "no duplicates", "random unique"),
        prompt = "Enter true or false:",
        iconMaterial = Material.COMPARATOR,
        description = listOf(
            "If enabled, each random reward slot shows a different reward.",
            "Extra slots are hidden when there are not enough unique rewards."
        ),
        visibleWhen = visibleWhen
    )
    list(
        rewardLore,
        displayName = "Reward Lore",
        searchTags = listOf("reward lore", "preview lore", "extra lore", "tooltip"),
        iconMaterial = Material.WRITABLE_BOOK,
        description = listOf(
            "Extra lore appended to preview reward items.",
            "Useful for showing reward metadata in preview."
        ),
        newValueFactory = EditorEntryFactories.text("Enter reward lore line:"),
        visibleWhen = visibleWhen
    )
    map(
        customButtons,
        displayName = "Custom Buttons",
        searchTags = listOf("buttons", "custom buttons", "pagination", "next page", "prev page"),
        iconMaterial = Material.STONE_BUTTON,
        description = listOf(
            "Additional buttons shown in the preview menu.",
            "Use ID 'next-page' or 'prev-page' to add pagination buttons.",
            "Pagination buttons are only visible when another page is available."
        ),
        mapKeyPrompt = "Enter custom button ID:",
        newMapEntryFactory = previewButtonEntryFactory(),
        visibleWhen = visibleWhen
    ) {
        with(PreviewButtonData) { defineEditor() }
    }
}
