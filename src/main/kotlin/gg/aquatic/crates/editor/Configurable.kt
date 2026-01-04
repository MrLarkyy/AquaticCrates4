package gg.aquatic.crates.editor

import gg.aquatic.crates.ClickType
import gg.aquatic.crates.editor.value.EditorValue
import gg.aquatic.crates.editor.value.ElementBehavior
import gg.aquatic.crates.editor.value.ListEditorValue
import gg.aquatic.crates.editor.value.ListGuiHandler
import gg.aquatic.crates.editor.value.SimpleEditorValue
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class Configurable<A: Configurable<A>> {
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
     * Unified DSL for simple Lists (e.g., List<String>, List<Component>).
     * It automatically wraps simple types into EditorValues.
     */
    protected fun <T> editList(
        key: String,
        initial: List<T> = emptyList(),
        serializer: ValueSerializer<T>,
        behavior: ElementBehavior<T>,
        onAdd: (Player) -> T?,
        listIcon: (List<EditorValue<T>>) -> ItemStack,
        guiHandler: ListGuiHandler<T>,
        visibleIf: () -> Boolean = { true }
    ): ListEditorValue<T> {
        val wrapSimple: (T) -> SimpleEditorValue<T> = { valData ->
            SimpleEditorValue("__value", valData, behavior.icon, behavior.handler, { it }, serializer)
        }

        val elementFactory: (ConfigurationSection) -> EditorValue<T> = { section ->
            val initialValue = if (section.contains("__value")) {
                serializer.deserialize(section, "__value")
            } else {
                initial.firstOrNull() ?: throw IllegalStateException("Missing default for $key")
            }
            wrapSimple(initialValue)
        }

        return ListEditorValue(
            key, initial.map(wrapSimple).toMutableList(),
            onAdd = { player -> onAdd(player)?.let(wrapSimple) },
            iconFactory = listIcon, openListGui = guiHandler,
            visibleIf = visibleIf, elementFactory = elementFactory
        ).also { _editorValues.add(it) }
    }

    /**
     * Unified DSL for Lists.
     * It handles the wrapping of elements automatically.
     */
    protected fun <T> editList(
        key: String,
        elementFactory: (ConfigurationSection) -> EditorValue<T>,
        onAdd: (Player) -> EditorValue<T>?,
        listIcon: (List<EditorValue<T>>) -> ItemStack,
        guiHandler: ListGuiHandler<T>,
        visibleIf: () -> Boolean = { true }
    ): ListEditorValue<T> {
        return ListEditorValue(
            key, mutableListOf(), onAdd, listIcon, guiHandler,
            visibleIf, elementFactory = elementFactory
        ).also { _editorValues.add(it) }
    }

    fun serialize(section: ConfigurationSection) {
        getEditorValues().forEach { it.save(section) }
    }

    fun deserialize(section: ConfigurationSection) {
        getEditorValues().forEach { it.load(section) }
    }

    @Suppress("UNCHECKED_CAST")
    fun asEditorValue(
        key: String,
        icon: (A) -> ItemStack,
        onClick: (Player, ClickType, () -> Unit) -> Unit
    ): EditorValue<A> {
        val outer = this as A
        return object : EditorValue<A> {
            override val key: String = key
            override var value: A = outer
            override val visibleIf: () -> Boolean = { true }
            override val defaultValue: A? = null

            override val serializer = object : ValueSerializer<A> {
                override fun serialize(section: ConfigurationSection, path: String, value: A) {
                    val sub = section.getConfigurationSection(path) ?: section.createSection(path)
                    value.serialize(sub)
                }

                override fun deserialize(section: ConfigurationSection, path: String): A {
                    val sub = section.getConfigurationSection(path) ?: MemoryConfiguration()
                    outer.deserialize(sub)
                    return outer
                }
            }

            override fun getDisplayItem(): ItemStack = icon(outer)
            override fun onClick(player: Player, clickType: ClickType, updateParent: () -> Unit) =
                onClick(player, clickType, updateParent)

            override fun clone(): EditorValue<A> = (outer.copy() as A).asEditorValue(key, icon, onClick)

            override fun save(section: ConfigurationSection) {
                outer.serialize(section)
            }

            override fun load(section: ConfigurationSection) {
                outer.deserialize(section)
            }
        }
    }

    /**
     * Requirement: Configurables must be able to deep-copy themselves for the editor's "cancel" logic.
     */
    abstract fun copy(): A


}