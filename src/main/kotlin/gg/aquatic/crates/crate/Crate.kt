package gg.aquatic.crates.crate

import gg.aquatic.crates.Messages
import gg.aquatic.crates.crate.preview.PreviewMenuSettings
import gg.aquatic.crates.crate.opening.CrateOpeningService
import gg.aquatic.crates.crate.opening.OpeningExecutionResult
import gg.aquatic.crates.interact.CrateClickType
import gg.aquatic.crates.interact.CrateInteractionService
import gg.aquatic.crates.data.interactable.CrateInteractableData
import gg.aquatic.crates.data.interaction.CrateClickMappingData
import gg.aquatic.crates.limit.LimitHandle
import gg.aquatic.crates.milestone.CrateMilestoneManager
import gg.aquatic.crates.open.OpenConditions
import gg.aquatic.crates.open.OpenPriceGroup
import gg.aquatic.crates.open.currency.CrateKeyCurrency
import gg.aquatic.kurrency.impl.VirtualCurrency
import gg.aquatic.crates.reward.processor.RewardProcessor
import gg.aquatic.crates.reward.provider.RewardProvider
import gg.aquatic.kholograms.Hologram
import gg.aquatic.stacked.event.StackedItemInteractEvent
import gg.aquatic.stacked.stackedItem
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.math.BigInteger

/**
 * Rewards Table
 * ID | Name (crate:name)
 *
 * Openings
 * ID | Crate Name - indexed | Timestamp | PlayerUUID | Amount
 *
 * Opened Rewards
 * Name (crate:name - Indexed) | Timestamp | PlayerUUID - indexed | cratename - indexed | Amount | Opening ID
 */
class Crate(
    val id: String,
    keyItemSupplier: () -> gg.aquatic.stacked.StackedItem<*>,
    val keyMustBeHeld: Boolean,
    val crateClickMapping: CrateClickMappingData,
    val keyClickMapping: CrateClickMappingData,
    val displayName: Component,
    hologramSupplier: () -> Hologram.Settings?,
    val hologramYOffset: Double,
    priceGroupsSupplier: () -> Collection<OpenPriceGroup>,
    openConditionsSupplier: () -> OpenConditions = { OpenConditions.DUMMY },
    val interactables: Collection<CrateInteractableData>,
    val disableOpenStats: Boolean,
    val limits: Collection<LimitHandle>,
    milestoneManagerSupplier: () -> CrateMilestoneManager,
    rewardProviderSupplier: () -> RewardProvider,
    rewardProcessorSupplier: () -> RewardProcessor,
    previewSupplier: () -> PreviewMenuSettings?,
) {

    val keyStackedItem: gg.aquatic.stacked.StackedItem<*> by lazy(keyItemSupplier)
    val keyItem: ItemStack by lazy { keyStackedItem.getItem() }
    val hologram: Hologram.Settings? by lazy(hologramSupplier)
    val priceGroups: Collection<OpenPriceGroup> by lazy(priceGroupsSupplier)
    val openConditions: OpenConditions by lazy(openConditionsSupplier)
    val milestoneManager: CrateMilestoneManager by lazy(milestoneManagerSupplier)
    val rewardProvider: RewardProvider by lazy(rewardProviderSupplier)
    val rewardProcessor: RewardProcessor by lazy(rewardProcessorSupplier)
    val preview: PreviewMenuSettings? by lazy(previewSupplier)
    val keyVirtualCurrency: VirtualCurrency by lazy {
        VirtualCurrency.of("aqcrates:key:$id")
    }
    val keyCurrency: CrateKeyCurrency by lazy {
        CrateKeyCurrency(id, { this }, keyVirtualCurrency)
    }

    val crateItem by lazy {
        stackedItem(Material.CHEST) {
            displayName = Component.text("$id Crate (Admin Only)")
            lore += Component.text("Right click to spawn!")
        }
    }

    internal fun handleCrateItemInteractions(event: StackedItemInteractEvent) {
        val player = event.player
        event.cancelled = true
        if (!player.hasPermission("aqcrates.admin")) {
            player.inventory.itemInMainHand.amount = 0
            return
        }

        val originalEvent = event.originalEvent
        if (originalEvent !is PlayerInteractEvent) {
            return
        }

        if (originalEvent.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        Messages.CRATE_PLACED.message().send(player)

        val rotation = player.yaw - 180
        val location = originalEvent.clickedBlock!!.location.add(originalEvent.blockFace.direction).apply { yaw = rotation }

        CrateHandler.spawnCrate(location, this)
    }

    val crateItemStack by lazy { crateItem.getItem() }

    internal fun handleKeyItemInteractions(event: StackedItemInteractEvent) {
        val interactType = event.interactType
        val clickType = when (interactType) {
            StackedItemInteractEvent.InteractType.LEFT -> CrateClickType.LEFT
            StackedItemInteractEvent.InteractType.RIGHT -> CrateClickType.RIGHT
            StackedItemInteractEvent.InteractType.SHIFT_LEFT -> CrateClickType.SHIFT_LEFT
            StackedItemInteractEvent.InteractType.SHIFT_RIGHT -> CrateClickType.SHIFT_RIGHT
            else -> return
        }

        val originalEvent = event.originalEvent as? PlayerInteractEvent
        if (CrateInteractionService.isPhysicalCrateInteraction(originalEvent)) {
            return
        }
        CrateInteractionService.handleKeyInteraction(this, event, clickType)
    }

    fun isHoldingKey(player: Player): Boolean {
        return player.inventory.itemInMainHand.isSimilar(keyItem) || player.inventory.itemInOffHand.isSimilar(keyItem)
    }

    suspend fun tryOpen(player: Player, crateHandle: CrateHandle? = null, ignoreKeyRequirement: Boolean = false): Boolean {
        return CrateOpeningService.tryOpen(player, this, crateHandle, ignoreKeyRequirement = ignoreKeyRequirement)
    }

    suspend fun openResult(player: Player, amount: BigInteger = BigInteger.ONE, ignoreKeyRequirement: Boolean = false): OpeningExecutionResult {
        return CrateOpeningService.tryOpenResult(player, this, null, amount, ignoreKeyRequirement)
    }

    suspend fun open(player: Player, amount: BigInteger = BigInteger.ONE, ignoreKeyRequirement: Boolean = false): Boolean {
        return CrateOpeningService.tryOpen(player, this, null, amount, ignoreKeyRequirement)
    }
}
