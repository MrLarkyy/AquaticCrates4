package gg.aquatic.crates

import gg.aquatic.common.Config
import gg.aquatic.common.coroutine.VirtualsCtx
import gg.aquatic.crates.await.awaitStartupDependencies
import gg.aquatic.crates.command.initializeCommands
import gg.aquatic.crates.crate.CrateHandler
import gg.aquatic.crates.crate.opening.worldchoose.WorldChooseRuntime
import gg.aquatic.crates.data.CrateStorage
import gg.aquatic.crates.debug.CratesLogger
import gg.aquatic.crates.interact.DestroyCrateClickAction
import gg.aquatic.crates.interact.OpenCrateClickAction
import gg.aquatic.crates.interact.PreviewCrateClickAction
import gg.aquatic.execute.Action
import gg.aquatic.crates.stats.CrateStats
import gg.aquatic.crates.stats.placeholder.CrateStatsPlaceholders
import gg.aquatic.kregistry.bootstrap.RegistryHolder
import gg.aquatic.kurrency.Currency
import gg.aquatic.kurrency.impl.VirtualCurrency
import gg.aquatic.stacked.StackedItem
import gg.aquatic.stacked.register
import gg.aquatic.waves.Waves
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin

object CratesPlugin : JavaPlugin(), RegistryHolder {
    var debugLevel: Int = 0
        private set
    private lateinit var pluginConfig: Config

    override fun onLoad() {
        registryBootstrap(Waves) {
            pre {
                reloadLoadedCrates()
            }

            registry(StackedItem.ITEM_REGISTRY_KEY) {
                for ((id, crate) in CrateHandler.crates) {
                    crate.crateItem.register(this, "acrates_chest", id) { e ->
                        crate.handleCrateItemInteractions(e)
                    }
                    crate.keyStackedItem.register(this, "acrates_key", id) { e ->
                        crate.handleKeyItemInteractions(e)
                    }
                }
            }

            registry(VirtualCurrency.REGISTRY_KEY) {
                for (crate in CrateHandler.crates.values) {
                    add(crate.keyVirtualCurrency.id, crate.keyVirtualCurrency)
                }
            }

            registry(Currency.REGISTRY_KEY) {
                for (crate in CrateHandler.crates.values) {
                    add(crate.keyCurrency.id, crate.keyCurrency)
                }
            }

            registry(Action.REGISTRY_KEY) {
                add("preview", PreviewCrateClickAction)
                add("open", OpenCrateClickAction)
                add("destroy", DestroyCrateClickAction)
            }
        }
    }

    override fun onEnable() {
        pluginConfig = Config("config.yml", this).also { it.loadSync() }
        saveResource("messages.yml", false)
        configurePluginState()
        CratesLogger.info("Crate stats configured=${CrateStats.configured}, ready=${CrateStats.ready}")
        CrateStatsPlaceholders.register()
        WorldChooseRuntime.initialize()

        initializeCommands()

        VirtualsCtx {
            Messages.load()
            awaitStartupDependencies()
            CrateHandler.loadPlacedCrates()
        }
    }

    override fun onDisable() {
        runBlocking(VirtualsCtx) {
            CrateHandler.shutdown()
        }
        CrateStats.shutdown()
    }

    suspend fun reload() {
        pluginConfig.load()
        configurePluginState()
        Messages.load()
        CrateHandler.reloadPlacedCrates {
            reloadLoadedCrates()
            Waves.rebuildRegistries(this)
        }
    }

    private fun configurePluginState() {
        debugLevel = pluginConfig.configuration.getInt("debug.level", 0).coerceAtLeast(0)
        CrateStats.initialize(pluginConfig.configuration)
        CrateStatsPlaceholders.configure(pluginConfig.configuration)
    }

    private fun reloadLoadedCrates() {
        CrateHandler.crates.clear()
        CrateHandler.crates.putAll(CrateStorage.loadAllCrates())
    }
}
