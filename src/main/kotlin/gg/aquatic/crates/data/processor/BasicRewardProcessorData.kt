package gg.aquatic.crates.data.processor

import gg.aquatic.crates.data.processor.editor.RewardDisplayMenuSectionFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("basic")
data class BasicRewardProcessorData(
    val resultMenu: RewardDisplayMenuData? = null,
) : RewardProcessorData() {
    companion object {
        fun TypedNestedSchemaBuilder<BasicRewardProcessorData>.defineEditor() {
            field(
                BasicRewardProcessorData::resultMenu,
                adapter = RewardDisplayMenuSectionFieldAdapter,
                displayName = "Result Menu",
                searchTags = listOf("result menu", "reward menu", "showcase", "won rewards", "post open menu"),
                iconMaterial = Material.CHEST,
                description = listOf(
                    "Optional showcase menu opened after the rewards are already given.",
                    "Use it to show the player which rewards were won.",
                    "Left click to edit it. Right click to enable or disable it."
                )
            )
        }
    }
}
