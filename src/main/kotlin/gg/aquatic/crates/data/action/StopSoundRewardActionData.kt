package gg.aquatic.crates.data.action

import gg.aquatic.common.argument.ObjectArguments
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineStopSoundEditor
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionHandles
import gg.aquatic.execute.ActionHandle
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player

@Serializable
@SerialName("stop-sound")
data class StopSoundRewardActionData(
    @SerialName("sound")
    val soundKey: String = "minecraft:entity.player.levelup"
) : RewardActionData() {
    override fun toActionHandle(): ActionHandle<Player> = PlayerExecuteActionHandles.rewardStopSound(soundKey)

    companion object {
        fun TypedNestedSchemaBuilder<StopSoundRewardActionData>.defineEditor() {
            defineStopSoundEditor(
                StopSoundRewardActionData::soundKey,
                "Enter sound key to stop:"
            )
        }
    }
}
