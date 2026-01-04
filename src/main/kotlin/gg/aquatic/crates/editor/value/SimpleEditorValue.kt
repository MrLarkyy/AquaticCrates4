package gg.aquatic.crates.editor.value

import gg.aquatic.crates.ClickType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SimpleEditorValue<T>(
    override val key: String,
    override var value: T,
    private val displayIcon: (T) -> ItemStack,
    private val clickHandler: gg.aquatic.crates.editor.EditorClickHandler<T>,
    private val cloning: (T) -> T = { it },
    override val serializer: gg.aquatic.crates.editor.ValueSerializer<T>,
    override val visibleIf: () -> Boolean = { true },
    override val defaultValue: T? = null,
) : EditorValue<T> {

    override fun getDisplayItem(): ItemStack = displayIcon(value)

    override fun onClick(player: Player, clickType: ClickType, updateParent: () -> Unit) {
        clickHandler.handle(player, value, clickType) { newValue ->
            if (newValue != null) {
                value = newValue
            }
            updateParent() // Refreshes the GUI
        }
    }

    override fun clone(): SimpleEditorValue<T> {
        return SimpleEditorValue(
            key,
            cloning(value),
            displayIcon,
            clickHandler,
            cloning,
            serializer,
            visibleIf,
            defaultValue
        )
    }
}
