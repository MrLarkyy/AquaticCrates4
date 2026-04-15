package gg.aquatic.crates.message

import kotlinx.serialization.Serializable

@Serializable
data class MessagesFileData(
    val help: EditableMessageData = EditableMessageData.lines(
        "<#18788C><bold>|</bold> <#3EC4DE>AquaticCrates <white>Commands"
    ),
    val crateGiven: EditableMessageData = EditableMessageData.lines("<green>You have been given the crate!"),
    val crateOpenSelfRequiresPlayer: EditableMessageData = EditableMessageData.lines("<red>You must specify a player when running this command from console!"),
    val crateOpenedSelf: EditableMessageData = EditableMessageData.lines("<green>Opened <yellow>%amount%x</yellow> <gold>%crate_id%</gold> for yourself."),
    val crateOpenedTarget: EditableMessageData = EditableMessageData.lines("<green><yellow>%amount%x</yellow> <gold>%crate_id%</gold> has been opened for you."),
    val crateOpenedSender: EditableMessageData = EditableMessageData.lines("<green>Opened <yellow>%amount%x</yellow> <gold>%crate_id%</gold> for <yellow>%player%</yellow>."),
    val crateOpenKeyRequired: EditableMessageData = EditableMessageData.lines("<red><gold>%crate_id%</gold> requires the target player to hold its key. Use <yellow>-nokey</yellow> to bypass."),
    val crateOpenFailed: EditableMessageData = EditableMessageData.lines("<red>Failed to open <gold>%crate_id%</gold> <yellow>%amount%x</yellow>."),
    val playerOnlyCommand: EditableMessageData = EditableMessageData.lines("<red>This command can only be used by a player."),
    val playerNotFound: EditableMessageData = EditableMessageData.lines("<red>Player '<yellow>%player%</yellow>' was not found."),
    val keysSelfRequiresPlayer: EditableMessageData = EditableMessageData.lines("<red>You must be a player to give yourself keys!"),
    val keysGivenSelf: EditableMessageData = EditableMessageData.lines("<green>You have been given <yellow>%amount%x</yellow> <gold>%key_type%</gold> key!"),
    val keysGivenTarget: EditableMessageData = EditableMessageData.lines("<green>You have been given <yellow>%amount%x</yellow> <gold>%key_type%</gold> key!"),
    val keysGivenSender: EditableMessageData = EditableMessageData.lines("<green>You have given <yellow>%player%</yellow> <yellow>%amount%x</yellow> <gold>%key_type%</gold> key!"),
    val keysGivenAllSelf: EditableMessageData = EditableMessageData.lines("<green>You have been given <yellow>%amount%x</yellow> <gold>%crate_id%</gold> <gold>%key_type%</gold> key through giveall."),
    val keysGivenAllTarget: EditableMessageData = EditableMessageData.lines("<green>You have been given <yellow>%amount%x</yellow> <gold>%crate_id%</gold> <gold>%key_type%</gold> key."),
    val keysGivenAllSender: EditableMessageData = EditableMessageData.lines("<green>Given <yellow>%amount%x</yellow> <gold>%crate_id%</gold> <gold>%key_type%</gold> key to <yellow>%player_count%</yellow> players."),
    val keysOfflineRequiresVirtual: EditableMessageData = EditableMessageData.lines("<red>Offline giveall requires <yellow>-v</yellow> because physical keys cannot be given offline."),
    val keyBank: EditableMessageData = EditableMessageData.lines("<#18788C><bold>|</bold> <yellow>%crate_name%</yellow><gray> (<white>%crate_id%</white>)<gray>: <aqua>%amount%</aqua>"),
    val keyBankEmpty: EditableMessageData = EditableMessageData.lines("<red>%player% has no virtual keys."),
    val noPermission: EditableMessageData = EditableMessageData.lines("<red>You do not have permission to do that."),
    val pluginReloading: EditableMessageData = EditableMessageData.lines("<yellow>Reloading..."),
    val pluginReloaded: EditableMessageData = EditableMessageData.lines("<green>Reloaded!"),
    val statsInvalidated: EditableMessageData = EditableMessageData.lines("<green>Stats invalidation finished."),
    val cratePlaced: EditableMessageData = EditableMessageData.lines("<green>You have placed the crate!"),
    val crateDestroyed: EditableMessageData = EditableMessageData.lines("<red>Crate destroyed!"),
    val crateSaved: EditableMessageData = EditableMessageData.lines("<green>Saved crate '<yellow>%crate_id%</yellow>'."),
    val crateCreatePrompt: EditableMessageData = EditableMessageData.lines("<aqua>Enter new crate ID:"),
    val crateInvalidId: EditableMessageData = EditableMessageData.lines("<red>Invalid crate ID. Use only letters, numbers, '_' or '-'."),
    val crateNotFound: EditableMessageData = EditableMessageData.lines("<red>Crate '<yellow>%crate_id%</yellow>' was not found."),
    val crateAlreadyExists: EditableMessageData = EditableMessageData.lines("<red>Crate '<yellow>%crate_id%</yellow>' already exists."),
    val crateEditorOpenFailed: EditableMessageData = EditableMessageData.lines("<red>Failed to open crate editor: <yellow>%reason%</yellow>"),
)
