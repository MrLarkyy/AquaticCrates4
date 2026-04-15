package gg.aquatic.crates.crate.opening.worldchoose

import gg.aquatic.common.coroutine.BukkitCtx
import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.common.event
import gg.aquatic.crates.crate.Crate
import gg.aquatic.crates.crate.CrateHandle
import gg.aquatic.crates.data.playeraction.PlayerActionbarManager
import gg.aquatic.crates.data.processor.GlowWorldRewardFocusEffectData
import gg.aquatic.crates.data.processor.ScaleWorldRewardFocusEffectData
import gg.aquatic.crates.data.processor.VerticalOffsetWorldRewardFocusEffectData
import gg.aquatic.crates.data.processor.WorldRewardDisplaySettingsData
import gg.aquatic.crates.reward.processor.RolledReward
import gg.aquatic.crates.reward.showcase.ItemDisplayRewardShowcase
import gg.aquatic.crates.reward.showcase.RewardShowcaseFocusStyle
import gg.aquatic.execute.ActionHandle
import gg.aquatic.execute.executeActions
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot

object WorldChooseRuntime {
    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true

        event<PlayerItemHeldEvent>(priority = EventPriority.HIGHEST) { event ->
            val session = WorldChooseSessionStore.current(event.player.uniqueId) ?: return@event
            event.isCancelled = true
            VirtualsCtx.launch {
                shiftFocus(session, scrollDelta(event.previousSlot, event.newSlot))
            }
        }

        event<PlayerInteractEvent>(priority = EventPriority.HIGHEST) { event ->
            if (event.hand == EquipmentSlot.OFF_HAND) return@event
            val session = WorldChooseSessionStore.current(event.player.uniqueId) ?: return@event
            when (event.action) {
                Action.LEFT_CLICK_AIR,
                Action.LEFT_CLICK_BLOCK,
                Action.RIGHT_CLICK_AIR,
                Action.RIGHT_CLICK_BLOCK -> {
                    event.isCancelled = true
                    VirtualsCtx.launch {
                        completeFocused(session)
                    }
                }
                else -> Unit
            }
        }

