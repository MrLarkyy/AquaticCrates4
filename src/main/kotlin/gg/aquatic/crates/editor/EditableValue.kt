package gg.aquatic.crates.editor

import org.bukkit.inventory.ItemStack

class EditableValue<A>(
    currentValue: A,
    val cloning: (A) -> A = { it },
    val displayItem: (A) -> ItemStack
): Cloneable {

    var currentValue: A = currentValue
        private set


}