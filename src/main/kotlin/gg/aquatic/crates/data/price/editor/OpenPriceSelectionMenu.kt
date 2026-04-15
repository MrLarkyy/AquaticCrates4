package gg.aquatic.crates.data.price.editor

import gg.aquatic.crates.data.price.*

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicSelectionMenu
import gg.aquatic.waves.serialization.editor.meta.EntryFactory

object OpenPriceSelectionMenu {
    private val definitions = OpenPriceTypes.definitions.map { definition ->
        PolymorphicSelectionMenu.Definition(
            id = definition.id,
            displayName = definition.displayName,
            description = definition.description,
            icon = definition.icon
        )
    }

    val entryFactory: EntryFactory = PolymorphicSelectionMenu.entryFactory(
        title = "Select Open Price",
        entrySlots = listOf(13),
        definitions = definitions,
        elementFactory = OpenPriceTypes::defaultElement
    )

    suspend fun select(player: org.bukkit.entity.Player): String? {
        return PolymorphicSelectionMenu.selectType(
            player = player,
            title = "Select Open Price",
            inventoryType = gg.aquatic.kmenu.inventory.InventoryType.GENERIC9X3,
            entrySlots = listOf(13),
            cancelSlot = 22,
            definitions = definitions
        )
    }
}


