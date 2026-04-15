package gg.aquatic.crates.data.action

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlNamingStrategy
import gg.aquatic.crates.data.editor.polymorphic.PolymorphicTypeDefinition
import gg.aquatic.crates.data.editor.polymorphic.PolymorphicTypeRegistry
import gg.aquatic.crates.data.editor.polymorphic.createPolymorphicYaml
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bukkit.Material

@OptIn(ExperimentalSerializationApi::class)
object RewardActionFormats {
    val module = SerializersModule {
        polymorphic(RewardActionData::class) {
            subclass(GiveItemRewardActionData::class)
            subclass(MessageRewardActionData::class)
            subclass(ActionbarRewardActionData::class)
            subclass(CommandRewardActionData::class)
            subclass(SoundRewardActionData::class)
            subclass(StopSoundRewardActionData::class)
            subclass(TitleRewardActionData::class)
            subclass(CloseInventoryRewardActionData::class)
        }
    }

    val yaml = createPolymorphicYaml(module)

    val legacyYaml = Yaml.default
}

object RewardActionTypes {
    private val registry = PolymorphicTypeRegistry(
        RewardActionData::class.java,
        RewardActionFormats.yaml,
        listOf(
        PolymorphicTypeDefinition(
            id = "give-item",
            displayName = "Give Item",
            description = listOf(
                "Gives the configured item directly",
                "to the player inventory."
            ),
            icon = Material.DIAMOND,
            factory = { GiveItemRewardActionData() },
            descriptorFactory = { GiveItemRewardActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "message",
            displayName = "Message",
            description = listOf(
                "Sends one or more chat lines",
                "to the winning player."
            ),
            icon = Material.PAPER,
            factory = { MessageRewardActionData() },
            descriptorFactory = { MessageRewardActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "actionbar",
            displayName = "Actionbar",
            description = listOf(
                "Shows a short actionbar message",
                "above the hotbar."
            ),
            icon = Material.NAME_TAG,
            factory = { ActionbarRewardActionData() },
            descriptorFactory = { ActionbarRewardActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "command",
            displayName = "Command",
            description = listOf(
                "Executes one or more commands",
                "as console or player."
            ),
            icon = Material.COMMAND_BLOCK,
            factory = { CommandRewardActionData() },
            descriptorFactory = { CommandRewardActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "sound",
            displayName = "Play Sound",
            description = listOf(
                "Plays a sound with custom",
                "volume and pitch."
            ),
            icon = Material.NOTE_BLOCK,
            factory = { SoundRewardActionData() },
            descriptorFactory = { SoundRewardActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "stop-sound",
            displayName = "Stop Sound",
            description = listOf(
                "Stops a specific sound for",
                "the winning player."
            ),
            icon = Material.BARRIER,
            factory = { StopSoundRewardActionData() },
            descriptorFactory = { StopSoundRewardActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "title",
            displayName = "Title",
            description = listOf(
                "Shows a title and subtitle",
                "with timing settings."
            ),
            icon = Material.BOOK,
            factory = { TitleRewardActionData() },
            descriptorFactory = { TitleRewardActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "close-inventory",
            displayName = "Close Inventory",
            description = listOf(
                "Closes the current inventory",
                "for the winning player."
            ),
            icon = Material.CHEST,
            factory = { CloseInventoryRewardActionData() },
            descriptorFactory = { CloseInventoryRewardActionData.serializer().descriptor }
        ),
        )
    )

    val definitions get() = registry.selectionDefinitions()
    fun parse(raw: String): String? = registry.parse(raw)
    fun definition(id: String) = registry.definition(id)
    fun create(id: String): RewardActionData? = registry.create(id)
    fun defaultElement(id: String) = registry.defaultElement(id)
    fun descriptor(id: String) = registry.descriptor(id)
}
