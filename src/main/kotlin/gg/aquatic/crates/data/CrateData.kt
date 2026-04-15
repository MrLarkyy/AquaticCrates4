package gg.aquatic.crates.data

import gg.aquatic.crates.data.interaction.CrateClickMappingData
import gg.aquatic.crates.data.interaction.OpenCrateClickActionData
import gg.aquatic.crates.data.interaction.PreviewCrateClickActionData
import gg.aquatic.crates.data.interactable.BlockCrateInteractableData
import gg.aquatic.crates.data.interactable.CrateInteractableData
import gg.aquatic.crates.data.item.StackedItemData
import gg.aquatic.crates.data.price.OpenPriceGroupData
import gg.aquatic.crates.data.processor.BasicRewardProcessorData
import gg.aquatic.crates.data.processor.RewardProcessorData
import gg.aquatic.crates.data.provider.RewardProviderData
import gg.aquatic.crates.data.provider.SimpleRewardProviderData
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
data class CrateData(
    val displayName: String = "<yellow>Crate",
    val keyItem: StackedItemData = StackedItemData(
        material = org.bukkit.Material.TRIPWIRE_HOOK.name,
        displayName = "<yellow>Crate Key"
    ),
    val keyMustBeHeld: Boolean = false,
    val crateClickMapping: CrateClickMappingData = CrateClickMappingData(),
    val keyClickMapping: CrateClickMappingData = CrateClickMappingData(
        right = listOf(OpenCrateClickActionData()),
        shiftRight = listOf(OpenCrateClickActionData()),
        left = listOf(PreviewCrateClickActionData()),
        shiftLeft = emptyList()
    ),
    val interactables: List<@Polymorphic CrateInteractableData> = listOf(BlockCrateInteractableData()),
    val openConditions: List<@Polymorphic gg.aquatic.crates.data.condition.PlayerConditionData> = emptyList(),
    val disableOpenStats: Boolean = false,
    val limits: List<LimitData> = emptyList(),
    val milestones: List<MilestoneData> = emptyList(),
    val repeatableMilestones: List<MilestoneData> = emptyList(),
    val priceGroups: List<OpenPriceGroupData> = listOf(OpenPriceGroupData()),
    val rarities: Map<String, RewardRarityData> = mapOf(
        DEFAULT_RARITY_ID to RewardRarityData(displayName = "<gray>Default")
    ),
    val rewardProvider: @Polymorphic RewardProviderData = SimpleRewardProviderData(),
    val rewardProcessor: @Polymorphic RewardProcessorData = BasicRewardProcessorData(),
    val hologram: CrateHologramData? = null,
    val preview: PreviewMenuData? = PreviewMenuData(),
) {
    companion object {
        const val DEFAULT_RARITY_ID = "default"

        fun createDefault(displayName: String = "<yellow>Crate"): CrateData {
            return CrateData(
                displayName = displayName,
                interactables = listOf(BlockCrateInteractableData()),
                priceGroups = listOf(OpenPriceGroupData()),
                rarities = mapOf(DEFAULT_RARITY_ID to RewardRarityData(displayName = "<gray>Default"))
            )
        }
    }
}
