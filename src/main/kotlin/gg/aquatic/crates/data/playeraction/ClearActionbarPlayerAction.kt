package gg.aquatic.crates.data.playeraction

import gg.aquatic.common.argument.ArgumentContext
import gg.aquatic.common.argument.ObjectArgument
import gg.aquatic.execute.Action
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object ClearActionbarPlayerAction : Action<Player> {
    override val binder: Class<out Player> = Player::class.java
    override val arguments: List<ObjectArgument<*>> = emptyList()

    override suspend fun execute(binder: Player, args: ArgumentContext<Player>) {
        PlayerActionbarManager.clear(binder)
        binder.sendActionBar(Component.empty())
    }
}
