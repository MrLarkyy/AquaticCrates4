package gg.aquatic.crates.data.action

import gg.aquatic.crates.data.playeraction.PlayerExecuteActionHandles
import gg.aquatic.crates.data.playeraction.PlayerExecuteActionEditors.defineFormattedMessageEditor
import gg.aquatic.execute.ActionHandle
import gg.aquatic.waves.serialization.editor.meta.DoubleFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.DoubleFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player

@Serializable
@SerialName("show-bossbar")
data class ShowBossbarRewardActionData(
    val id: String = "world-choose-focus",
    val title: String = "<white>%focused-reward%</white>",
    val progress: Double = 1.0,
    val color: String = "WHITE",
    val overlay: String = "PROGRESS",
) : RewardActionData() {
    override fun toActionHandle(): ActionHandle<Player> = PlayerExecuteActionHandles.rewardShowBossbar(
        id = id,
        title = title,
        progress = progress,
        color = color,
        overlay = overlay,
    )

    companion object {
        fun TypedNestedSchemaBuilder<ShowBossbarRewardActionData>.defineEditor() {
            field(
                ShowBossbarRewardActionData::id,
                TextFieldAdapter,
                TextFieldConfig(prompt = "Enter bossbar id:"),
                displayName = "Id",
                description = listOf("Stable bossbar id used to update or hide the same bar later.")
            )
            defineFormattedMessageEditor(
                ShowBossbarRewardActionData::title,
                "Enter bossbar title:",
                "Bossbar title shown to the player."
            )
            field(
                ShowBossbarRewardActionData::progress,
                DoubleFieldAdapter,
                DoubleFieldConfig(prompt = "Enter bossbar progress:", min = 0.0, max = 1.0),
                displayName = "Progress",
                description = listOf("Bossbar progress from 0.0 to 1.0.")
            )
            field(
                ShowBossbarRewardActionData::color,
                TextFieldAdapter,
                TextFieldConfig(prompt = "Enter bossbar color:"),
                displayName = "Color",
                description = listOf("Adventure bossbar color, e.g. WHITE, GREEN, RED, YELLOW.")
            )
            field(
                ShowBossbarRewardActionData::overlay,
                TextFieldAdapter,
                TextFieldConfig(prompt = "Enter bossbar overlay:"),
                displayName = "Overlay",
                description = listOf("Adventure bossbar overlay, e.g. PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20.")
            )
        }
    }
}
