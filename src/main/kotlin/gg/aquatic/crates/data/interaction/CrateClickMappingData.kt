package gg.aquatic.crates.data.interaction

import gg.aquatic.crates.data.interaction.editor.CrateClickActionEntryFieldAdapter
import gg.aquatic.crates.data.interaction.editor.CrateClickActionSelectionMenu
import gg.aquatic.crates.data.interaction.editor.defineCrateClickActionEditor
import gg.aquatic.crates.interact.CrateClickType
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class CrateClickMappingData(
    val right: List<@Polymorphic CrateClickActionData> = listOf(OpenCrateClickActionData()),
    val shiftRight: List<@Polymorphic CrateClickActionData> = listOf(OpenCrateClickActionData()),
    val left: List<@Polymorphic CrateClickActionData> = listOf(PreviewCrateClickActionData()),
    val shiftLeft: List<@Polymorphic CrateClickActionData> = listOf(DestroyCrateClickActionData()),
) {
    fun actions(clickType: CrateClickType): List<CrateClickActionData> {
        return when (clickType) {
            CrateClickType.RIGHT -> right
            CrateClickType.SHIFT_RIGHT -> shiftRight
            CrateClickType.LEFT -> left
            CrateClickType.SHIFT_LEFT -> shiftLeft
        }
    }

    companion object {
        fun TypedNestedSchemaBuilder<CrateClickMappingData>.defineEditor(
            sectionName: String,
            allowDestroy: Boolean = true,
        ) {
            list(
                CrateClickMappingData::right,
                displayName = "$sectionName Right",
                iconMaterial = Material.STONE_BUTTON,
                description = listOf("Actions executed on right click."),
                newValueFactory = CrateClickActionSelectionMenu.entryFactory(allowDestroy)
            ) {
                defineCrateClickActionEditor(CrateClickActionEntryFieldAdapter(allowDestroy))
            }
            list(
                CrateClickMappingData::shiftRight,
                displayName = "$sectionName Shift Right",
                iconMaterial = Material.STONE_BUTTON,
                description = listOf("Actions executed on shift-right click."),
                newValueFactory = CrateClickActionSelectionMenu.entryFactory(allowDestroy)
            ) {
                defineCrateClickActionEditor(CrateClickActionEntryFieldAdapter(allowDestroy))
            }
            list(
                CrateClickMappingData::left,
                displayName = "$sectionName Left",
                iconMaterial = Material.STONE_BUTTON,
                description = listOf("Actions executed on left click."),
                newValueFactory = CrateClickActionSelectionMenu.entryFactory(allowDestroy)
            ) {
                defineCrateClickActionEditor(CrateClickActionEntryFieldAdapter(allowDestroy))
            }
            list(
                CrateClickMappingData::shiftLeft,
                displayName = "$sectionName Shift Left",
                iconMaterial = Material.STONE_BUTTON,
                description = listOf("Actions executed on shift-left click."),
                newValueFactory = CrateClickActionSelectionMenu.entryFactory(allowDestroy)
            ) {
                defineCrateClickActionEditor(CrateClickActionEntryFieldAdapter(allowDestroy))
            }
        }
    }
}
