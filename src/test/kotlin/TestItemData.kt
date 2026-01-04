import gg.aquatic.crates.editor.Configurable
import gg.aquatic.crates.editor.Serializers.COMPONENT
import gg.aquatic.crates.editor.Serializers.INT
import gg.aquatic.crates.editor.Serializers.MATERIAL
import gg.aquatic.crates.editor.handlers.ChatInputHandler
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class TestItemData : Configurable() {

    val material = edit("material", Material.STONE, MATERIAL,
        icon = { mat -> ItemStack(mat).apply { editMeta { it.displayName(Component.text("§eMaterial: ${mat.name}")) } } },
        handler = ChatInputHandler("Enter Material:") { Material.matchMaterial(it) }
    )

    val amount = edit("amount", 1, INT,
        icon = { amt -> ItemStack(Material.PAPER).apply { amount = amt; editMeta { it.displayName(Component.text("§bAmount: $amt")) } } },
        handler = ChatInputHandler.forInteger("Enter Amount:")
    )

    // A list of complex objects (Lore)
    val lore = editList("lore", emptyList(), COMPONENT,
        elementIcon = { line -> ItemStack(Material.PAPER).apply { editMeta { it.displayName(line) } } },
        elementHandler = ChatInputHandler.forComponent("Enter line:"),
        listIcon = { list -> ItemStack(Material.BOOK).apply { editMeta { it.displayName(Component.text("§6Edit Lore (${list.size} lines)")) } } },
        openGui = { player, editor, update -> /* Open Generic List GUI */ }
    )
}