package gg.aquatic.crates.data.hologram

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.editor.polymorphic.createPolymorphicYaml
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.polymorphic.PolymorphicSelectionMenu
import gg.aquatic.waves.serialization.editor.meta.EntryFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bukkit.Material

@OptIn(ExperimentalSerializationApi::class)
object CrateHologramLineFormats {
    val module = SerializersModule {
        polymorphic(CrateHologramLineData::class) {
            subclass(TextCrateHologramLineData::class)
            subclass(ItemCrateHologramLineData::class)
            subclass(AnimatedCrateHologramLineData::class)
            subclass(RollRewardCrateHologramLineData::class)
        }
    }

    val yaml = createPolymorphicYaml(module)
}

object CrateHologramLineTypes {
    data class Definition(
        val id: String,
        val displayName: String,
        val description: List<String>,
        val icon: Material,
        val factory: () -> CrateHologramLineData,
        val descriptorFactory: () -> SerialDescriptor,
    )

    val definitions = listOf(
        Definition(
            "text",
            "Text Line",
            listOf("Displays MiniMessage text", "inside a text display hologram."),
            Material.PAPER,
            { TextCrateHologramLineData() },
            { TextCrateHologramLineData.serializer().descriptor }
        ),
        Definition(
            "item",
            "Item Line",
            listOf("Displays an item stack", "inside an item display hologram."),
            Material.ITEM_FRAME,
            { ItemCrateHologramLineData() },
            { ItemCrateHologramLineData.serializer().descriptor }
        ),
        Definition(
            "animated",
            "Animated Line",
            listOf("Cycles between multiple frames", "using any supported hologram line type."),
            Material.CLOCK,
            { AnimatedCrateHologramLineData() },
            { AnimatedCrateHologramLineData.serializer().descriptor }
        ),
        Definition(
            "roll-reward",
            "Roll Reward Line",
            listOf("Cycles through crate rewards", "using an item line and reward name above it."),
            Material.MINECART,
            { RollRewardCrateHologramLineData() },
            { RollRewardCrateHologramLineData.serializer().descriptor }
        ),
    )

    private val byId = definitions.associateBy { it.id }

    fun descriptor(id: String): SerialDescriptor? = byId[id]?.descriptorFactory?.invoke()

    fun defaultElement(id: String): YamlNode? {
        val line = byId[id]?.factory?.invoke() ?: return null
        return CrateHologramLineFormats.yaml.encodeToNode(
            PolymorphicSerializer(CrateHologramLineData::class),
            line
        )
    }

    fun defaultFrameElement(id: String): YamlNode? {
        val line = byId[id]?.factory?.invoke() ?: return null
        return CrateHologramLineFormats.yaml.encodeToNode(
            AnimatedHologramFrameData.serializer(),
            AnimatedHologramFrameData(line = line)
        )
    }

    fun definition(id: String): Definition? = byId[id]
}

object HologramLineSelectionMenu {
    private val entrySlots = listOf(10, 12, 14, 16)
    private val definitions = CrateHologramLineTypes.definitions.map {
        PolymorphicSelectionMenu.Definition(it.id, it.displayName, it.description, it.icon)
    }
    private val frameDefinitions = CrateHologramLineTypes.definitions
        .filter { it.id == "text" || it.id == "item" }
        .map {
            PolymorphicSelectionMenu.Definition(it.id, it.displayName, it.description, it.icon)
        }

    val entryFactory: EntryFactory = PolymorphicSelectionMenu.entryFactory(
        title = "Select Hologram Line",
        entrySlots = entrySlots,
        definitions = definitions,
        elementFactory = CrateHologramLineTypes::defaultElement
    )

    val frameEntryFactory: EntryFactory = PolymorphicSelectionMenu.entryFactory(
        title = "Select Animation Frame",
        entrySlots = entrySlots,
        definitions = frameDefinitions,
        elementFactory = CrateHologramLineTypes::defaultFrameElement
    )

    suspend fun select(player: org.bukkit.entity.Player): String? {
        return PolymorphicSelectionMenu.selectType(
            player = player,
            title = "Select Hologram Line",
            inventoryType = gg.aquatic.kmenu.inventory.InventoryType.GENERIC9X3,
            entrySlots = entrySlots,
            cancelSlot = 22,
            definitions = definitions
        )
    }

    suspend fun selectFrame(player: org.bukkit.entity.Player): String? {
        return PolymorphicSelectionMenu.selectType(
            player = player,
            title = "Select Animation Frame",
            inventoryType = gg.aquatic.kmenu.inventory.InventoryType.GENERIC9X3,
            entrySlots = entrySlots,
            cancelSlot = 22,
            definitions = frameDefinitions
        )
    }
}
