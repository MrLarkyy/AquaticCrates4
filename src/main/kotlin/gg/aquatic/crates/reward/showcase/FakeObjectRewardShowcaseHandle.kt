package gg.aquatic.crates.reward.showcase

import gg.aquatic.clientside.FakeObject
import org.bukkit.Location

class FakeObjectRewardShowcaseHandle(
    private val handle: FakeObject,
) : RewardShowcaseHandle {
    override fun setFocusStyle(focusStyle: RewardShowcaseFocusStyle) = Unit

    override fun move(location: Location) = Unit

    override fun destroy() {
        handle.destroy()
    }
}
