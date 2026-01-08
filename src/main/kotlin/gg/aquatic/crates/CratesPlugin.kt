package gg.aquatic.crates

import gg.aquatic.crates.input.InputHandler
import org.bukkit.plugin.java.JavaPlugin

object CratesPlugin : JavaPlugin() {
    override fun onLoad() {

    }

    override fun onEnable() {
        InputHandler.initialize()
    }

    override fun onDisable() {
        InputHandler.disable()
    }
}