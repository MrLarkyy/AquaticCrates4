package gg.aquatic.crates.editor

import gg.aquatic.crates.editor.value.EditorValue
import gg.aquatic.crates.editor.value.ListEditorValue
import gg.aquatic.crates.editor.value.SimpleEditorValue
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class Configurable {
    private val _editorValues = mutableListOf<EditorValue<*>>()

    /**
     * Returns all registered values in the order they were defined.
     */
    fun getEditorValues(): List<EditorValue<*>> = _editorValues

    /**
     * Unified DSL for any simple value.
     * You define the icon, click logic, and serialization in one place.
     */
    protected fun <T> edit(
        key: String,
        initial: T,
        serializer: ValueSerializer<T>,
        icon: (T) -> ItemStack,
        handler: EditorClickHandler<T>,
        visibleIf: () -> Boolean = { true }
    ): SimpleEditorValue<T> {
        return SimpleEditorValue(
            key,
            initial,
            icon,
            handler,
            { it },
            serializer,
            visibleIf
        ).also { _editorValues.add(it) }
    }

    /**
     * Unified DSL for Lists.
     * It handles the wrapping of elements automatically.
     */
    protected fun <T> editList(
        key: String,
        initial: List<T> = emptyList(),
        elementFactory: (Any) -> EditorValue<T>,
        listIcon: (List<EditorValue<T>>) -> ItemStack,
        openGui: (Player, ListEditorValue<T>, () -> Unit) -> Unit,
        visibleIf: () -> Boolean = { true }
    ): ListEditorValue<T> {
        val wrappedInitial = initial.map { elementFactory(it) }.toMutableList()
        return ListEditorValue(
            key,
            wrappedInitial,
            iconFactory = listIcon,
            openListGui = openGui,
            visibleIf = visibleIf
        ).also { _editorValues.add(it) }
    }

    fun serialize(section: ConfigurationSection) {
        getEditorValues().forEach { it.save(section) }
    }

    fun deserialize(section: ConfigurationSection) {
        getEditorValues().forEach { it.load(section) }
    }
}