import gg.aquatic.crates.editor.ValueSerializer
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SerializationTest {

    @Test
    fun `test serialization with configurable dsl`() {
        val config = YamlConfiguration()
        // We use the ListSection serializer which calls .serialize() on our Configurable objects
        val listSerializer = ValueSerializer.ListSection { TestItemData() }

        // 1. Prepare Data using the new DSL-backed properties
        val originalItems = mutableListOf(
            TestItemData().apply {
                material.value = Material.DIAMOND
                amount.value = 64
            },
            TestItemData().apply {
                material.value = Material.DIRT
                amount.value = 7
            }
        )

        // 2. Serialize to YAML
        listSerializer.serialize(config, "items", originalItems)

        // DEBUG: Check output
        println("Generated YAML:\n${config.saveToString()}")

        // 3. Deserialize back
        val loadedItems = listSerializer.deserialize(config, "items")

        // 4. Assertions
        assertEquals(2, loadedItems.size)

        assertEquals(Material.DIAMOND, loadedItems[0].material.value)
        assertEquals(64, loadedItems[0].amount.value)

        assertEquals(Material.DIRT, loadedItems[1].material.value)
        assertEquals(7, loadedItems[1].amount.value)

        // Check that order is preserved (important for GUIs)
        assertEquals("material", loadedItems[0].getEditorValues()[0].key)
        assertEquals("amount", loadedItems[0].getEditorValues()[1].key)
    }
}
