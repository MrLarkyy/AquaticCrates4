package gg.aquatic.crates.data.processor

enum class RewardProcessorType(val id: String) {
    BASIC("basic"),
    CHOOSE("choose"),
    WORLD_CHOOSE("world-choose");

    companion object {
        val entries = listOf(BASIC, CHOOSE, WORLD_CHOOSE)

        fun of(raw: String): RewardProcessorType {
            return entries.firstOrNull { it.id.equals(raw, true) } ?: BASIC
        }

        fun defaultData(type: String): RewardProcessorData {
            return when (of(type)) {
                BASIC -> BasicRewardProcessorData()
                CHOOSE -> ChooseRewardProcessorData()
                WORLD_CHOOSE -> WorldChooseRewardProcessorData()
            }
        }
    }
}
