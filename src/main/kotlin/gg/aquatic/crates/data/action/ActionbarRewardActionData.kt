package gg.aquatic.crates.data.action

import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineFormattedMessageEditor
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionHandles
import gg.aquatic.execute.ActionHandle
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player

@Serializable
@SerialName("actionbar")
data class ActionbarRewardActionData(
    val message: String = "<green>A Message!",
    val persistent: Boolean = false,
    val durationTicks: Int = 0,
) : RewardActionData() {
    override fun toActionHandle(): ActionHandle<Player> =
        PlayerExecuteActionHandles.rewardActionbar(message, persistent, durationTicks)

    companion object {
        fun TypedNestedSchemaBuilder<ActionbarRewardActionData>.defineEditor() {
            defineFormattedMessageEditor(
                ActionbarRewardActionData::message,
                "Enter actionbar message:",
                "Message shown above the hotbar when the reward is won."
            )
            field(
                ActionbarRewardActionData::persistent,
                displayName = "Persistent",
                description = listOf("If enabled, the actionbar is refreshed until cleared explicitly.")
            )
            field(
                ActionbarRewardActionData::durationTicks,
                displayName = "Duration Ticks",
                prompt = "Enter actionbar duration in ticks:",
                description = listOf(
                    "How long the actionbar should stay alive when Persistent is disabled.",
                    "Use 0 for a normal one-shot actionbar."
                )
            )
        }
    }
}
