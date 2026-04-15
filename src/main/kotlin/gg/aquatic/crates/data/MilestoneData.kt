package gg.aquatic.crates.data

import gg.aquatic.crates.data.validation.CrateDataValidators
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.waves.serialization.editor.meta.EditorEntryFactories
import gg.aquatic.waves.serialization.editor.meta.IntFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.IntFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class MilestoneData(
    val milestone: Int = 10,
    val displayName: String? = null,
    val rewards: Map<String, RewardData> = emptyMap(),
) {
    fun normalized(
        availableRarityIds: Set<String>,
        fallbackRarityId: String,
        currentCrateId: String? = null,
        existingCrateIds: Set<String> = emptySet(),
    ): MilestoneData {
        return copy(
            milestone = milestone.coerceAtLeast(1),
            rewards = rewards.mapValues { (_, rewardData) ->
                rewardData.normalized(availableRarityIds, fallbackRarityId, currentCrateId, existingCrateIds)
            }
        )
    }

    companion object {
        fun TypedNestedSchemaBuilder<MilestoneData>.defineEditor() {
            field(
                MilestoneData::milestone,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter milestone open count:", min = 1),
                displayName = "Open Count",
                iconMaterial = Material.CLOCK,
                description = listOf("How many alltime opens are needed to trigger this milestone.")
            )
            field(
                MilestoneData::displayName,
                TextFieldAdapter,
                TextFieldConfig(prompt = "Enter milestone display name:", showFormattedPreview = true),
                displayName = "Display Name",
                iconMaterial = Material.NAME_TAG,
                description = listOf("Optional label used for this milestone in future UI and placeholders.")
            )
            map(
                MilestoneData::rewards,
                displayName = "Rewards",
                iconMaterial = Material.CHEST_MINECART,
                description = listOf("Rewards granted when this milestone is reached."),
                mapKeyPrompt = "Enter reward ID:",
                newMapEntryFactory = EditorEntryFactories.map(
                    keyPrompt = "Enter reward ID:",
                    keyValidator = { if (CrateDataValidators.crateIdRegex.matches(it)) null else "Use only letters, numbers, '_' or '-'." },
                    valueFactory = { rewardId ->
                        CrateDataFormats.yaml.encodeToNode(
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
                with(RewardDataEditorSchema) { defineEditor() }
            }
        }
    }
}
