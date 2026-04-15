package gg.aquatic.crates.data.interaction

import gg.aquatic.crates.data.editor.polymorphic.findPolymorphicSubtypeId
import gg.aquatic.crates.data.editor.polymorphic.matchesPolymorphicSubtype
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext

internal fun EditorFieldContext.matchesCrateClickActionSubtype(id: String): Boolean {
    return matchesPolymorphicSubtype(id)
}

internal fun EditorFieldContext.findCrateClickActionSubtypeId(): String? {
    return findPolymorphicSubtypeId()
}
