package gg.aquatic.crates

import gg.aquatic.crates.input.InputHandler
import gg.aquatic.kommand.command
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object CratesPlugin : JavaPlugin() {
    override fun onLoad() {

    }

    override fun onEnable() {
        InputHandler.initialize()

        command("test") {
            "test" {
                execute<Player> {
                    sender.sendMessage("Test command executed!")
                    true
                }
            }
        }
    }

    override fun onDisable() {
        InputHandler.disable()
    }
}