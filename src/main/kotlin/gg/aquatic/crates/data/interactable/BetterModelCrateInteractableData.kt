package gg.aquatic.crates.data.interactable

import gg.aquatic.clientside.serialize.ClientsideBMSettings
import gg.aquatic.crates.data.interactable.editor.defineModelBackedInteractableEditor
import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("better-model")
data class BetterModelCrateInteractableData(
    val modelId: String = "crate_model",
    val viewRange: Int = 50,
    val offsetX: Double = 0.0,
    val offsetY: Double = 0.0,
    val offsetZ: Double = 0.0,
    val scale: Double = 1.0,
    val glowing: Boolean = false,
) : CrateInteractableData() {

    override fun toSettings() = ClientsideBMSettings(
        modelId = modelId,
        viewRange = viewRange,
        offsetX = offsetX,
        offsetY = offsetY,
        offsetZ = offsetZ,
        scale = scale.coerceAtLeast(0.1),
        glowing = glowing,
    )

    companion object {
        fun TypedNestedSchemaBuilder<BetterModelCrateInteractableData>.defineEditor() {
            defineModelBackedInteractableEditor(
                BetterModelCrateInteractableData::modelId,
                BetterModelCrateInteractableData::viewRange,
                BetterModelCrateInteractableData::offsetX,
                BetterModelCrateInteractableData::offsetY,
                BetterModelCrateInteractableData::offsetZ,
                prompt = "Enter BetterModel model id:",
                validator = CrateDataValidators::validateBetterModelModel,
                description = "BetterModel model id used for this clientside interactable.",
                modelIcon = Material.GLOW_ITEM_FRAME,
            )
            field(
                BetterModelCrateInteractableData::scale,
                displayName = "Scale",
                prompt = "Enter BetterModel interactable scale:",
                iconMaterial = Material.SLIME_BALL,
                description = listOf("Base scale applied to this BetterModel interactable.")
            )
            field(
                BetterModelCrateInteractableData::glowing,
                displayName = "Glowing",
                prompt = "Enter true or false:",
                iconMaterial = Material.GLOW_INK_SAC,
                description = listOf("If enabled, this BetterModel interactable renders with glow.")
            )
        }
    }
}
