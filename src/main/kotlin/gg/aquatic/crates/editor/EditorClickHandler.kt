package gg.aquatic.crates.editor

import org.bukkit.entity.Player

fun interface EditorClickHandler<T> {
    fun handle(player: Player, current: T, update: (T?) -> Unit)
}
