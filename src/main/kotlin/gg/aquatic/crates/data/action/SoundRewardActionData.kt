package gg.aquatic.crates.data.action

import gg.aquatic.common.argument.ObjectArguments
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineSoundEditor
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionHandles
import gg.aquatic.execute.ActionHandle
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player

@Serializable
@SerialName("sound")
data class SoundRewardActionData(
    @SerialName("sound")
    val soundKey: String = "minecraft:entity.player.levelup",
    val volume: Double = 1.0,
    val pitch: Double = 1.0,
) : RewardActionData() {
    override fun toActionHandle(): ActionHandle<Player> =
        PlayerExecuteActionHandles.rewardSound(soundKey, volume, pitch)

    companion object {
        fun TypedNestedSchemaBuilder<SoundRewardActionData>.defineEditor() {
            defineSoundEditor(
                SoundRewardActionData::soundKey,
                SoundRewardActionData::volume,
                SoundRewardActionData::pitch,
            )
        }
    }
}
