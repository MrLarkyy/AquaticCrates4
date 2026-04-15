package gg.aquatic.crates.data.action

import gg.aquatic.crates.data.playeraction.PlayerExecuteActionHandles
import gg.aquatic.execute.ActionHandle
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player

@Serializable
@SerialName("hide-bossbar")
data class HideBossbarRewardActionData(
    val id: String = "world-choose-focus",
) : RewardActionData() {
    override fun toActionHandle(): ActionHandle<Player> = PlayerExecuteActionHandles.rewardHideBossbar(id)

    companion object {
        fun TypedNestedSchemaBuilder<HideBossbarRewardActionData>.defineEditor() {
            field(
                HideBossbarRewardActionData::id,
                TextFieldAdapter,
                TextFieldConfig(prompt = "Enter bossbar id:"),
                displayName = "Id",
                description = listOf("Bossbar id that should be hidden.")
            )
        }
    }
}
