package gg.aquatic.crates.data.action.editor

import gg.aquatic.crates.data.action.*

import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder

fun TypedNestedSchemaBuilder<RewardActionData>.defineRewardActionEditor() {
    fieldPattern(
        displayName = "Action",
        adapter = RewardActionEntryFieldAdapter,
        description = listOf(
            "Left click to edit this action.",
            "Right click to change its action type."
        )
    )
    include(visibleWhen = { it.matchesSubtype("give-item") }) {
        with(GiveItemRewardActionData) {
            defineEditor()
        }
    }
    include(visibleWhen = { it.matchesSubtype("message") }) {
        with(MessageRewardActionData) {
            defineEditor()
        }
    }
    include(visibleWhen = { it.matchesSubtype("actionbar") }) {
        with(ActionbarRewardActionData) {
            defineEditor()
        }
    }
    include(visibleWhen = { it.matchesSubtype("command") }) {
        with(CommandRewardActionData) {
            defineEditor()
        }
    }
    include(visibleWhen = { it.matchesSubtype("sound") }) {
        with(SoundRewardActionData) {
            defineEditor()
        }
    }
    include(visibleWhen = { it.matchesSubtype("stop-sound") }) {
        with(StopSoundRewardActionData) {
            defineEditor()
        }
    }
    include(visibleWhen = { it.matchesSubtype("title") }) {
        with(TitleRewardActionData) {
            defineEditor()
        }
    }
    include(visibleWhen = { it.matchesSubtype("close-inventory") }) {
        with(CloseInventoryRewardActionData) {
            defineEditor()
        }
    }
}


