package gg.aquatic.crates.message.condition

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.message.MessagesFormats
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bukkit.Material

@OptIn(ExperimentalSerializationApi::class)
object MessageConditionFormats {
    val module = SerializersModule {
        polymorphic(MessageConditionData::class) {
            subclass(HasNextPageMessageConditionData::class)
            subclass(HasNextOrPreviousPageMessageConditionData::class)
            subclass(HasPreviousPageMessageConditionData::class)
            subclass(SenderPermissionMessageConditionData::class)
        }
    }
}

object MessageConditionTypes {
    data class Definition(
        val id: String,
        val displayName: String,
        val description: List<String>,
        val icon: Material,
        val factory: () -> MessageConditionData,
        val descriptorFactory: () -> SerialDescriptor,
    )

    val definitions = listOf(
        Definition(
            id = "has-previous-page",
            displayName = "Has Previous Page",
            description = listOf("Visible only when a previous page exists."),
            icon = Material.ARROW,
            factory = { HasPreviousPageMessageConditionData() },
            descriptorFactory = { HasPreviousPageMessageConditionData.serializer().descriptor }
        ),
        Definition(
            id = "has-next-page",
            displayName = "Has Next Page",
            description = listOf("Visible only when a next page exists."),
            icon = Material.ARROW,
            factory = { HasNextPageMessageConditionData() },
            descriptorFactory = { HasNextPageMessageConditionData.serializer().descriptor }
        ),
        Definition(
            id = "has-next-or-previous-page",
            displayName = "Has Next Or Previous Page",
            description = listOf("Visible when at least one adjacent page exists."),
            icon = Material.COMPASS,
            factory = { HasNextOrPreviousPageMessageConditionData() },
            descriptorFactory = { HasNextOrPreviousPageMessageConditionData.serializer().descriptor }
        ),
        Definition(
            id = "sender-permission",
            displayName = "Sender Permission",
            description = listOf("Visible only when the sender has the configured permission."),
            icon = Material.TRIPWIRE_HOOK,
            factory = { SenderPermissionMessageConditionData() },
            descriptorFactory = { SenderPermissionMessageConditionData.serializer().descriptor }
        )
    )

    private val definitionsById = definitions.associateBy { it.id }

    fun definition(id: String): Definition? = definitionsById[id]

    fun create(id: String): MessageConditionData? = definition(id)?.factory?.invoke()

    fun defaultElement(id: String): YamlNode? {
        val condition = create(id) ?: return null
        return MessagesFormats.yaml.encodeToNode(
            PolymorphicSerializer(MessageConditionData::class),
            condition
        )
    }

    fun descriptor(id: String): SerialDescriptor? = definition(id)?.descriptorFactory?.invoke()
}
