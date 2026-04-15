package gg.aquatic.crates

import gg.aquatic.klocale.LocaleManager
import gg.aquatic.klocale.impl.paper.PaperMessage
import gg.aquatic.klocale.impl.paper.handler.CfgMessageHandler
import gg.aquatic.crates.message.storage.MessagesProvider
import gg.aquatic.waves.Waves
import org.bukkit.Material

enum class Messages(
    override val path: String,
    val displayName: String,
    val description: List<String>,
    val placeholders: List<String> = emptyList(),
    val icon: Material = Material.PAPER,
) : CfgMessageHandler<PaperMessage> {
    HELP(
        path = "help",
        displayName = "Help",
        description = listOf("Main help message shown for `/aqcrates` and `/aqcrates help`."),
        icon = Material.BOOK
    ),
    CRATE_GIVEN(
        path = "crate-given",
        displayName = "Crate Given",
        description = listOf("Sent after giving a crate item to a player."),
        icon = Material.CHEST_MINECART
    ),
    CRATE_OPEN_SELF_REQUIRES_PLAYER(
        path = "crate-open-self-requires-player",
        displayName = "Crate Open Self Requires Player",
        description = listOf("Shown when console tries to open a crate for itself."),
        icon = Material.BARRIER
    ),
    CRATE_OPENED_SELF(
        path = "crate-opened-self",
        displayName = "Crate Opened Self",
        description = listOf("Shown to the executor when they open a crate for themselves."),
        placeholders = listOf("%crate_id%", "%amount%"),
        icon = Material.CHEST
    ),
    CRATE_OPENED_TARGET(
        path = "crate-opened-target",
        displayName = "Crate Opened Target",
        description = listOf("Shown to the target player after a crate is opened for them."),
        placeholders = listOf("%crate_id%", "%amount%"),
        icon = Material.CHEST
    ),
    CRATE_OPENED_SENDER(
        path = "crate-opened-sender",
        displayName = "Crate Opened Sender",
        description = listOf("Shown to the sender after opening a crate for another player."),
        placeholders = listOf("%player%", "%crate_id%", "%amount%"),
        icon = Material.CHEST
    ),
    CRATE_OPEN_KEY_REQUIRED(
        path = "crate-open-key-required",
        displayName = "Crate Open Key Required",
        description = listOf("Shown when a crate requires the target player to hold a key unless -nokey is used."),
        placeholders = listOf("%crate_id%"),
        icon = Material.TRIPWIRE_HOOK
    ),
    CRATE_OPEN_FAILED(
        path = "crate-open-failed",
        displayName = "Crate Open Failed",
        description = listOf("Shown when a crate open command could not complete."),
        placeholders = listOf("%crate_id%", "%amount%"),
        icon = Material.BARRIER
    ),
    PLAYER_ONLY_COMMAND(
        path = "player-only-command",
        displayName = "Player Only Command",
        description = listOf("Shown when a command can only be used by a player."),
        icon = Material.BARRIER
    ),
    PLAYER_NOT_FOUND(
        path = "player-not-found",
        displayName = "Player Not Found",
        description = listOf("Shown when a command references a player name that is not online or not valid for the argument."),
        placeholders = listOf("%player%"),
        icon = Material.BARRIER
    ),
    KEYS_SELF_REQUIRES_PLAYER(
        path = "keys-self-requires-player",
        displayName = "Keys Self Requires Player",
        description = listOf("Shown when console tries to give keys to itself."),
        icon = Material.BARRIER
    ),
    KEYS_GIVEN_SELF(
        path = "keys-given-self",
        displayName = "Keys Given Self",
        description = listOf("Shown to the executor when they give keys to themselves."),
        placeholders = listOf("%amount%", "%key_type%"),
        icon = Material.TRIPWIRE_HOOK
    ),
    KEYS_GIVEN_TARGET(
        path = "keys-given-target",
        displayName = "Keys Given Target",
        description = listOf("Shown to the target player after receiving keys."),
        placeholders = listOf("%amount%", "%key_type%"),
        icon = Material.TRIPWIRE_HOOK
    ),
    KEYS_GIVEN_SENDER(
        path = "keys-given-sender",
        displayName = "Keys Given Sender",
        description = listOf("Shown to the command sender after giving keys to another player."),
        placeholders = listOf("%player%", "%amount%", "%key_type%"),
        icon = Material.TRIPWIRE_HOOK
    ),
    KEYS_GIVEN_ALL_SELF(
        path = "keys-given-all-self",
        displayName = "Keys Given All Self",
        description = listOf("Shown when a player uses giveall and also receives the crate key themselves."),
        placeholders = listOf("%crate_id%", "%amount%", "%key_type%"),
        icon = Material.TRIPWIRE_HOOK
    ),
    KEYS_GIVEN_ALL_TARGET(
        path = "keys-given-all-target",
        displayName = "Keys Given All Target",
        description = listOf("Shown to online players who receive keys from giveall."),
        placeholders = listOf("%crate_id%", "%amount%", "%key_type%"),
        icon = Material.TRIPWIRE_HOOK
    ),
    KEYS_GIVEN_ALL_SENDER(
        path = "keys-given-all-sender",
        displayName = "Keys Given All Sender",
        description = listOf("Shown to the sender after giving one crate key to all players."),
        placeholders = listOf("%crate_id%", "%amount%", "%key_type%", "%player_count%"),
        icon = Material.TRIPWIRE_HOOK
    ),
    KEYS_OFFLINE_REQUIRES_VIRTUAL(
        path = "keys-offline-requires-virtual",
        displayName = "Keys Offline Requires Virtual",
        description = listOf("Shown when -off is used without -v for giveall."),
        icon = Material.BARRIER
    ),
    KEY_BANK(
        path = "key-bank",
        displayName = "Key Bank",
        description = listOf("Paginated key bank entry template used by `/aqcrates key bank`."),
        placeholders = listOf("%player%", "%crate_id%", "%crate_name%", "%amount%"),
        icon = Material.ENDER_CHEST
    ),
    KEY_BANK_EMPTY(
        path = "key-bank-empty",
        displayName = "Key Bank Empty",
        description = listOf("Shown when the target player has no virtual keys."),
        placeholders = listOf("%player%"),
        icon = Material.BARRIER
    ),
    NO_PERMISSION(
        path = "no-permission",
        displayName = "No Permission",
        description = listOf("Shown when the sender does not have the required permission."),
        placeholders = listOf("%permission%"),
        icon = Material.BARRIER
    ),
    PLUGIN_RELOADING(
        path = "plugin-reloading",
        displayName = "Plugin Reloading",
        description = listOf("Shown when `/acrates reload` starts."),
        icon = Material.CLOCK
    ),
    PLUGIN_RELOADED(
        path = "plugin-reloaded",
        displayName = "Plugin Reloaded",
        description = listOf("Shown when `/acrates reload` finishes."),
        icon = Material.EMERALD
    ),
    STATS_INVALIDATED(
        path = "stats-invalidated",
        displayName = "Stats Invalidated",
        description = listOf("Shown after invalidating obsolete crate stats rows and expired hourly buckets."),
        placeholders = listOf(
            "%total_deleted%",
            "%deleted_openings%",
            "%deleted_opening_rewards%",
            "%deleted_hourly_crate_buckets%",
            "%deleted_hourly_reward_buckets%",
            "%deleted_alltime_crate_rows%",
            "%deleted_alltime_reward_rows%",
            "%deleted_expired_hourly_crate_buckets%",
            "%deleted_expired_hourly_reward_buckets%",
        ),
        icon = Material.COMMAND_BLOCK
    ),
    CRATE_PLACED(
        path = "crate-placed",
        displayName = "Crate Placed",
        description = listOf("Shown after successfully placing a crate."),
        icon = Material.CHEST
    ),
    CRATE_DESTROYED(
        path = "crate-destroyed",
        displayName = "Crate Destroyed",
        description = listOf("Shown after removing a placed crate."),
        icon = Material.TNT
    ),
    CRATE_SAVED(
        path = "crate-saved",
        displayName = "Crate Saved",
        description = listOf("Shown after saving a crate from the in-game editor."),
        placeholders = listOf("%crate_id%"),
        icon = Material.WRITABLE_BOOK
    ),
    CRATE_CREATE_PROMPT(
        path = "crate-create-prompt",
        displayName = "Crate Create Prompt",
        description = listOf("Prompt shown before entering a new crate ID."),
        icon = Material.OAK_SIGN
    ),
    CRATE_INVALID_ID(
        path = "crate-invalid-id",
        displayName = "Crate Invalid ID",
        description = listOf("Shown when the entered crate ID format is invalid."),
        icon = Material.BARRIER
    ),
    CRATE_NOT_FOUND(
        path = "crate-not-found",
        displayName = "Crate Not Found",
        description = listOf("Shown when a command references a crate ID that does not exist."),
        placeholders = listOf("%crate_id%"),
        icon = Material.BARRIER
    ),
    CRATE_ALREADY_EXISTS(
        path = "crate-already-exists",
        displayName = "Crate Already Exists",
        description = listOf("Shown when creating a crate with an existing ID."),
        placeholders = listOf("%crate_id%"),
        icon = Material.REDSTONE_BLOCK
    ),
    CRATE_EDITOR_OPEN_FAILED(
        path = "crate-editor-open-failed",
        displayName = "Crate Editor Open Failed",
        description = listOf("Shown when the crate editor cannot be opened."),
        placeholders = listOf("%reason%"),
        icon = Material.BARRIER
    );

    override val manager: LocaleManager<PaperMessage> = Waves.locale

    companion object {
        private var injected = false

        suspend fun load() {
            if (!injected) {
                injected = true
                Waves.locale.injectProvider(MessagesProvider)
                return
            }

            Waves.locale.invalidate()
        }
    }
}
