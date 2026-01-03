package gg.aquatic.crates.editor

import gg.aquatic.crates.ClickType
import org.bukkit.entity.Player

fun interface EditorClickHandler<T> {
    fun handle(player: Player, current: T, update: (T?) -> Unit, clickType: ClickType)
}
