package gg.aquatic.crates.data.interactable

import gg.aquatic.clientside.serialize.ClientsideMEGSettings
import gg.aquatic.crates.data.interactable.editor.defineModelBackedInteractableEditor
import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("meg")
data class MEGCrateInteractableData(
    val modelId: String = "crate_model",
    val viewRange: Int = 50,
    val offsetX: Double = 0.0,
    val offsetY: Double = 0.0,
    val offsetZ: Double = 0.0,
) : CrateInteractableData() {

    override fun toSettings() = ClientsideMEGSettings(
        modelId = modelId,
        viewRange = viewRange,
        offsetX = offsetX,
        offsetY = offsetY,
        offsetZ = offsetZ
    )

    companion object {
        fun TypedNestedSchemaBuilder<MEGCrateInteractableData>.defineEditor() {
            defineModelBackedInteractableEditor(
                MEGCrateInteractableData::modelId,
                MEGCrateInteractableData::viewRange,
                MEGCrateInteractableData::offsetX,
                MEGCrateInteractableData::offsetY,
                MEGCrateInteractableData::offsetZ,
                prompt = "Enter MEG model id:",
                validator = CrateDataValidators::validateModelEngineModel,
                description = "ModelEngine model id used for this clientside interactable.",
                modelIcon = Material.ARMOR_STAND,
            )
        }
    }
}
