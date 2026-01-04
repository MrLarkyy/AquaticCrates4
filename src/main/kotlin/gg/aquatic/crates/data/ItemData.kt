package gg.aquatic.crates.data

import gg.aquatic.crates.editor.ValueSerializer
import gg.aquatic.crates.editor.handlers.ChatInputHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionType
import java.util.Optional

class ItemData(
    initialMaterial: Material = Material.STONE,
    initialDisplayName: Optional<Component> = Optional.empty(),
    initialLore: List<Component> = emptyList(),
    initialAmount: Int = 1
) {
    // 1. Material Selector
    val material = SimpleEditorValue(
        "material",
        initialMaterial,
        displayIcon = { mat ->
            ItemStack(mat).apply { editMeta { it.displayName(Component.text("Change Material")) } }
        },
        clickHandler = ChatInputHandler("Enter material name: ") { Material.matchMaterial(it) },
        serializer = ValueSerializer.Simple(Material.STONE, encode = {
            Material.matchMaterial(it.toString())
        }, decode = { it.toString() })
    )

    // 2. Display Name Editor
    val displayName = SimpleEditorValue(
        "display-name",
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
        },
        serializer = ValueSerializer.Simple(encode = {
            Optional.of(MiniMessage.miniMessage().deserialize(it.toString()))
        }, decode = { it.toString() })
    )

    // 3. Amount Editor
    val amount = SimpleEditorValue(
        "amount",
        initialAmount,
        displayIcon = { amt ->
            ItemStack(Material.PAPER).apply {
                editMeta { it.displayName(Component.text("Amount: $amt")) }
                setAmount(amt.coerceIn(1, 64))
            }
        },
        clickHandler = ChatInputHandler.forInteger("Enter amount:"),
        serializer = ValueSerializer.Simple(encode = { it.toString().toInt() })
    )

    // 4. Lore Editor (Recursive/Nested)
    // For a list, you might point to an 'EditableList' or a sub-menu
    val lore = SimpleEditorValue(
        "lore",
        initialLore,
        displayIcon = { lines ->
            ItemStack(Material.BOOK).apply {
                editMeta { it.displayName(Component.text("Edit Lore (${lines.size} lines)")) }
            }
        },
        clickHandler = { player, current, update, click ->
            // Open a specific Lore Editor GUI
        },
        serializer = ValueSerializer.Simple(encode =
            {
                it.toString().split("\n").map { line -> MiniMessage.miniMessage().deserialize(line) }
            })
    )

    val spawnerType = SimpleEditorValue(
        "entity-type",
        EntityType.PIG,
        displayIcon = { type ->
            ItemStack(Material.PIG_SPAWN_EGG).apply {
                editMeta { it.displayName(Component.text("Spawner Type: ${type.name}")) }
            }
        },
        clickHandler = ChatInputHandler("Enter Entity Type: ") { str ->
            EntityType.entries.find { it.name.equals(str, true) }
        },
        visibleIf = { material.value == Material.SPAWNER },
        serializer = ValueSerializer.Simple(encode = {
            EntityType.entries.find { it.name.equals(it.toString(), true) }
        })
    )

    val potionType = ListEditorValue<PotionType>(
        "potion-types",
        visibleIf = { material.value.name.contains("POTION") },
        value = mutableListOf(),
        entryCloner = { it },
        icon = { potions ->
            ItemStack(Material.POTION).apply {
                editMeta { meta ->
                    meta.displayName(Component.text("Potion Types (${potions.size})"))
                    if (potions.isEmpty()) {
                        meta.lore(listOf(Component.text("No potions set!")))
                    } else {
                        meta.lore(potions.map { Component.text(it.name) })
                    }
                }
            }
        },
        openSubGui = { p, potions ->

        },
        serializer = ValueSerializer.Simple(encode = { potions ->
            TODO("Not yet implemented")
        })
    )
}
