package gg.aquatic.crates.input

import gg.aquatic.crates.event
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent

object InputHandler {

    fun initialize() {
        event<PlayerQuitEvent> {
            forceCancel(it.player)
        }
    }

    fun disable() {
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            forceCancel(onlinePlayer)
        }
    }

    fun forceCancel(player: Player) {
        for (value in Input.REGISTRY.getAll().values) {
            value.forceCancel(player)
        }
    }
}