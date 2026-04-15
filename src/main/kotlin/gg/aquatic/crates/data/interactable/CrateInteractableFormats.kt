package gg.aquatic.crates.data.interactable

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.editor.polymorphic.createPolymorphicYaml
import gg.aquatic.crates.data.editor.core.encodeToNode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bukkit.Material

@OptIn(ExperimentalSerializationApi::class)
object CrateInteractableFormats {
    val module = SerializersModule {
        polymorphic(CrateInteractableData::class) {
            subclass(BlockCrateInteractableData::class)
            subclass(EntityCrateInteractableData::class)
            subclass(MEGCrateInteractableData::class)
            subclass(MultiBlockCrateInteractableData::class)
        }
    }

    val yaml = createPolymorphicYaml(module)
}

object CrateInteractableTypes {
    data class Definition(
        val id: String,
        val displayName: String,
        val description: List<String>,
        val icon: Material,
        val factory: () -> CrateInteractableData,
        val descriptorFactory: () -> SerialDescriptor,
    )

    val definitions: List<Definition> = listOf(
        Definition(
            id = "block",
            displayName = "Block",
            description = listOf(
                "Shows a single clientside block",
                "that players can interact with."
            ),
            icon = Material.CHEST,
            factory = { BlockCrateInteractableData() },
            descriptorFactory = { BlockCrateInteractableData.serializer().descriptor }
        ),
        Definition(
            id = "entity",
            displayName = "Entity",
            description = listOf(
                "Shows a clientside entity",
                "as the crate interactable."
            ),
            icon = Material.ARMOR_STAND,
            factory = { EntityCrateInteractableData() },
            descriptorFactory = { EntityCrateInteractableData.serializer().descriptor }
        ),
        Definition(
            id = "meg",
            displayName = "MEG",
            description = listOf(
                "Uses a ModelEngine model id",
                "for the interactable object."
            ),
            icon = Material.ITEM_FRAME,
            factory = { MEGCrateInteractableData() },
            descriptorFactory = { MEGCrateInteractableData.serializer().descriptor }
        ),
        Definition(
            id = "multiblock",
            displayName = "Multi Block",
            description = listOf(
                "Builds a full multiblock shape",
                "from block character layers."
            ),
            icon = Material.BRICKS,
            factory = { MultiBlockCrateInteractableData() },
            descriptorFactory = { MultiBlockCrateInteractableData.serializer().descriptor }
        )
    )

    private val definitionsById = definitions.associateBy { it.id }

    fun definition(id: String): Definition? = definitionsById[id]

    fun defaultElement(id: String): YamlNode? {
        val interactable = definition(id)?.factory?.invoke() ?: return null
        return CrateInteractableFormats.yaml.encodeToNode(
            PolymorphicSerializer(CrateInteractableData::class),
            interactable
        )
    }

    fun descriptor(id: String): SerialDescriptor? {
        return definition(id)?.descriptorFactory?.invoke()
    }
}
