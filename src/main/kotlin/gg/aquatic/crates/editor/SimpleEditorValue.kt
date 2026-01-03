package gg.aquatic.crates.editor

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SimpleEditorValue<T>(
    override var value: T,
    private val displayIcon: (T) -> ItemStack,
    private val clickHandler: EditorClickHandler<T>,
    private val cloning: (T) -> T = { it },
) : EditorValue<T> {

    override fun getDisplayItem(): ItemStack = displayIcon(value)

    override fun onClick(player: Player, updateParent: () -> Unit) {
        clickHandler.handle(player, value) { newValue ->
            if (newValue != null) {
                value = newValue
            }
            updateParent() // Refreshes the GUI
        }
    }

    override fun clone(): SimpleEditorValue<T> {
        return SimpleEditorValue(cloning(value), displayIcon, clickHandler, cloning)
    }
}
