package gg.aquatic.crates.crate

import gg.aquatic.snapshotmap.SuspendingSnapshotMap
import org.bukkit.Location
import java.util.concurrent.ConcurrentHashMap

object CrateHandleRegistry {
    val crates = ConcurrentHashMap<String, Crate>()
    val handles = SuspendingSnapshotMap<Location, CrateHandle>()
}
