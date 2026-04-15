package gg.aquatic.crates.data.price

import gg.aquatic.common.toMMComponent
import gg.aquatic.crates.open.OpenPriceGroup
import gg.aquatic.crates.data.editor.core.encodeToNode
import gg.aquatic.crates.data.price.editor.OpenPriceSelectionMenu
import gg.aquatic.crates.data.price.editor.defineOpenPriceEditor
import gg.aquatic.waves.serialization.editor.meta.EntryFactory
import gg.aquatic.waves.serialization.editor.meta.TextFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.TextFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

@Serializable
data class OpenPriceGroupData(
    val failMessage: String = "<red>You do not have enough keys to open this crate.",
    val prices: List<@Polymorphic OpenPriceData> = listOf(CrateKeyOpenPriceData()),
) {
    fun normalized(currentCrateId: String?, existingCrateIds: Set<String>): OpenPriceGroupData {
        return copy(
            prices = prices.map { price ->
                when (price) {
                    is CrateKeyOpenPriceData -> price.normalized(currentCrateId, existingCrateIds)
                    else -> price
                }
            }
        )
    }

    fun toOpenPriceGroup(crateId: String, keyItem: ItemStack): OpenPriceGroup {
        return OpenPriceGroup(
            prices = prices.map { it.toHandle(crateId, keyItem) },
            onFail = { player ->
                player.sendMessage(failMessage.toMMComponent())
            }
        )
    }

    companion object {
        val defaultEntryFactory: EntryFactory = EntryFactory { _, _ ->
            OpenPriceFormats.yaml.encodeToNode(OpenPriceGroupData.serializer(), OpenPriceGroupData())
        }

        fun TypedNestedSchemaBuilder<OpenPriceGroupData>.defineEditor() {
            field(
                OpenPriceGroupData::failMessage,
                TextFieldAdapter,
                TextFieldConfig(prompt = "Enter fail message:", showFormattedPreview = true),
                displayName = "Fail Message",
                description = listOf("Message shown if this price group cannot be paid.")
            )

            list(
                OpenPriceGroupData::prices,
                displayName = "Prices",
                description = listOf("All prices that must be paid together for this group."),
                newValueFactory = OpenPriceSelectionMenu.entryFactory
            ) {
                defineOpenPriceEditor()
            }
        }
    }
}
