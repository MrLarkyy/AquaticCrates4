package gg.aquatic.crates.data.price

import gg.aquatic.crates.crate.CrateHandler
import gg.aquatic.crates.data.price.editor.CrateReferenceFieldAdapter
import gg.aquatic.crates.open.OpenPriceHandle
import gg.aquatic.waves.serialization.editor.meta.IntFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.IntFieldConfig
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

@Serializable
@SerialName("crate-key")
data class CrateKeyOpenPriceData(
    val crateId: String? = null,
    val amount: Int = 1,
) : OpenPriceData() {
    fun normalized(currentCrateId: String?, existingCrateIds: Set<String>): CrateKeyOpenPriceData {
        val resolvedCurrent = currentCrateId?.trim()?.takeIf { it.isNotEmpty() }
        val normalizedReference = crateId?.trim()?.takeIf { it.isNotEmpty() && it in existingCrateIds && it != resolvedCurrent }
        return if (normalizedReference == crateId) this else copy(crateId = normalizedReference)
    }

    override fun toHandle(crateId: String, keyItem: ItemStack): OpenPriceHandle {
        val targetCrateId = this.crateId?.trim()
            ?.takeIf { it.isNotEmpty() && gg.aquatic.crates.crate.CrateHandler.crates.containsKey(it) }
            ?: crateId
        return OpenPriceHandle(
            currencyResolver = {
                CrateHandler.crates[targetCrateId]?.keyCurrency
                    ?: error("Crate '$targetCrateId' is not loaded.")
            },
            price = BigDecimal.valueOf(amount.toLong())
        )
    }

    companion object {
        fun TypedNestedSchemaBuilder<CrateKeyOpenPriceData>.defineEditor() {
            field(
                CrateKeyOpenPriceData::crateId,
                displayName = "Source Crate",
                description = listOf(
                    "Which crate provides the key currency.",
                    "Leave empty to use the current crate key."
                ),
                adapter = CrateReferenceFieldAdapter
            )
            field(
                CrateKeyOpenPriceData::amount,
                IntFieldAdapter,
                IntFieldConfig(prompt = "Enter required key amount:", min = 1),
                displayName = "Amount",
                description = listOf("How many keys must be spent to open the crate.")
            )
        }
    }
}
