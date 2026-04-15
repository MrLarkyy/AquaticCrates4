package gg.aquatic.crates.crate

import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.crates.CratesPlugin
import kotlinx.coroutines.withContext
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

object PlacedCrateRepository {
    private val awaitingPlacedCrates = ConcurrentHashMap<String, MutableSet<String>>()

    private val placedCratesFile: File
        get() = File(CratesPlugin.dataFolder, "placedcrates.yml")

    fun clearAwaitingEntries() {
        awaitingPlacedCrates.clear()
    }

    fun addAwaitingEntry(entry: PlacedCrateEntry) {
        val serializedEntry = entry.serialized()
        awaitingPlacedCrates
            .computeIfAbsent(entry.worldName) { Collections.synchronizedSet(linkedSetOf()) }
            .add(serializedEntry)
    }

    fun removeAwaitingEntry(entry: PlacedCrateEntry) {
        val serializedEntry = entry.serialized()
        awaitingPlacedCrates[entry.worldName]?.remove(serializedEntry)
        if (awaitingPlacedCrates[entry.worldName]?.isEmpty() == true) {
            awaitingPlacedCrates.remove(entry.worldName)
        }
    }

    suspend fun loadEntries(): List<PlacedCrateEntry> {
        return withContext(VirtualsCtx) {
            if (!placedCratesFile.exists()) {
                return@withContext emptyList()
            }

            val config = YamlConfiguration.loadConfiguration(placedCratesFile)
            buildList {
                for (worldName in config.getKeys(false)) {
                    for (entry in config.getStringList(worldName)) {
                        val parts = entry.split(";")
                        if (parts.size < 5) continue

                        val x = parts[1].toDoubleOrNull() ?: continue
                        val y = parts[2].toDoubleOrNull() ?: continue
                        val z = parts[3].toDoubleOrNull() ?: continue
                        val yaw = parts[4].toFloatOrNull() ?: 0f

                        add(PlacedCrateEntry(worldName, parts[0], x, y, z, yaw))
                    }
                }
            }
        }
    }

    suspend fun saveEntries(
        crateHandles: Collection<CrateHandle>,
        expectedEpoch: Long?,
        currentEpoch: () -> Long,
        persistenceSuppressed: () -> Boolean,
    ) {
        withContext(VirtualsCtx) {
            if (expectedEpoch != null && expectedEpoch != currentEpoch()) {
                return@withContext
            }
            if (persistenceSuppressed()) {
                return@withContext
            }
            placedCratesFile.parentFile.mkdirs()

            val config = YamlConfiguration()
            val grouped = crateHandles
                .groupBy { it.location.world?.name ?: return@groupBy "__invalid__" }
                .filterKeys { it != "__invalid__" }

            for ((worldName, handles) in grouped) {
                val serialized = handles.map { handle ->
                    "${handle.crate.id};${handle.location.x};${handle.location.y};${handle.location.z};${handle.location.yaw}"
                }.toMutableList()
                awaitingPlacedCrates[worldName]?.forEach { pending ->
                    if (pending !in serialized) {
                        serialized += pending
                    }
                }
                config.set(worldName, serialized)
            }

            for ((worldName, pendingEntries) in awaitingPlacedCrates) {
                if (config.contains(worldName)) continue
                config.set(worldName, pendingEntries.toList())
            }

            config.save(placedCratesFile)
        }
    }
}
