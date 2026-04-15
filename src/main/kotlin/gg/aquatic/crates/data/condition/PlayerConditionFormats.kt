package gg.aquatic.crates.data.condition

import com.charleskorn.kaml.YamlNode
import gg.aquatic.crates.data.editor.polymorphic.PolymorphicTypeDefinition
import gg.aquatic.crates.data.editor.polymorphic.PolymorphicTypeRegistry
import gg.aquatic.crates.data.editor.polymorphic.createPolymorphicYaml
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bukkit.Material

@OptIn(ExperimentalSerializationApi::class)
object PlayerConditionFormats {
    val module = SerializersModule {
        polymorphic(PlayerConditionData::class) {
            subclass(AvailableRewardsPlayerConditionData::class)
            subclass(BiomePlayerConditionData::class)
            subclass(DateRangePlayerConditionData::class)
            subclass(DayOfMonthPlayerConditionData::class)
            subclass(DayOfWeekPlayerConditionData::class)
            subclass(HasEmptyInventorySlotPlayerConditionData::class)
            subclass(MonthPlayerConditionData::class)
            subclass(OnlinePlayerCountPlayerConditionData::class)
            subclass(PermissionPlayerConditionData::class)
            subclass(TimeRangePlayerConditionData::class)
            subclass(WeekParityPlayerConditionData::class)
            subclass(WeekOfYearModuloPlayerConditionData::class)
            subclass(WorldBlacklistPlayerConditionData::class)
            subclass(WorldPlayerConditionData::class)
        }
    }

    val yaml = createPolymorphicYaml(module)
}

object PlayerConditionTypes {
    private val registry = PolymorphicTypeRegistry(
        PlayerConditionData::class.java,
        PlayerConditionFormats.yaml,
        listOf(
        PolymorphicTypeDefinition(
            id = "available-rewards",
            displayName = "Available Rewards",
            description = listOf(
                "Checks whether at least a minimum amount",
                "of rewards can currently be won."
            ),
            icon = Material.CHEST,
            factory = { AvailableRewardsPlayerConditionData() },
            descriptorFactory = { AvailableRewardsPlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "biome",
            displayName = "Biome",
            description = listOf(
                "Checks whether the player is inside",
                "one of the selected biomes."
            ),
            icon = Material.MOSS_BLOCK,
            factory = { BiomePlayerConditionData() },
            descriptorFactory = { BiomePlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "date-range",
            displayName = "Date Range",
            description = listOf(
                "Checks whether today is inside",
                "the configured date range."
            ),
            icon = Material.CLOCK,
            factory = { DateRangePlayerConditionData() },
            descriptorFactory = { DateRangePlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "day-of-month",
            displayName = "Day Of Month",
            description = listOf(
                "Checks whether today's day of month",
                "matches one of the configured values."
            ),
            icon = Material.CLOCK,
            factory = { DayOfMonthPlayerConditionData() },
            descriptorFactory = { DayOfMonthPlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "day-of-week",
            displayName = "Day Of Week",
            description = listOf(
                "Checks whether today is one",
                "of the selected weekdays."
            ),
            icon = Material.CLOCK,
            factory = { DayOfWeekPlayerConditionData() },
            descriptorFactory = { DayOfWeekPlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "month",
            displayName = "Month",
            description = listOf(
                "Checks whether the current month",
                "matches one of the configured values."
            ),
            icon = Material.CLOCK,
            factory = { MonthPlayerConditionData() },
            descriptorFactory = { MonthPlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "has-empty-inventory-slot",
            displayName = "Has Empty Inventory Slot",
            description = listOf(
                "Checks whether the player has enough",
                "empty inventory slots."
            ),
            icon = Material.HOPPER,
            factory = { HasEmptyInventorySlotPlayerConditionData() },
            descriptorFactory = { HasEmptyInventorySlotPlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "online-player-count",
            displayName = "Online Player Count",
            description = listOf(
                "Checks whether the number of online players",
                "is inside the configured range."
            ),
            icon = Material.PLAYER_HEAD,
            factory = { OnlinePlayerCountPlayerConditionData() },
            descriptorFactory = { OnlinePlayerCountPlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "permission",
            displayName = "Permission",
            description = listOf(
                "Requires the player to have",
                "a specific permission node."
            ),
            icon = Material.TRIPWIRE_HOOK,
            factory = { PermissionPlayerConditionData() },
            descriptorFactory = { PermissionPlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "week-of-year-mod",
            displayName = "Week Of Year Mod",
            description = listOf(
                "Checks ISO week number modulo a value.",
                "Useful for rotating weekly reward pools."
            ),
            icon = Material.CLOCK,
            factory = { WeekOfYearModuloPlayerConditionData() },
            descriptorFactory = { WeekOfYearModuloPlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "week-parity",
            displayName = "Week Parity",
            description = listOf(
                "Checks whether the current ISO week",
                "is odd or even."
            ),
            icon = Material.COMPARATOR,
            factory = { WeekParityPlayerConditionData() },
            descriptorFactory = { WeekParityPlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "time-range",
            displayName = "Time Range",
            description = listOf(
                "Checks whether the current local time",
                "is inside the configured range."
            ),
            icon = Material.CLOCK,
            factory = { TimeRangePlayerConditionData() },
            descriptorFactory = { TimeRangePlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "world-blacklist",
            displayName = "World Blacklist",
            description = listOf(
                "Blocks opening this crate in",
                "the selected worlds."
            ),
            icon = Material.BARRIER,
            factory = { WorldBlacklistPlayerConditionData() },
            descriptorFactory = { WorldBlacklistPlayerConditionData.serializer().descriptor }
        ),
        PolymorphicTypeDefinition(
            id = "world",
            displayName = "World",
            description = listOf(
                "Checks whether the player is in",
                "one of the selected worlds."
            ),
            icon = Material.GRASS_BLOCK,
            factory = { WorldPlayerConditionData() },
            descriptorFactory = { WorldPlayerConditionData.serializer().descriptor }
        ),
        )
    )

    val definitions get() = registry.selectionDefinitions()
    fun definition(id: String) = registry.definition(id)
    fun create(id: String): PlayerConditionData? = registry.create(id)
    fun defaultElement(id: String): YamlNode? = registry.defaultElement(id)
    fun descriptor(id: String): SerialDescriptor? = registry.descriptor(id)
}
