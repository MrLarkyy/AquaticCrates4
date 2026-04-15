package gg.aquatic.crates.data.hologram

import gg.aquatic.crates.data.hologram.editor.defineHologramFrameLineEditor
import gg.aquatic.kholograms.line.AnimatedHologramLine
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("animated")
data class AnimatedCrateHologramLineData(
    val height: Double = 0.3,
    val frames: List<AnimatedHologramFrameData> = listOf(
        AnimatedHologramFrameData(20, TextCrateHologramLineData("<yellow>Frame 1")),
        AnimatedHologramFrameData(20, TextCrateHologramLineData("<gold>Frame 2"))
    ),
) : CrateHologramLineData() {
    override fun toSettings(rewardEntries: List<RewardHologramEntry>): List<AnimatedHologramLine.Settings> {
        return listOf(AnimatedHologramLine.Settings(
            frames = frames.map { frame ->
                frame.stayTicks to frame.line.toSettings(rewardEntries).first()
            }.toMutableList(),
            height = height,
            filter = { true },
            failLine = null
        ))
    }

    companion object {
        fun TypedNestedSchemaBuilder<AnimatedCrateHologramLineData>.defineEditor() {
            field(
                AnimatedCrateHologramLineData::height,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig(prompt = "Enter line height:", min = 0.0),
                displayName = "Height",
                iconMaterial = Material.LIGHT_BLUE_DYE,
                description = listOf("Vertical space taken by this animated hologram line.")
            )
            list(
                AnimatedCrateHologramLineData::frames,
                displayName = "Frames",
                iconMaterial = Material.CLOCK,
                description = listOf("Frames played by this animated hologram line."),
                newValueFactory = HologramLineSelectionMenu.frameEntryFactory
            ) {
                with(AnimatedHologramFrameData) {
                    defineEditor()
                }
                defineHologramFrameLineEditor()
            }
        }
    }
}
