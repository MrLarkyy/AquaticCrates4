package gg.aquatic.crates.data.provider

enum class RewardProviderType(val id: String) {
    SIMPLE("simple"),
    CONDITIONAL_POOLS("conditional-pools");

    companion object {
        val entries = listOf(SIMPLE, CONDITIONAL_POOLS)

        fun of(raw: String): RewardProviderType {
            return entries.firstOrNull { it.id.equals(raw, true) } ?: SIMPLE
        }

        fun defaultData(type: String): RewardProviderData {
            return when (of(type)) {
                SIMPLE -> SimpleRewardProviderData()
                CONDITIONAL_POOLS -> ConditionalPoolsRewardProviderData()
            }
        }
    }
}
