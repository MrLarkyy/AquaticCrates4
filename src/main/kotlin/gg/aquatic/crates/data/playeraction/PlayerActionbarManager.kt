package gg.aquatic.crates.data.playeraction

import gg.aquatic.common.ticker.GlobalTicker
import gg.aquatic.dispatch.ScheduledTask
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PlayerActionbarManager {
    private const val REFRESH_INTERVAL_MS = 750L
    private const val CLEAR_REPEATS = 3
    private const val CLEAR_INTERVAL_MS = 50L

    private val sessionsByPlayer = ConcurrentHashMap<UUID, ActionbarSession>()

    fun show(
        player: Player,
        message: String,
        persistent: Boolean,
        durationTicks: Int,
    ) {
        clear(player)

        val rendered = MiniMessage.miniMessage().deserialize(message)
        player.sendActionBar(rendered)

        if (!persistent && durationTicks <= 0) {
            return
        }

        val expiresAtMillis = if (persistent) {
            Long.MAX_VALUE
        } else {
            System.currentTimeMillis() + (durationTicks.coerceAtLeast(1) * 50L)
        }

        var scheduledTask: ScheduledTask? = null
        scheduledTask = GlobalTicker.runRepeatFixedDelay(
            intervalMs = REFRESH_INTERVAL_MS,
            initialDelayMs = REFRESH_INTERVAL_MS,
        ) {
            if (!player.isOnline || System.currentTimeMillis() >= expiresAtMillis) {
                scheduledTask?.cancel()
                clear(player)
                return@runRepeatFixedDelay
            }

            player.sendActionBar(rendered)
        }

        sessionsByPlayer[player.uniqueId] = ActionbarSession(
            component = rendered,
            task = scheduledTask,
        )
    }

    fun clear(player: Player) {
        val session = sessionsByPlayer.remove(player.uniqueId) ?: return
        session.task.cancel()
        hardClear(player)
    }

    private fun hardClear(player: Player) {
        player.sendActionBar(Component.empty())
        GlobalTicker.runRepeatFixedDelay(
            intervalMs = CLEAR_INTERVAL_MS,
            initialDelayMs = CLEAR_INTERVAL_MS,
            repeats = CLEAR_REPEATS,
        ) {
            if (player.isOnline) {
                player.sendActionBar(Component.empty())
            }
        }
    }

    private data class ActionbarSession(
        val component: Component,
        val task: ScheduledTask,
    )
}
