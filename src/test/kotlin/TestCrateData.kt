import gg.aquatic.crates.editor.Configurable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class TestCrateData : Configurable<TestCrateData>() {

    val rewards = editList(
        key = "rewards",
        elementFactory = { section ->
            TestItemData().apply { deserialize(section) }.asEditorValue(
                key = "reward",
                icon = { it.material.getDisplayItem() }, // 'it' is ItemData!
                onClick = { _, _, _ -> }
            )
        },
        onAdd = {
            TestItemData().asEditorValue(
                key = "reward",
                icon = { it.material.getDisplayItem() }, // No cast needed
                onClick = { _, _, _ -> }
            )
        },
        listIcon = { ItemStack(Material.CHEST) },
        guiHandler = { _, _, _ -> }
    )

    override fun copy(): TestCrateData = TestCrateData().also { copy ->
        copy.rewards.value = this.rewards.value.map { it.clone() }.toMutableList()
    }
}
