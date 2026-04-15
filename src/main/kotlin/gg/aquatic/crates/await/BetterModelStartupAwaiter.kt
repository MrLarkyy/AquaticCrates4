package gg.aquatic.crates.await

import kr.toxicity.model.api.BetterModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import org.bukkit.Bukkit

internal object BetterModelStartupAwaiter : StartupDependencyAwaiter {
    override fun isAvailable(): Boolean {
        return Bukkit.getPluginManager().getPlugin("BetterModel") != null
    }

    override fun await(): Deferred<Unit> {
        val deferred = CompletableDeferred<Unit>()

        val initialized = runCatching {
            BetterModel.platform().modelManager().modelKeys()
            true
        }.getOrDefault(false)
        if (initialized) {
            deferred.complete(Unit)
            return deferred
        }

        BetterModel.platform().addReloadEndHandler {
            deferred.complete(Unit)
        }

        return deferred
    }
}
