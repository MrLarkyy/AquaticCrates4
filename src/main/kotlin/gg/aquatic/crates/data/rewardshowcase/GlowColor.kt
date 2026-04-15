package gg.aquatic.crates.data.rewardshowcase

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.Material

class GlowColor private constructor(
    val namedColor: NamedTextColor,
    val material: Material,
) {
    val id: String = NamedTextColor.NAMES.keyOrThrow(namedColor)
    val displayName: String = id
        .split('_')
        .joinToString(" ") { part -> part.replaceFirstChar(Char::uppercaseChar) }
    val color: Color = Color.fromRGB(namedColor.value())

    companion object {
        private val materialById = mapOf(
            "black" to Material.BLACK_DYE,
            "dark_blue" to Material.BLUE_DYE,
            "dark_green" to Material.GREEN_DYE,
            "dark_aqua" to Material.CYAN_DYE,
            "dark_red" to Material.RED_DYE,
            "dark_purple" to Material.PURPLE_DYE,
            "gold" to Material.ORANGE_DYE,
            "gray" to Material.GRAY_DYE,
            "dark_gray" to Material.LIGHT_GRAY_DYE,
            "blue" to Material.LIGHT_BLUE_DYE,
            "green" to Material.LIME_DYE,
            "aqua" to Material.LIGHT_BLUE_DYE,
            "red" to Material.RED_DYE,
            "light_purple" to Material.MAGENTA_DYE,
            "yellow" to Material.YELLOW_DYE,
            "white" to Material.WHITE_DYE,
        )

        private val orderedNamedColors = listOf(
            NamedTextColor.BLACK,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.DARK_GREEN,
            NamedTextColor.DARK_AQUA,
            NamedTextColor.DARK_RED,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.GOLD,
            NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY,
            NamedTextColor.BLUE,
            NamedTextColor.GREEN,
            NamedTextColor.AQUA,
            NamedTextColor.RED,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.YELLOW,
            NamedTextColor.WHITE,
        )

        val entries: List<GlowColor> = orderedNamedColors.map { namedColor ->
            val id = NamedTextColor.NAMES.keyOrThrow(namedColor)
            GlowColor(
                namedColor = namedColor,
                material = materialById[id] ?: Material.WHITE_DYE,
            )
        }

        private val byId = entries.associateBy { it.id }

        val WHITE: GlowColor = byId.getValue("white")

        fun of(raw: String?): GlowColor? {
            val normalized = raw
                ?.trim()
                ?.lowercase()
                ?.replace('-', '_')
                ?.replace(' ', '_')
                ?: return null
            return byId[normalized]
        }

        fun colorOf(raw: String?): Color? {
            return of(raw)?.color
        }
    }
}
