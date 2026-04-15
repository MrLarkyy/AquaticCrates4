package gg.aquatic.crates.crate

import gg.aquatic.common.audience.GlobalAudience
import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.crates.interact.CrateClickType
import gg.aquatic.crates.interact.CrateInteractionService
import gg.aquatic.replace.PlaceholderContext
import gg.aquatic.clientside.FakeObject
import kotlinx.coroutines.withContext
import org.bukkit.Location

class CrateHandle(
    val crate: Crate,
    val location: Location,
) {
    var hologram = createHologram()
        private set
    var interactables = createInteractables()
        private set
    private var hologramVisible = hologram != null
    private var interactablesVisible = interactables.isNotEmpty()

    private fun createHologram() = crate.hologram?.create(
        location.clone().add(0.5, 1.0 + crate.hologramYOffset, 0.5),
        { PlaceholderContext.player }
    )

    private fun createInteractables(): List<FakeObject> = crate.interactables.map {
        it.toSettings().create(location, GlobalAudience()) { _, player, isLeft ->
            val clickType = CrateClickType.fromInteraction(isLeft, player)
            CrateInteractionService.handleCrateInteraction(this@CrateHandle, player, clickType)
        }
    }

    suspend fun setHologramVisible(visible: Boolean) {
        if (hologramVisible == visible) {
            return
        }

        withContext(VirtualsCtx) {
            if (visible) {
                hologram = createHologram()
            } else {
                hologram?.destroy()
                hologram = null
            }
            hologramVisible = visible
        }
    }

    suspend fun setInteractablesVisible(visible: Boolean) {
        if (interactablesVisible == visible) {
            return
        }

        withContext(VirtualsCtx) {
            if (visible) {
                interactables = createInteractables().also { created ->
                    created.forEach { it.register() }
                }
            } else {
                interactables.forEach { it.destroy() }
                interactables = emptyList()
            }
            interactablesVisible = visible
        }
    }

    fun destroy() {
        CrateHandler.removeHandle(this)
        VirtualsCtx {
            hologram?.destroy()
        }
        interactables.forEach { it.destroy() }
    }
}
