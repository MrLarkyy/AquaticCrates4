package gg.aquatic.crates.reward.showcase

import org.bukkit.Location

interface RewardShowcaseHandle {
    fun setFocusStyle(focusStyle: RewardShowcaseFocusStyle)
    fun move(location: Location)
    fun destroy()
}
