package gg.aquatic.crates.data

import gg.aquatic.crates.data.hologram.CrateHologramLineData
import gg.aquatic.crates.data.hologram.RewardHologramEntry
import gg.aquatic.crates.data.hologram.HologramLineSelectionMenu
import gg.aquatic.crates.data.hologram.editor.defineHologramLineEditor
import gg.aquatic.kholograms.Hologram
import gg.aquatic.waves.serialization.editor.meta.IntFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.IntFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class CrateHologramData(
    val lines: List<@Polymorphic CrateHologramLineData> = emptyList(),
    val viewDistance: Int = 20,
    val yOffset: Double = 0.0,
) {

    fun toSettings(rewardEntries: List<RewardHologramEntry> = emptyList()): Hologram.Settings? {
        if (lines.isEmpty()) return null

        return Hologram.Settings(
            lines = lines.flatMap { it.toSettings(rewardEntries) },
            filter = { true },
            viewDistance = viewDistance
        )
    }

    companion object {
        fun TypedNestedSchemaBuilder<CrateHologramData>.defineEditor() {
            list(
                CrateHologramData::lines,
                "Hologram Lines",
                searchTags = listOf("lines", "hologram lines", "text", "item line", "animated line", "floating text"),
                iconMaterial = Material.END_CRYSTAL,
                description = listOf("All hologram lines displayed above the crate, including text, item and animated lines."),
                newValueFactory = HologramLineSelectionMenu.entryFactory
            ) {
                defineHologramLineEditor()
            }
            field(
                CrateHologramData::viewDistance,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter hologram view distance:", min = 1),
                displayName = "Hologram View Distance",
                searchTags = listOf("view distance", "distance", "range", "visibility", "render distance"),
                iconMaterial = Material.SPYGLASS,
                description = listOf("Maximum distance where the hologram is rendered.")
            )
            field(
                CrateHologramData::yOffset,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig(prompt = "Enter hologram Y offset:"),
                displayName = "Hologram Y Offset",
                searchTags = listOf("offset", "y offset", "height", "vertical offset", "spawn offset"),
                iconMaterial = Material.FEATHER,
                description = listOf("Additional vertical offset applied to the spawned hologram location.")
            )
        }
    }
}

