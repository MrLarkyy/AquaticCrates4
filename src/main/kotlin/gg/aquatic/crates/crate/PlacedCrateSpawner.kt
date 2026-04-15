package gg.aquatic.crates.crate

import gg.aquatic.common.location.world.AwaitingWorld
import org.bukkit.Location

object PlacedCrateSpawner {
    fun spawnCrate(location: Location, crate: Crate): CrateHandle {
        val blockLocation = location.toBlockLocation()
        CrateHandleRegistry.handles[blockLocation]?.destroy()

        val crateHandle = CrateHandle(crate, location).apply {
            interactables.forEach { it.register() }
        }
        CrateHandleRegistry.handles[blockLocation] = crateHandle
        return crateHandle
    }

    suspend fun despawnAll() {
        val handles = CrateHandleRegistry.handles.values.toList()
        handles.forEach { it.destroy() }
        CrateHandleRegistry.handles.clear()
    }

    suspend fun respawnPersistentCrates() {
        PlacedCrateRepository.clearAwaitingEntries()
        val entries = PlacedCrateRepository.loadEntries()
        for (entry in entries) {
            val crate = CrateHandleRegistry.crates[entry.crateId] ?: continue
            PlacedCrateRepository.addAwaitingEntry(entry)
            AwaitingWorld.create(entry.worldName) { world ->
                PlacedCrateRepository.removeAwaitingEntry(entry)
                spawnCrate(Location(world, entry.x, entry.y, entry.z, entry.yaw, 0f), crate)
            }
        }
    }
}
