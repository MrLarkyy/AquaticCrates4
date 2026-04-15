package gg.aquatic.crates.data.provider

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.CrateDataFormats
import gg.aquatic.crates.data.editor.core.SwitchingSectionFieldAdapter
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.core.mapValue
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.FieldEditResult
import org.bukkit.Material
import org.bukkit.entity.Player

object RewardProviderSectionFieldAdapter : SwitchingSectionFieldAdapter(
    sectionName = "Rewards",
    iconMaterial = Material.CHEST_MINECART,
    defaultType = RewardProviderType.SIMPLE.id,
    editHint = "Edit reward provider settings",
    changeHint = "Change reward provider type"
) {
    override suspend fun selectType(player: Player): String? = RewardProviderTypeSelectionMenu.select(player)

    override fun updateType(context: EditorFieldContext, selected: String): FieldEditResult {
        return FieldEditResult.Updated(defaultNode(selected))
    }

    override fun currentType(context: EditorFieldContext): String {
        return context.value
            .mapValue("type")
            ?.stringContentOrNull
            ?: RewardProviderType.SIMPLE.id
    }

    private fun defaultNode(type: String): YamlNode {
        return CrateDataFormats.yaml.encodeToNode(
            RewardProviderData.serializer(),
            RewardProviderType.defaultData(type)
        )
    }
}
