package gg.aquatic.crates.data.interactable.editor

import gg.aquatic.crates.data.interactable.*

import gg.aquatic.waves.serialization.editor.meta.IntFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.IntFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import org.bukkit.Material
import kotlin.reflect.KProperty1

internal fun <T> TypedNestedSchemaBuilder<T>.defineInteractableViewRangeEditor(
    property: KProperty1<T, Int>,
) {
    field(
        property,
        IntFieldAdapter,
        IntFieldConfig(prompt = "Enter interactable view range:", min = 1),
        displayName = "View Range",
        description = listOf("Maximum distance where this clientside interactable stays visible.")
    )
}

internal fun <T> TypedNestedSchemaBuilder<T>.defineModelBackedInteractableEditor(
    modelId: KProperty1<T, String>,
    viewRange: KProperty1<T, Int>,
    offsetX: KProperty1<T, Double>,
    offsetY: KProperty1<T, Double>,
    offsetZ: KProperty1<T, Double>,
    prompt: String,
    validator: (String) -> String?,
    description: String,
    modelIcon: Material,
) {
    field(
        modelId,
        TextFieldAdapter,
        TextFieldConfig(
            prompt = prompt,
            validator = validator
        ),
        displayName = "Model Id",
        iconMaterial = modelIcon,
        description = listOf(description)
    )
    defineInteractableViewRangeEditor(viewRange)
    defineInteractableOffsetEditor(offsetX, offsetY, offsetZ)
}


