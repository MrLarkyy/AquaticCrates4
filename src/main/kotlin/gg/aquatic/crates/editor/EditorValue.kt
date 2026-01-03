package gg.aquatic.crates.editor

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface EditorValue<T> {
    // 1. What to show in the GUI
    fun getDisplayItem(): ItemStack
    
    // 2. What happens when the player clicks this icon
    fun onClick(player: Player, updateParent: () -> Unit)
    
    // 3. Return a deep-copied version of this editor state
    fun clone(): EditorValue<T>

    // The actual data being edited
    var value: T
}
