package gg.aquatic.crates.stats.placeholder

import gg.aquatic.replace.PlaceholderDSLNode
import gg.aquatic.treepapi.PlaceholderNode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

internal fun PlaceholderNode.configurePapiStatsPlaceholderTree(
    crateOpensHandler: (String, String) -> String,
    rewardMetricHandler: (String, String, String, String) -> String,
    latestCrateHandler: (String, Int, String) -> String,
    latestPlayerHandler: (OfflinePlayer, Int, String) -> String,
    latestPlayerNamedHandler: (String, Int, String) -> String,
) {
    configureStatsPlaceholderBranches(
        root = this,
        group = { name, block -> name(block) },
        stringArg = { name, block -> stringArgument(name, block) },
        intArg = { name, block -> intArgument(name, block) },
        crateOpensValue = { crateOpensHandler(string("crateId").orEmpty(), string("timeframe").orEmpty()) },
        rewardMetricValue = {
            rewardMetricHandler(
                string("crateId").orEmpty(),
                string("rewardId").orEmpty(),
                string("timeframe").orEmpty(),
                string("metric").orEmpty()
            )
        },
        latestCrateValue = {
            latestCrateHandler(
                string("crateId").orEmpty(),
                getOrNull<Int>("index") ?: 0,
                string("field").orEmpty()
            )
        },
        latestPlayerValue = {
            latestPlayerHandler(
                binder,
                getOrNull<Int>("index") ?: 0,
                string("field").orEmpty()
            )
        },
        latestPlayerNamedValue = {
            latestPlayerNamedHandler(
                string("playerName").orEmpty(),
                getOrNull<Int>("index") ?: 0,
                string("field").orEmpty()
            )
        },
        handleValue = { handle(it) }
    )
}

internal fun PlaceholderDSLNode<Player>.configureReplaceStatsPlaceholderTree(
    crateOpensHandler: (String, String) -> String,
    rewardMetricHandler: (String, String, String, String) -> String,
    latestCrateHandler: (String, Int, String) -> String,
    latestPlayerHandler: (Player, Int, String) -> String,
    latestPlayerNamedHandler: (String, Int, String) -> String,
) {
    configureStatsPlaceholderBranches(
        root = this,
        group = { name, block -> name(block) },
        stringArg = { name, block -> stringArgument(name, block) },
        intArg = { name, block -> intArgument(name, block) },
        crateOpensValue = { crateOpensHandler(string("crateId").orEmpty(), string("timeframe").orEmpty()) },
        rewardMetricValue = {
            rewardMetricHandler(
                string("crateId").orEmpty(),
                string("rewardId").orEmpty(),
                string("timeframe").orEmpty(),
                string("metric").orEmpty()
            )
        },
        latestCrateValue = {
            latestCrateHandler(
                string("crateId").orEmpty(),
                arg<Int>("index") ?: 0,
                string("field").orEmpty()
            )
        },
        latestPlayerValue = {
            latestPlayerHandler(
                binder,
                arg<Int>("index") ?: 0,
                string("field").orEmpty()
            )
        },
        latestPlayerNamedValue = {
            latestPlayerNamedHandler(
                string("playerName").orEmpty(),
                arg<Int>("index") ?: 0,
                string("field").orEmpty()
            )
        },
        handleValue = { handle(it) }
    )
}

private fun <N, C> configureStatsPlaceholderBranches(
    root: N,
    group: N.(String, N.() -> Unit) -> Unit,
    stringArg: N.(String, N.() -> Unit) -> Unit,
    intArg: N.(String, N.() -> Unit) -> Unit,
    crateOpensValue: C.() -> String,
    rewardMetricValue: C.() -> String,
    latestCrateValue: C.() -> String,
    latestPlayerValue: C.() -> String,
    latestPlayerNamedValue: C.() -> String,
    handleValue: N.(C.() -> String) -> Unit,
) {
    with(root) {
        fun N.configureOpenBranches() {
            group("opens") {
                stringArg("crateId") {
                    stringArg("timeframe") {
                        handleValue(crateOpensValue)
                    }
                }
            }

            group("reward") {
                stringArg("crateId") {
                    stringArg("rewardId") {
                        stringArg("timeframe") {
                            stringArg("metric") {
                                handleValue(rewardMetricValue)
                            }
                        }
                    }
                }
            }
        }

        group("stats") {
            configureOpenBranches()
        }
        configureOpenBranches()

        group("latest") {
            group("crate") {
                stringArg("crateId") {
                    intArg("index") {
                        stringArg("field") {
                            handleValue(latestCrateValue)
                        }
                    }
                }
            }

            group("player") {
                intArg("index") {
                    stringArg("field") {
                        handleValue(latestPlayerValue)
                    }
                }

                stringArg("playerName") {
                    intArg("index") {
                        stringArg("field") {
                            handleValue(latestPlayerNamedValue)
                        }
                    }
                }
            }
        }
    }
}
