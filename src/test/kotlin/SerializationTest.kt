import gg.aquatic.crates.editor.Serializers.COMPONENT
import gg.aquatic.crates.editor.Serializers.MATERIAL
import gg.aquatic.crates.editor.value.ElementBehavior
import gg.aquatic.crates.editor.value.SimpleEditorValue
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SerializationTest {

    @Test
    fun `test item data round trip`() {
        val original = TestItemData()
        original.material.value = Material.DIAMOND_SWORD
        original.amount.value = 5

        val config = MemoryConfiguration()
        original.serialize(config)

        // We use the same Serializer to read it back, ensuring symmetry
        val loadedMaterial = MATERIAL.deserialize(config, "material")
        assertEquals(Material.DIAMOND_SWORD, loadedMaterial)
        assertEquals(5, config.getInt("amount"))
    }

    @Test
    fun `test primitive list serialization`() {
        val data = TestItemData()
        val behavior = ElementBehavior<Component>(
            icon = { ItemStack(Material.PAPER) },
            handler = { _, _, _, _ -> }
        )

        val text = "Test Line"
        val testEditor = SimpleEditorValue("__value", Component.text(text), behavior.icon, behavior.handler, { it }, COMPONENT)
        data.lore.value.add(testEditor)

        val config = MemoryConfiguration()
        data.serialize(config)

        // Instead of raw getList, use our getSectionList helper if it's a list of sections,
        // but since this is a list of primitives, we check if the serialized output matches the expectation of our COMPONENT serializer
        val rawList = config.getList("lore")!!
        val firstEntry = rawList[0]

        // If MemoryConfiguration didn't convert it to string yet, we use our serializer's decode logic to check what SHOULD be there
        val temp = MemoryConfiguration()
        COMPONENT.serialize(temp, "temp", Component.text(text))
        assertEquals(temp.get("temp"), firstEntry)
    }

    @Test
    fun `test nested configurable list serialization`() {
        val crate = TestCrateData()
        val reward = TestItemData()
        reward.material.value = Material.GOLD_INGOT

        crate.rewards.value.add(reward.asEditorValue("reward", { it.material.getDisplayItem() }, { _, _, _ -> }))

        val config = MemoryConfiguration()
        crate.serialize(config)

        val rewardsList = config.getList("rewards") as List<*>
        val firstReward = rewardsList[0] as Map<*, *>

        // Use the MATERIAL serializer to verify the content of the map entry
        val temp = MemoryConfiguration()
        temp.set("material", firstReward["material"])

        assertEquals(Material.GOLD_INGOT, MATERIAL.deserialize(temp, "material"))
    }
}
