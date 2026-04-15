package gg.aquatic.crates.data.rewardshowcase

import gg.aquatic.crates.data.rewardshowcase.editor.GlowColorFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EnumFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EnumFieldConfig
import gg.aquatic.waves.serialization.editor.meta.IntFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.IntFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay

@Serializable
data class ItemDisplayRewardShowcaseData(
    val viewRange: Int = 24,
    val scale: Double = 1.0,
    val displayNameYOffset: Double = 0.9,
    val glowing: Boolean = false,
    val glowColor: String = GlowColor.WHITE.id,
    val billboard: Display.Billboard = Display.Billboard.CENTER,
    val displayTransform: ItemDisplay.ItemDisplayTransform = ItemDisplay.ItemDisplayTransform.GROUND,
    val transformationDuration: Int = 0,
    val teleportInterpolation: Int = 0,
) {
    companion object {
        fun TypedNestedSchemaBuilder<ItemDisplayRewardShowcaseData>.defineEditor() {
            field(
                ItemDisplayRewardShowcaseData::viewRange,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter showcase view range:", min = 1),
                displayName = "View Range",
                iconMaterial = Material.SPYGLASS,
                description = listOf("Maximum distance where this reward showcase remains visible.")
            )
            field(
                ItemDisplayRewardShowcaseData::scale,
                displayName = "Scale",
                prompt = "Enter showcase scale:",
                iconMaterial = Material.SLIME_BALL,
                description = listOf("Base scale applied to the item display showcase.")
            )
            field(
                ItemDisplayRewardShowcaseData::displayNameYOffset,
                displayName = "Name Y Offset",
                prompt = "Enter reward name Y offset:",
                iconMaterial = Material.OAK_SIGN,
                description = listOf("Vertical offset of the reward display name label above this showcase.")
            )
            field(
                ItemDisplayRewardShowcaseData::glowing,
                displayName = "Glowing",
                prompt = "Enter true or false:",
                iconMaterial = Material.GLOW_INK_SAC,
                description = listOf("If enabled, the item display showcase always renders with glow.")
            )
            field(
                ItemDisplayRewardShowcaseData::glowColor,
                adapter = GlowColorFieldAdapter,
                displayName = "Glow Color",
                iconMaterial = Material.ORANGE_DYE,
                description = listOf("MC glow color used while the showcase is glowing.")
            )
            field(
                ItemDisplayRewardShowcaseData::billboard,
                EnumFieldAdapter,
                EnumFieldConfig(
                    prompt = "Enter showcase billboard mode:",
                    values = { Display.Billboard.entries.map { it.name } }
                ),
                displayName = "Billboard",
                iconMaterial = Material.ITEM_FRAME,
                description = listOf("Billboard mode used by the item display showcase.")
            )
            field(
                ItemDisplayRewardShowcaseData::displayTransform,
                EnumFieldAdapter,
                EnumFieldConfig(
                    prompt = "Enter item display transform:",
                    values = { ItemDisplay.ItemDisplayTransform.entries.map { it.name } }
                ),
                displayName = "Transform",
                iconMaterial = Material.ARMOR_STAND,
                description = listOf("Display transform used by the item showcase.")
            )
            field(
                ItemDisplayRewardShowcaseData::transformationDuration,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter transformation duration:", min = 0),
                displayName = "Transformation Duration",
                iconMaterial = Material.REPEATER,
                description = listOf("Interpolation duration for showcase transformation changes.")
            )
            field(
                ItemDisplayRewardShowcaseData::teleportInterpolation,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter teleport interpolation:", min = 0),
                displayName = "Teleport Interpolation",
                iconMaterial = Material.CLOCK,
                description = listOf("Interpolation duration used when the showcase moves.")
            )
        }
    }
}
