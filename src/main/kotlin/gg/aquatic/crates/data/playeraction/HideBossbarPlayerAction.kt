package gg.aquatic.crates.data.playeraction

import gg.aquatic.common.argument.ArgumentContext
import gg.aquatic.common.argument.ObjectArgument
import gg.aquatic.execute.Action
import org.bukkit.entity.Player

object HideBossbarPlayerAction : Action<Player> {
    override val binder: Class<out Player> = Player::class.java
    override val arguments: List<ObjectArgument<*>> = emptyList()

    override suspend fun execute(binder: Player, args: ArgumentContext<Player>) {
        PlayerBossbarManager.hide(binder, args.string("id") ?: return)
    }
}
