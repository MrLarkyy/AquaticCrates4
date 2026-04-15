package gg.aquatic.crates.data

object CrateLegacyTypeMigration {

    private val replacements = linkedMapOf(
        "gg.aquatic.crates.data.rewardshowcase.ItemDisplayRewardShowcaseData" to "item-display",
        "gg.aquatic.crates.data.rewardshowcase.ModelEngineRewardShowcaseData" to "model-engine",
        "gg.aquatic.crates.data.rewardshowcase.BetterModelRewardShowcaseData" to "better-model",
        "gg.aquatic.crates.data.rewardshowcase.GlowRewardShowcaseFocusEffectData" to "glow",
        "gg.aquatic.crates.data.rewardshowcase.ScaleRewardShowcaseFocusEffectData" to "scale",
        "gg.aquatic.crates.data.processor.DelayAnimationPropData" to "delay",
        "gg.aquatic.crates.data.processor.CrateHologramVisibilityAnimationPropData" to "crate-hologram-visibility",
        "gg.aquatic.crates.data.processor.CrateInteractablesVisibilityAnimationPropData" to "crate-interactables-visibility",
        "gg.aquatic.crates.data.processor.ShowWorldRewardsAnimationPropData" to "show-world-rewards",
        "gg.aquatic.crates.data.processor.ClearWorldRewardsAnimationPropData" to "clear-world-rewards",
    )

    fun migrate(content: String): String {
        var migrated = content
        for ((legacy, current) in replacements) {
            migrated = migrated.replace(legacy, current)
        }
        migrated = migrated
            .replace("\"on-focus-actions\":", "\"on-switch-actions\":")
            .replace("\"on-select-actions\":", "\"on-choose-actions\":")
        return migrated
    }
}
