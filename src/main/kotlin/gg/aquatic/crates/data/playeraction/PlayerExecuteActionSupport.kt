package gg.aquatic.crates.data.playeraction

import gg.aquatic.common.argument.ObjectArguments
import gg.aquatic.common.toMMComponent
import gg.aquatic.crates.interact.CrateClickBinder
import gg.aquatic.crates.interact.PlayerProxyCrateClickActions
import gg.aquatic.execute.Action
import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.action.impl.CloseInventory
import gg.aquatic.execute.action.impl.CommandAction
import gg.aquatic.execute.action.impl.SoundAction
import gg.aquatic.execute.action.impl.SoundStopAction
import gg.aquatic.execute.action.impl.TitleAction
import gg.aquatic.klocale.impl.paper.PaperMessage
import gg.aquatic.waves.serialization.editor.meta.*
import gg.aquatic.waves.util.action.MessageAction
import org.bukkit.entity.Player
import kotlin.reflect.KProperty1

object PlayerExecuteActionHandles {
    private fun <T : Any> handle(
        action: Action<T>,
        arguments: Map<String, Any> = emptyMap(),
    ): ActionHandle<T> = ActionHandle(action, ObjectArguments(arguments))

    fun rewardMessage(lines: List<String>): ActionHandle<Player> =
        handle(MessageAction, mapOf("message" to PaperMessage.of(lines.map(String::toMMComponent))))

    fun rewardActionbar(message: String, persistent: Boolean, durationTicks: Int): ActionHandle<Player> =
        handle(
            ShowActionbarPlayerAction,
            mapOf(
                "message" to message,
                "persistent" to persistent,
                "duration-ticks" to durationTicks,
            )
        )

    fun rewardClearActionbar(): ActionHandle<Player> =
        handle(ClearActionbarPlayerAction)

    fun rewardCommand(commands: List<String>, playerExecutor: Boolean): ActionHandle<Player> =
        handle(CommandAction, mapOf("command" to commands, "player-executor" to playerExecutor))

    fun rewardSound(sound: String, volume: Double, pitch: Double): ActionHandle<Player> =
        handle(SoundAction, mapOf("sound" to sound, "volume" to volume.toFloat(), "pitch" to pitch.toFloat()))

    fun rewardStopSound(sound: String): ActionHandle<Player> =
        handle(SoundStopAction, mapOf("sound" to sound))

    fun rewardTitle(title: String, subtitle: String, fadeIn: Int, stay: Int, fadeOut: Int): ActionHandle<Player> =
        handle(
            TitleAction,
            mapOf("title" to title, "subtitle" to subtitle, "fade-in" to fadeIn, "stay" to stay, "fade-out" to fadeOut)
        )

    fun rewardShowBossbar(
        id: String,
        title: String,
        progress: Double,
        color: String,
        overlay: String,
    ): ActionHandle<Player> = handle(
        ShowBossbarPlayerAction,
        mapOf(
            "id" to id,
            "title" to title,
            "progress" to progress.toFloat(),
            "color" to color,
            "overlay" to overlay,
        )
    )

    fun rewardHideBossbar(id: String): ActionHandle<Player> =
        handle(HideBossbarPlayerAction, mapOf("id" to id))

    fun rewardCloseInventory(): ActionHandle<Player> =
        handle(CloseInventory)

    fun clickMessage(lines: List<String>): ActionHandle<CrateClickBinder> =
        handle(PlayerProxyCrateClickActions.message, mapOf("message" to PaperMessage.of(lines.map(String::toMMComponent))))

    fun clickActionbar(message: String): ActionHandle<CrateClickBinder> =
        handle(PlayerProxyCrateClickActions.actionbar, mapOf("message" to message))

    fun clickCommand(commands: List<String>, playerExecutor: Boolean): ActionHandle<CrateClickBinder> =
        handle(PlayerProxyCrateClickActions.command, mapOf("command" to commands, "player-executor" to playerExecutor))

    fun clickSound(sound: String, volume: Double, pitch: Double): ActionHandle<CrateClickBinder> =
        handle(
            PlayerProxyCrateClickActions.sound,
            mapOf("sound" to sound, "volume" to volume.toFloat(), "pitch" to pitch.toFloat())
        )

    fun clickStopSound(sound: String): ActionHandle<CrateClickBinder> =
        handle(PlayerProxyCrateClickActions.stopSound, mapOf("sound" to sound))

    fun clickTitle(title: String, subtitle: String, fadeIn: Int, stay: Int, fadeOut: Int): ActionHandle<CrateClickBinder> =
        handle(
            PlayerProxyCrateClickActions.title,
            mapOf("title" to title, "subtitle" to subtitle, "fade-in" to fadeIn, "stay" to stay, "fade-out" to fadeOut)
        )

