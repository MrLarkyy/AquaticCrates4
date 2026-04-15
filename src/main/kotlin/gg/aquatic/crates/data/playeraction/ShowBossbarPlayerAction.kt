package gg.aquatic.crates.data.playeraction

import gg.aquatic.common.argument.ArgumentContext
import gg.aquatic.common.argument.ObjectArgument
import gg.aquatic.execute.Action
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.entity.Player

object ShowBossbarPlayerAction : Action<Player> {
    override val binder: Class<out Player> = Player::class.java
    override val arguments: List<ObjectArgument<*>> = emptyList()

    override suspend fun execute(binder: Player, args: ArgumentContext<Player>) {
        val id = args.string("id") ?: return
        val title = args.string("title") ?: return
        val progress = args.float("progress") ?: return
        val color = BossBar.Color.valueOf((args.string("color") ?: "WHITE").uppercase())
        val overlay = BossBar.Overlay.valueOf((args.string("overlay") ?: "PROGRESS").uppercase())
        PlayerBossbarManager.show(binder, id, title, progress, color, overlay)
    }
}
