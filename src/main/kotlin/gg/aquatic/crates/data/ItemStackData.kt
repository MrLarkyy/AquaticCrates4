package gg.aquatic.crates.data

import gg.aquatic.crates.editor.SimpleEditorValue
import gg.aquatic.crates.editor.handlers.ChatInputHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.Optional

class ItemStackData(
    initialMaterial: Material = Material.STONE,
    initialDisplayName: Optional<Component> = Optional.empty(),
    initialLore: List<Component> = emptyList(),
    initialAmount: Int = 1
) {
    // 1. Material Selector
    val material = SimpleEditorValue(
        initialMaterial,
        displayIcon = { mat ->
            ItemStack(mat).apply { editMeta { it.displayName(Component.text("Change Material")) } }
        },
        clickHandler = { player, current, update ->
            // Logic to open a Material Selector GUI
            // update(selectedMaterial)
        }
    )

    // 2. Display Name Editor
    val displayName = SimpleEditorValue(
        initialDisplayName,
        cloning = { it },
        displayIcon = { name ->
            if (name.isPresent) {
                ItemStack(Material.NAME_TAG).apply {
                    editMeta { it.displayName(name.get()) }
                }
            } else {
                ItemStack(Material.NAME_TAG).apply { editMeta { it.displayName(Component.text("Enter display name:")) } }
            }

        },
        clickHandler = ChatInputHandler("Enter display name: ") { str ->
            if (str.lowercase() == "null") Optional.empty()
            else Optional.of(MiniMessage.miniMessage().deserialize(str))
        }
    )

    // 3. Amount Editor
    val amount = SimpleEditorValue(
        initialAmount,
        displayIcon = { amt ->
            ItemStack(Material.PAPER).apply {
                editMeta { it.displayName(Component.text("Amount: $amt")) }
                setAmount(amt.coerceIn(1, 64))
            }
        },
        clickHandler = { player, current, update ->
            // Simple logic: Left click +1, Right click -1
            // update(current + 1)
        }
    )

    // 4. Lore Editor (Recursive/Nested)
    // For a list, you might point to an 'EditableList' or a sub-menu
    val lore = SimpleEditorValue(
        initialLore,
        displayIcon = { lines ->
            ItemStack(Material.BOOK).apply {
                editMeta { it.displayName(Component.text("Edit Lore (${lines.size} lines)")) }
            }
        },
        clickHandler = { player, current, update ->
            // Open a specific Lore Editor GUI
        }
    )
}
