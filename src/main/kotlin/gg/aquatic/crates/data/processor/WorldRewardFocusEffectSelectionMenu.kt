package gg.aquatic.crates.data.processor

import gg.aquatic.crates.data.CrateDataFormats
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.polymorphic.PolymorphicSelectionMenu
import gg.aquatic.kmenu.inventory.InventoryType
import gg.aquatic.waves.serialization.editor.meta.EntryFactory
import org.bukkit.Material

object WorldRewardFocusEffectSelectionMenu {
    private val entrySlots = listOf(11, 13, 15, 31)

    private val definitions = listOf(
        PolymorphicSelectionMenu.Definition(
            id = "scale",
            displayName = "Scale",
            description = listOf("Scales the focused reward showcase."),
            icon = Material.SLIME_BALL,
        ),
        PolymorphicSelectionMenu.Definition(
            id = "glow",
            displayName = "Glow",
            description = listOf("Applies glow to the focused reward showcase."),
            icon = Material.GLOW_INK_SAC,
        ),
        PolymorphicSelectionMenu.Definition(
            id = "vertical-offset",
            displayName = "Vertical Offset",
            description = listOf("Lifts the focused reward showcase upwards."),
            icon = Material.SCAFFOLDING,
        )
    )

    val entryFactory: EntryFactory = PolymorphicSelectionMenu.entryFactory(
        title = "Select Focus Effect",
        inventoryType = InventoryType.GENERIC9X3,
        entrySlots = entrySlots,
        cancelSlot = 22,
        definitions = definitions,
        elementFactory = ::defaultElement
    )

    private fun defaultElement(type: String) = when (type) {
        "scale" -> CrateDataFormats.yaml.encodeToNode(
            WorldRewardFocusEffectData.serializer(),
            ScaleWorldRewardFocusEffectData()
        )
        "glow" -> CrateDataFormats.yaml.encodeToNode(
            WorldRewardFocusEffectData.serializer(),
            GlowWorldRewardFocusEffectData()
        )
        "vertical-offset" -> CrateDataFormats.yaml.encodeToNode(
            WorldRewardFocusEffectData.serializer(),
            VerticalOffsetWorldRewardFocusEffectData()
        )
        else -> null
    }
}
