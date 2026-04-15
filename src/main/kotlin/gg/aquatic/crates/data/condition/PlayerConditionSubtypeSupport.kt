package gg.aquatic.crates.data.condition

import gg.aquatic.crates.data.editor.polymorphic.findPolymorphicSubtypeId
import gg.aquatic.crates.data.editor.polymorphic.matchesPolymorphicSubtype
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext

internal fun EditorFieldContext.matchesConditionSubtype(id: String): Boolean {
    return matchesPolymorphicSubtype(id)
}

internal fun EditorFieldContext.findConditionSubtypeId(): String? {
    return findPolymorphicSubtypeId()
}
