package gg.aquatic.crates.input

import gg.aquatic.kregistry.FrozenRegistry
import gg.aquatic.kregistry.Registry
import gg.aquatic.kregistry.RegistryId
import gg.aquatic.kregistry.RegistryKey
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

interface Input {

    val awaiting: Map<Player, AwaitingInput>

    fun forceCancel(player: Player) {
        awaiting[player]?.handle?.forceCancel(player)
    }

    companion object {
        val REGISTRY_KEY = RegistryKey<String, Input>(RegistryId("aquatic","input"))

        val REGISTRY: FrozenRegistry<String, Input>
            get() {
                return Registry[REGISTRY_KEY]
            }
    }
}

interface InputHandle {
    val input: Input
    fun await(player: Player): CompletableFuture<String?>
    fun forceCancel(player: Player)

}

class AwaitingInput(
    val player: Player,
    val future: CompletableFuture<String?>,
    val handle: InputHandle
)