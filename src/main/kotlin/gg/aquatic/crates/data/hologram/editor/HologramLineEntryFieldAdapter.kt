package gg.aquatic.crates.data.hologram.editor

import gg.aquatic.crates.data.hologram.*

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicEntryFieldAdapter

object HologramLineEntryFieldAdapter : PolymorphicEntryFieldAdapter(
    sectionName = "Hologram Line",
    iconResolver = { CrateHologramLineTypes.definition(it ?: "")?.icon },
    nameResolver = { CrateHologramLineTypes.definition(it ?: "")?.displayName },
    selectType = HologramLineSelectionMenu::select,
    createElement = CrateHologramLineTypes::defaultElement,
    currentTypeResolver = { it.findHologramLineSubtypeId() }
)

object HologramFrameLineEntryFieldAdapter : PolymorphicEntryFieldAdapter(
    sectionName = "Animation Frame",
    iconResolver = { CrateHologramLineTypes.definition(it ?: "")?.icon },
    nameResolver = { CrateHologramLineTypes.definition(it ?: "")?.displayName },
    selectType = HologramLineSelectionMenu::selectFrame,
    createElement = CrateHologramLineTypes::defaultElement,
    currentTypeResolver = { it.findHologramLineSubtypeId() }
)


