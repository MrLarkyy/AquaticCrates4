package gg.aquatic.crates.reward.runtime

import gg.aquatic.common.toMMComponent
import gg.aquatic.crates.data.RewardData
import gg.aquatic.crates.reward.Reward
import gg.aquatic.crates.reward.RewardPurchaseHandler
import gg.aquatic.crates.reward.showcase.RewardShowcaseRuntimeFactory
import gg.aquatic.kmenu.inventory.ButtonType

object RewardRuntimeFactory {
    fun create(
        data: RewardData,
        id: String,
        crateId: String,
        crateKeyItem: org.bukkit.inventory.ItemStack,
        rarity: gg.aquatic.crates.reward.RewardRarity,
    ): Reward {
        val previewItemStack = data.previewItem.asStacked().getItem()
        val fallbackItemStack = data.fallbackPreviewItem?.asStacked()?.getItem()
        val showcase = RewardShowcaseRuntimeFactory.create(data) { previewItemStack.clone() }
        val purchaseManager = data.cost.firstOrNull { it.prices.isNotEmpty() }
            ?.toOpenPriceGroup(crateId, crateKeyItem)
            ?.let { priceGroup ->
                RewardPurchaseHandler(
                    price = priceGroup,
                    failAction = { }
                )
            }

        return Reward(
            id = id,
            crateId = crateId,
            displayName = data.displayName?.toMMComponent(),
            previewItem = { previewItemStack.clone() },
            fallbackItem = fallbackItemStack?.let { built -> { built.clone() } },
            winActions = data.winActions.map { it.toActionHandle() },
            massWinActions = data.massWinActions.map { it.toActionHandle() },
            showcase = showcase,
            conditions = data.conditions.map { it.toConditionHandle() },
            purchaseManager = purchaseManager,
            amountRanges = data.amountRanges.map { it.toRange() },
            clickHandler = { reward, player, clickType: ButtonType ->
                when (clickType) {
                    ButtonType.LEFT -> if (reward.isPurchasable) reward.tryPurchase(player)
                    ButtonType.SHIFT_LEFT -> if (player.hasPermission("aquaticcrates.admin")) reward.win(player)
                    else -> {}
                }
            },
            rarity = rarity,
            limits = data.limits.map { it.toHandle() },
            chance = data.chance
        )
    }
}
