package gg.aquatic.crates.crate.opening.worldchoose

import gg.aquatic.crates.crate.Crate
import gg.aquatic.crates.crate.CrateHandle
import gg.aquatic.crates.data.processor.WorldRewardDisplaySettingsData
import gg.aquatic.crates.reward.processor.RolledReward
import gg.aquatic.execute.ActionHandle
import kotlinx.coroutines.CompletableDeferred
import org.bukkit.entity.Player

data class WorldChooseSession(
    val player: Player,
    val crate: Crate,
    val crateHandle: CrateHandle?,
    val offeredRewards: MutableList<RolledReward>,
    val chooseCount: Int,
    val hiddenRewards: Boolean,
    val hiddenItem: org.bukkit.inventory.ItemStack?,
    val onStartActions: Collection<ActionHandle<Player>>,
    val onSwitchActions: Collection<ActionHandle<Player>>,
    val onChooseActions: Collection<ActionHandle<Player>>,
    val onEndActions: Collection<ActionHandle<Player>>,
    val display: WorldRewardDisplaySettingsData,
    val completion: CompletableDeferred<List<RolledReward>>,
    val selectedRewards: MutableList<RolledReward> = mutableListOf(),
    val displayHandles: MutableList<WorldChooseRewardDisplayHandle> = mutableListOf(),
    var focusIndex: Int = 0,
    var started: Boolean = false,
    var completed: Boolean = false,
)
