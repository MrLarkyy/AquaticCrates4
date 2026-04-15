package gg.aquatic.crates.data.editor

import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.editor.core.yamlList
import gg.aquatic.crates.data.editor.core.yamlScalar
import gg.aquatic.kmenu.KMenu
import gg.aquatic.kmenu.inventory.InventoryType
import gg.aquatic.kmenu.menu.createMenu
import gg.aquatic.stacked.stackedItem
import gg.aquatic.waves.serialization.editor.meta.EditorFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.FieldEditResult
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import gg.aquatic.common.coroutine.BukkitCtx

object ItemFlagFieldAdapter : EditorFieldAdapter {

    private val entrySlots = listOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
    )

    override fun createItem(context: EditorFieldContext, defaultItem: () -> ItemStack): ItemStack {
        val selected = currentFlags(context)
        return stackedItem(Material.BOOK) {
            displayName = text(context.label, NamedTextColor.AQUA)
            if (context.description.isNotEmpty()) {
                lore += text("Description", NamedTextColor.DARK_AQUA)
                lore += context.description.map { text(it, NamedTextColor.GRAY) }
            }
            lore += text("Selected: ${selected.size}", NamedTextColor.WHITE)
            if (selected.isNotEmpty()) {
                selected.take(5).forEach { flag ->
                    lore += text("- $flag", NamedTextColor.GRAY)
                }
                if (selected.size > 5) {
                    lore += text("...and ${selected.size - 5} more", NamedTextColor.DARK_GRAY)
                }
            }
            lore += text("Click to toggle flags", NamedTextColor.GREEN)
        }.getItem()
    }

    override suspend fun edit(player: Player, context: EditorFieldContext): FieldEditResult {
        val selected = selectFlags(player, currentFlags(context).toMutableSet()) ?: return FieldEditResult.NoChange
        return FieldEditResult.Updated(yamlList(selected.sorted().map(::yamlScalar)))
    }

    private suspend fun selectFlags(player: Player, selected: MutableSet<String>): Set<String>? {
        return openPage(player, selected, 0)
    }

    private suspend fun openPage(player: Player, selected: MutableSet<String>, page: Int): Set<String>? {
        val flags = ItemFlag.entries.map { it.name }
        val safePage = page.coerceAtLeast(0)
        val start = safePage * entrySlots.size
        val pageEntries = flags.drop(start).take(entrySlots.size)
        val hasPrevious = safePage > 0
        val hasNext = start + entrySlots.size < flags.size

        return withContext(BukkitCtx.ofEntity(player)) {
            suspendCancellableCoroutine { continuation ->
                KMenu.scope.launch {
                    player.createMenu(Component.text("Select Item Flags"), InventoryType.GENERIC9X5) {
                        pageEntries.forEachIndexed { index, flagName ->
                            val slot = entrySlots[index]
                            button("flag_$flagName", slot) {
                                item = buildFlagItem(flagName, flagName in selected)
                                onClick {
                                    if (!selected.add(flagName)) {
                                        selected.remove(flagName)
                                    }
                                    continuation.resumeOnce(SelectionResult.Refresh)
                                }
                            }
                        }

                        button("clear_all", 36) {
                            item = stackedItem(Material.LAVA_BUCKET) {
                                displayName = text("Clear All", NamedTextColor.RED)
                                lore += text("Remove all selected flags", NamedTextColor.GRAY)
                            }.getItem()
                            onClick {
                                selected.clear()
                                continuation.resumeOnce(SelectionResult.Refresh)
                            }
                        }

                        if (hasPrevious) {
                            button("previous_page", 38) {
                                item = navItem("Previous Page")
                                onClick { continuation.resumeOnce(SelectionResult.PreviousPage) }
                            }
                        }

                        button("done", 40) {
                            item = stackedItem(Material.LIME_DYE) {
                                displayName = text("Done", NamedTextColor.GREEN)
                                lore += text("Save selected item flags", NamedTextColor.GRAY)
                            }.getItem()
                            onClick {
                                continuation.resumeOnce(SelectionResult.Done)
                            }
                        }

                        button("cancel", 44) {
                            item = stackedItem(Material.BARRIER) {
                                displayName = text("Cancel", NamedTextColor.RED)
                                lore += text("Discard changes", NamedTextColor.GRAY)
                            }.getItem()
                            onClick {
                                continuation.resumeOnce(SelectionResult.Cancelled)
                            }
                        }

                        if (hasNext) {
                            button("next_page", 42) {
                                item = navItem("Next Page")
                                onClick { continuation.resumeOnce(SelectionResult.NextPage) }
                            }
                        }
                    }.open(player)
                }
            }
        }.let { result ->
            when (result) {
                SelectionResult.Done -> selected.toSet()
                SelectionResult.Cancelled -> null
                SelectionResult.PreviousPage -> openPage(player, selected, safePage - 1)
                SelectionResult.NextPage -> openPage(player, selected, safePage + 1)
                SelectionResult.Refresh -> openPage(player, selected, safePage)
            }
        }
    }

    private fun currentFlags(context: EditorFieldContext): List<String> {
        val array = context.value as? com.charleskorn.kaml.YamlList ?: return emptyList()
        return array.items.mapNotNull { element -> element.stringContentOrNull }.distinct()
    }

    private fun buildFlagItem(flagName: String, enabled: Boolean) = stackedItem(
        if (enabled) Material.LIME_DYE else Material.GRAY_DYE
    ) {
        displayName = text(flagName, if (enabled) NamedTextColor.GREEN else NamedTextColor.GRAY)
        lore += text(
            if (enabled) "Currently enabled" else "Currently disabled",
            if (enabled) NamedTextColor.GREEN else NamedTextColor.DARK_GRAY
        )
        lore += text("Click to toggle this flag", NamedTextColor.GRAY)
    }.getItem()

    private fun navItem(label: String) = stackedItem(Material.ARROW) {
        displayName = text(label, NamedTextColor.AQUA)
        lore += text("Open another page of item flags", NamedTextColor.GRAY)
    }.getItem()

    private fun <T> Continuation<T>.resumeOnce(value: T) {
        runCatching { resume(value) }
    }

    private fun text(content: String, color: NamedTextColor): Component {
        return Component.text(content, color)
            .decoration(TextDecoration.ITALIC, false)
    }

    private enum class SelectionResult {
        Done,
        Cancelled,
        PreviousPage,
        NextPage,
        Refresh
    }
}
