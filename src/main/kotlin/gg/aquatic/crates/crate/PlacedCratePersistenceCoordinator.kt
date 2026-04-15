package gg.aquatic.crates.crate

import gg.aquatic.common.coroutine.VirtualsCtx
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

object PlacedCratePersistenceCoordinator {
    private val persistenceSuppressed = AtomicBoolean(false)
    private val persistenceEpoch = AtomicLong(0L)

    suspend fun savePlacedCrates(expectedEpoch: Long? = null) {
        PlacedCrateRepository.saveEntries(
            crateHandles = CrateHandleRegistry.handles.values.toList(),
            expectedEpoch = expectedEpoch,
            currentEpoch = { persistenceEpoch.get() },
            persistenceSuppressed = { persistenceSuppressed.get() },
        )
    }

    fun scheduleSavePlacedCrates() {
        val expectedEpoch = persistenceEpoch.get()
        VirtualsCtx {
            savePlacedCrates(expectedEpoch)
        }
    }

    suspend fun <T> runWithoutPersistenceUpdates(block: suspend () -> T): T {
        persistenceEpoch.incrementAndGet()
        persistenceSuppressed.set(true)
        return try {
            block()
        } finally {
            persistenceSuppressed.set(false)
        }
    }

    fun isPersistenceSuppressed(): Boolean = persistenceSuppressed.get()
}
