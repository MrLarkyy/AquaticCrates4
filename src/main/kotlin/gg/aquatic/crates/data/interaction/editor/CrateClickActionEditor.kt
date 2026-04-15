package gg.aquatic.crates.data.interaction.editor

import gg.aquatic.crates.data.interaction.*

import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder

fun TypedNestedSchemaBuilder<CrateClickActionData>.defineCrateClickActionEditor(
    adapter: CrateClickActionEntryFieldAdapter,
) {
    fieldPattern(
        displayName = "Action",
        adapter = adapter,
        description = listOf(
            "Left click to edit this action.",
            "Right click to change its action type."
        )
    )
    include(visibleWhen = { it.matchesCrateClickActionSubtype("message") }) {
        with(MessageCrateClickActionData) { defineEditor() }
    }
    include(visibleWhen = { it.matchesCrateClickActionSubtype("actionbar") }) {
        with(ActionbarCrateClickActionData) { defineEditor() }
    }
    include(visibleWhen = { it.matchesCrateClickActionSubtype("command") }) {
        with(CommandCrateClickActionData) { defineEditor() }
    }
    include(visibleWhen = { it.matchesCrateClickActionSubtype("sound") }) {
        with(SoundCrateClickActionData) { defineEditor() }
    }
    include(visibleWhen = { it.matchesCrateClickActionSubtype("stop-sound") }) {
        with(StopSoundCrateClickActionData) { defineEditor() }
    }
    include(visibleWhen = { it.matchesCrateClickActionSubtype("title") }) {
        with(TitleCrateClickActionData) { defineEditor() }
    }
}


