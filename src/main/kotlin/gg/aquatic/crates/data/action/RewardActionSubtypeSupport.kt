package gg.aquatic.crates.data.action

import gg.aquatic.crates.data.editor.polymorphic.findPolymorphicSubtypeId
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext

internal fun EditorFieldContext.matchesSubtype(id: String): Boolean {
    val currentType = findRewardActionSubtypeId() ?: return false
    return currentType.equals(id, ignoreCase = true)
}

internal fun EditorFieldContext.findRewardActionSubtypeId(): String? {
    return findPolymorphicSubtypeId()
}
