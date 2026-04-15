package gg.aquatic.crates.data

import gg.aquatic.crates.data.action.editor.RewardActionSelectionMenu
import gg.aquatic.crates.data.action.editor.defineRewardActionEditor
import gg.aquatic.crates.data.condition.editor.PlayerConditionSelectionMenu
import gg.aquatic.crates.data.condition.editor.definePlayerConditionEditor
import gg.aquatic.crates.data.editor.core.findByPath
import gg.aquatic.crates.data.editor.core.mapValue
import gg.aquatic.crates.data.editor.core.switchingSection
import gg.aquatic.crates.data.editor.RewardRarityFieldAdapter
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.item.StackedItemData
import gg.aquatic.crates.data.item.StackedItemDataEditor.defineBasicEditor
import gg.aquatic.crates.data.price.OpenPriceGroupData
import gg.aquatic.crates.data.range.RewardAmountRangeData
import gg.aquatic.crates.data.rewardshowcase.BetterModelRewardShowcaseData
import gg.aquatic.crates.data.rewardshowcase.ItemDisplayRewardShowcaseData
import gg.aquatic.crates.data.rewardshowcase.ModelEngineRewardShowcaseData
import gg.aquatic.crates.data.rewardshowcase.RewardShowcaseType
import gg.aquatic.crates.data.rewardshowcase.editor.RewardShowcaseSectionFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import org.bukkit.Material