    fun clickCloseInventory(): ActionHandle<CrateClickBinder> =
        handle(PlayerProxyCrateClickActions.closeInventory)
}

object PlayerExecuteActionEditors {
    fun TypedNestedSchemaBuilder<*>.defineMessageLinesEditor(
        property: KProperty1<*, List<String>>,
        description: String,
    ) {
        @Suppress("UNCHECKED_CAST")
        (this as TypedNestedSchemaBuilder<Any>).list<String>(
            property as KProperty1<Any, List<String>>,
            displayName = "Lines",
            description = listOf(description),
            newValueFactory = EditorEntryFactories.text("Enter message line:")
        )
    }

    fun <T> TypedNestedSchemaBuilder<T>.defineFormattedMessageEditor(
        property: KProperty1<T, String>,
        prompt: String,
        description: String,
    ) {
        field(
            property,
            TextFieldAdapter,
            TextFieldConfig(prompt = prompt, showFormattedPreview = true),
            displayName = "Message",
            description = listOf(description)
        )
    }

    fun <T> TypedNestedSchemaBuilder<T>.defineCommandEditor(
        commandsProperty: KProperty1<*, List<String>>,
        playerExecutorProperty: KProperty1<T, Boolean>,
        description: String,
    ) {
        @Suppress("UNCHECKED_CAST")
        list<String>(
            commandsProperty as KProperty1<T, List<String>>,
            displayName = "Commands",
            description = listOf(description),
            newValueFactory = EditorEntryFactories.text("Enter command:")
        )
        field(
            playerExecutorProperty,
            displayName = "Player Executor",
            description = listOf("If enabled, commands run as the player instead of console.")
        )
    }

    fun <T> TypedNestedSchemaBuilder<T>.defineSoundEditor(
        soundProperty: KProperty1<T, String>,
        volumeProperty: KProperty1<T, Double>,
        pitchProperty: KProperty1<T, Double>,
    ) {
        field(
            soundProperty,
            TextFieldAdapter,
            TextFieldConfig(prompt = "Enter sound key:"),
            displayName = "Sound Key",
            description = listOf("Namespaced sound key that should be played.")
        )
        field(
            volumeProperty,
            DoubleFieldAdapter,
            DoubleFieldConfig(prompt = "Enter sound volume:", min = 0.0),
            displayName = "Volume",
            description = listOf("Playback volume of the sound.")
        )
        field(
            pitchProperty,
            DoubleFieldAdapter,
            DoubleFieldConfig(prompt = "Enter sound pitch:", min = 0.0),
            displayName = "Pitch",
            description = listOf("Playback pitch of the sound.")
        )
    }

    fun <T> TypedNestedSchemaBuilder<T>.defineStopSoundEditor(
        property: KProperty1<T, String>,
        prompt: String = "Enter sound key:",
    ) {
        field(
            property,
            TextFieldAdapter,
            TextFieldConfig(prompt = prompt),
            displayName = "Sound Key",
            description = listOf("Namespaced sound key that should be stopped.")
        )
    }

    fun <T> TypedNestedSchemaBuilder<T>.defineTitleEditor(
        titleProperty: KProperty1<T, String>,
        subtitleProperty: KProperty1<T, String>,
        fadeInProperty: KProperty1<T, Int>,
        stayProperty: KProperty1<T, Int>,
        fadeOutProperty: KProperty1<T, Int>,
    ) {
        field(
            titleProperty,
            TextFieldAdapter,
            TextFieldConfig(prompt = "Enter title text:", showFormattedPreview = true),
            displayName = "Title",
            description = listOf("Main title text shown to the player.")
        )
        field(
            subtitleProperty,
            TextFieldAdapter,
            TextFieldConfig(prompt = "Enter subtitle text:", showFormattedPreview = true),
            displayName = "Subtitle",
            description = listOf("Secondary line shown below the title.")
        )
        field(
            fadeInProperty,
            IntFieldAdapter,
            IntFieldConfig(prompt = "Enter fade in ticks:", min = 0),
            displayName = "Fade In",
            description = listOf("Ticks used for the title fade-in animation.")
        )
        field(
            stayProperty,
            IntFieldAdapter,
            IntFieldConfig(prompt = "Enter stay ticks:", min = 0),
            displayName = "Stay",
            description = listOf("Ticks the title stays fully visible.")
        )
        field(
            fadeOutProperty,
            IntFieldAdapter,
            IntFieldConfig(prompt = "Enter fade out ticks:", min = 0),
            displayName = "Fade Out",
            description = listOf("Ticks used for the title fade-out animation.")
        )
    }

    fun <T> TypedNestedSchemaBuilder<T>.defineCloseInventoryToggle(
        property: KProperty1<T, Boolean>,
        description: String,
    ) {
        field(
            property,
            displayName = "Enabled",
            description = listOf(description)
        )
    }
}
