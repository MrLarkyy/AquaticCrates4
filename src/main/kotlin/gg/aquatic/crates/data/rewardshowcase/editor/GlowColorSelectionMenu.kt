package gg.aquatic.crates.data.rewardshowcase.editor

import gg.aquatic.crates.data.editor.menu.PagedSelectionMenu
import gg.aquatic.crates.data.rewardshowcase.GlowColor
import gg.aquatic.kmenu.inventory.InventoryType
import gg.aquatic.stacked.stackedItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

object GlowColorSelectionMenu {

    sealed interface SelectionResult {
        data object Cancelled : SelectionResult
        data class Selected(val colorId: String) : SelectionResult
        data object PreviousPage : SelectionResult
        data object NextPage : SelectionResult
    }

    suspend fun select(player: Player, currentValue: String?): SelectionResult {
        val options = GlowColor.entries
        return when (
            val result = PagedSelectionMenu.select(
                player = player,
                title = "Select Glow Color",
                options = options,
                currentValue = GlowColor.of(currentValue) ?: GlowColor.WHITE,
                inventoryType = InventoryType.GENERIC9X6,
                buildEntry = ::buildEntry,
                cancelDescription = "Keep the current glow color",
                navigationDescription = "Open another page of glow colors"
            )
        ) {
            PagedSelectionMenu.Result.Cancelled -> SelectionResult.Cancelled
            is PagedSelectionMenu.Result.Selected -> SelectionResult.Selected(result.value.id)
            PagedSelectionMenu.Result.PreviousPage -> SelectionResult.PreviousPage
            PagedSelectionMenu.Result.NextPage -> SelectionResult.NextPage
        }
    }

    private fun buildEntry(option: GlowColor, currentValue: GlowColor?) = stackedItem(
        option.material
    ) {
        displayName = text(option.displayName, NamedTextColor.AQUA)
        lore += text("Use the ${option.displayName.lowercase()} MC glow color.", NamedTextColor.GRAY)
        if (option == currentValue) {
            lore += text(" ", NamedTextColor.DARK_GRAY)
            lore += text("Currently selected", NamedTextColor.GREEN)
        }
    }.getItem()

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color).decoration(TextDecoration.ITALIC, false)
    }
}
