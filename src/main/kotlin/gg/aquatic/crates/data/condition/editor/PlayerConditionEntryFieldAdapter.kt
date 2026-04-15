package gg.aquatic.crates.data.condition.editor

import gg.aquatic.crates.data.condition.*

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicEntryFieldAdapter

object PlayerConditionEntryFieldAdapter : PolymorphicEntryFieldAdapter(
    sectionName = "Condition",
    iconResolver = { PlayerConditionTypes.definition(it ?: "")?.icon },
    nameResolver = { PlayerConditionTypes.definition(it ?: "")?.displayName },
    selectType = PlayerConditionSelectionMenu::select,
    createElement = PlayerConditionTypes::defaultElement,
    currentTypeResolver = { it.findConditionSubtypeId() }
)


