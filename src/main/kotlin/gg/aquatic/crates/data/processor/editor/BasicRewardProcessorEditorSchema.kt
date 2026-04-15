package gg.aquatic.crates.data.processor.editor

import gg.aquatic.crates.data.processor.*

import gg.aquatic.waves.serialization.editor.meta.EditableModel

object BasicRewardProcessorEditorSchema :
    EditableModel<BasicRewardProcessorData>(BasicRewardProcessorData.serializer()) {

    override fun gg.aquatic.waves.serialization.editor.meta.TypedEditorSchemaBuilder<BasicRewardProcessorData>.define() {
        include {
            with(BasicRewardProcessorData) {
                defineEditor()
            }
        }
    }
}


