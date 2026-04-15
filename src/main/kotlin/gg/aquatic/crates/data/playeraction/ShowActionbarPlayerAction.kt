package gg.aquatic.crates.data.playeraction

import gg.aquatic.common.argument.ArgumentContext
import gg.aquatic.common.argument.ObjectArgument
import gg.aquatic.common.argument.impl.PrimitiveObjectArgument
import gg.aquatic.execute.Action
import org.bukkit.entity.Player

object ShowActionbarPlayerAction : Action<Player> {
    override val binder: Class<out Player> = Player::class.java
    override val arguments: List<ObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("message", "", required = true),
        PrimitiveObjectArgument("persistent", false, required = false),
        PrimitiveObjectArgument("duration-ticks", 0, required = false),
    )

    override suspend fun execute(binder: Player, args: ArgumentContext<Player>) {
        PlayerActionbarManager.show(
            player = binder,
            message = args.string("message") ?: return,
            persistent = args.boolean("persistent") ?: false,
            durationTicks = args.int("duration-ticks") ?: 0,
        )
    }
}
