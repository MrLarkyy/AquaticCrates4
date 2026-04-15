package gg.aquatic.crates.data

import gg.aquatic.crates.data.action.RewardActionTypes
import gg.aquatic.crates.data.condition.PlayerConditionTypes
import gg.aquatic.crates.data.editor.core.mapValue
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.hologram.CrateHologramLineTypes
import gg.aquatic.crates.data.interaction.CrateClickActionTypes
import gg.aquatic.crates.data.interactable.CrateInteractableTypes
import gg.aquatic.crates.data.price.OpenPriceTypes
import gg.aquatic.crates.data.processor.BasicRewardProcessorData
import gg.aquatic.crates.data.processor.ChooseRewardProcessorData
import gg.aquatic.crates.data.processor.GlowWorldRewardFocusEffectData
import gg.aquatic.crates.data.processor.RewardProcessorType
import gg.aquatic.crates.data.processor.ScaleWorldRewardFocusEffectData
import gg.aquatic.crates.data.processor.VerticalOffsetWorldRewardFocusEffectData
import gg.aquatic.crates.data.processor.WorldChooseRewardProcessorData
import gg.aquatic.crates.data.provider.ConditionalPoolsRewardProviderData
import gg.aquatic.crates.data.provider.RewardProviderType
import gg.aquatic.crates.data.provider.SimpleRewardProviderData
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import kotlinx.serialization.descriptors.SerialDescriptor

internal fun resolveCrateDataDescriptor(context: EditorFieldContext) = when {
    context.pathSegments.contains("focusEffects") ->
        context.currentSubtypeId()?.let(::worldRewardFocusEffectDescriptor)
    context.pathSegments.contains("winActions")
            || context.pathSegments.contains("clickActions")
            || context.pathSegments.contains("onStartActions")
            || context.pathSegments.contains("onSwitchActions")
            || context.pathSegments.contains("onChooseActions")
            || context.pathSegments.contains("onEndActions") ->
        context.currentSubtypeId()?.let(RewardActionTypes::descriptor)
    context.pathSegments.contains("rewardProvider") ->
        context.currentSubtypeId()?.let(::rewardProviderDescriptor)
    context.pathSegments.contains("rewardProcessor") ->
        context.currentSubtypeId()?.let(::rewardProcessorDescriptor)
    context.isHologramLineContext() ->
        context.currentSubtypeId()?.let(CrateHologramLineTypes::descriptor)
    context.pathSegments.contains("interactables") ->
        context.currentSubtypeId()?.let(CrateInteractableTypes::descriptor)
    (context.pathSegments.contains("priceGroups") || context.pathSegments.contains("cost")) && context.pathSegments.contains("prices") ->
        context.currentSubtypeId()?.let(OpenPriceTypes::descriptor)
    context.pathSegments.any { it == "crateClickMapping" || it == "keyClickMapping" } ->
        context.currentSubtypeId()?.let(CrateClickActionTypes::descriptor)
    context.pathSegments.contains("openConditions") || context.pathSegments.contains("conditions") ->
        context.currentSubtypeId()?.let(PlayerConditionTypes::descriptor)
    else -> null
}

private fun EditorFieldContext.currentSubtypeId(): String? {
    return value.mapValue("type")?.stringContentOrNull
}

private fun EditorFieldContext.isHologramLineContext(): Boolean {
    if (pathSegments.contains("hologram")) return true
    if (descriptor.serialName.contains("CrateHologramLineData")) return true
    if (pathSegments.contains("lines")) return true
    return pathSegments.contains("frames") && pathSegments.contains("line")
}

internal fun EditorFieldContext.isRewardProviderType(type: RewardProviderType): Boolean {
    val current = value
        .mapValue("type")
        ?.stringContentOrNull
    if (current != null) {
        return current.equals(type.id, true)
    }

    val rootType = root
        .mapValue("rewardProvider")
        ?.mapValue("type")
        ?.stringContentOrNull

    return (rootType ?: RewardProviderType.SIMPLE.id).equals(type.id, true)
}

internal fun EditorFieldContext.isRewardProcessorType(type: RewardProcessorType): Boolean {
    val current = value
        .mapValue("type")
        ?.stringContentOrNull
    if (current != null) {
        return current.equals(type.id, true)
    }

    val rootType = root
        .mapValue("rewardProcessor")
        ?.mapValue("type")
        ?.stringContentOrNull

    return (rootType ?: RewardProcessorType.BASIC.id).equals(type.id, true)
}

private fun rewardProviderDescriptor(id: String): SerialDescriptor? = when (RewardProviderType.of(id)) {
    RewardProviderType.SIMPLE -> SimpleRewardProviderData.serializer().descriptor
    RewardProviderType.CONDITIONAL_POOLS -> ConditionalPoolsRewardProviderData.serializer().descriptor
}

private fun rewardProcessorDescriptor(id: String): SerialDescriptor? = when (RewardProcessorType.of(id)) {
    RewardProcessorType.BASIC -> BasicRewardProcessorData.serializer().descriptor
    RewardProcessorType.CHOOSE -> ChooseRewardProcessorData.serializer().descriptor
    RewardProcessorType.WORLD_CHOOSE -> WorldChooseRewardProcessorData.serializer().descriptor
}

private fun worldRewardFocusEffectDescriptor(id: String): SerialDescriptor? = when (id.lowercase()) {
    "scale" -> ScaleWorldRewardFocusEffectData.serializer().descriptor
    "glow" -> GlowWorldRewardFocusEffectData.serializer().descriptor
    "vertical-offset" -> VerticalOffsetWorldRewardFocusEffectData.serializer().descriptor
    else -> null
}
