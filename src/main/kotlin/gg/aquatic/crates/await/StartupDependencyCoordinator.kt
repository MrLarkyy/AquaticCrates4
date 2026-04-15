package gg.aquatic.crates.await

import kotlinx.coroutines.awaitAll

private val startupDependencyAwaiters: List<StartupDependencyAwaiter> = listOf(
    BetterModelStartupAwaiter,
    ItemsAdderStartupAwaiter,
    ModelEngineStartupAwaiter,
)

suspend fun awaitStartupDependencies() {
    startupDependencyAwaiters
        .asSequence()
        .filter { it.isAvailable() }
        .map { it.await() }
        .toList()
        .awaitAll()
}
