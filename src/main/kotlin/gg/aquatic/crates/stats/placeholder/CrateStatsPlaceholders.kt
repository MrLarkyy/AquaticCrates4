package gg.aquatic.crates.stats.placeholder

import gg.aquatic.crates.debug.CratesLogCategory
import gg.aquatic.crates.debug.CratesLogger
import gg.aquatic.replace.dslPlaceholder
import gg.aquatic.treepapi.papiPlaceholder
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object CrateStatsPlaceholders {
    private const val DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"

    @Volatile
    private var timestampFormatter: DateTimeFormatter = createTimestampFormatter(DEFAULT_TIMESTAMP_FORMAT)

    @Volatile
    private var registered = false

    fun configure(configuration: ConfigurationSection) {
        val pattern = configuration.getString("stats.timestamp-format", DEFAULT_TIMESTAMP_FORMAT).orEmpty()
        timestampFormatter = runCatching { createTimestampFormatter(pattern) }
            .getOrElse {
                CratesLogger.warning(
                    CratesLogCategory.STATS,
                    "Invalid stats.timestamp-format '$pattern'. Falling back to '$DEFAULT_TIMESTAMP_FORMAT'."
                )
                createTimestampFormatter(DEFAULT_TIMESTAMP_FORMAT)
            }
    }

    fun register() {
        if (registered) return

        registerReplacePlaceholders()
        registerPapiPlaceholders()
        registered = true
    }

    private fun registerReplacePlaceholders() {
        dslPlaceholder<Player>("acrates", isConst = false) {
            configureReplaceStatsPlaceholderTree(
                crateOpensHandler = CrateStatsPlaceholderResolver::crateOpens,
                rewardMetricHandler = CrateStatsPlaceholderResolver::rewardMetric,
                latestCrateHandler = { crateId, index, field ->
                    CrateStatsPlaceholderResolver.latestCrate(crateId, index, field, timestampFormatter)
                },
                latestPlayerHandler = { player, index, field ->
                    CrateStatsPlaceholderResolver.latestPlayer(player, index, field, timestampFormatter)
                },
                latestPlayerNamedHandler = { playerName, index, field ->
                    CrateStatsPlaceholderResolver.latestPlayerByName(playerName, index, field, timestampFormatter)
                }
            )
        }
    }

    private fun registerPapiPlaceholders() {
        papiPlaceholder("Aquatic", "acrates") {
            configurePapiStatsPlaceholderTree(
                crateOpensHandler = CrateStatsPlaceholderResolver::crateOpens,
                rewardMetricHandler = CrateStatsPlaceholderResolver::rewardMetric,
                latestCrateHandler = { crateId, index, field ->
                    CrateStatsPlaceholderResolver.latestCrate(crateId, index, field, timestampFormatter)
                },
                latestPlayerHandler = { player, index, field ->
                    CrateStatsPlaceholderResolver.latestPlayer(player, index, field, timestampFormatter)
                },
                latestPlayerNamedHandler = { playerName, index, field ->
                    CrateStatsPlaceholderResolver.latestPlayerByName(playerName, index, field, timestampFormatter)
                }
            )
        }
    }

    private fun createTimestampFormatter(pattern: String): DateTimeFormatter {
        return DateTimeFormatter.ofPattern(pattern.ifBlank { DEFAULT_TIMESTAMP_FORMAT })
            .withZone(ZoneId.systemDefault())
    }
}
