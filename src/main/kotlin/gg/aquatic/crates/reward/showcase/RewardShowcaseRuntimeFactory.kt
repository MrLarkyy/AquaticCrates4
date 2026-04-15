package gg.aquatic.crates.reward.showcase

import gg.aquatic.crates.data.RewardData
import gg.aquatic.crates.data.rewardshowcase.RewardShowcaseType

object RewardShowcaseRuntimeFactory {
    fun create(
        data: RewardData,
        previewItemSupplier: () -> org.bukkit.inventory.ItemStack,
    ): RewardShowcase {
        return when (RewardShowcaseType.of(data.rewardShowcaseType)) {
            RewardShowcaseType.ITEM_DISPLAY -> ItemDisplayRewardShowcase(
                data = data.itemDisplayShowcase,
                itemSupplier = previewItemSupplier
            )

            RewardShowcaseType.MODEL_ENGINE -> ModelEngineRewardShowcase(
                data = data.modelEngineShowcase,
                itemSupplier = previewItemSupplier
            )

            RewardShowcaseType.BETTER_MODEL -> BetterModelRewardShowcase(
                data = data.betterModelShowcase,
                itemSupplier = previewItemSupplier
            )
        }
    }
}
