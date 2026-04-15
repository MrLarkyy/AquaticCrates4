package gg.aquatic.crates.data.playeraction

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.util.UUID

object PlayerBossbarManager {
    private val barsByPlayer = mutableMapOf<UUID, MutableMap<String, BossBar>>()

    fun show(
        player: Player,
        id: String,
        title: String,
        progress: Float,
        color: BossBar.Color,
        overlay: BossBar.Overlay,
    ) {
        val playerBars = barsByPlayer.getOrPut(player.uniqueId) { mutableMapOf() }
        val bossBar = playerBars.getOrPut(id) {
            BossBar.bossBar(
                MiniMessage.miniMessage().deserialize(title),
                progress.coerceIn(0f, 1f),
                color,
                overlay,
            ).also(player::showBossBar)
        }

        bossBar.name(MiniMessage.miniMessage().deserialize(title))
        bossBar.progress(progress.coerceIn(0f, 1f))
        bossBar.color(color)
        bossBar.overlay(overlay)
    }

    fun hide(player: Player, id: String) {
        val playerBars = barsByPlayer[player.uniqueId] ?: return
        val bossBar = playerBars.remove(id) ?: return
        player.hideBossBar(bossBar)
        if (playerBars.isEmpty()) {
            barsByPlayer.remove(player.uniqueId)
        }
    }

    fun hideAll(player: Player) {
        val playerBars = barsByPlayer.remove(player.uniqueId) ?: return
        playerBars.values.forEach(player::hideBossBar)
    }
}
