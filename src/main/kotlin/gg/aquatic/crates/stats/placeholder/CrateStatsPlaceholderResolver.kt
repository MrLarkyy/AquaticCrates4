package gg.aquatic.crates.stats.placeholder

import gg.aquatic.crates.crate.CrateHandler
import gg.aquatic.crates.debug.CratesDebug
import gg.aquatic.crates.debug.CratesLogCategory
import gg.aquatic.crates.stats.CrateStats
import gg.aquatic.crates.stats.CrateStatsTimeframe
import gg.aquatic.crates.stats.LatestRewardSnapshot
import gg.aquatic.crates.stats.RewardStatsSnapshot
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.time.Instant
import java.time.format.DateTimeFormatter

internal object CrateStatsPlaceholderResolver {
    fun crateOpens(crateId: String, timeframeRaw: String): String {
        val timeframe = parseTimeframe(timeframeRaw) ?: return "0"
        if (!CrateStats.ready) {
            CratesDebug.log(CratesLogCategory.STATS, 1, "Stats placeholder opens crate='$crateId' timeframe='${timeframe.name}' -> stats unavailable")
            return "0"
        }
        val result = CrateStats.getCrateOpensCached(crateId, timeframe).toString()
        CratesDebug.log(CratesLogCategory.STATS, 1, "Stats placeholder opens crate='$crateId' timeframe='${timeframe.name}' -> $result")
        return result
    }

    fun rewardMetric(crateId: String, rewardId: String, timeframeRaw: String, metricRaw: String): String {
        val timeframe = parseTimeframe(timeframeRaw) ?: return "0"
        val metric = parseRewardMetric(metricRaw) ?: return "0"
        if (!CrateStats.ready) {
            CratesDebug.log(
                CratesLogCategory.STATS,
                1,
                "Stats placeholder reward crate='$crateId' reward='$rewardId' timeframe='${timeframe.name}' metric='${metric.name}' -> stats unavailable"
            )
            return "0"
        }
        val snapshot = CrateStats.getRewardStatsCached(crateId, rewardId, timeframe)
        val result = metric.read(snapshot)
        CratesDebug.log(
            CratesLogCategory.STATS,
            1,
            "Stats placeholder reward crate='$crateId' reward='$rewardId' timeframe='${timeframe.name}' metric='${metric.name}' -> $result"
        )
        return result
    }

    fun latestCrate(crateId: String, indexRaw: Int, fieldRaw: String, formatter: DateTimeFormatter): String {
        return latest(
            sourceDescription = "crate='$crateId'",
            indexRaw = indexRaw,
            fieldRaw = fieldRaw,
            formatter = formatter,
            rewards = { CrateStats.getLatestCrateRewardsCached(crateId) }
        )
    }

    fun latestPlayer(player: OfflinePlayer, indexRaw: Int, fieldRaw: String, formatter: DateTimeFormatter): String {
        val playerLabel = player.name ?: player.uniqueId.toString()
        return latest(
            sourceDescription = "player='$playerLabel'",
            indexRaw = indexRaw,
            fieldRaw = fieldRaw,
            formatter = formatter,
            rewards = { CrateStats.getLatestPlayerRewardsCached(player.uniqueId) }
        )
    }

    fun latestPlayerByName(playerName: String, indexRaw: Int, fieldRaw: String, formatter: DateTimeFormatter): String {
        val offlinePlayer = Bukkit.getOfflinePlayer(playerName)
        return latest(
            sourceDescription = "playerName='$playerName'",
            indexRaw = indexRaw,
            fieldRaw = fieldRaw,
            formatter = formatter,
            rewards = { CrateStats.getLatestPlayerRewardsCached(offlinePlayer.uniqueId) }
        )
    }

    private fun latest(
        sourceDescription: String,
        indexRaw: Int,
        fieldRaw: String,
        formatter: DateTimeFormatter,
        rewards: () -> List<LatestRewardSnapshot>,
    ): String {
        if (!CrateStats.ready) {
            CratesDebug.log(CratesLogCategory.STATS, 1, "Stats placeholder latest $sourceDescription index='$indexRaw' field='$fieldRaw' -> stats unavailable")
            return ""
        }

        val latestReward = rewards().getOrNull(indexRaw - 1) ?: return ""
        val result = resolveLatestRewardField(latestReward, fieldRaw, formatter)
        CratesDebug.log(CratesLogCategory.STATS, 1, "Stats placeholder latest $sourceDescription index='$indexRaw' field='$fieldRaw' -> $result")
        return result
    }

    private fun parseTimeframe(raw: String): CrateStatsTimeframe? {
        return when (raw.lowercase()) {
            "day", "daily", "24h" -> CrateStatsTimeframe.DAY
            "week", "weekly", "7d" -> CrateStatsTimeframe.WEEK
            "month", "monthly", "30d" -> CrateStatsTimeframe.MONTH
            "alltime", "all-time", "all", "total" -> CrateStatsTimeframe.ALL_TIME
            else -> null
        }
    }

    private fun parseRewardMetric(raw: String): RewardMetric? {
        return when (raw.lowercase()) {
            "wins", "count" -> RewardMetric.WINS
            "amount", "amountsum", "amount-sum", "sum" -> RewardMetric.AMOUNT
            else -> null
        }
    }

    private fun resolveLatestRewardField(
        snapshot: LatestRewardSnapshot,
        fieldRaw: String,
        formatter: DateTimeFormatter,
    ): String {
        val crate = CrateHandler.crates[snapshot.crateId]
        val reward = crate?.rewardProvider?.allRewards()?.firstOrNull { it.id == snapshot.rewardId }

        return when (fieldRaw.lowercase()) {
            "crate", "crateid", "crate_id" -> snapshot.crateId
            "cratename", "crate_name" -> crate?.displayName?.let(PlainTextComponentSerializer.plainText()::serialize) ?: snapshot.crateId
            "reward", "rewardid", "reward_id" -> snapshot.rewardId
            "rewardname", "reward_name" -> reward?.displayName?.let(PlainTextComponentSerializer.plainText()::serialize) ?: snapshot.rewardId
            "rarity", "rarityid", "rarity_id" -> snapshot.rarityId.orEmpty()
            "rarityname", "rarity_name" -> reward?.rarity?.displayName?.let(PlainTextComponentSerializer.plainText()::serialize)
                ?: snapshot.rarityId.orEmpty()
            "amount" -> snapshot.amount.toString()
            "timestamp", "time" -> snapshot.wonAtMillis.toString()
            "formattedtime", "formatted_time", "date" -> formatter.format(Instant.ofEpochMilli(snapshot.wonAtMillis))
            "player", "playeruuid", "player_uuid" -> snapshot.playerUuid.toString()
            else -> ""
        }
    }

    private enum class RewardMetric {
        WINS {
            override fun read(snapshot: RewardStatsSnapshot): String = snapshot.wins.toString()
        },
        AMOUNT {
            override fun read(snapshot: RewardStatsSnapshot): String = snapshot.amountSum.toString()
        };

        abstract fun read(snapshot: RewardStatsSnapshot): String
    }
}
