package gg.aquatic.crates.command.impl

import gg.aquatic.common.coroutine.BukkitCtx
import gg.aquatic.common.toMMComponent
import gg.aquatic.crates.Messages
import gg.aquatic.crates.command.crateArgument
import gg.aquatic.crates.command.onlinePlayerArgument
import gg.aquatic.crates.command.onlinePlayerArgumentIncludingSelf
import gg.aquatic.crates.command.onlinePlayerArgumentResult
import gg.aquatic.crates.crate.Crate
import gg.aquatic.crates.crate.CrateHandler
import gg.aquatic.crates.message.storage.MessageStorage
import gg.aquatic.crates.message.replacePlaceholder
import gg.aquatic.kommand.CommandBuilder
import gg.aquatic.kommand.hasPermission
import gg.aquatic.klocale.impl.paper.replacePlaceholders
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.math.BigInteger
import java.util.LinkedHashMap
import java.util.UUID

/**
 * aqcrates key give <crate-id> [player] [key]
 */
internal fun CommandBuilder<CommandSourceStack, CommandSender>.keyCommand() =
    "key" {
        hasPermission("aqcrates.admin")

        "give" {
            crateArgument("crate") {
                onlinePlayerArgumentIncludingSelf("player") {
                    bigIntegerArgument("amount", min = BigInteger.ONE) {
                        flagsArgument("options", listOf("-s", "-v"))
                    }
                }
                suspendExecute<CommandSender> {
                    val crate = get<Crate>("crate")
                    val playerArgument = onlinePlayerArgumentResult("player")
                    if (handleInvalidPlayerArgument(sender, playerArgument)) {
                        return@suspendExecute
                    }

                    val amount = getOrNull<BigInteger>("amount") ?: BigInteger.ONE
                    val silent = hasFlag("options", "-s")
                    val virtual = hasFlag("options", "-v")
                    val target = resolveKeyGiveTarget(sender, playerArgument, silent) ?: return@suspendExecute
                    executeKeyGive(sender, crate, target, amount, virtual, silent)
                }
            }
        }

        "giveall" {
            crateArgument("crate") {
                bigIntegerArgument("amount", min = BigInteger.ONE) {
                    flagsArgument("options", listOf("-s", "-v", "-off"))
                }

                suspendExecute<CommandSender> {
                    val crate = get<Crate>("crate")
                    val amount = getOrNull<BigInteger>("amount") ?: BigInteger.ONE
                    val silent = hasFlag("options", "-s")
                    val virtual = hasFlag("options", "-v")
                    val allowOffline = hasFlag("options", "-off")

                    if (allowOffline && !virtual) {
                        Messages.KEYS_OFFLINE_REQUIRES_VIRTUAL.message().send(sender)
                        return@suspendExecute
                    }

                    executeKeyGiveAll(
                        sender = sender,
                        crate = crate,
                        amount = amount,
                        virtual = virtual,
                        allowOffline = allowOffline,
                        silent = silent,
                    )
                }
            }
        }

        "bank" {
            onlinePlayerArgument("player") {}

            suspendExecute<CommandSender> {
                val playerArgument = onlinePlayerArgumentResult("player")
                if (playerArgument.isInvalid) {
                    Messages.PLAYER_NOT_FOUND.message()
                        .replacePlaceholder("%player%", playerArgument.rawName ?: "unknown")
                        .send(sender)
                    return@suspendExecute
                }

                val target = playerArgument.player ?: run {
                    if (sender !is Player) {
                        Messages.KEYS_SELF_REQUIRES_PLAYER.message().send(sender)
                        return@suspendExecute
                    }
                    sender as Player
                }

                if (target != sender && !sender.hasPermission("aqcrates.admin.keybank.others")) {
                    Messages.NO_PERMISSION.message()
                        .replacePlaceholder("%permission%", "aqcrates.admin.keybank.others")
                        .send(sender)
                    return@suspendExecute
                }

                sendKeyBank(sender, target)
            }
        }
    }

private suspend fun giveKeys(crate: Crate, player: Player, amount: BigInteger, virtual: Boolean) {
    if (virtual) {
        crate.keyCurrency.give(player, amount.toBigDecimal())
        return
    }

    givePhysicalKeys(crate.keyItem, player, amount)
}

private fun givePhysicalKeys(keyItem: ItemStack, player: Player, amount: BigInteger) {
    val maxStackSize = keyItem.maxStackSize.coerceAtLeast(1)
    var remaining = amount

    while (remaining > BigInteger.ZERO) {
        val stackAmount = remaining.min(maxStackSize.toBigInteger()).toInt()
        player.inventory.addItem(
            keyItem.clone().apply {
                this.amount = stackAmount
            }
        )
        remaining -= stackAmount.toBigInteger()
    }
}

