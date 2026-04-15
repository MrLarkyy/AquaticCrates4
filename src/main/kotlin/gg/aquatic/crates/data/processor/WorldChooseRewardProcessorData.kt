package gg.aquatic.crates.data.processor

import gg.aquatic.crates.data.action.RewardActionData
import gg.aquatic.crates.data.action.editor.RewardActionSelectionMenu
import gg.aquatic.crates.data.action.editor.defineRewardActionEditor
import gg.aquatic.crates.data.item.StackedItemData
import gg.aquatic.crates.data.item.StackedItemDataEditor.defineBasicEditor
import gg.aquatic.crates.data.range.RewardAmountRangeData
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("world-choose")
data class WorldChooseRewardProcessorData(
    val chooseCountRanges: List<RewardAmountRangeData> = listOf(RewardAmountRangeData(min = 1, max = 1)),
    val uniqueRewards: Boolean = true,
    val hiddenRewards: Boolean = false,
    val onStartActions: List<@Polymorphic RewardActionData> = emptyList(),
    val onSwitchActions: List<@Polymorphic RewardActionData> = emptyList(),
    val onChooseActions: List<@Polymorphic RewardActionData> = emptyList(),
    val onEndActions: List<@Polymorphic RewardActionData> = emptyList(),
    val hiddenItem: StackedItemData = StackedItemData(
        material = Material.GRAY_STAINED_GLASS_PANE.name,
        displayName = "<gray>Hidden Reward"
    ),
    val display: WorldRewardDisplaySettingsData = WorldRewardDisplaySettingsData(),
) : RewardProcessorData() {
    fun normalized(): WorldChooseRewardProcessorData {
        return copy(
            chooseCountRanges = chooseCountRanges.map { it.normalized() },
            display = display.normalized(),
        )
    }

    companion object {
        fun TypedNestedSchemaBuilder<WorldChooseRewardProcessorData>.defineEditor() {
            list(
                WorldChooseRewardProcessorData::chooseCountRanges,
                displayName = "Choose Count Ranges",
                searchTags = listOf("choose count", "world choose", "selection count", "pick amount"),
                iconMaterial = Material.HOPPER,
                description = listOf(
                    "Controls how many rewards the player may choose from the world-space selection.",
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
                WorldChooseRewardProcessorData::uniqueRewards,
                displayName = "Unique Rewards",
                searchTags = listOf("unique", "no duplicates", "distinct rewards", "world offers"),
                prompt = "Enter true or false:",
                iconMaterial = Material.TARGET,
                description = listOf("If enabled, the offered reward list will not contain duplicate rewards.")
            )
            field(
                WorldChooseRewardProcessorData::hiddenRewards,
                displayName = "Hidden Rewards",
                searchTags = listOf("hidden", "blind pick", "mystery rewards", "concealed rewards"),
                prompt = "Enter true or false:",
                iconMaterial = Material.ENDER_EYE,
                description = listOf("If enabled, the world-space choices render the configured hidden item instead of the real preview.")
            )
            list(
                WorldChooseRewardProcessorData::onStartActions,
                displayName = "On Start Actions",
                searchTags = listOf("start", "start actions", "world choose start"),
                iconMaterial = Material.LIME_CANDLE,
                description = listOf("Actions executed once when the world choose selection starts."),
                newValueFactory = RewardActionSelectionMenu.entryFactory
            ) {
                defineRewardActionEditor()
            }
            list(
                WorldChooseRewardProcessorData::onSwitchActions,
                displayName = "On Switch Actions",
                searchTags = listOf("switch", "focus", "switch actions", "world choose focus"),
                iconMaterial = Material.BEACON,
                description = listOf("Actions executed whenever the focused world reward changes."),
                newValueFactory = RewardActionSelectionMenu.entryFactory
            ) {
                defineRewardActionEditor()
            }
            list(
                WorldChooseRewardProcessorData::onChooseActions,
                displayName = "On Choose Actions",
                searchTags = listOf("choose", "confirm", "choose actions", "world choose actions"),
                iconMaterial = Material.BLAZE_POWDER,
                description = listOf("Actions executed when the player confirms a focused reward."),
                newValueFactory = RewardActionSelectionMenu.entryFactory
            ) {
                defineRewardActionEditor()
            }
            list(
                WorldChooseRewardProcessorData::onEndActions,
                displayName = "On End Actions",
                searchTags = listOf("end", "cleanup", "world choose end"),
                iconMaterial = Material.BARRIER,
                description = listOf("Actions executed when the world choose selection finishes or is cancelled."),
                newValueFactory = RewardActionSelectionMenu.entryFactory
            ) {
                defineRewardActionEditor()
            }
            group(WorldChooseRewardProcessorData::hiddenItem) {
                with(StackedItemData) {
                    defineBasicEditor(
                        materialLabel = "Hidden Material",
                        nameLabel = "Hidden Name",
                        namePrompt = "Enter hidden item display name:",
                        loreLabel = "Hidden Lore",
                        amountLabel = "Hidden Amount"
                    )
                }
            }
            group(WorldChooseRewardProcessorData::display) {
                with(WorldRewardDisplaySettingsData) {
                    defineEditor()
                }
            }
        }
    }
}

