package gg.aquatic.crates.data.price

import gg.aquatic.crates.data.editor.polymorphic.findPolymorphicSubtypeId
import gg.aquatic.crates.data.editor.polymorphic.matchesPolymorphicSubtype
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext

internal fun EditorFieldContext.matchesSubtype(id: String): Boolean {
    return matchesPolymorphicSubtype(id)
}

internal fun EditorFieldContext.findOpenPriceSubtypeId(): String? {
    return findPolymorphicSubtypeId()
}