private fun keyTypeName(virtual: Boolean): String = if (virtual) "virtual" else "physical"

private fun handleInvalidPlayerArgument(
    sender: CommandSender,
    playerArgument: gg.aquatic.crates.command.OnlinePlayerArgumentResult,
): Boolean {
    if (!playerArgument.isInvalid) {
        return false
    }
    Messages.PLAYER_NOT_FOUND.message()
        .replacePlaceholder("%player%", playerArgument.rawName ?: "unknown")
        .send(sender)
    return true
}

private fun resolveKeyGiveTarget(
    sender: CommandSender,
    playerArgument: gg.aquatic.crates.command.OnlinePlayerArgumentResult,
    silent: Boolean,
): Player? {
    playerArgument.player?.let { return it }
    if (sender !is Player) {
        if (!silent) {
            Messages.KEYS_SELF_REQUIRES_PLAYER.message().send(sender)
        }
        return null
    }
    return sender
}

private suspend fun executeKeyGive(
    sender: CommandSender,
    crate: Crate,
    player: Player,
    amount: BigInteger,
    virtual: Boolean,
    silent: Boolean,
) {
    withContext(BukkitCtx.ofEntity(player)) {
        giveKeys(crate, player, amount, virtual)
        if (!silent) {
            sendKeyGiveMessages(sender, player, amount, virtual)
        }
    }
}

private suspend fun executeKeyGiveAll(
    sender: CommandSender,
    crate: Crate,
    amount: BigInteger,
    virtual: Boolean,
    allowOffline: Boolean,
    silent: Boolean,
) {
    val targets = resolveKeyGiveAllTargets(allowOffline)
    for (target in targets) {
        if (target.onlinePlayer != null) {
            withContext(BukkitCtx.ofEntity(target.onlinePlayer)) {
                giveKeysToTarget(crate, target, amount, virtual)
            }
        } else {
            giveKeysToTarget(crate, target, amount, virtual)
        }

        if (!silent && target.onlinePlayer != null) {
            sendKeyGiveAllTargetMessage(sender, target.onlinePlayer, crate, amount, virtual)
        }
    }

    if (!silent) {
        sendKeyGiveAllSenderMessage(sender, crate, amount, virtual, targets.size)
    }
}

private fun sendKeyGiveMessages(
    sender: CommandSender,
    player: Player,
    amount: BigInteger,
    virtual: Boolean,
) {
    if (sender == player) {
        Messages.KEYS_GIVEN_SELF.message()
            .replacePlaceholder("%amount%", amount.toString())
            .replacePlaceholder("%key_type%", keyTypeName(virtual))
            .send(player)
        return
    }

    Messages.KEYS_GIVEN_TARGET.message()
        .replacePlaceholder("%amount%", amount.toString())
        .replacePlaceholder("%key_type%", keyTypeName(virtual))
        .send(player)
    Messages.KEYS_GIVEN_SENDER.message()
        .replacePlaceholder("%player%", player.name)
        .replacePlaceholder("%amount%", amount.toString())
        .replacePlaceholder("%key_type%", keyTypeName(virtual))
        .send(sender)
}

private fun sendKeyGiveAllTargetMessage(
    sender: CommandSender,
    target: Player,
    crate: Crate,
    amount: BigInteger,
    virtual: Boolean,
) {
    val amountString = amount.toString()
    val keyType = keyTypeName(virtual)

    if (sender == target) {
        Messages.KEYS_GIVEN_ALL_SELF.message()
            .replacePlaceholder("%amount%", amountString)
            .replacePlaceholder("%key_type%", keyType)
            .replacePlaceholder("%crate_id%", crate.id)
            .send(target)
        return
    }

    Messages.KEYS_GIVEN_ALL_TARGET.message()
        .replacePlaceholder("%amount%", amountString)
        .replacePlaceholder("%key_type%", keyType)
        .replacePlaceholder("%crate_id%", crate.id)
        .send(target)
}

private fun sendKeyGiveAllSenderMessage(
    sender: CommandSender,
    crate: Crate,
    amount: BigInteger,
    virtual: Boolean,
    playerCount: Int,
) {
    Messages.KEYS_GIVEN_ALL_SENDER.message()
        .replacePlaceholder("%crate_id%", crate.id)
        .replacePlaceholder("%amount%", amount.toString())
        .replacePlaceholder("%key_type%", keyTypeName(virtual))
        .replacePlaceholder("%player_count%", playerCount.toString())
        .send(sender)
}

