package gg.aquatic.crates.data.interaction

import gg.aquatic.common.argument.ObjectArguments
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineCloseInventoryToggle
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineCommandEditor
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineFormattedMessageEditor
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineMessageLinesEditor
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineSoundEditor
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineStopSoundEditor
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineTitleEditor
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionHandles
import gg.aquatic.crates.interact.CrateClickBinder
import gg.aquatic.crates.interact.DestroyCrateClickAction
import gg.aquatic.crates.interact.OpenCrateClickAction
import gg.aquatic.crates.interact.PreviewCrateClickAction
import gg.aquatic.execute.ActionHandle
import gg.aquatic.waves.serialization.editor.meta.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class CrateClickActionData {
    abstract fun toActionHandle(): ActionHandle<CrateClickBinder>
}

@Serializable
@SerialName("preview")
class PreviewCrateClickActionData : CrateClickActionData() {
    override fun toActionHandle(): ActionHandle<CrateClickBinder> =
        ActionHandle(PreviewCrateClickAction, ObjectArguments(emptyMap()))
}

@Serializable
@SerialName("open")
class OpenCrateClickActionData : CrateClickActionData() {
    override fun toActionHandle(): ActionHandle<CrateClickBinder> =
        ActionHandle(OpenCrateClickAction, ObjectArguments(emptyMap()))
}

@Serializable
@SerialName("destroy")
class DestroyCrateClickActionData : CrateClickActionData() {
    override fun toActionHandle(): ActionHandle<CrateClickBinder> =
        ActionHandle(DestroyCrateClickAction, ObjectArguments(emptyMap()))
}

@Serializable
@SerialName("message")
data class MessageCrateClickActionData(
    val lines: List<String> = listOf("<green>Interacted with the crate!")
) : CrateClickActionData() {
    override fun toActionHandle(): ActionHandle<CrateClickBinder> = PlayerExecuteActionHandles.clickMessage(lines)

    companion object {
        fun TypedNestedSchemaBuilder<MessageCrateClickActionData>.defineEditor() {
            defineMessageLinesEditor(
                MessageCrateClickActionData::lines,
                "Chat lines sent to the player after this click."
            )
        }
    }
}

@Serializable
@SerialName("actionbar")
data class ActionbarCrateClickActionData(
    val message: String = "<green>Crate clicked!"
) : CrateClickActionData() {
    override fun toActionHandle(): ActionHandle<CrateClickBinder> = PlayerExecuteActionHandles.clickActionbar(message)

    companion object {
        fun TypedNestedSchemaBuilder<ActionbarCrateClickActionData>.defineEditor() {
            defineFormattedMessageEditor(
                ActionbarCrateClickActionData::message,
                "Enter actionbar message:",
                "Message shown above the hotbar after this click."
            )
        }
    }
}

@Serializable
@SerialName("command")
data class CommandCrateClickActionData(
    val commands: List<String> = emptyList(),
    val playerExecutor: Boolean = false,
) : CrateClickActionData() {
    override fun toActionHandle(): ActionHandle<CrateClickBinder> =
        PlayerExecuteActionHandles.clickCommand(commands, playerExecutor)

    companion object {
        fun TypedNestedSchemaBuilder<CommandCrateClickActionData>.defineEditor() {
            defineCommandEditor(
                CommandCrateClickActionData::commands,
                CommandCrateClickActionData::playerExecutor,
                "Commands executed after this click."
            )
        }
    }
}

@Serializable
@SerialName("sound")
data class SoundCrateClickActionData(
    @SerialName("sound")
    val soundKey: String = "minecraft:entity.player.levelup",
    val volume: Double = 1.0,
    val pitch: Double = 1.0,
) : CrateClickActionData() {
    override fun toActionHandle(): ActionHandle<CrateClickBinder> =
        PlayerExecuteActionHandles.clickSound(soundKey, volume, pitch)

    companion object {
        fun TypedNestedSchemaBuilder<SoundCrateClickActionData>.defineEditor() {
            defineSoundEditor(
                SoundCrateClickActionData::soundKey,
                SoundCrateClickActionData::volume,
                SoundCrateClickActionData::pitch,
            )
        }
    }
}

@Serializable
@SerialName("stop-sound")
data class StopSoundCrateClickActionData(
    @SerialName("sound")
    val soundKey: String = "minecraft:entity.player.levelup",
) : CrateClickActionData() {
    override fun toActionHandle(): ActionHandle<CrateClickBinder> = PlayerExecuteActionHandles.clickStopSound(soundKey)

    companion object {
        fun TypedNestedSchemaBuilder<StopSoundCrateClickActionData>.defineEditor() {
            defineStopSoundEditor(
                StopSoundCrateClickActionData::soundKey,
            )
        }
    }
}

@Serializable
@SerialName("title")
data class TitleCrateClickActionData(
    val title: String = "<green>Crate!",
    val subtitle: String = "",
    val fadeIn: Int = 0,
    val stay: Int = 60,
    val fadeOut: Int = 0,
) : CrateClickActionData() {
    override fun toActionHandle(): ActionHandle<CrateClickBinder> =
        PlayerExecuteActionHandles.clickTitle(title, subtitle, fadeIn, stay, fadeOut)

    companion object {
        fun TypedNestedSchemaBuilder<TitleCrateClickActionData>.defineEditor() {
            defineTitleEditor(
                TitleCrateClickActionData::title,
                TitleCrateClickActionData::subtitle,
                TitleCrateClickActionData::fadeIn,
                TitleCrateClickActionData::stay,
                TitleCrateClickActionData::fadeOut,
            )
        }
    }
}

@Serializable
@SerialName("close-inventory")
class CloseInventoryCrateClickActionData : CrateClickActionData() {
    override fun toActionHandle(): ActionHandle<CrateClickBinder> = PlayerExecuteActionHandles.clickCloseInventory()
}
