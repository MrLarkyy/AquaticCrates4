package gg.aquatic.crates.message.editor

import gg.aquatic.crates.message.*

import gg.aquatic.common.toMMComponent
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.meta.EditorFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object MessageEntryFieldAdapter : EditorFieldAdapter {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val message = context.decodeOrNull(EditableMessageData.serializer()) ?: return defaultItem()
        return stackedItem(Material.BOOK) {
            displayName = title(context.label)
            appendDescription(context.description)
            lore += value("Lines: ", message.lines.size.toString())
            lore += value("Paginated: ", if (message.pagination != null) "yes" else "no")
            message.pagination?.let {
                lore += value("Page Size: ", it.pageSize.toString())
            }
            lore += value("Send Actions: ", message.actions.size.toString())
            lore += section("Preview")
            lore += message.renderPreviewLines(limit = 5)
            lore += hint("Left Click: Edit message")
        }.getItem()
    }

    override suspend fun edit(
        player: org.bukkit.entity.Player,
        context: EditorFieldContext,
        buttonType: gg.aquatic.kmenu.inventory.ButtonType
    ): gg.aquatic.waves.serialization.editor.meta.FieldEditResult {
        return gg.aquatic.waves.serialization.editor.meta.FieldEditResult.PassThrough
    }
}

object MessageLineEntryFieldAdapter : EditorFieldAdapter {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val line = context.decodeOrNull(EditableMessageLineData.serializer()) ?: return defaultItem()
        return stackedItem(Material.WRITABLE_BOOK) {
            displayName = title(context.label)
            lore += value("Components: ", line.components.size.toString())
            lore += value("Visibility Conditions: ", line.visibilityConditions.size.toString())
            lore += section("Preview")
            lore += line.renderPreviewComponent()
            lore += hint("Left Click: Edit line")
        }.getItem()
    }

    override suspend fun edit(
        player: org.bukkit.entity.Player,
        context: EditorFieldContext,
        buttonType: gg.aquatic.kmenu.inventory.ButtonType
    ): gg.aquatic.waves.serialization.editor.meta.FieldEditResult {
        return gg.aquatic.waves.serialization.editor.meta.FieldEditResult.PassThrough
    }
}

object MessageComponentEntryFieldAdapter : EditorFieldAdapter {
    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val component = context.decodeOrNull(MessageComponentData.serializer()) ?: return defaultItem()
        return stackedItem(Material.PAPER) {
            displayName = title(context.label)
            lore += value("Hover Lines: ", component.hover.size.toString())
            lore += value("Click Action: ", component.click?.type?.name ?: "none")
            lore += value("Visibility Conditions: ", component.visibilityConditions.size.toString())
            lore += section("Preview")
            lore += component.renderPreviewComponent()
            lore += hint("Left Click: Edit component")
        }.getItem()
    }

    override suspend fun edit(
        player: org.bukkit.entity.Player,
        context: EditorFieldContext,
        buttonType: gg.aquatic.kmenu.inventory.ButtonType
    ): gg.aquatic.waves.serialization.editor.meta.FieldEditResult {
        return gg.aquatic.waves.serialization.editor.meta.FieldEditResult.PassThrough
    }
}

private fun EditableMessageData.renderPreviewLines(limit: Int): List<Component> {
    val pageSize = pagination?.pageSize?.coerceAtLeast(1) ?: limit
    val previewLimit = minOf(limit, pageSize)
    val rendered = buildList {
        pagination?.header?.let { add(it.renderPreviewComponent()) }
        addAll(lines.take(previewLimit).map { it.renderPreviewComponent() })
        pagination?.footer?.let { add(it.renderPreviewComponent()) }
    }
    if (rendered.isEmpty()) {
        return listOf(hint("<empty>"))
    }

    val flattened = rendered.flatten().toMutableList()
    if (lines.size > previewLimit) {
        flattened += hint("...and ${lines.size - previewLimit} more lines")
    }
    return flattened
}

private fun EditableMessageLineData.renderPreviewComponent(): List<Component> {
    return listOf(
        runCatching { toMiniMessage().toMMComponent().decoration(TextDecoration.ITALIC, false) }
            .getOrElse { hint("<invalid MiniMessage>") }
    )
}

private fun MessageComponentData.renderPreviewComponent(): List<Component> {
    return listOf(
        runCatching { text.toMMComponent().decoration(TextDecoration.ITALIC, false) }
            .getOrElse { hint("<invalid MiniMessage>") }
    )
}

private fun gg.aquatic.stacked.StackedItemBuilder.appendDescription(description: List<String>) {
    if (description.isEmpty()) return
    lore += section("Description")
    lore += description.map(::hint)
}

private fun title(text: String): Component {
    return Component.text(text, NamedTextColor.AQUA)
        .decoration(TextDecoration.ITALIC, false)
}

private fun section(text: String): Component {
    return Component.text(text, NamedTextColor.DARK_AQUA)
        .decoration(TextDecoration.ITALIC, false)
}

private fun value(label: String, value: String): Component {
    return Component.text(label, NamedTextColor.GRAY)
        .decoration(TextDecoration.ITALIC, false)
        .append(Component.text(value, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
}

private fun hint(text: String): Component {
    return Component.text(text, NamedTextColor.GRAY)
        .decoration(TextDecoration.ITALIC, false)
}

private fun <T> EditorFieldContext.decodeOrNull(serializer: kotlinx.serialization.KSerializer<T>): T? {
    return runCatching {
        MessagesFormats.yaml.decodeFromYamlNode(serializer, value)
    }.getOrNull()
}


