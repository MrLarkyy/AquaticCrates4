package gg.aquatic.crates.data.price.editor

import gg.aquatic.crates.data.price.*

import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder

fun TypedNestedSchemaBuilder<OpenPriceData>.defineOpenPriceEditor() {
    fieldPattern(
        displayName = "Price",
        adapter = OpenPriceEntryFieldAdapter,
        description = listOf(
            "Left click to edit this price.",
            "Right click to change its price type."
        )
    )
    include(visibleWhen = { it.matchesSubtype("crate-key") }) {
        with(CrateKeyOpenPriceData) {
            defineEditor()
        }
    }
    include(visibleWhen = { it.matchesSubtype("vault") }) {
        with(VaultOpenPriceData) {
            defineEditor()
        }
    }
}


