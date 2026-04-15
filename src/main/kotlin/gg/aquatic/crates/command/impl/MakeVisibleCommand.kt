package gg.aquatic.crates.command.impl

import gg.aquatic.common.coroutine.BukkitCtx
import gg.aquatic.crates.CratesPlugin
import gg.aquatic.crates.command.onlinePlayerArgumentIncludingSelf
import gg.aquatic.crates.command.onlinePlayerArgumentResult
import gg.aquatic.crates.command.requirePlayerSender
import gg.aquatic.kommand.CommandBuilder
import gg.aquatic.kommand.hasPermission
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

internal fun CommandBuilder<CommandSourceStack, CommandSender>.makeVisibleCommand() =
    "makevisible" {
        hasPermission("aqcrates.admin")

        onlinePlayerArgumentIncludingSelf("player") {
            suspendExecute<CommandSender> {
                val playerArgument = onlinePlayerArgumentResult("player")
                if (playerArgument.isInvalid) {
                    sender.sendMessage(error("Player '${playerArgument.rawName ?: "unknown"}' is not online."))
                    return@suspendExecute
                }

                val target = playerArgument.player ?: sender.requirePlayerSender() ?: return@suspendExecute

                withContext(BukkitCtx.ofEntity(target)) {
                    restoreVisibility(target)
                }

                sender.sendMessage(success("Visibility restored for ${target.name}."))
                if (sender != target) {
                    target.sendMessage(success("Your visibility was restored by an administrator."))
                }
            }
        }

        suspendExecute<CommandSender> {
            val target = sender.requirePlayerSender() ?: return@suspendExecute

            withContext(BukkitCtx.ofEntity(target)) {
                restoreVisibility(target)
            }

            sender.sendMessage(success("Visibility restored for ${target.name}."))
        }
    }

private fun restoreVisibility(player: Player) {
    player.isInvisible = false
    player.isInvulnerable = false

    if (player.gameMode == GameMode.SPECTATOR && !player.isFlying) {
        player.allowFlight = false
    }
}

private fun success(message: String): Component =
    Component.text(message, NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)

private fun error(message: String): Component =
    Component.text(message, NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
