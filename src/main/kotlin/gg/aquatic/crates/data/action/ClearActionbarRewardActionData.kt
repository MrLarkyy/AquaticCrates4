package gg.aquatic.crates.data.action

import gg.aquatic.crates.data.playeraction.PlayerExecuteActionHandles
import gg.aquatic.execute.ActionHandle
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player

@Serializable
@SerialName("clear-actionbar")
class ClearActionbarRewardActionData : RewardActionData() {
    override fun toActionHandle(): ActionHandle<Player> = PlayerExecuteActionHandles.rewardClearActionbar()

    companion object {
        fun TypedNestedSchemaBuilder<ClearActionbarRewardActionData>.defineEditor() {
        }
    }
}
