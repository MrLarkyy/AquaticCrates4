package gg.aquatic.crates.data.processor

import gg.aquatic.crates.data.action.RewardActionData
import gg.aquatic.crates.data.action.editor.RewardActionSelectionMenu
import gg.aquatic.crates.data.action.editor.defineRewardActionEditor
import gg.aquatic.crates.data.item.StackedItemData
import gg.aquatic.crates.data.item.StackedItemDataEditor
import gg.aquatic.crates.data.editor.core.mapValue
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.range.RewardAmountRangeData
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import kotlinx.serialization.Polymorphic
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("choose")
data class ChooseRewardProcessorData(
    val chooseCountRanges: List<RewardAmountRangeData> = listOf(RewardAmountRangeData(min = 1, max = 1)),
    val uniqueRewards: Boolean = true,
    val hiddenRewards: Boolean = false,
    val onSelectActions: List<@Polymorphic RewardActionData> = emptyList(),
    val hiddenItem: StackedItemData = StackedItemData(
        material = Material.GRAY_STAINED_GLASS_PANE.name,
        displayName = "<gray>Hidden Reward"
    ),
    val menu: RewardDisplayMenuData = RewardDisplayMenuData(title = "<yellow>Choose Rewards"),
) : RewardProcessorData() {
    fun normalized(): ChooseRewardProcessorData {
        return copy(
            chooseCountRanges = chooseCountRanges.map { it.normalized() }
        )
    }

    companion object {
        fun TypedNestedSchemaBuilder<ChooseRewardProcessorData>.defineEditor() {
            list(
                ChooseRewardProcessorData::chooseCountRanges,
                displayName = "Choose Count Ranges",
                searchTags = listOf("choose count", "choice count", "amount of choices", "pick amount", "selection count"),
                iconMaterial = Material.HOPPER,
                description = listOf(
                    "Controls how many offered rewards the player may choose from the menu.",
                    "If empty, the player may choose one reward."
                )
            ) {
                with(RewardAmountRangeData) {
                    defineEditor(
                        minLabel = "Min Choices",
                        maxLabel = "Max Choices",
                        chanceLabel = "Range Weight"
                    )
                }
            }
            field(
                ChooseRewardProcessorData::uniqueRewards,
                displayName = "Unique Rewards",
                searchTags = listOf("unique", "no duplicates", "distinct rewards", "offered rewards"),
                prompt = "Enter true or false:",
                iconMaterial = Material.TARGET,
                description = listOf("If enabled, the offered reward list will not contain duplicate rewards.")
            )
            field(
                ChooseRewardProcessorData::hiddenRewards,
                displayName = "Hidden Rewards",
                searchTags = listOf("hidden", "blind pick", "mystery rewards", "concealed rewards"),
                prompt = "Enter true or false:",
                iconMaterial = Material.ENDER_EYE,
                description = listOf(
                    "If enabled, the offered rewards stay hidden while the player is choosing.",
                    "Clicking a hidden reward selects it blindly without revealing it first.",
                    "After the final choice, all offered rewards are revealed."
                )
            )
            list(
                ChooseRewardProcessorData::onSelectActions,
                displayName = "On Select Actions",
                searchTags = listOf("on select", "select actions", "choose actions", "on pick", "selection actions"),
                iconMaterial = Material.BLAZE_POWDER,
                description = listOf(
                    "Actions executed each time the player selects one offered reward.",
                    "These actions use placeholders from the selected reward and its rolled amount."
                ),
                newValueFactory = RewardActionSelectionMenu.entryFactory
            ) {
                defineRewardActionEditor()
            }
            include<ChooseRewardProcessorData>(visibleWhen = { it.currentBoolean("hiddenRewards") == true }) {
                group(ChooseRewardProcessorData::hiddenItem) {
                    with(StackedItemDataEditor) {
                        defineBasicEditor(
                            materialLabel = "Hidden Material",
                            nameLabel = "Hidden Name",
                            namePrompt = "Enter hidden item display name:",
                            loreLabel = "Hidden Lore",
                            amountLabel = "Hidden Amount"
                        )
                    }
                }
            }
            group(ChooseRewardProcessorData::menu) {
                with(RewardDisplayMenuData) {
                    defineEditor()
                }
            }
        }
    }
}

private fun EditorFieldContext.currentBoolean(key: String): Boolean? {
    val current = value.mapValue(key)?.stringContentOrNull
    if (current != null) {
        return current.toBooleanStrictOrNull()
    }

    val rootValue = root
        .mapValue("rewardProcessor")
        ?.mapValue(key)
        ?.stringContentOrNull

    return rootValue?.toBooleanStrictOrNull()
}

