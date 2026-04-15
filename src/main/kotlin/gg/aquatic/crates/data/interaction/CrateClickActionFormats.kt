package gg.aquatic.crates.data.interaction

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
object CrateClickActionFormats {
    val module = SerializersModule {
        polymorphic(CrateClickActionData::class) {
            subclass(PreviewCrateClickActionData::class)
            subclass(OpenCrateClickActionData::class)
            subclass(DestroyCrateClickActionData::class)
            subclass(MessageCrateClickActionData::class)
            subclass(ActionbarCrateClickActionData::class)
            subclass(CommandCrateClickActionData::class)
            subclass(SoundCrateClickActionData::class)
            subclass(StopSoundCrateClickActionData::class)
            subclass(TitleCrateClickActionData::class)
            subclass(CloseInventoryCrateClickActionData::class)
        }
    }

    val yaml = createPolymorphicYaml(module)
}

object CrateClickActionTypes {
    private val registry = PolymorphicTypeRegistry(
        CrateClickActionData::class.java,
        CrateClickActionFormats.yaml,
        listOf(
        PolymorphicTypeDefinition(
            id = "preview",
            displayName = "Preview",
            description = listOf("Opens the crate preview menu."),
            icon = Material.ENDER_EYE,
            factory = { PreviewCrateClickActionData() },
            descriptorFactory = { PreviewCrateClickActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "open",
            displayName = "Open",
            description = listOf("Attempts to open the crate."),
            icon = Material.CHEST,
            factory = { OpenCrateClickActionData() },
            descriptorFactory = { OpenCrateClickActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "destroy",
            displayName = "Destroy",
            description = listOf("Destroys the placed crate.", "Only useful for direct crate interaction."),
            icon = Material.BARRIER,
            factory = { DestroyCrateClickActionData() },
            descriptorFactory = { DestroyCrateClickActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "message",
            displayName = "Message",
            description = listOf("Sends one or more chat lines", "to the clicking player."),
            icon = Material.PAPER,
            factory = { MessageCrateClickActionData() },
            descriptorFactory = { MessageCrateClickActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "actionbar",
            displayName = "Actionbar",
            description = listOf("Shows a short actionbar message", "above the hotbar."),
            icon = Material.NAME_TAG,
            factory = { ActionbarCrateClickActionData() },
            descriptorFactory = { ActionbarCrateClickActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "command",
            displayName = "Command",
            description = listOf("Executes one or more commands", "as console or player."),
            icon = Material.COMMAND_BLOCK,
            factory = { CommandCrateClickActionData() },
            descriptorFactory = { CommandCrateClickActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "sound",
            displayName = "Play Sound",
            description = listOf("Plays a sound with custom", "volume and pitch."),
            icon = Material.NOTE_BLOCK,
            factory = { SoundCrateClickActionData() },
            descriptorFactory = { SoundCrateClickActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "stop-sound",
            displayName = "Stop Sound",
            description = listOf("Stops a specific sound", "for the clicking player."),
            icon = Material.JUKEBOX,
            factory = { StopSoundCrateClickActionData() },
            descriptorFactory = { StopSoundCrateClickActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "title",
            displayName = "Title",
            description = listOf("Shows a title and subtitle", "with timing settings."),
            icon = Material.BOOK,
            factory = { TitleCrateClickActionData() },
            descriptorFactory = { TitleCrateClickActionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "close-inventory",
            displayName = "Close Inventory",
            description = listOf("Closes the current inventory", "for the clicking player."),
            icon = Material.CHEST_MINECART,
            factory = { CloseInventoryCrateClickActionData() },
            descriptorFactory = { CloseInventoryCrateClickActionData.serializer().descriptor }
        ),
        )
    )

    val definitions get() = registry.selectionDefinitions()
    fun definition(id: String) = registry.definition(id)
    fun create(id: String): CrateClickActionData? = registry.create(id)
    fun descriptor(id: String) = registry.descriptor(id)
    fun defaultElement(id: String) = registry.defaultElement(id)
}
