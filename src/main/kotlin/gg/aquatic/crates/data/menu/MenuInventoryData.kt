package gg.aquatic.crates.data.menu

import gg.aquatic.crates.data.action.editor.RewardActionSelectionMenu
import gg.aquatic.crates.data.action.editor.defineRewardActionEditor
import gg.aquatic.crates.data.action.RewardActionData
import gg.aquatic.crates.data.editor.InventoryTypeFieldAdapter
import gg.aquatic.crates.data.editor.core.mapValue
import gg.aquatic.crates.data.editor.core.stringContentOrNull
import gg.aquatic.crates.data.item.StackedItemData
import gg.aquatic.crates.data.item.StackedItemDataEditor
import gg.aquatic.execute.ActionHandle
import gg.aquatic.kmenu.inventory.AnvilInventoryType
import gg.aquatic.kmenu.inventory.InventoryType
import gg.aquatic.kmenu.privateMenu
import gg.aquatic.kmenu.menu.PrivateMenu
import gg.aquatic.kmenu.menu.component.Button
import gg.aquatic.replace.PlaceholderContext
import gg.aquatic.waves.serialization.editor.meta.EditorFieldContext
import gg.aquatic.waves.serialization.editor.meta.TypedNestedSchemaBuilder
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.entity.Player

@Serializable
data class MenuInventoryData(
    val type: String = "GENERIC9X3",
    val anvil: AnvilMenuInventoryData? = null,
) {
    fun toInventoryType(): InventoryType {
        return runCatching { InventoryType.valueOf(type.trim()) }
            .getOrDefault(InventoryType.GENERIC9X3)
    }

    fun toRuntimeSettings(): MenuInventoryRuntimeSettings {
        return MenuInventoryRuntimeSettings(
            inventoryType = toInventoryType(),
            anvil = anvil?.toRuntimeSettings()
        )
    }

    companion object {
        fun TypedNestedSchemaBuilder<MenuInventoryData>.defineEditor(
            typeLabel: String = "Inventory Type",
            anvilLabel: String = "Anvil Settings",
        ) {
            field(
                MenuInventoryData::type,
                adapter = InventoryTypeFieldAdapter,
                displayName = typeLabel,
                searchTags = listOf("inventory type", "menu type", "layout", "size", "rows"),
                iconMaterial = Material.CHEST,
                description = listOf("Menu layout used for this UI.")
            )
            field(
                MenuInventoryData::anvil,
                displayName = anvilLabel,
                searchTags = listOf("anvil", "anvil settings", "confirm", "rename", "anvil input"),
                iconMaterial = Material.ANVIL,
                description = listOf(
                    "Optional ANVIL-specific settings.",
                    "Use these to add confirm actions and a confirm button item for anvil menus.",
                    "The placeholder %anvil_input% is available inside confirm actions.",
                    "Anvil confirm uses slot 2, so avoid placing rewards or custom buttons there."
                ),
                visibleWhen = { it.currentInventoryType() == "ANVIL" }
            )
            optionalGroup(MenuInventoryData::anvil) {
                with(AnvilMenuInventoryData) {
                    defineEditor()
                }
            }
        }
    }
}

@Serializable
data class AnvilMenuInventoryData(
    val closeAfterConfirm: Boolean = true,
    val confirmItem: StackedItemData = StackedItemData(
        material = Material.LIME_DYE.name,
        displayName = "<green>Confirm"
    ),
    val confirmActions: List<@Polymorphic RewardActionData> = emptyList(),
) {
    fun toRuntimeSettings(): AnvilMenuRuntimeSettings {
        return AnvilMenuRuntimeSettings(
            closeAfterConfirm = closeAfterConfirm,
            confirmItem = confirmItem.asStacked().getItem(),
            confirmActions = confirmActions.map { it.toActionHandle() }
        )
    }

    companion object {
        fun TypedNestedSchemaBuilder<AnvilMenuInventoryData>.defineEditor() {
            field(
                AnvilMenuInventoryData::closeAfterConfirm,
                displayName = "Close After Confirm",
                searchTags = listOf("close", "close after confirm", "auto close", "confirm behavior"),
                prompt = "Enter true or false:",
                iconMaterial = Material.BARRIER,
                description = listOf("If enabled, the anvil menu closes after the confirm button is clicked.")
            )
            group(AnvilMenuInventoryData::confirmItem) {
        with(StackedItemDataEditor) {
                    defineBasicEditor(
                        materialLabel = "Confirm Material",
                        nameLabel = "Confirm Name",
                        namePrompt = "Enter confirm button display name:",
                        loreLabel = "Confirm Lore",
                        amountLabel = "Confirm Amount"
                    )
                }
            }
            list(
                AnvilMenuInventoryData::confirmActions,
                displayName = "Confirm Actions",
                searchTags = listOf("confirm actions", "anvil actions", "submit actions", "on confirm"),
                iconMaterial = Material.LIGHTNING_ROD,
                description = listOf(
                    "Actions executed when the player confirms the anvil menu.",
                    "Use %anvil_input% inside action text or commands to access the typed input."
                ),
                newValueFactory = RewardActionSelectionMenu.entryFactory
            ) {
                defineRewardActionEditor()
            }
        }
    }
}

data class MenuInventoryRuntimeSettings(
    val inventoryType: InventoryType,
    val anvil: AnvilMenuRuntimeSettings?,
)

data class AnvilMenuRuntimeSettings(
    val closeAfterConfirm: Boolean,
    val confirmItem: org.bukkit.inventory.ItemStack,
    val confirmActions: Collection<ActionHandle<Player>>,
) {
    suspend fun applyTo(
        menu: PrivateMenu,
        placeholderUpdater: (String) -> String = { it },
    ) {
        val inventoryType = menu.type as? AnvilInventoryType ?: return
        inventoryType.onRename { _, name, inventory ->
            inventory.anvilInput = name
        }

        val button = Button(
            id = "anvil-confirm",
            itemstack = confirmItem.clone(),
            slots = listOf(2),
            priority = 100,
            updateEvery = -1,
            textUpdater = PlaceholderContext.privateMenu()
        ) {
            val input = it.inventory.anvilInput
            if (confirmActions.isNotEmpty()) {
                confirmActions.forEach { action ->
                    action.execute(menu.player) { _, str ->
                        placeholderUpdater(str).replace("%anvil_input%", input)
                    }
                }
            }
            if (closeAfterConfirm) {
                menu.close()
            }
        }
        menu.addComponent(button)
    }
}

private fun EditorFieldContext.currentInventoryType(): String {
    val current = value.mapValue("type")?.stringContentOrNull
    if (current != null) {
        return current.uppercase()
    }

    val rootType = root.mapValue("inventory")
        ?.mapValue("type")
        ?.stringContentOrNull

    return (rootType ?: "GENERIC9X3").uppercase()
}

