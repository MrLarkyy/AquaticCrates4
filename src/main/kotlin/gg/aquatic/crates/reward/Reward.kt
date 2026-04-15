package gg.aquatic.crates.reward
import gg.aquatic.crates.limit.LimitHandle
import gg.aquatic.crates.limit.LimitService
import gg.aquatic.crates.data.rewardshowcase.ItemDisplayRewardShowcaseData
import gg.aquatic.crates.reward.showcase.RewardShowcase
import gg.aquatic.crates.reward.showcase.ItemDisplayRewardShowcase
import gg.aquatic.crates.util.replaceCratePlaceholders
import gg.aquatic.crates.util.replacePlayerPlaceholder
import gg.aquatic.crates.util.Weightable
import gg.aquatic.crates.util.randomItem
import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.checkConditions
import gg.aquatic.execute.condition.ConditionHandle
import gg.aquatic.execute.executeActions
import gg.aquatic.kmenu.inventory.ButtonType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.math.RoundingMode

class Reward(
    val id: String,
    val crateId: String,
    displayName: Component?,
    val previewItem: () -> ItemStack,
    val fallbackItem: (() -> ItemStack)?,
    val winActions: Collection<ActionHandle<Player>>,
    val massWinActions: Collection<ActionHandle<Player>>,
    val showcase: RewardShowcase = ItemDisplayRewardShowcase(
        data = ItemDisplayRewardShowcaseData(),
        itemSupplier = previewItem
    ),
    val conditions: Collection<ConditionHandle<Player>>,
    val purchaseManager: RewardPurchaseHandler?,
    val amountRanges: Collection<RewardAmountRange>,
    val clickHandler: suspend (reward: Reward, player: Player, clickType: ButtonType) -> Unit,
    val rarity: RewardRarity,
    val limits: Collection<LimitHandle>,
    override var chance: Double
) : Weightable {

    val isPurchasable: Boolean = purchaseManager != null

    suspend fun canWinIgnoringLimits(player: Player): Boolean {
        return conditions.checkConditions(player)
    }

    suspend fun canWin(player: Player): Boolean {
        return canWinIgnoringLimits(player) && LimitService.canWinReward(player, crateId, id, limits)
    }

    fun rollAmount(): Int {
        return if (amountRanges.isEmpty()) 1 else amountRanges.randomItem().roll()
    }

    suspend fun win(player: Player, randomAmount: Int = rollAmount()) {
        if (winActions.isEmpty()) {
            giveDefaultItem(player, randomAmount.toLong())
            return
        }

        winActions.executeActions(player) { _, str ->
            updatePlaceholders(str, player, randomAmount)
        }
    }

    suspend fun tryWin(player: Player, randomAmount: Int = rollAmount()): Boolean {
        if (!canWin(player)) {
            return false
        }

        win(player, randomAmount)
        return true
    }

    suspend fun tryPurchase(player: Player): Boolean {
        val success = purchaseManager?.tryPurchase(player) ?: false
        if (success) {
            win(player)
        }
        return success
    }

    suspend fun massWin(player: Player, winCount: Long, totalAmount: Long) {
        if (winCount <= 0L || totalAmount <= 0L) {
            return
        }

        if (massWinActions.isNotEmpty()) {
            massWinActions.executeActions(player) { _, str ->
                updateMassPlaceholders(str, player, winCount, totalAmount)
            }
            return
        }

        if (winActions.isEmpty()) {
            giveDefaultItem(player, totalAmount)
            return
        }

        winActions.executeActions(player) { _, str ->
            updateMassPlaceholders(str, player, winCount, totalAmount)
        }
    }

    val displayName by lazy {
        displayName ?: previewItem().itemMeta.displayName() ?: Component.text(id)
    }

    fun updatePlaceholders(str: String, player: Player, randomAmount: Int): String {
        return updateMassPlaceholders(str, player, 1L, randomAmount.toLong())
            .replace("%random-amount%", randomAmount.toString())
    }

    fun updateMassPlaceholders(str: String, player: Player, winCount: Long, totalAmount: Long): String {
        val chanceRaw = formatChanceValue(chance)
        val chanceFormatted = formatChanceValue(chance * 100.0)

        return str
            .replacePlayerPlaceholder(player)
            .replaceCratePlaceholders(
                mapOf(
                    "%reward-id%" to id,
                    "%reward-name%" to PlainTextComponentSerializer.plainText().serialize(displayName),
                    "%reward-rarity-id%" to rarity.id,
                    "%reward-rarity-name%" to PlainTextComponentSerializer.plainText().serialize(rarity.displayName),
                    "%reward-chance%" to chanceRaw,
                    "%reward-chance-formatted%" to chanceFormatted,
                    "%reward-real-chance%" to chanceFormatted,
                    "%reward-real-chance-formatted%" to chanceFormatted,
                    "%reward-win-count%" to winCount.toString(),
                    "%reward-drawn-count%" to winCount.toString(),
                    "%reward-total-amount%" to totalAmount.toString(),
                    "%reward-total-random-amount%" to totalAmount.toString(),
                )
            )
    }

    private fun formatChanceValue(value: Double): String {
        return BigDecimal.valueOf(value)
            .setScale(2, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    }

    private fun giveDefaultItem(player: Player, totalAmount: Long) {
        val item = previewItem()
        val originalAmount = item.amount.coerceAtLeast(1).toLong()
        var remaining = (originalAmount * totalAmount).coerceAtLeast(1L)
        val maxStackSize = item.maxStackSize.coerceAtLeast(1)

        while (remaining > 0L) {
            val stackAmount = minOf(remaining, maxStackSize.toLong())
            remaining -= stackAmount

            val stack = item.clone()
            stack.amount = stackAmount.toInt()

            val leftovers = player.inventory.addItem(stack)
            leftovers.values.forEach { player.world.dropItemNaturally(player.location, it) }
        }
    }
}
