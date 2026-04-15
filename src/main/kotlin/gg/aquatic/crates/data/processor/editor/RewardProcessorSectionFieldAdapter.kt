package gg.aquatic.crates.data.processor.editor

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.processor.*
import gg.aquatic.crates.data.CrateDataFormats
import gg.aquatic.crates.data.editor.core.SwitchingSectionFieldAdapter
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.editor.core.mapValue
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.FieldEditResult
import org.bukkit.Material
import org.bukkit.entity.Player

object RewardProcessorSectionFieldAdapter : SwitchingSectionFieldAdapter(
    sectionName = "Reward Processor",
    iconMaterial = Material.HOPPER_MINECART,
    defaultType = RewardProcessorType.BASIC.id,
    editHint = "Edit processor settings",
    changeHint = "Change processor type"
) {
    override suspend fun selectType(player: Player): String? = RewardProcessorTypeSelectionMenu.select(player)

    override fun updateType(context: EditorFieldContext, selected: String): FieldEditResult {
        return FieldEditResult.Updated(defaultNode(selected))
    }

    override fun currentType(context: EditorFieldContext): String {
        return context.value
            .mapValue("type")
            ?.stringContentOrNull
            ?: RewardProcessorType.BASIC.id
    }

    private fun defaultNode(type: String): YamlNode {
        return CrateDataFormats.yaml.encodeToNode(
            RewardProcessorData.serializer(),
            RewardProcessorType.defaultData(type)
        )
    }
}


