package gg.aquatic.crates.data.rewardshowcase.editor

import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.editor.core.SwitchingSectionFieldAdapter
import gg.aquatic.crates.data.editor.core.findByPath
import gg.aquatic.crates.data.editor.core.mapValue
import gg.aquatic.crates.data.editor.core.replaceByPath
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.editor.core.withMapValue
import gg.aquatic.crates.data.editor.core.yamlScalar
import gg.aquatic.crates.data.rewardshowcase.RewardShowcaseType
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.FieldEditResult
import org.bukkit.Material
import org.bukkit.entity.Player

object RewardShowcaseSectionFieldAdapter : SwitchingSectionFieldAdapter(
    sectionName = "Reward Showcase",
    iconMaterial = Material.ENDER_EYE,
    defaultType = RewardShowcaseType.ITEM_DISPLAY.id,
    editHint = "Edit showcase settings",
    changeHint = "Change showcase type"
) {
    override suspend fun selectType(player: Player): String? = RewardShowcaseTypeSelectionMenu.select(player)

    override fun updateType(context: EditorFieldContext, selected: String): FieldEditResult {
        val ownerPath = context.findShowcaseOwnerPath() ?: return FieldEditResult.NoChange
        val currentOwner = context.root.findByPath(ownerPath) ?: return FieldEditResult.NoChange
        val updatedOwner = updateShowcaseType(currentOwner, selected)
        return FieldEditResult.UpdatedRoot(context.root.replaceByPath(ownerPath, updatedOwner))
    }

    override fun currentType(context: EditorFieldContext): String {
        val ownerPath = context.findShowcaseOwnerPath() ?: return RewardShowcaseType.ITEM_DISPLAY.id
        val owner = context.root.findByPath(ownerPath) as? YamlMap ?: return RewardShowcaseType.ITEM_DISPLAY.id
        return owner.mapValue("rewardShowcaseType")?.stringContentOrNull ?: RewardShowcaseType.ITEM_DISPLAY.id
    }

    private fun updateShowcaseType(root: YamlNode, type: String): YamlNode {
        return root.withMapValue("rewardShowcaseType", yamlScalar(type))
    }

    private fun EditorFieldContext.findShowcaseOwnerPath(): List<String>? {
        val candidates = (pathSegments.size downTo 0).map { pathSegments.take(it) }
        return candidates.firstOrNull { candidate ->
            val node = root.findByPath(candidate) as? YamlMap ?: return@firstOrNull false
            node.mapValue("rewardShowcaseType") != null
        }
    }
}
