package gg.aquatic.crates.message

import gg.aquatic.crates.util.normalizeCratePlaceholderKey
import gg.aquatic.klocale.impl.paper.PaperMessage
import net.kyori.adventure.text.Component

fun PaperMessage.replacePlaceholder(
    placeholder: String,
    replacement: String
): PaperMessage {
    val normalized = normalizeCratePlaceholderKey(placeholder)
    val replacements = lines
        .flatMap { it.placeholders }
        .toSet()
        .associateWith { key ->
            if (key == normalized) replacement else "%$key%"
        }

    return replace(replacements)
}

fun PaperMessage.replacePlaceholder(
    placeholder: String,
    component: Component
): PaperMessage {
    val normalized = normalizeCratePlaceholderKey(placeholder)
    return replace(normalized, component)
}
