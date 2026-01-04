package gg.aquatic.crates.editor.value

import gg.aquatic.crates.ClickType
import gg.aquatic.crates.editor.EditorClickHandler
import gg.aquatic.crates.editor.ValueSerializer
import gg.aquatic.crates.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ListEditorValue<T>(
    override val key: String,
    override var value: MutableList<EditorValue<T>>,
    val onAdd: (Player) -> EditorValue<T>?,
    private val iconFactory: (MutableList<EditorValue<T>>) -> ItemStack,
    private val openListGui: ListGuiHandler<T>,
    override val visibleIf: () -> Boolean = { true },
    override val defaultValue: MutableList<EditorValue<T>>? = null,
    private val elementFactory: (ConfigurationSection) -> EditorValue<T>
) : EditorValue<MutableList<EditorValue<T>>> {

    override val serializer: ValueSerializer<MutableList<EditorValue<T>>> = Serializer(elementFactory)

    override fun getDisplayItem(): ItemStack = iconFactory(value)

    override fun onClick(player: Player, clickType: ClickType, updateParent: () -> Unit) {
        openListGui.open(player, this, updateParent)
    }

    override fun clone(): ListEditorValue<T> {
        return ListEditorValue(
            key, value.map { it.clone() }.toMutableList(),
            onAdd, iconFactory, openListGui,
            visibleIf, defaultValue, elementFactory
        )
    }

    class Serializer<T>(
        private val elementFactory: (ConfigurationSection) -> EditorValue<T>
    ) : ValueSerializer<MutableList<EditorValue<T>>> {

        override fun serialize(section: ConfigurationSection, path: String, value: MutableList<EditorValue<T>>) {
            val list = value.map { editor ->
                val temp = MemoryConfiguration()
                editor.save(temp)

                val values = temp.getValues(false)
                when {
                    // Primitive value wrapped in our internal key
                    values.containsKey("__value") -> values["__value"]
                    // Complex object (Configurable)
                    values.isNotEmpty() -> values
                    // Fallback
                    else -> editor.value
                }
            }
            section.set(path, list)
        }

        override fun deserialize(section: ConfigurationSection, path: String): MutableList<EditorValue<T>> {
            val rawList = section.getList(path) ?: return mutableListOf()

            return rawList.map { obj ->
                val temp = MemoryConfiguration()
                if (obj is Map<*, *>) {
                    val elementSection = gg.aquatic.crates.createConfigurationSectionFromMap(obj)
                    elementFactory(elementSection).apply { load(elementSection) }
                } else {
                    // Wrap simple types in our internal key before passing to factory
                    temp.set("__value", obj)
                    elementFactory(temp).apply { load(temp) }
                }
            }.toMutableList()
        }
    }
}

fun interface ListGuiHandler<T> {
    fun open(player: Player, editor: ListEditorValue<T>, updateParent: () -> Unit)
}

data class ElementBehavior<T>(
    val icon: (T) -> ItemStack,
    val handler: EditorClickHandler<T>
)