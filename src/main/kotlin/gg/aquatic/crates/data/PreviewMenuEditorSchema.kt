package gg.aquatic.crates.data

import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.EntryFactory
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import org.bukkit.Material

fun TypedNestedSchemaBuilder<PreviewMenuData>.definePreviewMenuEditor() {
    field(PreviewMenuData::previewType, visibleWhen = { false })
    include<PreviewMenuData>(visibleWhen = { it.isPreviewType(PREVIEW_TYPE_AUTOMATIC) }) {
        definePreviewInventorySection(
            inventory = PreviewMenuData::inventory,
            title = PreviewMenuData::title,
            titleLabel = "Preview Title",
            titlePrompt = "Enter preview title:",
            rewardSlots = PreviewMenuData::rewardSlots,
            randomRewardSlots = PreviewMenuData::randomRewardSlots,
            randomRewardSwitchTicks = PreviewMenuData::randomRewardSwitchTicks,
            randomRewardUnique = PreviewMenuData::randomRewardUnique,
            rewardLore = PreviewMenuData::rewardLore,
            customButtons = PreviewMenuData::customButtons,
            inventoryTypeLabel = "Preview Inventory Type",
            visibleWhen = { it.isPreviewType(PREVIEW_TYPE_AUTOMATIC) }
        )
    }
    list(
        PreviewMenuData::pages,
        displayName = "Pages",
        searchTags = listOf("pages", "custom pages", "multi page", "page list"),
        iconMaterial = Material.BOOK,
        description = listOf("Custom preview pages used when preview type is set to custom-pages."),
        newValueFactory = EntryFactory { _, _ ->
            CrateDataFormats.yaml.encodeToNode(PreviewPageData.serializer(), PreviewPageData())
        },
        visibleWhen = { it.isPreviewType(PREVIEW_TYPE_CUSTOM_PAGES) }
    ) {
        definePreviewPageEditor()
    }
}

fun TypedNestedSchemaBuilder<PreviewPageData>.definePreviewPageEditor() {
    definePreviewInventorySection(
        inventory = PreviewPageData::inventory,
        title = PreviewPageData::title,
        titleLabel = "Page Title",
        titlePrompt = "Enter page title:",
        rewardSlots = PreviewPageData::rewardSlots,
        randomRewardSlots = PreviewPageData::randomRewardSlots,
        randomRewardSwitchTicks = PreviewPageData::randomRewardSwitchTicks,
        randomRewardUnique = PreviewPageData::randomRewardUnique,
        rewardLore = PreviewPageData::rewardLore,
        customButtons = PreviewPageData::customButtons,
        inventoryTypeLabel = "Page Inventory Type",
    )
}

internal fun previewButtonEntryFactory() = EditorEntryFactories.map(
    keyPrompt = "Enter custom button ID:",
    keyValidator = { if (CrateDataValidators.crateIdRegex.matches(it)) null else "Use only letters, numbers, '_' or '-'." },
    valueFactory = { buttonId ->
        CrateDataFormats.yaml.encodeToNode(
            PreviewButtonData.serializer(),
            PreviewButtonData(
                item = gg.aquatic.crates.data.item.StackedItemData(
                    material = Material.PAPER.name,
                    displayName = "<yellow>$buttonId"
                )
            )
        )
    }
)

internal fun EditorFieldContext.isPreviewType(type: String): Boolean {
    val current = (value as? YamlMap)
        ?.get<YamlNode>("previewType")
        ?.stringContentOrNull
    if (current != null) {
        return normalizePreviewType(current).equals(type, true)
    }

    val rootType = (root as? YamlMap)
        ?.get<YamlNode>("preview")
        ?.let { it as? YamlMap }
        ?.get<YamlNode>("previewType")
        ?.stringContentOrNull

    return normalizePreviewType(rootType ?: PREVIEW_TYPE_AUTOMATIC).equals(type, true)
}
