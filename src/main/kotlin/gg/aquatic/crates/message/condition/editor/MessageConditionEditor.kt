package gg.aquatic.crates.message.condition.editor

import gg.aquatic.crates.message.condition.*

import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder

fun TypedNestedSchemaBuilder<MessageConditionData>.defineMessageConditionEditor() {
    fieldPattern(
        displayName = "Visibility Condition",
        adapter = MessageConditionEntryFieldAdapter,
        description = listOf(
            "Left click to edit this visibility condition.",
            "Right click to change its condition type."
        )
    )
    include<HasPreviousPageMessageConditionData>(visibleWhen = { it.matchesMessageConditionSubtype("has-previous-page") }) { }
    include<HasNextPageMessageConditionData>(visibleWhen = { it.matchesMessageConditionSubtype("has-next-page") }) { }
    include<HasNextOrPreviousPageMessageConditionData>(visibleWhen = { it.matchesMessageConditionSubtype("has-next-or-previous-page") }) { }
    include<SenderPermissionMessageConditionData>(visibleWhen = { it.matchesMessageConditionSubtype("sender-permission") }) {
        with(SenderPermissionMessageConditionData) {
            defineEditor()
        }
    }
}


