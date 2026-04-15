package gg.aquatic.crates.message

import gg.aquatic.common.toMMComponent
import gg.aquatic.klocale.impl.paper.PaginationSettings
import gg.aquatic.klocale.impl.paper.replacePlaceholders
import gg.aquatic.crates.message.editor.defineMessageLineEditor
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class EditableMessagePaginationData(
    val pageSize: Int = 8,
    val header: EditableMessageLineData? = null,
    val footer: EditableMessageLineData? = null,
) {
    fun toSettings(replacements: Map<String, String> = emptyMap()): PaginationSettings {
        return PaginationSettings(
            pageSize = pageSize.coerceAtLeast(1),
            header = header?.toMiniMessage()?.toMMComponent()?.replacePlaceholders(replacements),
            footer = footer?.toMiniMessage()?.toMMComponent()?.replacePlaceholders(replacements)
        )
    }

    companion object {
        fun TypedNestedSchemaBuilder<EditableMessagePaginationData>.defineEditor() {
            field(
                EditableMessagePaginationData::pageSize,
                displayName = "Page Size",
                iconMaterial = Material.CLOCK,
                description = listOf("How many message lines should be shown on one page.")
            )
            field(
                EditableMessagePaginationData::header,
                displayName = "Header",
                iconMaterial = Material.NAME_TAG,
                description = listOf("Optional line shown above each paginated page.")
            )
            optionalGroup(EditableMessagePaginationData::header) {
                defineMessageLineEditor()
            }
            field(
                EditableMessagePaginationData::footer,
                displayName = "Footer",
                iconMaterial = Material.OAK_SIGN,
                description = listOf("Optional line shown below each paginated page.")
            )
            optionalGroup(EditableMessagePaginationData::footer) {
                defineMessageLineEditor()
            }
        }
    }
}
