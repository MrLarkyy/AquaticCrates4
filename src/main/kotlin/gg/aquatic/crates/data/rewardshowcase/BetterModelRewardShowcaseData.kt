package gg.aquatic.crates.data.rewardshowcase

import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.crates.data.rewardshowcase.editor.GlowColorFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.IntFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.IntFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class BetterModelRewardShowcaseData(
    val modelId: String = "crate_model",
    val viewRange: Int = 24,
    val scale: Double = 1.0,
    val displayNameYOffset: Double = 1.2,
    val glowing: Boolean = false,
    val glowColor: String = GlowColor.WHITE.id,
) {
    companion object {
        fun TypedNestedSchemaBuilder<BetterModelRewardShowcaseData>.defineEditor() {
            field(
                BetterModelRewardShowcaseData::modelId,
                TextFieldAdapter,
                TextFieldConfig(
                    prompt = "Enter BetterModel showcase model id:",
                    validator = CrateDataValidators::validateBetterModelModel
                ),
                displayName = "Model ID",
                iconMaterial = Material.GLOW_ITEM_FRAME,
                description = listOf("BetterModel model id used for this reward showcase.")
            )
            field(
                BetterModelRewardShowcaseData::viewRange,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter showcase view range:", min = 1),
                displayName = "View Range",
                iconMaterial = Material.SPYGLASS,
                description = listOf("Maximum distance where this reward showcase remains visible.")
            )
            field(
                BetterModelRewardShowcaseData::scale,
                displayName = "Scale",
                prompt = "Enter BetterModel showcase scale:",
                iconMaterial = Material.SLIME_BALL,
                description = listOf("Base scale applied to the BetterModel showcase.")
            )
            field(
                BetterModelRewardShowcaseData::displayNameYOffset,
                displayName = "Name Y Offset",
                prompt = "Enter reward name Y offset:",
                iconMaterial = Material.OAK_SIGN,
                description = listOf("Vertical offset of the reward display name label above this showcase.")
            )
            field(
                BetterModelRewardShowcaseData::glowing,
                displayName = "Glowing",
                prompt = "Enter true or false:",
                iconMaterial = Material.GLOW_INK_SAC,
                description = listOf("If enabled, the BetterModel showcase always renders with glow.")
            )
            field(
                BetterModelRewardShowcaseData::glowColor,
                adapter = GlowColorFieldAdapter,
                displayName = "Glow Color",
                iconMaterial = Material.ORANGE_DYE,
                description = listOf("MC glow color used while the BetterModel showcase is glowing.")
            )
        }
    }
}
