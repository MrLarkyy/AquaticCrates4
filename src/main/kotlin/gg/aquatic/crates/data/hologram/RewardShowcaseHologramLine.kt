package gg.aquatic.crates.data.hologram

import gg.aquatic.crates.reward.showcase.RewardShowcase
import gg.aquatic.crates.reward.showcase.RewardShowcaseFocusStyle
import gg.aquatic.crates.reward.showcase.RewardShowcaseHandle
import gg.aquatic.kholograms.HologramLine
import gg.aquatic.kholograms.HologramLineHandle
import gg.aquatic.kholograms.HologramRenderHandle
import gg.aquatic.kholograms.PacketEntityHologramRenderHandle
import gg.aquatic.kholograms.serialize.LineSettings
import gg.aquatic.pakket.Pakket
import gg.aquatic.pakket.api.nms.entity.EntityDataValue
import gg.aquatic.replace.PlaceholderContext
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.joml.Vector3f

class RewardShowcaseHologramLine(
    private val rewardShowcase: RewardShowcase,
    override val height: Double,
    override val filter: suspend (Player) -> Boolean,
    override val failLine: HologramLine?,
    override var scale: Float,
    override var billboard: Display.Billboard,
    override var transformationDuration: Int,
    override var teleportInterpolation: Int,
    override var translation: Vector3f,
) : HologramLine {

    override suspend fun spawn(
        location: Location,
        player: Player,
        placeholderContext: PlaceholderContext<Player>,
    ): HologramRenderHandle {
        val anchor = Pakket.handler.createEntity(location, EntityType.INTERACTION, null)
            ?: error("Failed to create hologram showcase anchor entity")
        val anchorHandle = PacketEntityHologramRenderHandle(anchor).also { it.sendSpawn(player) }
        return RewardShowcaseHologramRenderHandle(
            rewardShowcase = rewardShowcase,
            anchorHandle = anchorHandle,
            player = player,
            location = showcaseLocation(location),
            scale = scale.toDouble().coerceAtLeast(0.01),
        )
    }

    override suspend fun tick(hologramLineHandle: HologramLineHandle) = Unit

    override fun buildData(
        placeholderContext: PlaceholderContext<Player>,
        player: Player,
    ): List<EntityDataValue> = emptyList()

    private fun showcaseLocation(base: Location): Location {
        return base.clone().add(
            translation.x.toDouble(),
            translation.y.toDouble(),
            translation.z.toDouble(),
        )
    }

    private class RewardShowcaseHologramRenderHandle(
        private val rewardShowcase: RewardShowcase,
        private val anchorHandle: PacketEntityHologramRenderHandle,
        private val player: Player,
        location: Location,
        private val scale: Double,
    ) : HologramRenderHandle {

        private var location = location.clone()
        private var showcaseHandle: RewardShowcaseHandle = createShowcaseHandle(location)

        override val entityIds: IntArray
            get() = anchorHandle.entityIds

        override suspend fun move(location: Location, player: Player) {
            this.location = location.clone()
            anchorHandle.move(location, player)
            showcaseHandle.destroy()
            showcaseHandle = createShowcaseHandle(this.location)
        }

        override fun destroy(player: Player) {
            showcaseHandle.destroy()
            anchorHandle.destroy(player)
        }

        private fun createShowcaseHandle(location: Location): RewardShowcaseHandle {
            return rewardShowcase.createHandle(
                player = player,
                location = location,
                focusStyle = RewardShowcaseFocusStyle(scaleMultiplier = scale),
            )
        }
    }

    class Settings(
        private val rewardShowcase: RewardShowcase,
        private val height: Double,
        private val filter: suspend (Player) -> Boolean,
        private val failLine: LineSettings?,
        private val scale: Float,
        private val billboard: Display.Billboard,
        private val transformationDuration: Int,
        private val teleportInterpolation: Int,
        private val translation: Vector3f,
    ) : LineSettings {
        override fun create(): HologramLine {
            return RewardShowcaseHologramLine(
                rewardShowcase = rewardShowcase,
                height = height,
                filter = filter,
                failLine = failLine?.create(),
                scale = scale,
                billboard = billboard,
                transformationDuration = transformationDuration,
                teleportInterpolation = teleportInterpolation,
                translation = Vector3f(translation),
            )
        }
    }
}
