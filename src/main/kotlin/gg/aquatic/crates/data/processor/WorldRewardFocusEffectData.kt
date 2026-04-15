package gg.aquatic.crates.data.processor

import gg.aquatic.crates.data.rewardshowcase.GlowColor
import gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
sealed class WorldRewardFocusEffectData

@Serializable
@SerialName("scale")
data class ScaleWorldRewardFocusEffectData(
    val scaleMultiplier: Double = 1.2,
) : WorldRewardFocusEffectData() {
    fun normalized(): ScaleWorldRewardFocusEffectData {
        return copy(scaleMultiplier = scaleMultiplier.coerceAtLeast(0.1))
    }

    companion object {
        fun TypedNestedSchemaBuilder<ScaleWorldRewardFocusEffectData>.defineEditor() {
            field(
                ScaleWorldRewardFocusEffectData::scaleMultiplier,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter focused scale multiplier:", min = 0.1),
                displayName = "Scale Multiplier",
                iconMaterial = Material.SLIME_BALL,
                description = listOf("Multiplier applied while this reward is focused.")
            )
        }
    }
}

@Serializable
@SerialName("glow")
data class GlowWorldRewardFocusEffectData(
    val glowColor: String = GlowColor.WHITE.id,
) : WorldRewardFocusEffectData() {
    companion object {
        fun TypedNestedSchemaBuilder<GlowWorldRewardFocusEffectData>.defineEditor() {
            field(
                GlowWorldRewardFocusEffectData::glowColor,
                adapter = WorldRewardFocusGlowColorFieldAdapter,
                displayName = "Glow Color",
                iconMaterial = Material.GLOW_INK_SAC,
                description = listOf("Glow color applied while this reward is focused.")
            )
        }
    }
}

@Serializable
@SerialName("vertical-offset")
data class VerticalOffsetWorldRewardFocusEffectData(
    val yOffset: Double = 0.2,
) : WorldRewardFocusEffectData() {
    companion object {
        fun TypedNestedSchemaBuilder<VerticalOffsetWorldRewardFocusEffectData>.defineEditor() {
            field(
                VerticalOffsetWorldRewardFocusEffectData::yOffset,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter focused vertical offset:"),
                displayName = "Y Offset",
                iconMaterial = Material.SCAFFOLDING,
                description = listOf("Vertical offset applied while this reward is focused.")
            )
        }
    }
}
