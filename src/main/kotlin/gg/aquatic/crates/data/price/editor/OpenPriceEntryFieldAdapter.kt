package gg.aquatic.crates.data.price.editor

import gg.aquatic.crates.data.price.*

import gg.aquatic.crates.data.editor.polymorphic.PolymorphicEntryFieldAdapter

object OpenPriceEntryFieldAdapter : PolymorphicEntryFieldAdapter(
    sectionName = "Price",
    iconResolver = { OpenPriceTypes.definition(it ?: "")?.icon },
    nameResolver = { OpenPriceTypes.definition(it ?: "")?.displayName },
    selectType = OpenPriceSelectionMenu::select,
    createElement = OpenPriceTypes::defaultElement,
    currentTypeResolver = { it.findOpenPriceSubtypeId() }
)


