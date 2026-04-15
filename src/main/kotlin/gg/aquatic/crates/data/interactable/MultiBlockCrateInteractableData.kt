package gg.aquatic.crates.data.interactable

import gg.aquatic.blokk.BlockShape
import gg.aquatic.blokk.MultiBlokk
import gg.aquatic.clientside.serialize.ClientsideMultiBlockSettings
import gg.aquatic.crates.data.CrateDataFormats
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.interactable.editor.defineInteractableViewRangeEditor
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
@SerialName("multiblock")
data class MultiBlockCrateInteractableData(
    val blocks: Map<String, BlockDefinitionData> = mapOf("x" to BlockDefinitionData()),
    val layers: Map<String, MultiBlockLayerData> = mapOf("0" to MultiBlockLayerData()),
    val viewRange: Int = 50,
    val offsetX: Double = 0.0,
    val offsetY: Double = 0.0,
    val offsetZ: Double = 0.0,
) : CrateInteractableData() {

    override fun toSettings() = ClientsideMultiBlockSettings(
        multiBlock = MultiBlokk(
            BlockShape(
                layers = layers.mapNotNull { (layerIndex, layerData) ->
                    layerIndex.toIntOrNull()?.let { it to layerData.rows.mapNotNull { (rowIndex, rowPattern) ->
                        rowIndex.toIntOrNull()?.let { row -> row to rowPattern }
                    }.toMap().toMutableMap() }
                }.toMap().toMutableMap(),
                blocks = blocks.mapNotNull { (key, blockData) ->
                    key.singleOrNull()?.let { it to blockData.toBlokk() }
                }.toMap().toMutableMap()
            )
        ),
        viewRange = viewRange,
        offsetX = offsetX,
        offsetY = offsetY,
        offsetZ = offsetZ
    )

    companion object {
        private val schemaJson = Json { encodeDefaults = true }

        fun TypedNestedSchemaBuilder<MultiBlockCrateInteractableData>.defineEditor() {
            map(
                MultiBlockCrateInteractableData::blocks,
                displayName = "Ingredient Blocks",
                description = listOf("Character to block definition mapping used by the multiblock shape."),
                mapKeyPrompt = "Enter block character:",
                newMapEntryFactory = EditorEntryFactories.map(
                    keyPrompt = "Enter block character:",
                    keyValidator = { raw ->
                        if (raw.length == 1) null else "Use exactly one character."
                    },
                    valueFactory = {
                        CrateDataFormats.yaml.encodeToNode(BlockDefinitionData.serializer(), BlockDefinitionData())
                    }
                )
            ) {
                with(BlockDefinitionData) {
                    defineEditor(
                        titlePrefix = "Ingredient",
                        materialDescription = listOf("Block used when this multiblock shape references the selected character.")
                    )
                }
            }
            map(
                MultiBlockCrateInteractableData::layers,
                displayName = "Layers",
                description = listOf("Y layers that define the multiblock shape around the crate location."),
                mapKeyPrompt = "Enter layer Y coordinate:",
                newMapEntryFactory = EditorEntryFactories.map(
                    keyPrompt = "Enter layer Y coordinate:",
                    keyValidator = { raw ->
                        if (raw.trim().toIntOrNull() != null) null else "Layer Y coordinate must be an integer."
                    },
                    valueFactory = {
                        CrateDataFormats.yaml.encodeToNode(MultiBlockLayerData.serializer(), MultiBlockLayerData())
                    }
                )
            ) {
                with(MultiBlockLayerData) {
                    defineEditor()
                }
            }
            defineInteractableViewRangeEditor(MultiBlockCrateInteractableData::viewRange)
            defineInteractableOffsetEditor(
                MultiBlockCrateInteractableData::offsetX,
                MultiBlockCrateInteractableData::offsetY,
                MultiBlockCrateInteractableData::offsetZ
            )
        }
    }
}
