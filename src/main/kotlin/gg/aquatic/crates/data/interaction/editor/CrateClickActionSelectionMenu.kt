package gg.aquatic.crates.data.interaction.editor

import gg.aquatic.crates.data.interaction.*

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicSelectionMenu
import gg.aquatic.kmenu.inventory.InventoryType
import gg.aquatic.waves.serialization.editor.meta.EntryFactory

object CrateClickActionSelectionMenu {
    private val entrySlots = listOf(10, 11, 12, 13, 14, 15, 16, 19, 20, 21)

    fun entryFactory(allowDestroy: Boolean): EntryFactory {
        val definitions = definitions(allowDestroy)
        return PolymorphicSelectionMenu.entryFactory(
            title = "Select Click Action",
            inventoryType = InventoryType.GENERIC9X4,
            entrySlots = entrySlots,
            cancelSlot = 31,
            definitions = definitions,
            elementFactory = CrateClickActionTypes::defaultElement
        )
    }

    suspend fun select(player: org.bukkit.entity.Player, allowDestroy: Boolean): String? {
        return PolymorphicSelectionMenu.selectType(
            player = player,
            title = "Select Click Action",
            inventoryType = InventoryType.GENERIC9X4,
            entrySlots = entrySlots,
            cancelSlot = 31,
            definitions = definitions(allowDestroy)
        )
    }

    private fun definitions(allowDestroy: Boolean): List<PolymorphicSelectionMenu.Definition> {
        return CrateClickActionTypes.definitions
            .filterNot { !allowDestroy && it.id == "destroy" }
    }
}


