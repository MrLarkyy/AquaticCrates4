package gg.aquatic.crates.stats

import gg.aquatic.crates.stats.table.CrateOpeningRewardsTable
import gg.aquatic.crates.stats.table.CrateOpeningsTable
import java.util.UUID
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.sum
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.select

internal fun JdbcTransaction.queryPlayerCrateOpens(playerUuid: UUID, crateId: String, timeframe: CrateStatsTimeframe): Long {
    val countExpr = CrateOpeningsTable.openCount.sum()
    return CrateOpeningsTable
        .select(countExpr)
        .where {
            (CrateOpeningsTable.playerUuid eq playerUuid.toString()) and
                (CrateOpeningsTable.crateId eq crateId) and
                timeframeCondition(CrateOpeningsTable.openedAtMillis, timeframe)
        }
        .singleOrNull()
        ?.get(countExpr)
        ?: 0L
}

internal fun JdbcTransaction.queryPlayerRewardWins(playerUuid: UUID, crateId: String, rewardId: String, timeframe: CrateStatsTimeframe): Long {
    val countExpr = CrateOpeningRewardsTable.winCount.sum()
    return CrateOpeningRewardsTable
        .select(countExpr)
        .where {
            (CrateOpeningRewardsTable.playerUuid eq playerUuid.toString()) and
                (CrateOpeningRewardsTable.crateId eq crateId) and
                (CrateOpeningRewardsTable.rewardId eq rewardId) and
                timeframeCondition(CrateOpeningRewardsTable.wonAtMillis, timeframe)
        }
        .singleOrNull()
        ?.get(countExpr)
        ?: 0L
}

internal fun JdbcTransaction.queryLatestRewards(crateId: String?, playerUuid: UUID?, limit: Int): List<LatestRewardSnapshot> {
    require(crateId != null || playerUuid != null) { "crateId or playerUuid must be provided." }

    val conditions = buildList<Op<Boolean>> {
        if (crateId != null) add(CrateOpeningRewardsTable.crateId eq crateId)
        if (playerUuid != null) add(CrateOpeningRewardsTable.playerUuid eq playerUuid.toString())
    }.reduce { acc, op -> acc and op }

    return CrateOpeningRewardsTable
        .select(
            CrateOpeningRewardsTable.playerUuid,
            CrateOpeningRewardsTable.crateId,
            CrateOpeningRewardsTable.rewardId,
            CrateOpeningRewardsTable.rarityId,
            CrateOpeningRewardsTable.amount,
            CrateOpeningRewardsTable.wonAtMillis
        )
        .where { conditions }
        .orderBy(
            CrateOpeningRewardsTable.wonAtMillis to SortOrder.DESC,
            CrateOpeningRewardsTable.id to SortOrder.DESC
        )
        .limit(limit)
        .map {
            LatestRewardSnapshot(
                playerUuid = UUID.fromString(it[CrateOpeningRewardsTable.playerUuid]),
                crateId = it[CrateOpeningRewardsTable.crateId],
                rewardId = it[CrateOpeningRewardsTable.rewardId],
                rarityId = it[CrateOpeningRewardsTable.rarityId],
                amount = it[CrateOpeningRewardsTable.amount],
                wonAtMillis = it[CrateOpeningRewardsTable.wonAtMillis]
            )
        }
}
