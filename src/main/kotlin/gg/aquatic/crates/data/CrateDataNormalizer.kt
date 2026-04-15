package gg.aquatic.crates.data

import gg.aquatic.crates.data.processor.RewardProcessorType
import gg.aquatic.crates.data.provider.RewardProviderType

fun CrateData.normalized(crateId: String? = null, existingCrateIds: Set<String> = emptySet()): CrateData {
    val normalizedRarities = rarities
        .mapNotNull { (rarityId, data) ->
            rarityId.trim()
                .takeIf { it.isNotEmpty() }
                ?.let { it to data }
        }
        .toMap()
        .ifEmpty { mapOf(CrateData.DEFAULT_RARITY_ID to RewardRarityData(displayName = "<gray>Default")) }
    val fallbackRarityId = normalizedRarities.keys.first()
    val availableRarityIds = normalizedRarities.keys

    return copy(
        rarities = normalizedRarities,
        rewardProvider = when (val provider = rewardProvider) {
            is gg.aquatic.crates.data.provider.SimpleRewardProviderData ->
                provider.normalized(availableRarityIds, fallbackRarityId, crateId, existingCrateIds)
            is gg.aquatic.crates.data.provider.ConditionalPoolsRewardProviderData ->
                provider.normalized(availableRarityIds, fallbackRarityId, crateId, existingCrateIds)
        },
        rewardProcessor = when (val processor = rewardProcessor) {
            is gg.aquatic.crates.data.processor.BasicRewardProcessorData -> processor
            is gg.aquatic.crates.data.processor.ChooseRewardProcessorData -> processor.normalized()
            is gg.aquatic.crates.data.processor.WorldChooseRewardProcessorData -> processor.normalized()
        },
        limits = limits.map { it.normalized() }.distinctBy { it.timeframe },
        milestones = milestones
            .map { it.normalized(availableRarityIds, fallbackRarityId, crateId, existingCrateIds) }
            .sortedBy { it.milestone },
        repeatableMilestones = repeatableMilestones
            .map { it.normalized(availableRarityIds, fallbackRarityId, crateId, existingCrateIds) }
            .sortedBy { it.milestone },
        priceGroups = priceGroups.map { it.normalized(crateId, existingCrateIds) },
    )
}
