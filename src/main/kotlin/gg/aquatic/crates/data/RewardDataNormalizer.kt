package gg.aquatic.crates.data

import gg.aquatic.crates.data.rewardshowcase.RewardShowcaseType

fun RewardData.normalized(
    availableRarities: Set<String>,
    fallbackRarityId: String,
    currentCrateId: String? = null,
    existingCrateIds: Set<String> = emptySet(),
): RewardData {
    val resolvedRarity = rarity.takeIf { it in availableRarities } ?: fallbackRarityId
    return copy(
        rarity = resolvedRarity,
        rewardShowcaseType = RewardShowcaseType.of(rewardShowcaseType).id,
        limits = limits.map { it.normalized() }.distinctBy { it.timeframe },
        cost = cost.map { it.normalized(currentCrateId, existingCrateIds) },
        amountRanges = amountRanges.map { it.normalized() }
    )
}
