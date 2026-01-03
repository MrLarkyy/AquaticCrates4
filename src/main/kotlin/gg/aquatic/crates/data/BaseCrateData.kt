package gg.aquatic.crates.data

import gg.aquatic.crates.editor.EditableValue
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class BaseCrateData(
    val id: String,
    displayName: Component
) {

    val displayName = EditableValue(
        displayName,
        { it },
        {
            ItemStack.of(Material.NAME_TAG).apply {
                editMeta {
                    it.displayName(displayName)
                }
            }
        }
    )


}