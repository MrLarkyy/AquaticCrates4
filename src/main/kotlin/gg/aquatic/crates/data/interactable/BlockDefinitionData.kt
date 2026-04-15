package gg.aquatic.crates.data.interactable

import gg.aquatic.blokk.Blokk
import gg.aquatic.blokk.BlokkSerializer
import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.waves.serialization.editor.meta.*
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs
import org.bukkit.configuration.MemoryConfiguration

@Serializable
data class BlockDefinitionData(
    val material: String = Material.CHEST.name,
    val face: String? = null,
    val opened: Boolean = false,
    val powered: Boolean = false,
    val half: String? = null,
    val waterlogged: Boolean = false,
    val faces: List<String> = emptyList(),
    val stairsShape: String? = null,
    val slabType: String? = null,
) {

    fun toBlokk(): Blokk {
        return BlokkSerializer.load(toSection())
    }

    fun toSection(): MemoryConfiguration {
        return MemoryConfiguration().apply {
            set("material", material)
            face?.takeIf { it.isNotBlank() }?.let { set("face", it) }
            set("opened", opened)
            set("powered", powered)
            half?.takeIf { it.isNotBlank() }?.let { set("half", it) }
            set("waterlogged", waterlogged)
            if (faces.isNotEmpty()) {
                set("faces", faces)
            }
            stairsShape?.takeIf { it.isNotBlank() }?.let { set("stairs-shape", it) }
            slabType?.takeIf { it.isNotBlank() }?.let { set("slab-type", it) }
        }
    }

    companion object {
        fun TypedNestedSchemaBuilder<BlockDefinitionData>.defineEditor(
            titlePrefix: String = "Block",
            materialDescription: List<String> = listOf("Vanilla block material or factory:id used by this clientside block.")
        ) {
            field(
                BlockDefinitionData::material,
                TextFieldAdapter,
                TextFieldConfig(
                    prompt = "Enter block material or factory:id:",
                    validator = CrateDataValidators::validateBlockMaterialLike
                ),
                displayName = "$titlePrefix Material",
                description = materialDescription
            )
            field(
                BlockDefinitionData::face,
                EnumFieldAdapter,
                EnumFieldConfig(
                    prompt = "Enter block face:",
                    values = { BlockFace.entries.map { it.name } }
                ),
                displayName = "Facing",
                description = listOf("Facing used by directional block data.")
            )
            field(
                BlockDefinitionData::half,
                EnumFieldAdapter,
                EnumFieldConfig(
                    prompt = "Enter block half:",
                    values = { Bisected.Half.entries.map { it.name } }
                ),
                displayName = "Half",
                description = listOf("Top or bottom half used by bisected blocks.")
            )
            list(
                BlockDefinitionData::faces,
                displayName = "Faces",
                description = listOf("Enabled faces used by multiple-facing block data."),
                newValueFactory = EditorEntryFactories.text(
                    prompt = "Enter block face:",
                    validator = CrateDataValidators::validateBlockFace
                )
            )
            field(
                BlockDefinitionData::stairsShape,
                EnumFieldAdapter,
                EnumFieldConfig(
                    prompt = "Enter stairs shape:",
                    values = { Stairs.Shape.entries.map { it.name } }
                ),
                displayName = "Stairs Shape",
                description = listOf("Shape used when the block is a stairs variant.")
            )
            field(
                BlockDefinitionData::slabType,
                EnumFieldAdapter,
                EnumFieldConfig(
                    prompt = "Enter slab type:",
                    values = { Slab.Type.entries.map { it.name } }
                ),
                displayName = "Slab Type",
                description = listOf("Top, bottom or double slab variant.")
            )
        }
    }
}
