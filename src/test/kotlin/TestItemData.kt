import gg.aquatic.crates.ClickType
import gg.aquatic.crates.editor.Configurable
import gg.aquatic.crates.editor.Serializers.COMPONENT
import gg.aquatic.crates.editor.Serializers.INT
import gg.aquatic.crates.editor.Serializers.MATERIAL
import gg.aquatic.crates.editor.handlers.ChatInputHandler
import gg.aquatic.crates.editor.value.ElementBehavior
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class TestItemData : Configurable<TestItemData>() {

    val material = edit(
        "material",
        Material.STONE,
        MATERIAL,
        icon = { mat ->
            ItemStack(mat).apply {
                editMeta { it.displayName(Component.text("§eMaterial: ${mat.name}")) }
            }
        },
        handler = ChatInputHandler("Enter Material:") { Material.matchMaterial(it) })

    val amount = edit(
        "amount", 1, INT, icon = { amt ->
            ItemStack(Material.PAPER).apply {
                amount = amt; editMeta { it.displayName(Component.text("§bAmount: $amt")) }
            }
        }, handler = { p, current, clickType, update ->
            when (clickType) {
                ClickType.SHIFT_LEFT -> {
                    ChatInputHandler.forInteger("Enter Amount:").handle(p, current, clickType, update)
                }
                ClickType.LEFT -> {
                    update(current + 1)
                }
                ClickType.RIGHT -> {
                    update(current - 1)
                }

                else -> {

                }
            }
        }
    )

    // A list of simple objects (Lore) using the new behavior pattern
    val lore = editList(
        "lore",
        emptyList(),
        COMPONENT,
        behavior = ElementBehavior(
            icon = { line -> ItemStack(Material.PAPER).apply { editMeta { it.displayName(line) } } },
            handler = ChatInputHandler.forComponent("Enter line:")
        ),
        onAdd = { Component.empty() },
        listIcon = { list ->
            ItemStack(Material.BOOK).apply {
                editMeta { it.displayName(Component.text("§6Edit Lore (${list.size} lines)")) }
            }
        },
        guiHandler = { player, editor, update -> /* Open Generic List GUI */ })

    override fun copy(): TestItemData {
        return TestItemData().also { copy ->
            copy.material.value = this.material.value
            copy.amount.value = this.amount.value
            copy.lore.value = this.lore.value.map { it.clone() }.toMutableList()
        }
    }
}