object RewardDataEditorSchema {
    fun TypedNestedSchemaBuilder<RewardData>.defineEditor() {
        field(RewardData::rewardShowcaseType, visibleWhen = { false })
        field(
            RewardData::displayName,
            TextFieldAdapter,
            TextFieldConfig(prompt = "Enter reward display name:", showFormattedPreview = true),
            displayName = "Reward Name",
            searchTags = listOf("reward name", "display name", "title", "preview name"),
            iconMaterial = Material.NAME_TAG,
            description = listOf("Optional custom name used for the reward in previews.")
        )
        list(
            RewardData::conditions,
            displayName = "Conditions",
            searchTags = listOf("conditions", "requirements", "player conditions", "must pass"),
            iconMaterial = Material.IRON_BARS,
            description = listOf("Conditions that must pass before this reward can be selected."),
            newValueFactory = PlayerConditionSelectionMenu.entryFactory
        ) {
            definePlayerConditionEditor()
        }
        list(
            RewardData::limits,
            displayName = "Limits",
            searchTags = listOf("limits", "reward limits", "cap", "cooldown", "max wins"),
            iconMaterial = Material.CLOCK,
            description = listOf("Per-player rolling limits for how often this reward can be won.")
        ) {
            with(LimitData) { defineEditor() }
        }
        field(
            RewardData::chance,
            DoubleFieldAdapter,
            DoubleFieldConfig(prompt = "Enter reward chance:", min = 0.0),
            displayName = "Chance",
            searchTags = listOf("chance", "weight", "odds", "probability", "drop chance"),
            iconMaterial = Material.EMERALD,
            description = listOf("Relative weight used inside the selected rarity.")
        )
        list(
            RewardData::amountRanges,
            displayName = "Amount Ranges",
            searchTags = listOf("amount", "amount ranges", "random amount", "quantity", "stack size"),
            iconMaterial = Material.COPPER_INGOT,
            description = listOf(
                "Weighted random amount multiplier for this reward.",
                "If empty, the reward uses amount 1.",
                "The rolled value is available in win actions as %random-amount%."
            )
        ) {
            with(RewardAmountRangeData) {
                defineEditor(
                    minLabel = "Min Amount",
                    maxLabel = "Max Amount",
                    chanceLabel = "Range Weight"
                )
            }
        }
        list(
            RewardData::cost,
            displayName = "Cost",
            searchTags = listOf("cost", "buy", "purchase", "shop", "price", "price groups"),
            iconMaterial = Material.GOLD_INGOT,
            description = listOf(
                "Alternative price groups used to purchase this reward directly in preview.",
                "If empty, the reward cannot be purchased."
            ),
            newValueFactory = OpenPriceGroupData.defaultEntryFactory
        ) {
            with(OpenPriceGroupData) { defineEditor() }
        }
        field(
            RewardData::rarity,
            adapter = RewardRarityFieldAdapter,
            displayName = "Rarity",
            searchTags = listOf("rarity", "group", "tier", "rarity group"),
            iconMaterial = Material.NETHER_STAR,
            description = listOf("Which crate rarity group this reward belongs to.")
        )
        list(
            RewardData::winActions,
            displayName = "Win Actions",
            searchTags = listOf("actions", "win actions", "reward actions", "normal actions", "on win"),
            iconMaterial = Material.BLAZE_POWDER,
            description = listOf(
                "Actions executed when this reward is won.",
                "Use %player%, %random-amount%, %reward-chance% and %reward-chance-formatted% placeholders in normal opening actions.",
                "If empty, the preview item is given automatically."
            ),
            newValueFactory = RewardActionSelectionMenu.entryFactory
        ) {
            defineRewardActionEditor()
        }
        list(
            RewardData::massWinActions,
            displayName = "Mass Win Actions",
            searchTags = listOf("mass open", "mass win actions", "bulk actions", "batch actions", "mass opening"),
            iconMaterial = Material.FIRE_CHARGE,
            description = listOf(
                "Actions executed once per reward during mass opening.",
                "Use %player%, %reward-win-count% / %reward-drawn-count%, %reward-total-amount% / %reward-total-random-amount%, and %reward-chance% / %reward-chance-formatted% placeholders.",
                "If empty, mass opening falls back to the normal win action behavior."
            ),
            newValueFactory = RewardActionSelectionMenu.entryFactory
        ) {
            defineRewardActionEditor()
        }
        group(RewardData::previewItem) {
            with(StackedItemData) {
                defineBasicEditor(
                    materialLabel = "Preview Material",
                    nameLabel = "Preview Name",
                    namePrompt = "Enter preview display name:",
                    loreLabel = "Preview Lore",
                    amountLabel = "Preview Amount"
                )
            }
        }
        field(
            RewardData::fallbackPreviewItem,
            displayName = "Fallback Item",
            searchTags = listOf("fallback", "locked item", "unavailable item", "preview fallback"),
            iconMaterial = Material.CHEST_MINECART,
            description = listOf(
                "Optional item shown in preview when the player does not meet this reward's conditions.",
                "This lets you show a locked or unavailable variant instead of the normal preview item.",
                "If left unset, the normal preview item is shown even when the reward is unavailable.",
                "Click to create it. Press Q to clear it back to null."
            )
        )
        optionalGroup(RewardData::fallbackPreviewItem) {
            with(StackedItemData) {
                defineBasicEditor(
                    materialLabel = "Fallback Material",
                    nameLabel = "Fallback Name",
                    namePrompt = "Enter fallback display name:",
                    loreLabel = "Fallback Lore",
                    amountLabel = "Fallback Amount"
                )
            }
        }
        switchingSection(
            RewardData::itemDisplayShowcase,
            adapter = RewardShowcaseSectionFieldAdapter,
            displayName = "Reward Showcase",
            iconMaterial = Material.ENDER_EYE,
            description = listOf(
                "Controls how this reward is shown in world-based processors and presentations.",
                "Left click to edit showcase settings.",
                "Right click to change the showcase type."
            ),
            searchTags = listOf("showcase", "reward showcase", "world display", "world choose", "presentation"),
            visibleWhen = { it.isRewardShowcaseType(RewardShowcaseType.ITEM_DISPLAY) }
        ) {
            with(ItemDisplayRewardShowcaseData) { defineEditor() }
        }
        switchingSection(
            RewardData::modelEngineShowcase,
            adapter = RewardShowcaseSectionFieldAdapter,
            displayName = "Reward Showcase",
            iconMaterial = Material.ENDER_EYE,
            description = listOf(
                "Controls how this reward is shown in world-based processors and presentations.",
                "Left click to edit showcase settings.",
                "Right click to change the showcase type."
            ),
            searchTags = listOf("showcase", "reward showcase", "world display", "world choose", "presentation", "meg", "modelengine"),
            visibleWhen = { it.isRewardShowcaseType(RewardShowcaseType.MODEL_ENGINE) }
        ) {
            with(ModelEngineRewardShowcaseData) { defineEditor() }
        }
        switchingSection(
            RewardData::betterModelShowcase,
            adapter = RewardShowcaseSectionFieldAdapter,
            displayName = "Reward Showcase",
            iconMaterial = Material.ENDER_EYE,
            description = listOf(
                "Controls how this reward is shown in world-based processors and presentations.",
                "Left click to edit showcase settings.",
                "Right click to change the showcase type."
            ),
            searchTags = listOf("showcase", "reward showcase", "world display", "world choose", "presentation", "bettermodel", "bm"),
            visibleWhen = { it.isRewardShowcaseType(RewardShowcaseType.BETTER_MODEL) }
        ) {
            with(BetterModelRewardShowcaseData) { defineEditor() }
        }
    }

    private fun EditorFieldContext.isRewardShowcaseType(type: RewardShowcaseType): Boolean {
        val candidates = (pathSegments.size downTo 0).map { pathSegments.take(it) }
        val ownerType = candidates.firstNotNullOfOrNull { candidate ->
            root.findByPath(candidate)
                ?.mapValue("rewardShowcaseType")
                ?.stringContentOrNull
        }
        return RewardShowcaseType.of(ownerType ?: RewardShowcaseType.ITEM_DISPLAY.id) == type
    }
}

