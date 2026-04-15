package gg.aquatic.crates.data

import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.crates.data.editor.PreviewSectionFieldAdapter
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.hologram.editor.HologramSettingsSectionFieldAdapter
import gg.aquatic.crates.data.interaction.editor.InteractionSettingsSectionFieldAdapter
import gg.aquatic.crates.data.key.KeySettingsSectionFieldAdapter
import gg.aquatic.crates.data.milestone.MilestoneSettingsSectionFieldAdapter
import gg.aquatic.crates.data.processor.BasicRewardProcessorData
import gg.aquatic.crates.data.processor.ChooseRewardProcessorData
import gg.aquatic.crates.data.processor.editor.RewardProcessorSectionFieldAdapter
import gg.aquatic.crates.data.processor.RewardProcessorType
import gg.aquatic.crates.data.processor.WorldChooseRewardProcessorData
import gg.aquatic.crates.data.provider.ConditionalPoolsRewardProviderData
import gg.aquatic.crates.data.provider.RewardProviderSectionFieldAdapter
import gg.aquatic.crates.data.provider.RewardProviderType
import gg.aquatic.crates.data.provider.SimpleRewardProviderData
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedEditorSchemaBuilder
import org.bukkit.Material

internal fun TypedEditorSchemaBuilder<CrateData>.defineCrateDataRootSchema() {
    field(
        CrateData::displayName,
        TextFieldAdapter,
        TextFieldConfig(prompt = "Enter crate display name:", showFormattedPreview = true),
        displayName = "Display Name",
        searchTags = listOf("name", "title", "crate name", "display"),
        iconMaterial = Material.NAME_TAG,
        description = listOf("Main crate name shown in menus and other UI.")
    )
    field(
        CrateData::interactables,
        adapter = InteractionSettingsSectionFieldAdapter,
        displayName = "Interaction Settings",
        searchTags = listOf("interaction", "open", "click", "price", "conditions", "limits", "stats", "crate click", "open settings"),
        iconMaterial = Material.ARMOR_STAND,
        description = listOf(
            "Open behaviour, interactables, price groups and crate click mapping.",
            "All interaction and opening rules are grouped here."
        )
    )
    field(CrateData::openConditions, visibleWhen = { false })
    field(CrateData::disableOpenStats, visibleWhen = { false })
    field(CrateData::limits, visibleWhen = { false })
    field(CrateData::repeatableMilestones, visibleWhen = { false })
    field(CrateData::priceGroups, visibleWhen = { false })
    map(
        CrateData::rarities,
        displayName = "Rarities",
        searchTags = listOf("rarity", "rarities", "groups", "chance groups", "tiers"),
        iconMaterial = Material.NETHER_STAR,
        description = listOf(
            "All rarity groups available in this crate.",
            "Rewards from any provider reference these crate-level rarities."
        ),
        mapKeyPrompt = "Enter rarity ID:",
        newMapEntryFactory = EditorEntryFactories.map(
            keyPrompt = "Enter rarity ID:",
            keyValidator = { if (CrateDataValidators.crateIdRegex.matches(it)) null else "Use only letters, numbers, '_' or '-'." },
            valueFactory = { rarityId ->
                CrateDataFormats.yaml.encodeToNode(
                    RewardRarityData.serializer(),
                    RewardRarityData(displayName = rarityId, chance = 1.0)
                )
            }
        )
    ) {
        with(RewardRarityData) { defineEditor() }
    }
    field(
        CrateData::rewardProvider,
        adapter = RewardProviderSectionFieldAdapter,
        displayName = "Rewards",
        searchTags = listOf("rewards", "provider", "reward provider", "simple provider", "reward pool"),
        iconMaterial = Material.CHEST_MINECART,
        description = listOf(
            "Active reward provider for this crate.",
            "Left click to edit its settings.",
            "Right click to change the reward provider type."
        )
    )
    group(CrateData::rewardProvider) {
        include<SimpleRewardProviderData> {
            with(SimpleRewardProviderData) { defineEditor() }
        }
        include<ConditionalPoolsRewardProviderData> {
            with(ConditionalPoolsRewardProviderData) { defineEditor() }
        }
    }
    field(
        CrateData::rewardProcessor,
        adapter = RewardProcessorSectionFieldAdapter,
        displayName = "Reward Processor",
        searchTags = listOf("processor", "reward processor", "basic processor", "result menu", "reward display"),
        iconMaterial = Material.HOPPER_MINECART,
        description = listOf(
            "Controls what happens after rewards are rolled.",
            "Left click to edit its settings.",
            "Right click to change the processor type."
        )
    )
    group(CrateData::rewardProcessor) {
        include<BasicRewardProcessorData> {
            with(BasicRewardProcessorData) { defineEditor() }
        }
        include<ChooseRewardProcessorData> {
            with(ChooseRewardProcessorData) { defineEditor() }
        }
        include<WorldChooseRewardProcessorData> {
            with(WorldChooseRewardProcessorData) { defineEditor() }
        }
    }
    field(
        CrateData::milestones,
        adapter = MilestoneSettingsSectionFieldAdapter,
        displayName = "Milestones",
        searchTags = listOf("milestone", "milestones", "repeatable", "opens", "alltime opens", "progress rewards"),
        iconMaterial = Material.DIAMOND,
        description = listOf(
            "Milestone rewards granted based on alltime player opens.",
            "Supports one-time and repeatable milestones."
        )
    )
    field(
        CrateData::keyItem,
        adapter = KeySettingsSectionFieldAdapter,
        displayName = "Key Settings",
        searchTags = listOf("key", "keys", "crate key", "key item", "key click", "must be held"),
        iconMaterial = Material.TRIPWIRE_HOOK,
        description = listOf(
            "All key-related settings for this crate.",
            "Edit the key item, key interaction mapping and hold requirement here."
        )
    )
    field(CrateData::keyMustBeHeld, visibleWhen = { false })
    field(CrateData::keyClickMapping, visibleWhen = { false })
    field(CrateData::crateClickMapping, visibleWhen = { false })
    field(
        CrateData::hologram,
        adapter = HologramSettingsSectionFieldAdapter,
        displayName = "Hologram Settings",
        searchTags = listOf("hologram", "holo", "lines", "view distance", "offset", "floating text"),
        iconMaterial = Material.END_CRYSTAL,
        description = listOf(
            "All hologram settings for this crate.",
            "Edit lines and view distance here."
        )
    )
    field(
        CrateData::preview,
        adapter = PreviewSectionFieldAdapter,
        displayName = "Preview",
        searchTags = listOf("preview", "menu", "preview menu", "reward slots", "buttons", "pages"),
        iconMaterial = Material.ENDER_EYE,
        description = listOf(
            "Preview menu configuration for this crate.",
            "Left click to edit it.",
            "Right click to change the preview type."
        )
    )
    optionalGroup(CrateData::preview) {
        definePreviewMenuEditor()
    }
}

