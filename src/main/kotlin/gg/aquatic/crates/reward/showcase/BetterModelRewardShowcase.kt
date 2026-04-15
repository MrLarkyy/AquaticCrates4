package gg.aquatic.crates.reward.showcase

import gg.aquatic.clientside.FakeObject
import gg.aquatic.clientside.serialize.ClientsideBMSettings
import gg.aquatic.common.audience.WhitelistAudience
import gg.aquatic.crates.data.rewardshowcase.BetterModelRewardShowcaseData
import gg.aquatic.crates.data.rewardshowcase.GlowColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BetterModelRewardShowcase(
    private val data: BetterModelRewardShowcaseData,
    private val itemSupplier: () -> ItemStack,
) : RewardShowcase {

    override fun previewItem(): ItemStack = itemSupplier().clone()
    override fun displayNameYOffset(): Double = data.displayNameYOffset

    override fun createHandle(
        player: Player,
        location: Location,
        focusStyle: RewardShowcaseFocusStyle,
    ): RewardShowcaseHandle {
        return BetterModelRewardShowcaseHandle(
            player = player,
            location = location,
            data = data,
            focusStyle = focusStyle,
        )
    }

    private class BetterModelRewardShowcaseHandle(
        private val player: Player,
        private val location: Location,
        private val data: BetterModelRewardShowcaseData,
        private var focusStyle: RewardShowcaseFocusStyle,
    ) : RewardShowcaseHandle {
        private var handle: FakeObject = createHandle()

        init {
            handle.register()
        }

        override fun setFocusStyle(focusStyle: RewardShowcaseFocusStyle) {
            if (this.focusStyle == focusStyle) return
            this.focusStyle = focusStyle
            handle.destroy()
            handle = createHandle()
            handle.register()
        }

        override fun move(location: Location) {
            this.location.world = location.world
            this.location.x = location.x
            this.location.y = location.y
            this.location.z = location.z
            this.location.yaw = location.yaw
            this.location.pitch = location.pitch

            runCatching {
                (handle as? gg.aquatic.clientside.bm.FakeBM)?.model?.forceUpdate(true)
            }
        }

        override fun destroy() {
            handle.destroy()
        }

        private fun createHandle(): FakeObject {
            return ClientsideBMSettings(
                modelId = data.modelId,
                viewRange = data.viewRange,
                offsetX = 0.0,
                offsetY = 0.0,
                offsetZ = 0.0,
                scale = (data.scale * focusStyle.scaleMultiplier).coerceAtLeast(0.1),
                glowing = data.glowing || focusStyle.glowing,
            ).create(
                location = location,
                audience = WhitelistAudience(mutableListOf(player.uniqueId)),
                onInteract = { _, _, _ -> }
            ).also { handle ->
                handle.setGlowColor(
                    when {
                        data.glowing -> GlowColor.colorOf(data.glowColor)?.asRGB()
                        focusStyle.glowing -> focusStyle.glowColor.color.asRGB()
                        else -> null
                    }
                )
            }
        }
    }
}
