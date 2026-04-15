package gg.aquatic.crates.data.interactable

import gg.aquatic.crates.data.editor.core.yamlScalar
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Serializable

@Serializable
data class MultiBlockLayerData(
    val rows: Map<String, String> = mapOf("0" to "x"),
) {
    companion object {
        fun TypedNestedSchemaBuilder<MultiBlockLayerData>.defineEditor() {
            map(
                MultiBlockLayerData::rows,
                displayName = "Rows",
                description = listOf("Row index to block character pattern for this multiblock layer."),
                mapKeyPrompt = "Enter row index:",
                newMapEntryFactory = EditorEntryFactories.map(
                    keyPrompt = "Enter row index:",
                    keyValidator = { raw ->
                        if (raw.trim().toIntOrNull() != null) null else "Row index must be an integer."
                    },
                    valueFactory = { yamlScalar("x") }
                )
            )
        }
    }
}
