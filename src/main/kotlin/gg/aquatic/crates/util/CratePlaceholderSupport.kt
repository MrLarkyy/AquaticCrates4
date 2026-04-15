package gg.aquatic.crates.util

import gg.aquatic.treepapi.updatePAPIPlaceholders
import org.bukkit.entity.Player

fun normalizeCratePlaceholderKey(placeholder: String): String {
    return placeholder.removePrefix("%").removeSuffix("%")
}

fun String.replaceCratePlaceholder(placeholder: String, replacement: String): String {
    val normalized = normalizeCratePlaceholderKey(placeholder)
    return replace("%$normalized%", replacement)
}

fun String.replaceCratePlaceholders(replacements: Map<String, String>): String {
    var updated = this
    for ((placeholder, replacement) in replacements) {
        updated = updated.replaceCratePlaceholder(placeholder, replacement)
    }
    return updated
}

fun String.replacePlayerPlaceholder(player: Player): String {
    val updated = replaceCratePlaceholder("%player%", player.name)
    return runCatching { updated.updatePAPIPlaceholders(player) }.getOrElse { updated }
}

fun <T> withPlayerPlaceholder(player: Player, updater: ((T, String) -> String)? = null): (T, String) -> String {
    return { binder, text ->
        val updated = updater?.invoke(binder, text) ?: text
        updated.replacePlayerPlaceholder(player)
    }
}
