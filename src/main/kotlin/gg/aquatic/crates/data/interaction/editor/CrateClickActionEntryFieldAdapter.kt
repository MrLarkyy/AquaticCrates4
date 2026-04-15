package gg.aquatic.crates.data.interaction.editor

import gg.aquatic.crates.data.interaction.*

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicEntryFieldAdapter

class CrateClickActionEntryFieldAdapter(
    allowDestroy: Boolean,
) : PolymorphicEntryFieldAdapter(
    sectionName = "Action",
    iconResolver = { CrateClickActionTypes.definition(it ?: "")?.icon },
    nameResolver = { CrateClickActionTypes.definition(it ?: "")?.displayName },
    selectType = { player -> CrateClickActionSelectionMenu.select(player, allowDestroy) },
    createElement = CrateClickActionTypes::defaultElement,
    currentTypeResolver = { it.findCrateClickActionSubtypeId() }
)


