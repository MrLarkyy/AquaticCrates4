package gg.aquatic.crates.stats

import gg.aquatic.crates.stats.table.AllTimeCrateStatsTable
import gg.aquatic.crates.stats.table.AllTimeRewardStatsTable
import gg.aquatic.crates.stats.table.CrateOpeningRewardsTable
import gg.aquatic.crates.stats.table.CrateOpeningsTable
import gg.aquatic.crates.stats.table.HourlyCrateStatsTable
import gg.aquatic.crates.stats.table.HourlyRewardStatsTable
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.sum
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.select

internal fun JdbcTransaction.queryCrateOpens(crateId: String, timeframe: CrateStatsTimeframe): Long {
    return when (timeframe) {
        CrateStatsTimeframe.ALL_TIME -> {
            AllTimeCrateStatsTable
                .select(AllTimeCrateStatsTable.opens)
                .where { AllTimeCrateStatsTable.crateId eq crateId }
                .singleOrNull()
                ?.get(AllTimeCrateStatsTable.opens)
                ?: 0L
        }

        else -> exactRollingOpenCount(crateId, System.currentTimeMillis() - (timeframe.windowMillis ?: 0L))
    }
}

internal fun JdbcTransaction.queryRewardStats(crateId: String, rewardId: String, timeframe: CrateStatsTimeframe): RewardStatsSnapshot {
    return when (timeframe) {
        CrateStatsTimeframe.ALL_TIME -> {
            AllTimeRewardStatsTable
                .select(AllTimeRewardStatsTable.wins, AllTimeRewardStatsTable.amountSum)
                .where {
                    (AllTimeRewardStatsTable.crateId eq crateId) and
                        (AllTimeRewardStatsTable.rewardId eq rewardId)
                }
                .singleOrNull()
                ?.let {
                    RewardStatsSnapshot(
                        wins = it[AllTimeRewardStatsTable.wins],
                        amountSum = it[AllTimeRewardStatsTable.amountSum]
                    )
                }
                ?: RewardStatsSnapshot(0L, 0L)
        }

        else -> exactRollingRewardStats(crateId, rewardId, System.currentTimeMillis() - (timeframe.windowMillis ?: 0L))
    }
}

internal fun JdbcTransaction.exactRollingOpenCount(crateId: String, startMillis: Long): Long {
    val currentHour = CrateStats.truncateHour(System.currentTimeMillis())
    val firstFullHour = CrateStats.truncateHour(startMillis) + CrateStats.HOUR_MILLIS
    val rawCountExpr = CrateOpeningsTable.openCount.sum()

    val rawCount = CrateOpeningsTable
        .select(rawCountExpr)
        .where {
            (CrateOpeningsTable.crateId eq crateId) and
                (CrateOpeningsTable.openedAtMillis greaterEq startMillis) and
                (CrateOpeningsTable.openedAtMillis less firstFullHour)
        }
        .singleOrNull()
        ?.get(rawCountExpr)
        ?: 0L

    val hourlyCount = if (firstFullHour <= currentHour) {
        val sumExpr = HourlyCrateStatsTable.opens.sum()
        HourlyCrateStatsTable
            .select(sumExpr)
            .where {
                (HourlyCrateStatsTable.crateId eq crateId) and
                    (HourlyCrateStatsTable.bucketHourMillis greaterEq firstFullHour) and
                    (HourlyCrateStatsTable.bucketHourMillis less currentHour + CrateStats.HOUR_MILLIS)
            }
            .singleOrNull()
            ?.get(sumExpr)
            ?: 0L
    } else {
        0L
    }

    return rawCount + hourlyCount
}

internal fun JdbcTransaction.exactRollingRewardStats(crateId: String, rewardId: String, startMillis: Long): RewardStatsSnapshot {
    val currentHour = CrateStats.truncateHour(System.currentTimeMillis())
    val firstFullHour = CrateStats.truncateHour(startMillis) + CrateStats.HOUR_MILLIS

    val rawWinsExpr = CrateOpeningRewardsTable.winCount.sum()
    val rawAmountExpr = CrateOpeningRewardsTable.amount.sum()
    val rawRow = CrateOpeningRewardsTable
        .select(rawWinsExpr, rawAmountExpr)
        .where {
            (CrateOpeningRewardsTable.crateId eq crateId) and
                (CrateOpeningRewardsTable.rewardId eq rewardId) and
                (CrateOpeningRewardsTable.wonAtMillis greaterEq startMillis) and
                (CrateOpeningRewardsTable.wonAtMillis less firstFullHour)
        }
        .singleOrNull()

    val rawStats = RewardStatsSnapshot(
        wins = rawRow?.get(rawWinsExpr) ?: 0L,
        amountSum = rawRow?.get(rawAmountExpr)?.toLong() ?: 0L
    )

    val hourlyStats = if (firstFullHour <= currentHour) {
        val winsExpr = HourlyRewardStatsTable.wins.sum()
        val amountExpr = HourlyRewardStatsTable.amountSum.sum()
        val row = HourlyRewardStatsTable
            .select(winsExpr, amountExpr)
            .where {
                (HourlyRewardStatsTable.crateId eq crateId) and
                    (HourlyRewardStatsTable.rewardId eq rewardId) and
                    (HourlyRewardStatsTable.bucketHourMillis greaterEq firstFullHour) and
                    (HourlyRewardStatsTable.bucketHourMillis less currentHour + CrateStats.HOUR_MILLIS)
            }
            .singleOrNull()

        RewardStatsSnapshot(
            wins = row?.get(winsExpr) ?: 0L,
            amountSum = row?.get(amountExpr) ?: 0L
        )
    } else {
        RewardStatsSnapshot(0L, 0L)
    }

    return RewardStatsSnapshot(
        wins = rawStats.wins + hourlyStats.wins,
        amountSum = rawStats.amountSum + hourlyStats.amountSum
    )
}

internal fun timeframeCondition(column: org.jetbrains.exposed.v1.core.Column<Long>, timeframe: CrateStatsTimeframe): Op<Boolean> {
    val windowMillis = timeframe.windowMillis ?: return Op.TRUE
    return column greaterEq System.currentTimeMillis() - windowMillis
}
