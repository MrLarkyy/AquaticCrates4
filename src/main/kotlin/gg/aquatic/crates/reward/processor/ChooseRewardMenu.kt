package gg.aquatic.crates.reward.processor

import gg.aquatic.crates.data.processor.RewardDisplayMenuSettings
import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.executeActions
import gg.aquatic.kmenu.privateMenu
import gg.aquatic.kmenu.menu.PrivateMenu
import gg.aquatic.kmenu.menu.component.Button
import gg.aquatic.replace.PlaceholderContext
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ChooseRewardMenu private constructor(
    player: Player,
    private val rewards: List<RolledReward>,
    private val chooseCount: Int,
    private val hiddenRewards: Boolean,
    private val onSelectActions: Collection<ActionHandle<Player>>,
    private val hiddenItem: ItemStack?,
    private val settings: RewardDisplayMenuSettings,
    private val onCompleted: suspend (List<RolledReward>) -> Unit,
) : PrivateMenu(settings.invSettings.title, settings.invSettings.type, player, true) {

    companion object {
        suspend fun create(
            player: Player,
            rewards: List<RolledReward>,
            chooseCount: Int,
            hiddenRewards: Boolean,
            onSelectActions: Collection<ActionHandle<Player>>,
            hiddenItem: ItemStack?,
            settings: RewardDisplayMenuSettings,
            onCompleted: suspend (List<RolledReward>) -> Unit,
        ): ChooseRewardMenu {
            return ChooseRewardMenu(
                player = player,
                rewards = rewards,
                chooseCount = chooseCount,
                hiddenRewards = hiddenRewards,
                onSelectActions = onSelectActions,
                hiddenItem = hiddenItem,
                settings = settings,
                onCompleted = onCompleted,
            ).apply {
                addButtons()
                addRewardButtons()
                settings.anvilSettings?.applyTo(this)
            }
        }
    }

    private val context = PlaceholderContext.privateMenu()
    private val selectedIndices = linkedSetOf<Int>()
    private val rewardButtons = ArrayList<Button>()
    private var completed = false

    private suspend fun addButtons() {
        for ((id, comp) in settings.invSettings.components) {
            addComponent(comp.create(context))
        }
    }

    private suspend fun addRewardButtons() {
        val slots = settings.rewardSlots.toList()
        rewards.take(slots.size).forEachIndexed { index, rolled ->
            val button = Button(
                id = "reward:$index",
                itemstack = displayItem(index),
                slots = listOf(slots[index]),
                priority = 5,
                updateEvery = -1,
                textUpdater = context,
            ) {
                handleRewardClick(index)
            }
            rewardButtons += button
            addComponent(button)
        }
    }

    private suspend fun handleRewardClick(index: Int) {
        if (completed) return

        if (index in selectedIndices) {
            selectedIndices.remove(index)
            refreshRewardButtons()
            return
        }

        selectedIndices += index
        val rolled = rewards.getOrNull(index)
        if (rolled != null && onSelectActions.isNotEmpty()) {
            onSelectActions.executeActions(player) { _, str ->
                rolled.reward.updatePlaceholders(str, player, rolled.amount)
            }
        }
        if (selectedIndices.size >= chooseCount) {
            completeSelection(closeMenu = true)
            return
        }

        refreshRewardButtons()
    }

    private suspend fun completeSelection(closeMenu: Boolean) {
        completed = true
        refreshRewardButtons()

        val selectedRewards = selectedIndices.mapNotNull(rewards::getOrNull)
        onCompleted(selectedRewards)
        if (closeMenu) {
            close()
        }
    }

    private suspend fun completeSelectionWithRandomChoices(closeMenu: Boolean) {
        if (completed) return

        val completedSelection = ChooseRewardSelection.completedSelection(
            rewardCount = rewards.size,
            chooseCount = chooseCount,
            selectedIndices = selectedIndices
        )
        selectedIndices.clear()
        selectedIndices += completedSelection

        completeSelection(closeMenu)
    }

    private suspend fun refreshRewardButtons() {
        rewardButtons.forEachIndexed { index, button ->
            button.itemStack = displayItem(index)
            updateComponent(button)
        }
    }

    override suspend fun onClosed(player: Player) {
        if (completed) {
            return
        }

        completeSelectionWithRandomChoices(closeMenu = false)
    }

    private fun displayItem(index: Int): ItemStack {
        val hidden = hiddenRewards && !completed
        return rewards[index].displayItem(
            selected = index in selectedIndices,
            hidden = hidden,
            hiddenItem = hiddenItem,
            completed = completed,
        )
    }
}
