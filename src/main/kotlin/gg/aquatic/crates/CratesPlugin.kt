package gg.aquatic.crates

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import gg.aquatic.crates.input.InputHandler
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import net.kyori.adventure.text.minimessage.translation.Argument.argument
import org.bukkit.plugin.java.JavaPlugin

object CratesPlugin : JavaPlugin() {

    val commandDispatcher: CommandDispatcher<CommandSourceStack> by lazy { commandDispatcher() }

    override fun onLoad() {

    }

    override fun onEnable() {
        InputHandler.initialize()

        commandDispatcher.register(
            literal("example")
                .then(
                    argument("bar", integer())
                        .executes { c ->
                            val num = getInteger(c, "bar")
                            1
                        }
                )
        )
    }

    override fun onDisable() {
        InputHandler.disable()
    }


    @JvmName("_getCommandDispatcher")
    @Suppress("UNCHECKED_CAST")
    private fun commandDispatcher(): CommandDispatcher<CommandSourceStack> {
        val getServer = server.javaClass.getDeclaredMethod("getServer")
        getServer.isAccessible = true

        val server = getServer.invoke(server)
        val resourcesField = server.javaClass.getDeclaredField("resources")
        resourcesField.isAccessible = true

        val resources = resourcesField.get(server)
        val getManagers = resources.javaClass.getDeclaredMethod("managers")
        getManagers.isAccessible = true

        val managers = getManagers.invoke(resources)
        val commandsField = managers.javaClass.getDeclaredField("commands")
        commandsField.isAccessible = true

        val commands = commandsField.get(managers)
        val getDispatcher = commands.javaClass.getDeclaredMethod("getDispatcher")
        getDispatcher.isAccessible = true

        return getDispatcher.invoke(commands) as CommandDispatcher<CommandSourceStack>
    }
}