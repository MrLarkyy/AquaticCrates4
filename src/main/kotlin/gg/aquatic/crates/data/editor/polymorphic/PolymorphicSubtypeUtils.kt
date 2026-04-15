package gg.aquatic.crates.data.editor.polymorphic

import gg.aquatic.crates.data.editor.core.findByPath
import gg.aquatic.crates.data.editor.core.mapValue
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext

internal fun EditorFieldContext.findPolymorphicSubtypeId(): String? {
    val direct = value.mapValue("type")?.stringContentOrNull
    if (direct != null) {
        return direct
    }

    val numericIndex = pathSegments.indexOfLast { it.toIntOrNull() != null }
    if (numericIndex == -1) {
        return null
    }

    return root.findByPath(pathSegments.take(numericIndex + 1))
        ?.mapValue("type")
        ?.stringContentOrNull
}

internal fun EditorFieldContext.matchesPolymorphicSubtype(id: String): Boolean {
    val currentType = findPolymorphicSubtypeId() ?: return false
    return currentType.equals(id, ignoreCase = true)
}
