package gg.aquatic.crates.data.editor.core

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlNode
import gg.aquatic.kmenu.inventory.ButtonType
import gg.aquatic.waves.serialization.editor.SerializableEditor
import gg.aquatic.waves.serialization.editor.meta.ConfigurableFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EditorFieldAdapter
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.EditorSchema
import gg.aquatic.waves.serialization.editor.meta.FieldEditResult
import kotlinx.serialization.KSerializer
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

abstract class PassThroughSectionFieldAdapter : EditorFieldAdapter {

    protected open fun acceptsButton(buttonType: ButtonType): Boolean = buttonType == ButtonType.LEFT

    final override suspend fun edit(
        player: Player,
        context: EditorFieldContext,
        buttonType: ButtonType
    ): FieldEditResult {
        return if (acceptsButton(buttonType)) {
            FieldEditResult.PassThrough
        } else {
            FieldEditResult.NoChange
        }
    }
}

abstract class RootSectionFieldAdapter<T : Any>(
    private val serializer: KSerializer<T>,
    private val yaml: Yaml,
    private val schema: EditorSchema<T>,
    private val title: Component,
) : EditorFieldAdapter {

    protected abstract fun loadSection(context: EditorFieldContext): T?
    protected abstract fun defaultSectionValue(): T
    protected abstract fun updateRoot(context: EditorFieldContext, root: YamlNode, edited: T): YamlNode

    protected open fun acceptsButton(buttonType: ButtonType): Boolean = buttonType == ButtonType.LEFT

    override suspend fun edit(
        player: Player,
        context: EditorFieldContext,
        buttonType: ButtonType
    ): FieldEditResult {
        if (!acceptsButton(buttonType)) {
            return FieldEditResult.NoChange
        }

        val edited = SerializableEditor.editValueInActiveContext(
            player = player,
            title = title,
            serializer = serializer,
            yaml = yaml,
            schema = schema,
            loadFresh = { loadSection(context) ?: defaultSectionValue() }
        ) ?: return FieldEditResult.NoChange

        return FieldEditResult.UpdatedRoot(updateRoot(context, context.root, edited))
    }
}

abstract class ValueSectionFieldAdapter<T : Any>(
    private val serializer: KSerializer<T>,
    private val yaml: Yaml,
    private val schema: EditorSchema<T>,
    private val title: Component,
) : EditorFieldAdapter {

    protected abstract fun loadSection(context: EditorFieldContext): T?
    protected abstract fun defaultSectionValue(): T
    protected abstract fun updateValue(edited: T): YamlNode

    protected open fun acceptsButton(buttonType: ButtonType): Boolean = buttonType == ButtonType.LEFT

    protected suspend fun editSectionValue(player: Player, context: EditorFieldContext): FieldEditResult {
        val edited = SerializableEditor.editValueInActiveContext(
            player = player,
            title = title,
            serializer = serializer,
            yaml = yaml,
            schema = schema,
            loadFresh = { loadSection(context) ?: defaultSectionValue() }
        ) ?: return FieldEditResult.NoChange

        return FieldEditResult.Updated(updateValue(edited))
    }

    override suspend fun edit(
        player: Player,
        context: EditorFieldContext,
        buttonType: ButtonType
    ): FieldEditResult {
        if (!acceptsButton(buttonType)) {
            return FieldEditResult.NoChange
        }

        return editSectionValue(player, context)
    }
}

abstract class ConfigurableValueSectionFieldAdapter<C : Any, T : Any>(
    private val serializer: KSerializer<T>,
    private val yaml: Yaml,
    private val schema: EditorSchema<T>,
) : ConfigurableFieldAdapter<C> {

    protected abstract fun title(context: EditorFieldContext, config: C): Component
    protected abstract fun loadSection(context: EditorFieldContext, config: C): T?
    protected abstract fun defaultSectionValue(config: C): T
    protected abstract fun updateRoot(root: YamlNode, edited: T, config: C): YamlNode

    protected open fun acceptsButton(buttonType: ButtonType, config: C): Boolean = buttonType == ButtonType.LEFT

    protected suspend fun editSectionValue(
        player: Player,
        context: EditorFieldContext,
        config: C,
    ): FieldEditResult {
        val edited = SerializableEditor.editValueInActiveContext(
            player = player,
            title = title(context, config),
            serializer = serializer,
            yaml = yaml,
            schema = schema,
            loadFresh = { loadSection(context, config) ?: defaultSectionValue(config) }
        ) ?: return FieldEditResult.NoChange

        return FieldEditResult.UpdatedRoot(updateRoot(context.root, edited, config))
    }

    override suspend fun edit(
        player: Player,
        context: EditorFieldContext,
        config: C,
        buttonType: ButtonType
    ): FieldEditResult {
        if (!acceptsButton(buttonType, config)) {
            return FieldEditResult.NoChange
        }

        return editSectionValue(player, context, config)
    }
}
