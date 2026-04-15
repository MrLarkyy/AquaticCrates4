package gg.aquatic.crates.reward.showcase

import gg.aquatic.clientside.meg.FakeMEG
import gg.aquatic.common.audience.WhitelistAudience
import gg.aquatic.crates.data.rewardshowcase.GlowColor
import gg.aquatic.crates.data.rewardshowcase.ModelEngineRewardShowcaseData
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ModelEngineRewardShowcase(
    private val data: ModelEngineRewardShowcaseData,
    private val itemSupplier: () -> ItemStack,
) : RewardShowcase {

    override fun previewItem(): ItemStack = itemSupplier().clone()
    override fun displayNameYOffset(): Double = data.displayNameYOffset

    override fun createHandle(
        player: Player,
        location: Location,
        focusStyle: RewardShowcaseFocusStyle,
    ): RewardShowcaseHandle {
        return ModelEngineRewardShowcaseHandle(
            player = player,
            location = location,
            data = data,
            focusStyle = focusStyle,
        )
    }

    private class ModelEngineRewardShowcaseHandle(
        private val player: Player,
        private val location: Location,
        private val data: ModelEngineRewardShowcaseData,
        private var focusStyle: RewardShowcaseFocusStyle,
    ) : RewardShowcaseHandle {
        private val handle = FakeMEG(
            location = location.clone(),
            modelId = data.modelId,
            viewRange = data.viewRange,
            initialAudience = WhitelistAudience(mutableListOf(player.uniqueId)),
            onInteract = { _, _, _ -> }
        )
        init {
            applyState()
            handle.register()
        }

        override fun setFocusStyle(focusStyle: RewardShowcaseFocusStyle) {
            if (this.focusStyle == focusStyle) return
            this.focusStyle = focusStyle
            applyState()
        }

        override fun move(location: Location) {
            this.location.world = location.world
            this.location.x = location.x
            this.location.y = location.y
            this.location.z = location.z
            this.location.yaw = location.yaw
            this.location.pitch = location.pitch

            handle.dummy.location = this.location
            handle.dummy.bodyRotationController.yBodyRot = this.location.yaw
            handle.dummy.bodyRotationController.xHeadRot = this.location.pitch
            handle.dummy.bodyRotationController.yHeadRot = this.location.yaw
            handle.dummy.yHeadRot = this.location.yaw
            handle.dummy.yBodyRot = this.location.yaw
        }

        override fun destroy() {
            handle.destroy()
        }

        private fun applyState() {
            handle.setScale((data.scale * focusStyle.scaleMultiplier).coerceAtLeast(0.1))
            handle.setGlowing(data.glowing || focusStyle.glowing)
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
