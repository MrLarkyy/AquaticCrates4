package gg.aquatic.crates.data.processor

import gg.aquatic.crates.data.CrateHologramData
import gg.aquatic.crates.data.processor.editor.WorldChooseRewardHologramSectionFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class WorldRewardDisplaySettingsData(
    val radius: Double = 2.15,
    val xOffset: Double = 0.0,
    val yOffset: Double = 1.15,
    val zOffset: Double = 0.0,
    val rewardHologram: CrateHologramData? = null,
    val rewardHologramYOffset: Double = 0.3,
    val defaultScale: Double = 1.0,
    val focusedScale: Double = 1.2,
    val focusEffects: List<@Polymorphic WorldRewardFocusEffectData> = emptyList(),
) {
    fun normalized(): WorldRewardDisplaySettingsData {
        val normalizedDefaultScale = defaultScale.coerceAtLeast(0.25)
        val normalizedFocusedScale = focusedScale.coerceAtLeast(0.25)
        val normalizedEffects = focusEffects.map {
            when (it) {
                is ScaleWorldRewardFocusEffectData -> it.normalized()
                is GlowWorldRewardFocusEffectData -> it
                is VerticalOffsetWorldRewardFocusEffectData -> it
            }
        }

        return copy(
            radius = radius.coerceAtLeast(0.5),
            defaultScale = normalizedDefaultScale,
            focusedScale = normalizedFocusedScale,
            focusEffects = when {
                normalizedEffects.isNotEmpty() -> normalizedEffects
                normalizedFocusedScale != normalizedDefaultScale -> listOf(
                    ScaleWorldRewardFocusEffectData(
                        scaleMultiplier = (normalizedFocusedScale / normalizedDefaultScale).coerceAtLeast(0.1)
                    )
                )
                else -> emptyList()
            }
        )
    }

    companion object {
        fun TypedNestedSchemaBuilder<WorldRewardDisplaySettingsData>.defineEditor() {
            field(
                WorldRewardDisplaySettingsData::radius,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter display radius:", min = 0.5),
                displayName = "Radius",
                iconMaterial = Material.COMPASS,
                description = listOf("Horizontal radius used to place the reward previews around the center.")
            )
            field(
                WorldRewardDisplaySettingsData::xOffset,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter X offset:"),
                displayName = "X Offset",
                iconMaterial = Material.SCAFFOLDING,
                description = listOf("Horizontal offset of the world choose layout.")
            )
            field(
                WorldRewardDisplaySettingsData::yOffset,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter Y offset:"),
                displayName = "Y Offset",
                iconMaterial = Material.SCAFFOLDING,
                description = listOf("Vertical offset of the world choose layout.")
            )
            field(
                WorldRewardDisplaySettingsData::zOffset,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter Z offset:"),
                displayName = "Z Offset",
                iconMaterial = Material.SCAFFOLDING,
                description = listOf("Depth offset of the world choose layout.")
            )
            field(
                WorldRewardDisplaySettingsData::rewardHologram,
                adapter = WorldChooseRewardHologramSectionFieldAdapter,
                displayName = "Reward Hologram",
                iconMaterial = Material.END_CRYSTAL,
                description = listOf(
                    "Optional hologram attached above each shown reward showcase.",
                    "Useful for names, lore lines, rarity text or any extra detail lines."
                )
            )
            field(
                WorldRewardDisplaySettingsData::rewardHologramYOffset,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter reward hologram Y offset:"),
                displayName = "Reward Hologram Y Offset",
                iconMaterial = Material.FEATHER,
                description = listOf("Vertical offset applied from the showcase position to the reward hologram.")
            )
            field(
                WorldRewardDisplaySettingsData::defaultScale,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter default scale:", min = 0.25),
                displayName = "Default Scale",
                iconMaterial = Material.SLIME_BALL,
                description = listOf("Scale used for non-focused reward previews.")
            )
            field(
                WorldRewardDisplaySettingsData::focusedScale,
                visibleWhen = { false }
            )
            list(
                WorldRewardDisplaySettingsData::focusEffects,
                displayName = "Focus Effects",
                iconMaterial = Material.SPECTRAL_ARROW,
                description = listOf(
                    "Effects applied to the currently focused reward.",
                    "Use scale and glow to highlight the active selection."
                ),
                newValueFactory = WorldRewardFocusEffectSelectionMenu.entryFactory
            ) {
                include<ScaleWorldRewardFocusEffectData> {
                    with(ScaleWorldRewardFocusEffectData) { defineEditor() }
                }
                include<GlowWorldRewardFocusEffectData> {
                    with(GlowWorldRewardFocusEffectData) { defineEditor() }
                }
                include<VerticalOffsetWorldRewardFocusEffectData> {
                    with(VerticalOffsetWorldRewardFocusEffectData) { defineEditor() }
                }
            }
        }
    }
}
