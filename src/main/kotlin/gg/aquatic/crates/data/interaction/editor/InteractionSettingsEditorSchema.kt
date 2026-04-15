package gg.aquatic.crates.data.interaction.editor

import gg.aquatic.crates.data.interaction.*

import gg.aquatic.crates.data.LimitData
import gg.aquatic.crates.data.resolveCrateDataDescriptor
import gg.aquatic.crates.data.condition.editor.OpenPlayerConditionEntryFieldAdapter
import gg.aquatic.crates.data.condition.editor.OpenPlayerConditionSelectionMenu
import gg.aquatic.crates.data.condition.editor.definePlayerConditionEditor
import gg.aquatic.crates.data.interactable.BlockCrateInteractableData
import gg.aquatic.crates.data.interactable.BetterModelCrateInteractableData
import gg.aquatic.crates.data.interactable.editor.CrateInteractableEntryFieldAdapter
import gg.aquatic.crates.data.interactable.editor.CrateInteractableSelectionMenu
import gg.aquatic.crates.data.interactable.EntityCrateInteractableData
import gg.aquatic.crates.data.interactable.MEGCrateInteractableData
import gg.aquatic.crates.data.interactable.MultiBlockCrateInteractableData
import gg.aquatic.crates.data.price.OpenPriceGroupData
import gg.aquatic.waves.serialization.editor.meta.EditableModel
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.TypedEditorSchemaBuilder
import org.bukkit.Material

object InteractionSettingsEditorSchema : EditableModel<InteractionSettingsData>(InteractionSettingsData.serializer()) {
    override fun resolveDescriptor(context: EditorFieldContext) = resolveCrateDataDescriptor(context)

    override fun TypedEditorSchemaBuilder<InteractionSettingsData>.define() {
        list(
            InteractionSettingsData::interactables,
            displayName = "Interactables",
            searchTags = listOf("interactables", "click objects", "clientside", "entity", "block", "hologram click"),
            iconMaterial = Material.ARMOR_STAND,
            description = listOf("Clientside objects players can click to open this crate."),
            newValueFactory = CrateInteractableSelectionMenu.entryFactory
        ) {
            fieldPattern(
                displayName = "Interactable",
                adapter = CrateInteractableEntryFieldAdapter,
                description = listOf(
                    "Left click to edit this interactable.",
                    "Right click to change its interactable type."
                )
            )
            include { with(BlockCrateInteractableData) { defineEditor() } }
            include { with(BetterModelCrateInteractableData) { defineEditor() } }
            include { with(EntityCrateInteractableData) { defineEditor() } }
            include { with(MEGCrateInteractableData) { defineEditor() } }
            include { with(MultiBlockCrateInteractableData) { defineEditor() } }
        }
        list(
            InteractionSettingsData::openConditions,
            displayName = "Open Conditions",
            searchTags = listOf("open conditions", "conditions", "requirements", "must pass", "open requirements"),
            iconMaterial = Material.TRIPWIRE_HOOK,
            description = listOf("Conditions that must pass before the crate can be opened."),
            newValueFactory = OpenPlayerConditionSelectionMenu.entryFactory
        ) {
            definePlayerConditionEditor(
                includeOpenOnlyConditions = true,
                adapter = OpenPlayerConditionEntryFieldAdapter
            )
        }
        field(
            InteractionSettingsData::disableOpenStats,
            displayName = "Disable Open Stats",
            searchTags = listOf("stats", "disable stats", "no stats", "tracking", "database"),
            prompt = "Enter true or false:",
            iconMaterial = Material.LECTERN,
            description = listOf("If enabled, openings and won rewards from this crate will not be written into the stats database.")
        )
        list(
            InteractionSettingsData::limits,
            displayName = "Limits",
            searchTags = listOf("limits", "open limits", "crate limits", "cooldown", "max opens"),
            iconMaterial = Material.CLOCK,
            description = listOf("Per-player rolling limits for how often this crate can be opened.")
        ) {
            with(LimitData) { defineEditor() }
        }
        list(
            InteractionSettingsData::priceGroups,
            displayName = "Price Groups",
            searchTags = listOf("price", "prices", "price groups", "open cost", "cost", "keys", "currency"),
            iconMaterial = Material.GOLD_INGOT,
            description = listOf(
                "Alternative price groups used to open this crate.",
                "If one group can be paid in full, the crate opens."
            ),
            newValueFactory = OpenPriceGroupData.defaultEntryFactory
        ) {
            with(OpenPriceGroupData) { defineEditor() }
        }
        group(InteractionSettingsData::crateClickMapping) {
            with(CrateClickMappingData) { defineEditor("Crate Click") }
        }
    }
}