private suspend fun sendKeyBank(sender: CommandSender, target: Player) {
    val data = MessageStorage.loadData()
    val entries = loadKeyBankEntries(target)

    if (entries.isEmpty()) {
        Messages.KEY_BANK_EMPTY.message()
            .replacePlaceholder("%player%", target.name)
            .send(sender)
        return
    }

    sendRenderedKeyBank(sender, target, data, entries)
}

private suspend fun loadKeyBankEntries(target: Player): List<KeyBankEntry> {
    val crates = CrateHandler.crates.values.sortedBy(Crate::id)
    val entries = ArrayList<KeyBankEntry>(crates.size)

    for (crate in crates) {
        val entry = createKeyBankEntry(crate, target) ?: continue
        entries += entry
    }

    return entries
}

private suspend fun giveKeysToTarget(
    crate: Crate,
    target: KeyGiveTarget,
    amount: BigInteger,
    virtual: Boolean,
) {
    val onlinePlayer = target.onlinePlayer
    if (onlinePlayer != null) {
        giveKeys(crate, onlinePlayer, amount, virtual)
        return
    }

    crate.keyVirtualCurrency.give(target.uuid, amount.toBigDecimal())
}

private suspend fun createKeyBankEntry(crate: Crate, target: Player): KeyBankEntry? {
    val balance = crate.keyVirtualCurrency.getBalance(target)
    if (balance <= BigDecimal.ZERO) {
        return null
    }

    return KeyBankEntry(
        crateId = crate.id,
        crateName = PlainTextComponentSerializer.plainText().serialize(crate.displayName),
        amount = balance.stripTrailingZeros().toPlainString()
    )
}

private fun sendRenderedKeyBank(
    sender: CommandSender,
    target: Player,
    data: gg.aquatic.crates.message.MessagesFileData,
    entries: List<KeyBankEntry>,
) {
    val renderedLines = renderKeyBankLines(target, data, entries)
    data.keyBank.toPaperMessage(
        renderedLines,
        paginationReplacements = mapOf("player" to target.name)
    ).send(sender)
}

private fun renderKeyBankLines(
    target: Player,
    data: gg.aquatic.crates.message.MessagesFileData,
    entries: List<KeyBankEntry>,
): List<net.kyori.adventure.text.Component> {
    val renderedLines = ArrayList<net.kyori.adventure.text.Component>(entries.size * data.keyBank.lines.size)
    for (entry in entries) {
        renderedLines += renderKeyBankEntryLines(target, data.keyBank, entry)
    }
    return renderedLines
}

private fun renderKeyBankEntryLines(
    target: Player,
    message: gg.aquatic.crates.message.EditableMessageData,
    entry: KeyBankEntry,
): List<net.kyori.adventure.text.Component> {
    val placeholders = keyBankPlaceholders(target, entry)
    return message.lines.map { line ->
        line.toMiniMessage().toMMComponent().replacePlaceholders(placeholders)
    }
}

private fun keyBankPlaceholders(
    target: Player,
    entry: KeyBankEntry,
): Map<String, String> {
    return mapOf(
        "player" to target.name,
        "crate_id" to entry.crateId,
        "crate_name" to entry.crateName,
        "amount" to entry.amount,
    )
}

private fun resolveKeyGiveAllTargets(allowOffline: Boolean): List<KeyGiveTarget> {
    val targets = LinkedHashMap<UUID, KeyGiveTarget>()
    Bukkit.getOnlinePlayers().forEach { player ->
        targets[player.uniqueId] = KeyGiveTarget(
            name = player.name,
            uuid = player.uniqueId,
            onlinePlayer = player,
        )
    }

    if (!allowOffline) {
        return targets.values.toList()
    }

    Bukkit.getOfflinePlayers().forEach { offlinePlayer ->
        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline) {
            return@forEach
        }
        val uuid = offlinePlayer.uniqueId
        if (targets.containsKey(uuid)) {
            return@forEach
        }
        targets[uuid] = KeyGiveTarget(
            name = offlinePlayer.name ?: uuid.toString(),
            uuid = uuid,
            onlinePlayer = offlinePlayer.player,
        )
    }

    return targets.values.toList()
}

private data class KeyBankEntry(
    val crateId: String,
    val crateName: String,
    val amount: String,
)

private data class KeyGiveTarget(
    val name: String,
    val uuid: UUID,
    val onlinePlayer: Player?,
)
