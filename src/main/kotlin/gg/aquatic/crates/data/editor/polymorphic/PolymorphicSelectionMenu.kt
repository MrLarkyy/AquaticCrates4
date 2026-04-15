package gg.aquatic.crates.data.editor.polymorphic

import com.charleskorn.kaml.YamlNode
import gg.aquatic.kmenu.KMenu
import gg.aquatic.kmenu.inventory.InventoryType
import gg.aquatic.kmenu.menu.PrivateMenu
import gg.aquatic.kmenu.menu.createMenu
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.SerializableEditor
import gg.aquatic.waves.serialization.editor.meta.EntryFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.coroutines.resume

object PolymorphicSelectionMenu {

    data class Availability(
        val available: Boolean,
        val lockedDescription: List<String> = emptyList(),
        val deniedMessage: String? = null,
    )

    data class Definition(
        val id: String,
        val displayName: String,
        val description: List<String>,
        val icon: Material,
        val availability: (Player) -> Availability = { Availability(true) },
    )

    private data class ResolvedDefinition(
        val definition: Definition,
        val availability: Availability,
    )

    fun entryFactory(
        title: String,
        inventoryType: InventoryType = InventoryType.GENERIC9X3,
        entrySlots: List<Int>,
        cancelSlot: Int = 22,
        definitions: List<Definition>,
        elementFactory: (String) -> YamlNode?
    ): EntryFactory {
        return EntryFactory { player, _ ->
            val selected = selectType(
                player = player,
                title = title,
                inventoryType = inventoryType,
                entrySlots = entrySlots,
                cancelSlot = cancelSlot,
                definitions = definitions
            ) ?: return@EntryFactory null
            elementFactory(selected)
        }
    }

    suspend fun selectType(
        player: Player,
        title: String,
        inventoryType: InventoryType,
        entrySlots: List<Int>,
        cancelSlot: Int,
        definitions: List<Definition>,
    ): String? {
        val resolvedDefinitions = definitions.map { definition ->
            ResolvedDefinition(definition, definition.availability(player))
        }

        return suspendCancellableCoroutine { continuation ->
            var completed = false

            fun complete(result: String?) {
                if (completed || !continuation.isActive) {
                    return
                }
                completed = true
                continuation.resume(result)
            }

            fun openPage(page: Int) {
                CoroutineScope(continuation.context).launch {
                    SerializableEditor.suppressEditorClose(player)
                    val pageCount = resolvedDefinitions.chunked(entrySlots.size).size.coerceAtLeast(1)
                    val safePage = page.coerceIn(0, pageCount - 1)
                    val pageEntries = resolvedDefinitions.chunked(entrySlots.size).getOrElse(safePage) { emptyList() }

                    player.createMenu(Component.text(title), inventoryType) {
                        menuFactory = { customTitle, customType, customPlayer, cancelInteractions ->
                            object : PrivateMenu(customTitle, customType, customPlayer, cancelInteractions) {
                                override suspend fun onClosed(player: Player) {
                                    complete(null)
                                }
                            }
                        }

                        pageEntries.forEachIndexed { index, resolved ->
                            val slot = entrySlots.getOrNull(index) ?: return@forEachIndexed
                            button("selection_${resolved.definition.id}", slot) {
                                item = buildOptionItem(resolved)
                                onClick {
                                    handleSelectionClick(player, resolved, ::complete)
                                }
                            }
                        }

                        if (safePage > 0) {
                            button("previous_page", cancelSlot - 1) {
                                item = buildPageItem("Previous", Material.ARROW)
                                onClick { openPage(safePage - 1) }
                            }
                        }

                        if (safePage < pageCount - 1) {
                            button("next_page", cancelSlot + 1) {
                                item = buildPageItem("Next", Material.ARROW)
                                onClick { openPage(safePage + 1) }
                            }
                        }

                        button("cancel", cancelSlot) {
                            item = buildCancelItem()
                            onClick {
                                complete(null)
                            }
                        }
                    }.open(player)
                }
            }

            openPage(0)
        }
    }

    private fun buildOptionItem(resolved: ResolvedDefinition) = stackedItem(resolved.definition.icon) {
        displayName = text(
            resolved.definition.displayName,
            if (resolved.availability.available) NamedTextColor.AQUA else NamedTextColor.DARK_GRAY
        )

        resolved.definition.description.forEach { line ->
            lore += text(
                line,
                if (resolved.availability.available) NamedTextColor.GRAY else NamedTextColor.DARK_GRAY
            )
        }

        lore += text(" ", NamedTextColor.DARK_GRAY)

        if (resolved.availability.available) {
            lore += text("Click to add this option", NamedTextColor.GREEN)
        } else {
            lore += text("Locked", NamedTextColor.RED)
            resolved.availability.lockedDescription.forEach { line ->
                lore += text(line, NamedTextColor.GRAY)
            }
        }
    }.getItem()

    private fun buildCancelItem() = stackedItem(Material.BARRIER) {
        displayName = text("Cancel", NamedTextColor.RED)
        lore += text("Close selection", NamedTextColor.GRAY)
    }.getItem()

    private fun buildPageItem(label: String, material: Material) = stackedItem(material) {
        displayName = text(label, NamedTextColor.YELLOW)
        lore += text("Open another page", NamedTextColor.GRAY)
    }.getItem()

    private fun handleSelectionClick(
        player: Player,
        resolved: ResolvedDefinition,
        complete: (String?) -> Unit,
    ) {
        if (!resolved.availability.available) {
            resolved.availability.deniedMessage?.let(player::sendMessage)
            return
        }

        complete(resolved.definition.id)
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }
}
