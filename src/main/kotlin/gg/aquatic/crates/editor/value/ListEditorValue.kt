package gg.aquatic.crates.editor.value

import gg.aquatic.crates.ClickType
import gg.aquatic.crates.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ListEditorValue<T>(
    override val key: String,
    override var value: MutableList<EditorValue<T>>,
    val elementFactory: (section: ConfigurationSection) -> EditorValue<T>,
    val onAdd: (Player) -> T?,
    private val iconFactory: (MutableList<EditorValue<T>>) -> ItemStack,
    private val openListGui: (Player, ListEditorValue<T>, updateParent: () -> Unit) -> Unit,
    override val visibleIf: () -> Boolean = { true },
    override val defaultValue: MutableList<EditorValue<T>>? = null
) : EditorValue<MutableList<EditorValue<T>>> {

    override val serializer: gg.aquatic.crates.editor.ValueSerializer<MutableList<EditorValue<T>>> =
        Serializer(elementFactory)

    override fun getDisplayItem(): ItemStack = iconFactory(value)

    override fun onClick(player: Player, clickType: ClickType, updateParent: () -> Unit) {
        openListGui(player, this, updateParent)
    }

    override fun clone(): ListEditorValue<T> {
        return ListEditorValue(
            key, value.map { it.clone() }.toMutableList(),
            elementFactory, onAdd, iconFactory, openListGui,
            visibleIf, defaultValue
        )
    }

    class Serializer<T>(
        private val elementFactory: (section: ConfigurationSection) -> EditorValue<T>
    ) : gg.aquatic.crates.editor.ValueSerializer<MutableList<EditorValue<T>>> {

        override fun serialize(section: ConfigurationSection, path: String, value: MutableList<EditorValue<T>>) {
            val tempSection = MemoryConfiguration()
            value.forEachIndexed { index, editor ->
                editor.serializer.serialize(tempSection, index.toString(), editor.value)
            }
            section.set(path, tempSection.getValues(false).values.toList())
        }

        override fun deserialize(section: ConfigurationSection, path: String): MutableList<EditorValue<T>> {
            val rawList = section.getSectionList(path)
            return rawList.map { elementFactory(it) }.toMutableList()
        }
    }
}