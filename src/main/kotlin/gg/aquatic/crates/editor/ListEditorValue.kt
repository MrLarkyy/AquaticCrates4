package gg.aquatic.crates.editor

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ListEditorValue<T>(
    override var value: MutableList<T>,
    private val entryCloner: (T) -> T,
    private val icon: ItemStack,
    private val openSubGui: (Player, MutableList<T>) -> Unit
) : EditorValue<MutableList<T>> {

    override fun getDisplayItem(): ItemStack = icon

    override fun onClick(player: Player, updateParent: () -> Unit) {
        // Open the sub-menu for this list
        openSubGui(player, value)
    }

    override fun clone(): ListEditorValue<T> {
        val clonedList = value.map { entryCloner(it) }.toMutableList()
        return ListEditorValue(clonedList, entryCloner, icon.clone(), openSubGui)
    }
}
