package gg.aquatic.crates.data.interactable.editor

import gg.aquatic.crates.data.interactable.*

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicSelectionMenu
import gg.aquatic.waves.serialization.editor.meta.EntryFactory
import org.bukkit.Bukkit

object CrateInteractableSelectionMenu {

    private val entrySlots = listOf(10, 12, 14, 16)
    private val definitions = CrateInteractableTypes.definitions.map { definition ->
        PolymorphicSelectionMenu.Definition(
            id = definition.id,
            displayName = definition.displayName,
            description = definition.description,
            icon = definition.icon,
            availability = { _ ->
                when (definition.id) {
                    "meg" -> PolymorphicSelectionMenu.Availability(
                        available = Bukkit.getPluginManager().getPlugin("ModelEngine") != null,
                        lockedDescription = listOf("Requires ModelEngine on the server"),
                        deniedMessage = "ModelEngine is not installed, so this interactable type is unavailable."
                    )

                    else -> PolymorphicSelectionMenu.Availability(true)
                }
            }
        )
    }

    val entryFactory: EntryFactory = PolymorphicSelectionMenu.entryFactory(
        title = "Select Interactable",
        entrySlots = entrySlots,
        definitions = definitions,
        elementFactory = CrateInteractableTypes::defaultElement
    )

    suspend fun select(player: org.bukkit.entity.Player): String? {
        return PolymorphicSelectionMenu.selectType(
            player = player,
            title = "Select Interactable",
            inventoryType = gg.aquatic.kmenu.inventory.InventoryType.GENERIC9X3,
            entrySlots = entrySlots,
            cancelSlot = 22,
            definitions = definitions
        )
    }
}


