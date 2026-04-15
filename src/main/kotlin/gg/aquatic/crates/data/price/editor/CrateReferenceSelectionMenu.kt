package gg.aquatic.crates.data.price.editor

import gg.aquatic.crates.data.price.*

import gg.aquatic.crates.crate.CrateHandler
import gg.aquatic.crates.data.editor.menu.PagedSelectionMenu
import gg.aquatic.kmenu.inventory.InventoryType
import gg.aquatic.stacked.stackedItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

object CrateReferenceSelectionMenu {

    sealed interface SelectionResult {
        data object Cancelled : SelectionResult
        data class Selected(val crateId: String?) : SelectionResult
        data object PreviousPage : SelectionResult
        data object NextPage : SelectionResult
    }

    suspend fun select(player: Player, currentValue: String?): SelectionResult {
        val crateIds = CrateHandler.crates.keys.sorted()
        val options = listOf<String?>(null) + crateIds
        return when (val result = PagedSelectionMenu.select(
            player = player,
            title = "Select Source Crate",
            options = options,
            currentValue = currentValue,
            inventoryType = InventoryType.GENERIC9X6,
            buildEntry = ::buildEntry,
            cancelDescription = "Keep the current value",
            navigationDescription = "Open another page of crates"
        )) {
            PagedSelectionMenu.Result.Cancelled -> SelectionResult.Cancelled
            is PagedSelectionMenu.Result.Selected -> SelectionResult.Selected(result.value)
            PagedSelectionMenu.Result.PreviousPage -> SelectionResult.PreviousPage
            PagedSelectionMenu.Result.NextPage -> SelectionResult.NextPage
        }
    }

    private fun buildEntry(crateId: String?, currentValue: String?) = stackedItem(
        if (crateId == null) Material.TRIPWIRE_HOOK else Material.CHEST
    ) {
        displayName = text(crateId ?: "Current Crate", NamedTextColor.AQUA)
        if (crateId == null) {
            lore += text("Uses the key of the crate", NamedTextColor.GRAY)
            lore += text("that owns this price group.", NamedTextColor.GRAY)
        } else {
            lore += text("Use the key from crate '$crateId'.", NamedTextColor.GRAY)
        }
        if (currentValue == crateId || (currentValue.isNullOrBlank() && crateId == null)) {
            lore += text(" ", NamedTextColor.DARK_GRAY)
            lore += text("Currently selected", NamedTextColor.GREEN)
        }
    }.getItem()

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }
}


