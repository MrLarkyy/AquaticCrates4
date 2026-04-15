package gg.aquatic.crates.reward.showcase

import gg.aquatic.crates.data.rewardshowcase.ItemDisplayRewardShowcaseData
import gg.aquatic.pakket.Pakket
import gg.aquatic.pakket.api.nms.PacketEntity
import gg.aquatic.pakket.api.nms.entity.EntityDataValue
import gg.aquatic.pakket.api.nms.entity.data.impl.BaseEntityData
import gg.aquatic.pakket.api.nms.entity.data.impl.display.DisplayEntityData
import gg.aquatic.pakket.api.nms.entity.data.impl.display.ItemDisplayEntityData
import gg.aquatic.pakket.sendPacket
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.joml.Vector3f

class ItemDisplayRewardShowcase(
    private val data: ItemDisplayRewardShowcaseData,
    private val itemSupplier: () -> ItemStack,
) : RewardShowcase {

    override fun previewItem(): ItemStack = itemSupplier().clone()
    override fun displayNameYOffset(): Double = data.displayNameYOffset

    override fun createHandle(
        player: Player,
        location: Location,
        focusStyle: RewardShowcaseFocusStyle,
    ): RewardShowcaseHandle {
        val packetEntity = Pakket.handler.createEntity(location, EntityType.ITEM_DISPLAY, null)
            ?: error("Failed to create item display reward showcase entity")
        val handle = ItemDisplayRewardShowcaseHandle(
            packetEntity = packetEntity,
            player = player,
            data = data,
            focusStyle = focusStyle,
            itemSupplier = itemSupplier,
        )
        handle.spawn()
        return handle
    }

    private class ItemDisplayRewardShowcaseHandle(
        private val packetEntity: PacketEntity,
        private val player: Player,
        private val data: ItemDisplayRewardShowcaseData,
        private var focusStyle: RewardShowcaseFocusStyle,
        private val itemSupplier: () -> ItemStack,
    ) : RewardShowcaseHandle {
        fun spawn() {
            packetEntity.updatePacket = Pakket.handler.createEntityUpdatePacket(
                packetEntity.entityId,
                buildData()
            )
            packetEntity.sendSpawnComplete(Pakket.handler, false, player)
        }

        override fun setFocusStyle(focusStyle: RewardShowcaseFocusStyle) {
            if (this.focusStyle == focusStyle) return
            this.focusStyle = focusStyle
            sendUpdate()
        }

        override fun move(location: Location) {
            packetEntity.teleport(Pakket.handler, location, false, player)
        }

        override fun destroy() {
            packetEntity.sendDespawn(Pakket.handler, false, player)
        }

        private fun sendUpdate() {
            val packet = Pakket.handler.createEntityUpdatePacket(packetEntity.entityId, buildData())
            packetEntity.updatePacket = packet
            player.sendPacket(packet, false)
        }

        private fun buildData(): List<EntityDataValue> {
            val scale = (data.scale * focusStyle.scaleMultiplier).coerceAtLeast(0.01).toFloat()

            return buildList {
                addAll(ItemDisplayEntityData.Item.generate(itemSupplier()))
                addAll(ItemDisplayEntityData.ItemDisplayTransform.generate(data.displayTransform))
                addAll(DisplayEntityData.Billboard.generate(data.billboard))
                addAll(DisplayEntityData.Scale.generate(Vector3f(scale, scale, scale)))
                addAll(DisplayEntityData.ViewRange.generate(data.viewRange.toFloat()))
                addAll(DisplayEntityData.TransformationInterpolationDuration.generate(data.transformationDuration))
                addAll(DisplayEntityData.TeleportationDuration.generate(data.teleportInterpolation))
                addAll(BaseEntityData.HasGravity.generate(false))
                addAll(
                    BaseEntityData.Visuals.generate(
                        isOnFire = false,
                        isSneaking = false,
                        isSprinting = false,
                        isSwimming = false,
                        isInvisible = false,
                        isGlowing = data.glowing || focusStyle.glowing,
                        isElytraFlying = false,
                    )
                )
            }
        }
    }
}
