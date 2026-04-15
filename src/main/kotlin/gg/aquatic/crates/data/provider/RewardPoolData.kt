package gg.aquatic.crates.data.provider

import gg.aquatic.crates.data.CrateData
import gg.aquatic.crates.data.RewardData
import gg.aquatic.crates.data.RewardDataEditorSchema
import gg.aquatic.crates.data.normalized
import gg.aquatic.crates.data.condition.PlayerConditionData
import gg.aquatic.crates.data.condition.editor.PlayerConditionSelectionMenu
import gg.aquatic.crates.data.condition.editor.definePlayerConditionEditor
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.normalizeRewardChances
import gg.aquatic.crates.reward.provider.RewardPool
import gg.aquatic.crates.reward.runtime.RewardRuntimeFactory
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class RewardPoolData(
    val displayName: String? = null,
    val conditions: List<@Polymorphic PlayerConditionData> = emptyList(),
    val rewards: Map<String, RewardData> = emptyMap(),
) {
    fun normalized(
        availableRarityIds: Set<String>,
        fallbackRarityId: String,
        currentCrateId: String? = null,
        existingCrateIds: Set<String> = emptySet(),
    ): RewardPoolData {
        return copy(
            rewards = rewards.mapValues { (_, rewardData) ->
                rewardData.normalized(availableRarityIds, fallbackRarityId, currentCrateId, existingCrateIds)
            }
        )
    }

    fun toRewardPool(
        poolId: String,
        crateId: String,
        crateKeyItem: org.bukkit.inventory.ItemStack,
        rarities: Map<String, gg.aquatic.crates.data.RewardRarityData>,
    ): RewardPool {
        val resolvedRarities = rarities.mapValues { (rarityId, rarityData) ->
            rarityData.toRewardRarity(rarityId)
        }
        val fallbackRarity = resolvedRarities.values.first()
        val resolvedRewards = rewards.entries.map { (rewardId, rewardData) ->
            val rewardRarity = resolvedRarities[rewardData.rarity] ?: fallbackRarity
            RewardRuntimeFactory.create(rewardData, rewardId, crateId, crateKeyItem, rewardRarity)
        }.toMutableList().also { rewards ->
            normalizeRewardChances(rewards, resolvedRarities)
        }

        return RewardPool(
            id = poolId,
            rewards = resolvedRewards,
            conditions = conditions.map { it.toConditionHandle() }
        )
    }

    fun rewardEntries(): List<Pair<String, RewardData>> {
        return rewards.entries.map { it.toPair() }
    }

    companion object {
        fun TypedNestedSchemaBuilder<RewardPoolData>.defineEditor() {
            field(
                RewardPoolData::displayName,
                adapter = gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter,
                config = gg.aquatic.waves.serialization.editor.meta.TextFieldConfig(
                    prompt = "Enter pool display name:",
                    showFormattedPreview = true
                ),
                displayName = "Pool Name",
                iconMaterial = Material.NAME_TAG,
                description = listOf("Optional display name used to identify this reward pool.")
            )
            list(
                RewardPoolData::conditions,
                displayName = "Pool Conditions",
                iconMaterial = Material.IRON_BARS,
                description = listOf(
                    "Conditions that decide whether this reward pool is active.",
                    "Example: week-of-year modulo, permission, or weekday checks."
                ),
                newValueFactory = PlayerConditionSelectionMenu.entryFactory
            ) {
                definePlayerConditionEditor()
            }
            map(
                RewardPoolData::rewards,
                displayName = "Pool Rewards",
                iconMaterial = Material.CHEST_MINECART,
                description = listOf("Rewards available inside this pool."),
                mapKeyPrompt = "Enter reward ID:",
                newMapEntryFactory = EditorEntryFactories.map(
                    keyPrompt = "Enter reward ID:",
                    keyValidator = { if (gg.aquatic.crates.data.validation.CrateDataValidators.crateIdRegex.matches(it)) null else "Use only letters, numbers, '_' or '-'." },
                    valueFactory = { rewardId ->
                        gg.aquatic.crates.data.CrateDataFormats.yaml.encodeToNode(
                            RewardData.serializer(),
                            RewardData(
                                displayName = rewardId,
                                previewItem = gg.aquatic.crates.data.item.StackedItemData(
                                    material = Material.CHEST.name,
                                    displayName = rewardId
                                ),
                                rarity = CrateData.DEFAULT_RARITY_ID
                            )
                        )
                    }
                )
            ) {
                with(RewardDataEditorSchema) {
                    defineEditor()
                }
            }
        }
    }
}

