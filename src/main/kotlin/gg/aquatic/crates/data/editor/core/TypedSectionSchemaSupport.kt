package gg.aquatic.crates.data.editor.core

import gg.aquatic.waves.serialization.editor.meta.EditorFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.TypedEditorSchemaBuilder
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import org.bukkit.Material
import kotlin.reflect.KProperty1

fun <T, S> TypedEditorSchemaBuilder<T>.switchingSection(
    property: KProperty1<T, S>,
    adapter: EditorFieldAdapter,
    displayName: String,
    iconMaterial: Material,
    description: List<String>,
    searchTags: List<String> = emptyList(),
    searchable: Boolean = true,
    visibleWhen: (EditorFieldContext) -> Boolean,
    block: TypedNestedSchemaBuilder<S>.() -> Unit
) {
    field(
        property,
        adapter = adapter,
        displayName = displayName,
        iconMaterial = iconMaterial,
        description = description,
        searchTags = searchTags,
        searchable = searchable,
        visibleWhen = visibleWhen
    )
    include<T>(visibleWhen = visibleWhen) {
        group(property, block)
    }
}

fun <T, S> TypedNestedSchemaBuilder<T>.switchingSection(
    property: KProperty1<T, S>,
    adapter: EditorFieldAdapter,
    displayName: String,
    iconMaterial: Material,
    description: List<String>,
    searchTags: List<String> = emptyList(),
    searchable: Boolean = true,
    visibleWhen: (EditorFieldContext) -> Boolean,
    block: TypedNestedSchemaBuilder<S>.() -> Unit
) {
    field(
        property,
        adapter = adapter,
        displayName = displayName,
        iconMaterial = iconMaterial,
        description = description,
        searchTags = searchTags,
        searchable = searchable,
        visibleWhen = visibleWhen
    )
    include<T>(visibleWhen = visibleWhen) {
        group(property, block)
    }
}
