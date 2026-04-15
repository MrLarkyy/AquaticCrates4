package gg.aquatic.crates.data.editor

import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.PREVIEW_TYPE_AUTOMATIC
import gg.aquatic.crates.data.editor.core.SwitchingSectionFieldAdapter
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.editor.core.withMapValue
import gg.aquatic.crates.data.editor.core.yamlScalar
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.FieldEditResult
import org.bukkit.Material
import org.bukkit.entity.Player

object PreviewSectionFieldAdapter : SwitchingSectionFieldAdapter(
    sectionName = "Preview",
    iconMaterial = Material.ENDER_EYE,
    defaultType = PREVIEW_TYPE_AUTOMATIC,
    editHint = "Edit preview settings",
    changeHint = "Change preview type"
) {
    override suspend fun selectType(player: Player): String? = PreviewTypeSelectionMenu.select(player)

    override fun updateType(context: EditorFieldContext, selected: String): FieldEditResult {
        return FieldEditResult.Updated(updatePreviewType(context.value, selected))
    }

    override fun currentType(context: EditorFieldContext): String {
        val currentObject = context.value as? YamlMap ?: return PREVIEW_TYPE_AUTOMATIC
        return currentObject.get<YamlNode>("previewType")?.stringContentOrNull ?: PREVIEW_TYPE_AUTOMATIC
    }

    private fun updatePreviewType(current: YamlNode, type: String): YamlNode {
        return current.withMapValue("previewType", yamlScalar(type))
    }
}
