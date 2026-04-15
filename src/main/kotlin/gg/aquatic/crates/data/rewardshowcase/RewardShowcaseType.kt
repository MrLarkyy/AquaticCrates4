package gg.aquatic.crates.data.rewardshowcase

enum class RewardShowcaseType(val id: String) {
    ITEM_DISPLAY("item-display"),
    MODEL_ENGINE("model-engine"),
    BETTER_MODEL("better-model");

    companion object {
        val entries = listOf(ITEM_DISPLAY, MODEL_ENGINE, BETTER_MODEL)

        fun of(raw: String): RewardShowcaseType {
            return entries.firstOrNull { it.id.equals(raw, ignoreCase = true) } ?: ITEM_DISPLAY
        }
    }
}
