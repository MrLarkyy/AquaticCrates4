package gg.aquatic.crates.data.provider

import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.range.RewardAmountRangeData
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("conditional-pools")
data class ConditionalPoolsRewardProviderData(
    val poolSelectionMode: String = PoolSelectionMode.FIRST_MATCH.id,
    val fallbackPoolId: String? = null,
    val rewardCountRanges: List<RewardAmountRangeData> = emptyList(),
    val pools: Map<String, RewardPoolData> = emptyMap(),
) : RewardProviderData() {
    fun normalized(
        availableRarityIds: Set<String>,
        fallbackRarityId: String,
        currentCrateId: String? = null,
        existingCrateIds: Set<String> = emptySet(),
    ): ConditionalPoolsRewardProviderData {
        val normalizedPools = pools
            .mapNotNull { (poolId, data) ->
                poolId.trim()
                    .takeIf { it.isNotEmpty() }
                    ?.let { it to data.normalized(availableRarityIds, fallbackRarityId, currentCrateId, existingCrateIds) }
            }
            .toMap()

        return copy(
            poolSelectionMode = PoolSelectionMode.of(poolSelectionMode).id,
            fallbackPoolId = fallbackPoolId?.trim()?.takeIf { it.isNotEmpty() && it in normalizedPools },
            rewardCountRanges = rewardCountRanges.map { it.normalized() },
            pools = normalizedPools
        )
    }

    fun rewardEntries(): List<Pair<String, gg.aquatic.crates.data.RewardData>> {
        return pools.values.flatMap { it.rewardEntries() }
    }

    companion object {
        fun TypedNestedSchemaBuilder<ConditionalPoolsRewardProviderData>.defineEditor() {
            field(
                ConditionalPoolsRewardProviderData::poolSelectionMode,
                adapter = PoolSelectionModeFieldAdapter,
                displayName = "Pool Selection Mode",
                iconMaterial = Material.COMPARATOR,
                description = listOf(
                    "First Match uses the first pool whose conditions pass.",
                    "Merge All combines rewards from all matching pools."
                )
            )
            field(
                ConditionalPoolsRewardProviderData::fallbackPoolId,
                TextFieldAdapter,
                TextFieldConfig(prompt = "Enter fallback pool id:"),
                displayName = "Fallback Pool",
                iconMaterial = Material.RECOVERY_COMPASS,
                description = listOf(
                    "Optional pool used when no conditional pool matches.",
                    "Leave it unset to have no fallback pool.",
                    "Press Q to clear it back to null."
                )
            )
            list(
                ConditionalPoolsRewardProviderData::rewardCountRanges,
                displayName = "Reward Count Ranges",
                iconMaterial = Material.HOPPER,
                description = listOf(
                    "Controls how many rewards can be rolled after the active pool set is resolved.",
                    "If empty, one reward is rolled by default."
                )
            ) {
                with(RewardAmountRangeData) {
                    defineEditor(
                        minLabel = "Min Rewards",
                        maxLabel = "Max Rewards",
                        chanceLabel = "Range Weight"
                    )
                }
            }
            map(
                ConditionalPoolsRewardProviderData::pools,
                displayName = "Reward Pools",
                iconMaterial = Material.BOOKSHELF,
                description = listOf(
                    "Conditional reward pools available for this crate.",
                    "Each pool has its own conditions and rewards."
                ),
                mapKeyPrompt = "Enter pool ID:",
                newMapEntryFactory = EditorEntryFactories.map(
                    keyPrompt = "Enter pool ID:",
                    keyValidator = { if (CrateDataValidators.crateIdRegex.matches(it)) null else "Use only letters, numbers, '_' or '-'." },
                    valueFactory = {
                        gg.aquatic.crates.data.CrateDataFormats.yaml.encodeToNode(
                            RewardPoolData.serializer(),
                            RewardPoolData(displayName = it)
                        )
                    }
                )
            ) {
                with(RewardPoolData) {
                    defineEditor()
                }
            }
        }
    }
}