        event<PlayerQuitEvent> { event ->
            val session = WorldChooseSessionStore.current(event.player.uniqueId) ?: return@event
            VirtualsCtx.launch {
                cancel(session)
            }
        }
    }

    suspend fun execute(
        player: Player,
        crate: Crate,
        crateHandle: CrateHandle?,
        offeredRewards: List<RolledReward>,
        chooseCount: Int,
        hiddenRewards: Boolean,
        hiddenItem: org.bukkit.inventory.ItemStack?,
        onStartActions: Collection<ActionHandle<Player>>,
        onSwitchActions: Collection<ActionHandle<Player>>,
        onChooseActions: Collection<ActionHandle<Player>>,
        onEndActions: Collection<ActionHandle<Player>>,
        display: WorldRewardDisplaySettingsData,
    ): List<RolledReward> {
        initialize()

        val session = WorldChooseSessionStore.register(
            WorldChooseSession(
                player = player,
                crate = crate,
                crateHandle = crateHandle,
                offeredRewards = offeredRewards.toMutableList(),
                chooseCount = chooseCount,
                hiddenRewards = hiddenRewards,
                hiddenItem = hiddenItem,
                onStartActions = onStartActions,
                onSwitchActions = onSwitchActions,
                onChooseActions = onChooseActions,
                onEndActions = onEndActions,
                display = display.normalized(),
                completion = CompletableDeferred(),
            )
        )

        return try {
            render(session)
            session.completion.await()
        } finally {
            destroy(session)
            WorldChooseSessionStore.finish(session)
        }
    }

    private suspend fun render(session: WorldChooseSession) {
        withContext(BukkitCtx.ofEntity(session.player)) {
            destroyDisplays(session)
            val center = baseLocation(session)
            val layout = layout(center, session.offeredRewards.size, session.display)

            layout.forEachIndexed { index, location ->
                session.displayHandles += createShowcaseHandle(session, index, location)
            }
            refreshFocus(session)
        }

        if (!session.started) {
            session.started = true
            executeActions(session.player, session.onStartActions, session.offeredRewards.getOrNull(session.focusIndex))
        }
    }

    private suspend fun shiftFocus(session: WorldChooseSession, delta: Int) {
        if (session.completed || session.offeredRewards.isEmpty() || delta == 0) return
        session.focusIndex = Math.floorMod(session.focusIndex + delta, session.offeredRewards.size)
        withContext(BukkitCtx.ofEntity(session.player)) {
            if (hasPositionalFocusEffect(session.display)) {
                repositionDisplays(session)
            }
            refreshFocus(session)
        }
        executeActions(session.player, session.onSwitchActions, session.offeredRewards.getOrNull(session.focusIndex))
    }

    private suspend fun completeFocused(session: WorldChooseSession) {
        if (session.completed) return
        val selected = session.offeredRewards.getOrNull(session.focusIndex) ?: return
        session.selectedRewards += selected
        executeActions(session.player, session.onChooseActions, selected)

        if (session.selectedRewards.size >= session.chooseCount) {
            finishSelection(session, session.selectedRewards.toList())
            return
        }

        session.offeredRewards.removeAt(session.focusIndex)
        if (session.offeredRewards.isEmpty()) {
            finishSelection(session, session.selectedRewards.toList())
            return
        }

        session.focusIndex = session.focusIndex.coerceAtMost(session.offeredRewards.lastIndex)
        render(session)
    }

    private suspend fun cancel(session: WorldChooseSession) {
        if (session.completed) return
        finishSelection(session, emptyList())
    }

    private suspend fun finishSelection(session: WorldChooseSession, rewards: List<RolledReward>) {
        if (session.completed) return
        session.completed = true
        executeActions(session.player, session.onEndActions, session.offeredRewards.getOrNull(session.focusIndex))
        session.completion.complete(rewards)
    }

    private suspend fun destroy(session: WorldChooseSession) {
        withContext(BukkitCtx.ofEntity(session.player)) {
            destroyDisplays(session)
            PlayerActionbarManager.clear(session.player)
        }
    }

    private fun destroyDisplays(session: WorldChooseSession) {
        session.displayHandles.forEach { it.destroy() }
        session.displayHandles.clear()
    }

    private fun baseLocation(session: WorldChooseSession): Location {
        val base = session.crateHandle
            ?.location
            ?.clone()
            ?.add(0.5, 0.0, 0.5)
            ?: session.player.location.clone()
        val (offsetX, offsetZ) = rotateHorizontalOffsets(
            xOffset = session.display.xOffset,
            zOffset = session.display.zOffset,
            yaw = session.crateHandle?.location?.yaw ?: 0f,
        )
        return base.add(offsetX, session.display.yOffset, offsetZ)
    }

    private fun layout(center: Location, count: Int, settings: WorldRewardDisplaySettingsData): List<Location> {
        if (count <= 1) {
            return listOf(center.clone())
        }

        val yaw = center.yaw

        return List(count) { index ->
            val angle = (Math.PI * 2.0 * index) / count.toDouble()
            val localX = kotlin.math.cos(angle) * settings.radius
            val localZ = kotlin.math.sin(angle) * settings.radius
            val (worldX, worldZ) = rotateHorizontalOffsets(localX, localZ, yaw)
            center.clone().add(worldX, 0.0, worldZ)
        }
    }

    private fun shownItem(session: WorldChooseSession, index: Int): org.bukkit.inventory.ItemStack {
        return if (session.hiddenRewards && !session.completed) {
            session.hiddenItem?.clone() ?: session.offeredRewards[index].reward.previewItem()
        } else {
            session.offeredRewards[index].displayItem(completed = true)
        }
    }

    private fun refreshFocus(session: WorldChooseSession) {
        session.displayHandles.forEachIndexed { index, handle ->
            handle.setFocusStyle(if (index == session.focusIndex) focusedStyle(session.display) else defaultStyle(session.display))
        }
    }

    private fun repositionDisplays(session: WorldChooseSession) {
        val center = baseLocation(session)
        val layout = layout(center, session.offeredRewards.size, session.display)
        layout.forEachIndexed { index, location ->
            val focusStyle = if (index == session.focusIndex) focusedStyle(session.display) else defaultStyle(session.display)
            session.displayHandles.getOrNull(index)?.move(applyFocusOffset(location, focusStyle))
        }
    }

    private suspend fun executeActions(
        player: Player,
        actions: Collection<ActionHandle<Player>>,
        reward: RolledReward?,
    ) {
        if (actions.isEmpty() || reward == null) return
        VirtualsCtx.launch {
            actions.executeActions(player) { _, str ->
                reward.reward.updatePlaceholders(str, player, reward.amount)
            }
        }
    }

    private fun scrollDelta(previousSlot: Int, newSlot: Int): Int {
        val raw = (newSlot - previousSlot + 9) % 9
        return when (raw) {
            0 -> 0
            in 1..4 -> 1
            else -> -1
        }
    }

    private fun createShowcaseHandle(
        session: WorldChooseSession,
        index: Int,
        location: Location,
    ): WorldChooseRewardDisplayHandle {
        val focused = index == session.focusIndex
        val focusStyle = if (focused) focusedStyle(session.display) else defaultStyle(session.display)
        val reward = session.offeredRewards[index]
        if (session.hiddenRewards && !session.completed) {
            val showcase = ItemDisplayRewardShowcase(
                data = gg.aquatic.crates.data.rewardshowcase.ItemDisplayRewardShowcaseData(
                    viewRange = 24,
                    scale = 1.0,
                ),
                itemSupplier = { shownItem(session, index) }
            )
            return WorldChooseRewardDisplayHandle(
                showcaseHandle = showcase.createHandle(
                    player = session.player,
                    location = applyFocusOffset(location, focusStyle),
                    focusStyle = focusStyle,
                ),
                hologram = null,
                hologramYOffset = 0.0,
            )
        }

        val showcase = reward.reward.showcase
        val showcaseLocation = applyFocusOffset(location, focusStyle)
        return WorldChooseRewardDisplayHandle(
            showcaseHandle = showcase.createHandle(
                player = session.player,
                location = showcaseLocation,
                focusStyle = focusStyle,
            ),
            hologram = session.display.rewardHologram?.let { rewardHologram ->
                WorldChooseRewardDisplayHandle.createRewardHologram(
                    player = session.player,
                    rewardHologram = rewardHologram,
                    rewardHologramYOffset = session.display.rewardHologramYOffset,
                    showcaseLocation = showcaseLocation,
                    rolledReward = reward,
                    rewardShowcase = showcase,
                    rewardDisplayName = reward.reward.displayName,
                )
            },
            hologramYOffset = session.display.rewardHologramYOffset + (session.display.rewardHologram?.yOffset ?: 0.0),
        )
    }

    private fun defaultStyle(display: WorldRewardDisplaySettingsData): RewardShowcaseFocusStyle {
        return RewardShowcaseFocusStyle(scaleMultiplier = display.defaultScale)
    }

    private fun focusedStyle(display: WorldRewardDisplaySettingsData): RewardShowcaseFocusStyle {
        var scaleMultiplier = display.defaultScale
        var yOffset = 0.0
        var glowing = false
        var glowColor = gg.aquatic.crates.data.rewardshowcase.GlowColor.WHITE

        display.focusEffects.forEach { effect ->
            when (effect) {
                is ScaleWorldRewardFocusEffectData -> scaleMultiplier *= effect.scaleMultiplier
                is VerticalOffsetWorldRewardFocusEffectData -> yOffset += effect.yOffset
                is GlowWorldRewardFocusEffectData -> {
                    glowing = true
                    glowColor = gg.aquatic.crates.data.rewardshowcase.GlowColor.of(effect.glowColor)
                        ?: gg.aquatic.crates.data.rewardshowcase.GlowColor.WHITE
                }
            }
        }

        return RewardShowcaseFocusStyle(
            scaleMultiplier = scaleMultiplier,
            yOffset = yOffset,
            glowing = glowing,
            glowColor = glowColor,
        )
    }

    private fun applyFocusOffset(location: Location, focusStyle: RewardShowcaseFocusStyle): Location {
        return location.clone().add(0.0, focusStyle.yOffset, 0.0)
    }

    private fun hasPositionalFocusEffect(display: WorldRewardDisplaySettingsData): Boolean {
        return display.focusEffects.any { it is VerticalOffsetWorldRewardFocusEffectData && it.yOffset != 0.0 }
    }

    private fun rotateHorizontalOffsets(xOffset: Double, zOffset: Double, yaw: Float): Pair<Double, Double> {
        val yawRad = Math.toRadians(yaw.toDouble())
        val forwardX = -kotlin.math.sin(yawRad)
        val forwardZ = kotlin.math.cos(yawRad)
        val rightX = -forwardZ
        val rightZ = forwardX

        val worldX = rightX * xOffset + forwardX * zOffset
        val worldZ = rightZ * xOffset + forwardZ * zOffset
        return worldX to worldZ
    }
}
