package gg.aquatic.crates.data.hologram

import gg.aquatic.common.toMMComponent
import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.kholograms.line.TextHologramLine
import gg.aquatic.waves.serialization.editor.meta.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Display
import org.joml.Vector3f

@Serializable
@SerialName("text")
data class TextCrateHologramLineData(
    val text: String = "<yellow>Crate",
    val height: Double = 0.3,
    val lineWidth: Int = 200,
    val scale: Double = 1.0,
    val billboard: Display.Billboard = Display.Billboard.CENTER,
    val hasShadow: Boolean = true,
    val backgroundColor: String? = null,
    val seeThrough: Boolean = true,
    val transformationDuration: Int = 0,
    val teleportInterpolation: Int = 0,
    val translationX: Double = 0.0,
    val translationY: Double = 0.0,
    val translationZ: Double = 0.0,
) : CrateHologramLineData() {

    override fun toSettings(rewardEntries: List<RewardHologramEntry>): List<TextHologramLine.Settings> {
        return listOf(TextHologramLine.Settings(
            height = height,
            text = text.toMMComponent(),
            lineWidth = lineWidth,
            scale = scale.toFloat(),
            billboard = billboard,
            filter = { true },
            hasShadow = hasShadow,
            backgroundColor = backgroundColor?.let(::parseColor),
            isSeeThrough = seeThrough,
            transformationDuration = transformationDuration,
            failLine = null,
            teleportInterpolation = teleportInterpolation,
            translation = Vector3f(translationX.toFloat(), translationY.toFloat(), translationZ.toFloat())
        ))
    }

    companion object {
        fun TypedNestedSchemaBuilder<TextCrateHologramLineData>.defineEditor() {
            field(
                TextCrateHologramLineData::text,
                TextFieldAdapter,
                TextFieldConfig(prompt = "Enter hologram text:", showFormattedPreview = true),
                displayName = "Text",
                iconMaterial = Material.PAPER,
                description = listOf("Text displayed by this hologram line.")
            )
            field(
                TextCrateHologramLineData::lineWidth,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter line width:", min = 1),
                displayName = "Line Width",
                iconMaterial = Material.OAK_SIGN,
                description = listOf("Maximum width before the text display wraps.")
            )
            defineSharedEditor()
            field(
                TextCrateHologramLineData::hasShadow,
                displayName = "Has Shadow",
                iconMaterial = Material.GRAY_DYE,
                description = listOf("If enabled, renders a text shadow behind the line.")
            )
            field(
                TextCrateHologramLineData::backgroundColor,
                ColorFieldAdapter,
                ColorFieldConfig(prompt = "Enter background color (#RRGGBB or r;g;b):"),
                displayName = "Background Color",
                iconMaterial = Material.PURPLE_DYE,
                description = listOf("Optional background color behind the text.")
            )
            field(
                TextCrateHologramLineData::seeThrough,
                displayName = "See Through",
                iconMaterial = Material.GLASS,
                description = listOf("If enabled, allows the text to remain visible through blocks.")
            )
        }

        private fun TypedNestedSchemaBuilder<TextCrateHologramLineData>.defineSharedEditor() {
            field(
                TextCrateHologramLineData::height,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig(prompt = "Enter line height:", min = 0.0),
                displayName = "Height",
                iconMaterial = Material.LIGHT_BLUE_DYE,
                description = listOf("Vertical space taken by this hologram line.")
            )
            field(
                TextCrateHologramLineData::scale,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig(prompt = "Enter scale:", min = 0.0),
                displayName = "Scale",
                iconMaterial = Material.SLIME_BALL,
                description = listOf("Display scale applied to this line.")
            )
            field(
                TextCrateHologramLineData::billboard,
                EnumFieldAdapter,
                EnumFieldConfig(
                    prompt = "Enter billboard mode:",
                    values = { Display.Billboard.entries.map { it.name } }
                ),
                displayName = "Billboard",
                iconMaterial = Material.ITEM_FRAME,
                description = listOf("Billboard mode used by the display entity.")
            )
            field(
                TextCrateHologramLineData::transformationDuration,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter transformation duration:", min = 0),
                displayName = "Transformation Duration",
                iconMaterial = Material.REPEATER,
                description = listOf("Interpolation duration for transformation changes.")
            )
            field(
                TextCrateHologramLineData::teleportInterpolation,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter teleport interpolation:", min = 0),
                displayName = "Teleport Interpolation",
                iconMaterial = Material.CLOCK,
                description = listOf("Interpolation duration used when the line moves.")
            )
            field(
                TextCrateHologramLineData::translationX,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig(prompt = "Enter translation X:"),
                displayName = "Translation X",
                iconMaterial = Material.ARROW,
                description = listOf("X translation offset of the display entity.")
            )
            field(
                TextCrateHologramLineData::translationY,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig(prompt = "Enter translation Y:"),
                displayName = "Translation Y",
                iconMaterial = Material.SPECTRAL_ARROW,
                description = listOf("Y translation offset of the display entity.")
            )
            field(
                TextCrateHologramLineData::translationZ,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter,
                gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig(prompt = "Enter translation Z:"),
                displayName = "Translation Z",
                iconMaterial = Material.TIPPED_ARROW,
                description = listOf("Z translation offset of the display entity.")
            )
        }

        private fun parseColor(raw: String): Color? {
            return if (CrateDataValidators.isValidColor(raw)) {
                val value = raw.trim()
                if (value.startsWith("#")) {
                    Color.fromRGB(
                        value.substring(1, 3).toInt(16),
                        value.substring(3, 5).toInt(16),
                        value.substring(5, 7).toInt(16)
                    )
                } else {
                    val split = if (';' in value) value.split(';') else value.split(',')
                    Color.fromRGB(split[0].trim().toInt(), split[1].trim().toInt(), split[2].trim().toInt())
                }
            } else null
        }
    }
}
