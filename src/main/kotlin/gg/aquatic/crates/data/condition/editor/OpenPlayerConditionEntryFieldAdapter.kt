package gg.aquatic.crates.data.condition.editor

import gg.aquatic.crates.data.condition.*

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicEntryFieldAdapter

object OpenPlayerConditionEntryFieldAdapter : PolymorphicEntryFieldAdapter(
    sectionName = "Condition",
    iconResolver = { PlayerConditionTypes.definition(it ?: "")?.icon },
    nameResolver = { PlayerConditionTypes.definition(it ?: "")?.displayName },
    selectType = OpenPlayerConditionSelectionMenu::select,
    createElement = PlayerConditionTypes::defaultElement,
    currentTypeResolver = { it.findConditionSubtypeId() }
)


