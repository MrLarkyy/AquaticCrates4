package gg.aquatic.crates.data.hologram

import gg.aquatic.kholograms.line.AnimatedHologramLine
import gg.aquatic.kholograms.line.TextHologramLine
import gg.aquatic.kholograms.serialize.LineSettings
import gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig
import gg.aquatic.waves.serialization.editor.meta.IntFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.IntFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.joml.Vector3f

@Serializable
@SerialName("roll-reward")
data class RollRewardCrateHologramLineData(
    val switchTicks: Int = 20,
    val itemHeight: Double = 0.6,
    val itemScale: Double = 1.0,
    val itemBillboard: Display.Billboard = Display.Billboard.CENTER,
    val itemDisplayTransform: ItemDisplay.ItemDisplayTransform = ItemDisplay.ItemDisplayTransform.GROUND,
    val itemTransformationDuration: Int = 0,
    val itemTeleportInterpolation: Int = 0,
    val itemTranslationX: Double = 0.0,
    val itemTranslationY: Double = 0.0,
    val itemTranslationZ: Double = 0.0,
    val textHeight: Double = 0.3,
    val textScale: Double = 1.0,
    val textLineWidth: Int = 200,
    val textBillboard: Display.Billboard = Display.Billboard.CENTER,
    val textHasShadow: Boolean = true,
    val textSeeThrough: Boolean = true,
    val textTransformationDuration: Int = 0,
    val textTeleportInterpolation: Int = 0,
    val textTranslationX: Double = 0.0,
    val textTranslationY: Double = 0.0,
    val textTranslationZ: Double = 0.0,
) : CrateHologramLineData() {

    override fun toSettings(rewardEntries: List<RewardHologramEntry>): List<AnimatedHologramLine.Settings> {
        if (rewardEntries.isEmpty()) {
            return TextCrateHologramLineData(
                text = "<red>No rewards configured",
                height = textHeight,
                lineWidth = textLineWidth,
                scale = textScale
            ).toSettings(rewardEntries).map { settings ->
                AnimatedHologramLine.Settings(
                    frames = mutableListOf(switchTicks to settings),
                    height = textHeight,
                    filter = { true },
                    failLine = null
                )
            }
        }

        val itemFrames: MutableList<Pair<Int, LineSettings>> = rewardEntries.map { entry ->
            switchTicks to RewardShowcaseHologramLine.Settings(
                rewardShowcase = entry.showcase,
                height = itemHeight,
                scale = itemScale.toFloat().coerceAtLeast(0.01f),
                billboard = itemBillboard,
                filter = { true },
                failLine = null,
                transformationDuration = itemTransformationDuration,
                teleportInterpolation = itemTeleportInterpolation,
                translation = Vector3f(itemTranslationX.toFloat(), itemTranslationY.toFloat(), itemTranslationZ.toFloat())
            )
        }.toMutableList()

        val textFrames: MutableList<Pair<Int, LineSettings>> = rewardEntries.map { entry ->
            switchTicks to TextHologramLine.Settings(
                height = textLineHeight(entry),
                text = entry.displayName,
                lineWidth = textLineWidth,
                scale = textScale.toFloat(),
                billboard = textBillboard,
                filter = { true },
                hasShadow = textHasShadow,
                backgroundColor = null,
                isSeeThrough = textSeeThrough,
                transformationDuration = textTransformationDuration,
                failLine = null,
                teleportInterpolation = textTeleportInterpolation,
                translation = Vector3f(
                    (itemTranslationX + textTranslationX).toFloat(),
                    (itemTranslationY + textTranslationY).toFloat(),
                    (itemTranslationZ + textTranslationZ).toFloat()
                )
            )
        }.toMutableList()

        return listOf(
            AnimatedHologramLine.Settings(
                frames = textFrames,
                height = textHeight,
                filter = { true },
                failLine = null
            ),
            AnimatedHologramLine.Settings(
                frames = itemFrames,
                height = itemHeight,
                filter = { true },
                failLine = null
            )
        )
    }

    private fun textLineHeight(entry: RewardHologramEntry): Double {
        val offsetSpacing = (entry.showcase.displayNameYOffset() * 2.0 - itemHeight).coerceAtLeast(0.01)
        return maxOf(textHeight, offsetSpacing)
    }

    companion object {
        fun TypedNestedSchemaBuilder<RollRewardCrateHologramLineData>.defineEditor() {
            field(
                RollRewardCrateHologramLineData::switchTicks,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter switch ticks:", min = 1),
                displayName = "Switch Ticks",
                iconMaterial = Material.CLOCK,
                description = listOf("How many ticks each reward stays visible before switching.")
            )
            field(
                RollRewardCrateHologramLineData::itemHeight,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter item line height:", min = 0.0),
                displayName = "Item Height",
                iconMaterial = Material.ITEM_FRAME,
                description = listOf("Vertical space used by the rotating item line.")
            )
            field(
                RollRewardCrateHologramLineData::itemScale,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter item scale:", min = 0.0),
                displayName = "Item Scale",
                iconMaterial = Material.SLIME_BALL,
                description = listOf("Display scale applied to the rotating reward item.")
            )
            field(
                RollRewardCrateHologramLineData::itemBillboard,
                gg.aquatic.waves.serialization.editor.meta.EnumFieldAdapter,
                gg.aquatic.waves.serialization.editor.meta.EnumFieldConfig(
                    prompt = "Enter item billboard mode:",
                    values = { Display.Billboard.entries.map { it.name } }
                ),
                displayName = "Item Billboard",
                iconMaterial = Material.ITEM_FRAME,
                description = listOf("Billboard mode used by the rotating reward item.")
            )
            field(
                RollRewardCrateHologramLineData::itemDisplayTransform,
                gg.aquatic.waves.serialization.editor.meta.EnumFieldAdapter,
                gg.aquatic.waves.serialization.editor.meta.EnumFieldConfig(
                    prompt = "Enter item display transform:",
                    values = { ItemDisplay.ItemDisplayTransform.entries.map { it.name } }
                ),
                displayName = "Item Transform",
                iconMaterial = Material.ARMOR_STAND,
                description = listOf("Display transform used by the rotating reward item.")
            )
            field(
                RollRewardCrateHologramLineData::itemTransformationDuration,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter item transformation duration:", min = 0),
                displayName = "Item Transformation Duration",
                iconMaterial = Material.REPEATER,
                description = listOf("Interpolation duration for item transformation changes.")
            )
            field(
                RollRewardCrateHologramLineData::itemTeleportInterpolation,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter item teleport interpolation:", min = 0),
                displayName = "Item Teleport Interpolation",
                iconMaterial = Material.CLOCK,
                description = listOf("Interpolation duration used when the reward item moves.")
            )
            field(
                RollRewardCrateHologramLineData::itemTranslationX,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter item translation X:"),
                displayName = "Item Translation X",
                iconMaterial = Material.ARROW,
                description = listOf("X translation offset of the rotating reward item.")
            )
            field(
                RollRewardCrateHologramLineData::itemTranslationY,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter item translation Y:"),
                displayName = "Item Translation Y",
                iconMaterial = Material.SPECTRAL_ARROW,
                description = listOf("Y translation offset of the rotating reward item.")
            )
            field(
                RollRewardCrateHologramLineData::itemTranslationZ,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter item translation Z:"),
                displayName = "Item Translation Z",
                iconMaterial = Material.TIPPED_ARROW,
                description = listOf("Z translation offset of the rotating reward item.")
            )
            field(
                RollRewardCrateHologramLineData::textHeight,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter text line height:", min = 0.0),
                displayName = "Text Height",
                iconMaterial = Material.PAPER,
                description = listOf("Vertical space used by the rotating reward name line.")
            )
            field(
                RollRewardCrateHologramLineData::textScale,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter text scale:", min = 0.0),
                displayName = "Text Scale",
                iconMaterial = Material.MAP,
                description = listOf("Display scale applied to the rotating reward name.")
            )
            field(
                RollRewardCrateHologramLineData::textLineWidth,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter text line width:", min = 1),
                displayName = "Text Line Width",
                iconMaterial = Material.OAK_SIGN,
                description = listOf("Maximum width before the reward name wraps.")
            )
            field(
                RollRewardCrateHologramLineData::textBillboard,
                gg.aquatic.waves.serialization.editor.meta.EnumFieldAdapter,
                gg.aquatic.waves.serialization.editor.meta.EnumFieldConfig(
                    prompt = "Enter text billboard mode:",
                    values = { Display.Billboard.entries.map { it.name } }
                ),
                displayName = "Text Billboard",
                iconMaterial = Material.ITEM_FRAME,
                description = listOf("Billboard mode used by the rotating reward name.")
            )
            field(
                RollRewardCrateHologramLineData::textHasShadow,
                displayName = "Text Has Shadow",
                iconMaterial = Material.GRAY_DYE,
                description = listOf("If enabled, renders a shadow behind the reward name.")
            )
            field(
                RollRewardCrateHologramLineData::textSeeThrough,
                displayName = "Text See Through",
                iconMaterial = Material.GLASS,
                description = listOf("If enabled, keeps the reward name visible through blocks.")
            )
            field(
                RollRewardCrateHologramLineData::textTransformationDuration,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter text transformation duration:", min = 0),
                displayName = "Text Transformation Duration",
                iconMaterial = Material.REPEATER,
                description = listOf("Interpolation duration for text transformation changes.")
            )
            field(
                RollRewardCrateHologramLineData::textTeleportInterpolation,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter text teleport interpolation:", min = 0),
                displayName = "Text Teleport Interpolation",
                iconMaterial = Material.CLOCK,
                description = listOf("Interpolation duration used when the reward name moves.")
            )
            field(
                RollRewardCrateHologramLineData::textTranslationX,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter text translation X:"),
                displayName = "Text Translation X",
                iconMaterial = Material.ARROW,
                description = listOf("X translation offset of the rotating reward name.")
            )
            field(
                RollRewardCrateHologramLineData::textTranslationY,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter text translation Y:"),
                displayName = "Text Translation Y",
                iconMaterial = Material.SPECTRAL_ARROW,
                description = listOf("Y translation offset of the rotating reward name.")
            )
            field(
                RollRewardCrateHologramLineData::textTranslationZ,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter text translation Z:"),
                displayName = "Text Translation Z",
                iconMaterial = Material.TIPPED_ARROW,
                description = listOf("Z translation offset of the rotating reward name.")
            )
        }
    }
}
