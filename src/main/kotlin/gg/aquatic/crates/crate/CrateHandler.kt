package gg.aquatic.crates.crate

import org.bukkit.Location

object CrateHandler {
    val crates get() = CrateHandleRegistry.crates
    val crateHandles get() = CrateHandleRegistry.handles

    fun spawnCrate(location: Location, crate: Crate): CrateHandle {
        val handle = PlacedCrateSpawner.spawnCrate(location, crate)
        if (!PlacedCratePersistenceCoordinator.isPersistenceSuppressed()) {
            PlacedCratePersistenceCoordinator.scheduleSavePlacedCrates()
        }
        return handle
    }

    fun removeHandle(crateHandle: CrateHandle) {
        val blockLocation = crateHandle.location.toBlockLocation()
        val existing = CrateHandleRegistry.handles[blockLocation]
        if (existing === crateHandle) {
            CrateHandleRegistry.handles.remove(blockLocation)
        }
        if (!PlacedCratePersistenceCoordinator.isPersistenceSuppressed()) {
            PlacedCratePersistenceCoordinator.scheduleSavePlacedCrates()
        }
    }

    suspend fun reloadPlacedCrates(reloadCrates: () -> Unit) {
        PlacedCratePersistenceCoordinator.savePlacedCrates()
        PlacedCratePersistenceCoordinator.runWithoutPersistenceUpdates {
            PlacedCrateSpawner.despawnAll()
            reloadCrates()
            PlacedCrateSpawner.respawnPersistentCrates()
        }
    }

    suspend fun loadPlacedCrates() {
        PlacedCratePersistenceCoordinator.runWithoutPersistenceUpdates {
            PlacedCrateSpawner.respawnPersistentCrates()
        }
    }

    suspend fun shutdown() {
        PlacedCratePersistenceCoordinator.savePlacedCrates()
        PlacedCratePersistenceCoordinator.runWithoutPersistenceUpdates {
            PlacedCrateSpawner.despawnAll()
        }
    }
}
