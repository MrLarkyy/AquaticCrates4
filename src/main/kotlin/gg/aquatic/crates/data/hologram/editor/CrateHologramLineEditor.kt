package gg.aquatic.crates.data.hologram.editor

import gg.aquatic.crates.data.hologram.*

import gg.aquatic.crates.data.editor.core.listValue
import gg.aquatic.crates.data.editor.core.mapValue
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder

fun TypedNestedSchemaBuilder<CrateHologramLineData>.defineHologramLineEditor() {
    fieldPattern(
        displayName = "Hologram Line",
        adapter = HologramLineEntryFieldAdapter,
        description = listOf(
            "Left click to edit this hologram line.",
            "Right click to change its line type."
        )
    )
    include<TextCrateHologramLineData>(visibleWhen = { it.matchesHologramLineSubtype("text") }) {
        with(TextCrateHologramLineData) {
            defineEditor()
        }
    }
    include<ItemCrateHologramLineData>(visibleWhen = { it.matchesHologramLineSubtype("item") }) {
        with(ItemCrateHologramLineData) {
            defineEditor()
        }
    }
    include<AnimatedCrateHologramLineData>(visibleWhen = { it.matchesHologramLineSubtype("animated") }) {
        with(AnimatedCrateHologramLineData) {
            defineEditor()
        }
    }
    include<RollRewardCrateHologramLineData>(visibleWhen = { it.matchesHologramLineSubtype("roll-reward") }) {
        with(RollRewardCrateHologramLineEditor) {
            defineEditor()
        }
    }
}

fun TypedNestedSchemaBuilder<AnimatedHologramFrameData>.defineHologramFrameLineEditor() {
    fieldPattern(
        "line",
        displayName = "Animation Frame",
        adapter = HologramFrameLineEntryFieldAdapter,
        description = listOf(
            "Left click to edit this animation frame.",
            "Right click to change its frame line type."
        )
    )
    include<TextCrateHologramLineData>(visibleWhen = { it.matchesHologramLineSubtype("text") }) {
        group(AnimatedHologramFrameData::line) {
            with(TextCrateHologramLineData) { defineEditor() }
        }
    }
    include<ItemCrateHologramLineData>(visibleWhen = { it.matchesHologramLineSubtype("item") }) {
        group(AnimatedHologramFrameData::line) {
            with(ItemCrateHologramLineData) { defineEditor() }
        }
    }
}

internal fun EditorFieldContext.matchesHologramLineSubtype(id: String): Boolean {
    return findHologramLineSubtypeId()?.equals(id, ignoreCase = true) == true
}

internal fun EditorFieldContext.findHologramLineSubtypeId(): String? {
    val direct = value.mapValue("type")?.stringContentOrNull
    if (direct != null) return direct

    var current: com.charleskorn.kaml.YamlNode = root
    var candidate: String? = null
    for (segment in pathSegments) {
        current = when (val index = segment.toIntOrNull()) {
            null -> {
                current.mapValue(segment) ?: return candidate
            }
            else -> {
                current.listValue(index) ?: return candidate
            }
        }

        val currentType = current.mapValue("type")?.stringContentOrNull
        if (currentType != null) {
            candidate = currentType
        }
    }

    return candidate
}


