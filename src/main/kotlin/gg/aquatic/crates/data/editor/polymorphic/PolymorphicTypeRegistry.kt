package gg.aquatic.crates.data.editor.polymorphic

import gg.aquatic.crates.data.editor.core.encodeToNode

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlNamingStrategy
import com.charleskorn.kaml.YamlNode
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import org.bukkit.Material

data class PolymorphicTypeDefinition<T : Any>(
    val id: String,
    val displayName: String,
    val description: List<String>,
    val icon: Material,
    val factory: () -> T,
    val descriptorFactory: () -> SerialDescriptor,
    val availability: (org.bukkit.entity.Player) -> PolymorphicSelectionMenu.Availability = {
        PolymorphicSelectionMenu.Availability(true)
    },
)

class PolymorphicTypeRegistry<T : Any>(
    private val baseClass: Class<T>,
    private val yaml: Yaml,
    definitions: List<PolymorphicTypeDefinition<T>>,
) {
    val definitions: List<PolymorphicTypeDefinition<T>> = definitions
    private val definitionsById = definitions.associateBy { it.id }

    fun definition(id: String): PolymorphicTypeDefinition<T>? = definitionsById[id]
    fun create(id: String): T? = definition(id)?.factory?.invoke()
    fun descriptor(id: String): SerialDescriptor? = definition(id)?.descriptorFactory?.invoke()
    fun parse(raw: String): String? {
        val normalized = raw.trim()
        if (normalized.isEmpty()) {
            return null
        }

        definitionsById[normalized]?.let { return it.id }
        definitions.firstOrNull { it.id.equals(normalized, ignoreCase = true) }?.let { return it.id }
        definitions.firstOrNull { it.descriptorFactory().serialName.equals(normalized, ignoreCase = true) }?.let { return it.id }
        return null
    }
    fun selectionDefinitions(
        filter: (PolymorphicTypeDefinition<T>) -> Boolean = { true }
    ): List<PolymorphicSelectionMenu.Definition> = definitions.filter(filter).map { definition ->
        PolymorphicSelectionMenu.Definition(
            id = definition.id,
            displayName = definition.displayName,
            description = definition.description,
            icon = definition.icon,
            availability = definition.availability
        )
    }

    fun defaultElement(id: String): YamlNode? {
        val element = create(id) ?: return null
        return yaml.encodeToNode(
            PolymorphicSerializer(baseClass.kotlin),
            element
        )
    }
}

fun createPolymorphicYaml(module: kotlinx.serialization.modules.SerializersModule): Yaml = Yaml(
    serializersModule = module,
    configuration = YamlConfiguration(
        yamlNamingStrategy = YamlNamingStrategy.KebabCase,
        polymorphismStyle = PolymorphismStyle.Property,
        polymorphismPropertyName = "type"
    )
)
