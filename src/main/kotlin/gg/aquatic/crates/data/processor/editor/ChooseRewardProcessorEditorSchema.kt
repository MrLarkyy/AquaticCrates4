package gg.aquatic.crates.data.processor.editor

import gg.aquatic.crates.data.processor.*

import gg.aquatic.waves.serialization.editor.meta.EditableModel

object ChooseRewardProcessorEditorSchema :
    EditableModel<ChooseRewardProcessorData>(ChooseRewardProcessorData.serializer()) {

    override fun gg.aquatic.waves.serialization.editor.meta.TypedEditorSchemaBuilder<ChooseRewardProcessorData>.define() {
        include<ChooseRewardProcessorData> {
            with(ChooseRewardProcessorData) {
                defineEditor()
            }
        }
    }
}


