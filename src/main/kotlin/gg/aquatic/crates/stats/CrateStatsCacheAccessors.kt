package gg.aquatic.crates.stats

import java.util.UUID

internal suspend fun CrateStats.getCrateOpensInternal(crateId: String, timeframe: CrateStatsTimeframe): Long {
    if (!ready) return 0L
    return dbQuery { queryCrateOpens(crateId, timeframe) }
}

internal suspend fun CrateStats.getRewardStatsInternal(crateId: String, rewardId: String, timeframe: CrateStatsTimeframe): RewardStatsSnapshot {
    if (!ready) return RewardStatsSnapshot(0L, 0L)
    return dbQuery { queryRewardStats(crateId, rewardId, timeframe) }
}

internal suspend fun CrateStats.getPlayerCrateOpensInternal(playerUuid: UUID, crateId: String, timeframe: CrateStatsTimeframe): Long {
    if (!ready) return 0L
    if (timeframe == CrateStatsTimeframe.ALL_TIME) {
        val key = playerCrateKey(playerUuid, crateId)
        playerAllTimeOpenCache[key]?.let { return it }
    }

    val value = dbQuery { queryPlayerCrateOpens(playerUuid, crateId, timeframe) }
    if (timeframe == CrateStatsTimeframe.ALL_TIME) {
        playerAllTimeOpenCache[playerCrateKey(playerUuid, crateId)] = value
    }
    return value
}

internal suspend fun CrateStats.getPlayerRewardWinsInternal(playerUuid: UUID, crateId: String, rewardId: String, timeframe: CrateStatsTimeframe): Long {
    if (!ready) return 0L
    if (timeframe == CrateStatsTimeframe.ALL_TIME) {
        val key = playerRewardKey(playerUuid, crateId, rewardId)
        playerAllTimeRewardWinCache[key]?.let { return it }
    }

    val value = dbQuery { queryPlayerRewardWins(playerUuid, crateId, rewardId, timeframe) }
    if (timeframe == CrateStatsTimeframe.ALL_TIME) {
        playerAllTimeRewardWinCache[playerRewardKey(playerUuid, crateId, rewardId)] = value
    }
    return value
}

internal fun CrateStats.getCrateOpensCachedInternal(crateId: String, timeframe: CrateStatsTimeframe): Long {
    if (!ready) return 0L
    val key = "crate:$crateId:${timeframe.name}"
    return getCachedValue(key) { dbQuerySync { queryCrateOpens(crateId, timeframe) } } as Long
}

internal fun CrateStats.getRewardStatsCachedInternal(crateId: String, rewardId: String, timeframe: CrateStatsTimeframe): RewardStatsSnapshot {
    if (!ready) return RewardStatsSnapshot(0L, 0L)
    val key = "reward:$crateId:$rewardId:${timeframe.name}"
    return getCachedValue(key) { dbQuerySync { queryRewardStats(crateId, rewardId, timeframe) } } as RewardStatsSnapshot
}

internal fun CrateStats.getLatestCrateRewardsCachedInternal(crateId: String, limit: Int): List<LatestRewardSnapshot> {
    if (!ready) return emptyList()
    val safeLimit = limit.coerceIn(1, 100)
    val key = "latest:crate:$crateId:$safeLimit"
    @Suppress("UNCHECKED_CAST")
    return getCachedValue(key) { dbQuerySync { queryLatestRewards(crateId = crateId, playerUuid = null, limit = safeLimit) } } as List<LatestRewardSnapshot>
}

internal fun CrateStats.getLatestPlayerRewardsCachedInternal(playerUuid: UUID, limit: Int): List<LatestRewardSnapshot> {
    if (!ready) return emptyList()
    val safeLimit = limit.coerceIn(1, 100)
    val key = "latest:player:$playerUuid:$safeLimit"
    @Suppress("UNCHECKED_CAST")
    return getCachedValue(key) { dbQuerySync { queryLatestRewards(crateId = null, playerUuid = playerUuid, limit = safeLimit) } } as List<LatestRewardSnapshot>
}
