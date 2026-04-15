package gg.aquatic.crates.yaml

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlNull
import com.charleskorn.kaml.YamlScalar
import gg.aquatic.crates.data.editor.core.encodeToNode
import kotlinx.serialization.KSerializer

fun <T> Yaml.encodeCompactString(serializer: KSerializer<T>, value: T): String {
    val compactNode = encodeToNode(serializer, value).pruneEmpty()
    return encodeToString(YamlNode.serializer(), compactNode)
}

fun Yaml.parseCompactNode(text: String): YamlNode {
    return parseToYamlNode(text).pruneEmpty()
}

fun YamlNode.mergeMissing(defaults: YamlNode): YamlNode {
    return when {
        this is YamlNull -> defaults
        this is YamlMap && defaults is YamlMap -> {
            val merged = LinkedHashMap(entries)
            val currentEntriesByName = entries.entries.associateBy { it.key.lookupKey() }

            for ((defaultKey, defaultValue) in defaults.entries) {
                val currentEntry = currentEntriesByName[defaultKey.lookupKey()]
                when {
                    currentEntry == null -> merged[defaultKey] = defaultValue
                    else -> merged[currentEntry.key] = currentEntry.value.mergeMissing(defaultValue)
                }
            }
            copy(entries = merged)
        }

        else -> this
    }
}

private fun YamlNode.lookupKey(): String {
    return when (this) {
        is YamlScalar -> content
        else -> toString()
    }
}

private fun YamlNode.pruneEmpty(): YamlNode {
    return when (this) {
        is YamlNull -> this
        is YamlList -> {
            val items = items.mapNotNull { child ->
                child.pruneEmpty().takeUnless { it is YamlNull }
            }
            copy(items = items)
        }

        is YamlMap -> {
            val entries = entries.mapNotNull { (key, value) ->
                value.pruneEmpty()
                    .takeUnless { pruned -> pruned is YamlNull || pruned.isEmptyContainer() }
                    ?.let { key to it }
            }.toMap()
            copy(entries = entries)
        }

        else -> this
    }
}

private fun YamlNode.isEmptyContainer(): Boolean {
    return when (this) {
        is YamlList -> items.isEmpty()
        is YamlMap -> entries.isEmpty()
        else -> false
    }
}
