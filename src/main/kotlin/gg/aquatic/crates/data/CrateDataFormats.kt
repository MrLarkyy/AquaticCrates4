package gg.aquatic.crates.data

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlNamingStrategy
import gg.aquatic.crates.data.action.RewardActionFormats
import gg.aquatic.crates.data.condition.PlayerConditionFormats
import gg.aquatic.crates.data.hologram.CrateHologramLineFormats
import gg.aquatic.crates.data.interactable.CrateInteractableFormats
import gg.aquatic.crates.data.interaction.CrateClickActionFormats
import gg.aquatic.crates.data.price.OpenPriceFormats
import gg.aquatic.crates.data.processor.BasicRewardProcessorData
import gg.aquatic.crates.data.processor.ChooseRewardProcessorData
import gg.aquatic.crates.data.processor.GlowWorldRewardFocusEffectData
import gg.aquatic.crates.data.processor.RewardProcessorData
import gg.aquatic.crates.data.processor.ScaleWorldRewardFocusEffectData
import gg.aquatic.crates.data.processor.VerticalOffsetWorldRewardFocusEffectData
import gg.aquatic.crates.data.processor.WorldRewardFocusEffectData
import gg.aquatic.crates.data.processor.WorldChooseRewardProcessorData
import gg.aquatic.crates.data.provider.ConditionalPoolsRewardProviderData
import gg.aquatic.crates.data.provider.RewardProviderData
import gg.aquatic.crates.data.provider.SimpleRewardProviderData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
object CrateDataFormats {
    val module = SerializersModule {
        include(RewardActionFormats.module)
        include(PlayerConditionFormats.module)
        include(CrateHologramLineFormats.module)
        include(CrateInteractableFormats.module)
        include(CrateClickActionFormats.module)
        include(OpenPriceFormats.module)
        polymorphic(RewardProviderData::class) {
            subclass(SimpleRewardProviderData::class)
            subclass(ConditionalPoolsRewardProviderData::class)
        }
        polymorphic(RewardProcessorData::class) {
            subclass(BasicRewardProcessorData::class)
            subclass(ChooseRewardProcessorData::class)
            subclass(WorldChooseRewardProcessorData::class)
        }
        polymorphic(WorldRewardFocusEffectData::class) {
            subclass(ScaleWorldRewardFocusEffectData::class)
            subclass(GlowWorldRewardFocusEffectData::class)
            subclass(VerticalOffsetWorldRewardFocusEffectData::class)
        }
    }

    val json = Json {
        serializersModule = module
        classDiscriminator = "type"
        prettyPrint = true
        prettyPrintIndent = "  "
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    val yaml = Yaml(
        serializersModule = module,
        configuration = YamlConfiguration(
            encodeDefaults = true,
            strictMode = false,
            yamlNamingStrategy = YamlNamingStrategy.KebabCase,
            polymorphismStyle = PolymorphismStyle.Property,
            polymorphismPropertyName = "type"
        )
    )
}
