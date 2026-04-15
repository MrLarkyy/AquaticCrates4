package gg.aquatic.crates.message.condition.editor

import gg.aquatic.crates.message.condition.*

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicSelectionMenu
import gg.aquatic.waves.serialization.editor.meta.EntryFactory

object MessageConditionSelectionMenu {
    private val entrySlots = listOf(13)
    private val definitions = MessageConditionTypes.definitions.map { definition ->
        PolymorphicSelectionMenu.Definition(
            id = definition.id,
            displayName = definition.displayName,
            description = definition.description,
            icon = definition.icon
        )
    }

    val entryFactory: EntryFactory = PolymorphicSelectionMenu.entryFactory(
        title = "Select Visibility Condition",
        entrySlots = entrySlots,
        definitions = definitions,
        elementFactory = MessageConditionTypes::defaultElement
    )

    suspend fun select(player: org.bukkit.entity.Player): String? {
        return PolymorphicSelectionMenu.selectType(
            player = player,
            title = "Select Visibility Condition",
            inventoryType = gg.aquatic.kmenu.inventory.InventoryType.GENERIC9X3,
            entrySlots = entrySlots,
            cancelSlot = 22,
            definitions = definitions
        )
    }
}


