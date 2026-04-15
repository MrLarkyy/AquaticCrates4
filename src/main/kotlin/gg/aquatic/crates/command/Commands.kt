package gg.aquatic.crates.command

import gg.aquatic.crates.CratesPlugin
import gg.aquatic.crates.Messages
import gg.aquatic.crates.command.impl.crateCommand
import gg.aquatic.crates.command.impl.keyCommand
import gg.aquatic.crates.command.impl.makeVisibleCommand
import gg.aquatic.crates.command.impl.messagesCommand
import gg.aquatic.crates.command.impl.reloadCommand
import gg.aquatic.crates.command.impl.statsCommand
import gg.aquatic.kommand.command
import org.bukkit.command.CommandSender

internal fun CratesPlugin.initializeCommands() {
    command("aqcrates", "acrates", "crates") {
        "help" {
            intArgument("page") {
                suspendExecute<CommandSender> {
                    sendHelp(sender, get<Int>("page"))
                }
            }

            suspendExecute<CommandSender> {
                sendHelp(sender, 0)
            }
        }

        crateCommand()
        keyCommand()
        makeVisibleCommand()
        messagesCommand()
        statsCommand()
        reloadCommand()

        execute<CommandSender>(inheritToChildren = false) {
            sendHelp(sender, 0)
            true
        }
    }
}

private fun sendHelp(sender: CommandSender, page: Int) {
    Messages.HELP.message().send(listOf(sender), page.coerceAtLeast(0))
}
