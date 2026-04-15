package gg.aquatic.crates.data

import gg.aquatic.crates.data.action.RewardActionData
import gg.aquatic.crates.data.condition.PlayerConditionData
import gg.aquatic.crates.data.item.StackedItemData
import gg.aquatic.crates.data.price.OpenPriceGroupData
import gg.aquatic.crates.data.range.RewardAmountRangeData
import gg.aquatic.crates.data.rewardshowcase.BetterModelRewardShowcaseData
import gg.aquatic.crates.data.rewardshowcase.ItemDisplayRewardShowcaseData
import gg.aquatic.crates.data.rewardshowcase.ModelEngineRewardShowcaseData
import gg.aquatic.crates.data.rewardshowcase.RewardShowcaseType
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class RewardData(
    val displayName: String? = null,
    val previewItem: StackedItemData = StackedItemData(material = Material.CHEST.name),
    val fallbackPreviewItem: StackedItemData? = null,
    val conditions: List<@Polymorphic PlayerConditionData> = emptyList(),
    val limits: List<LimitData> = emptyList(),
    val rarity: String = CrateData.DEFAULT_RARITY_ID,
    val chance: Double = 1.0,
    val cost: List<OpenPriceGroupData> = emptyList(),
    val amountRanges: List<RewardAmountRangeData> = emptyList(),
    val winActions: List<@Polymorphic RewardActionData> = emptyList(),
    val massWinActions: List<@Polymorphic RewardActionData> = emptyList(),
    val rewardShowcaseType: String = RewardShowcaseType.ITEM_DISPLAY.id,
    val itemDisplayShowcase: ItemDisplayRewardShowcaseData = ItemDisplayRewardShowcaseData(),
    val modelEngineShowcase: ModelEngineRewardShowcaseData = ModelEngineRewardShowcaseData(),
    val betterModelShowcase: BetterModelRewardShowcaseData = BetterModelRewardShowcaseData(),
)
