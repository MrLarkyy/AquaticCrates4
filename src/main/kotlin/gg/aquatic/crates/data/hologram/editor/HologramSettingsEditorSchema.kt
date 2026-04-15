package gg.aquatic.crates.data.hologram.editor

import gg.aquatic.crates.data.hologram.*

import gg.aquatic.crates.data.CrateHologramData
import gg.aquatic.crates.data.resolveCrateDataDescriptor
import gg.aquatic.waves.serialization.editor.meta.EditableModel
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.TypedEditorSchemaBuilder

object HologramSettingsEditorSchema : EditableModel<CrateHologramData>(CrateHologramData.serializer()) {
    override fun resolveDescriptor(context: EditorFieldContext) = resolveCrateDataDescriptor(context)

    override fun TypedEditorSchemaBuilder<CrateHologramData>.define() {
        include<CrateHologramData> {
            with(CrateHologramData) { defineEditor() }
        }
    }
}


