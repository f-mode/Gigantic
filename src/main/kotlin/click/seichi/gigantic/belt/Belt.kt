package click.seichi.gigantic.belt

import click.seichi.gigantic.item.Armor
import click.seichi.gigantic.item.Button
import click.seichi.gigantic.item.HandItem
import click.seichi.gigantic.item.HotButton
import click.seichi.gigantic.item.items.Armors
import click.seichi.gigantic.item.items.HandItems
import click.seichi.gigantic.item.items.HotButtons
import click.seichi.gigantic.message.LocalizedText
import click.seichi.gigantic.message.messages.BeltMessages
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author tar0ss
 */
enum class Belt(
        val id: Int,
        val localizedName: LocalizedText,
        mainHandItem: Pair<Int, HandItem>,
        val offHandItem: HandItem?,
        val helmet: Armor?,
        val chestPlate: Armor?,
        val leggings: Armor?,
        val boots: Armor?,
        vararg hotButtons: Pair<Int, HotButton>
) {
    DIG(
            1,
            BeltMessages.DIG,
            0 to HandItems.SHOVEL,
            HandItems.MANA_STONE,
            Armors.HELMET,
            Armors.ELYTRA,
            Armors.LEGGINGS,
            Armors.BOOTS,
            1 to HotButtons.FLASH,
            2 to HotButtons.MINE_BURST,
            7 to HotButtons.TELEPORT_DOOR,
            8 to HotButtons.BELT_SWITCHER_SETTING
    ),
    MINE(
            2,
            BeltMessages.MINE,
            0 to HandItems.PICKEL,
            HandItems.MANA_STONE,
            Armors.HELMET,
            Armors.ELYTRA,
            Armors.LEGGINGS,
            Armors.BOOTS,
            1 to HotButtons.FLASH,
            2 to HotButtons.MINE_BURST,
            7 to HotButtons.TELEPORT_DOOR,
            8 to HotButtons.BELT_SWITCHER_SETTING
    ),
    CUT(
            3,
            BeltMessages.CUT,
            0 to HandItems.AXE,
            HandItems.MANA_STONE,
            Armors.HELMET,
            Armors.ELYTRA,
            Armors.LEGGINGS,
            Armors.BOOTS,
            1 to HotButtons.FLASH,
            2 to HotButtons.MINE_BURST,
            7 to HotButtons.TELEPORT_DOOR,
            8 to HotButtons.BELT_SWITCHER_SETTING
    ),
    ;

    companion object {
        private val idMap = values().map { it.id to it }.toMap()
        fun findById(id: Int) = idMap[id]
    }

    // 手に固定されたスロット番号
    private var fixedSlot = mainHandItem.first

    private val buttonMap: MutableMap<Int, Button> = mutableMapOf(
            *hotButtons,
            mainHandItem
    )

    /**
     * ベルトを身に着ける
     *
     * @param applyMainHandItem 固定しているアイテムも更新するかどうか
     * @param applyOffHandItem オフハンドも更新するかどうか
     */
    fun wear(player: Player, applyMainHandItem: Boolean = true, applyOffHandItem: Boolean = true) {
        player.inventory?.let { inv ->
            inv.heldItemSlot = fixedSlot
            (0..8).forEach { slot ->
                if (!applyMainHandItem && slot == fixedSlot) return@forEach
                inv.setItem(slot,
                        buttonMap[slot]?.getItemStack(player) ?: ItemStack(Material.AIR)
                )
            }
            inv.helmet = helmet?.getItemStack(player) ?: ItemStack(Material.AIR)
            inv.chestplate = chestPlate?.getItemStack(player) ?: ItemStack(Material.AIR)
            inv.leggings = leggings?.getItemStack(player) ?: ItemStack(Material.AIR)
            inv.boots = boots?.getItemStack(player) ?: ItemStack(Material.AIR)

            if (!applyOffHandItem) return@let
            inv.itemInOffHand = offHandItem?.getItemStack(player) ?: ItemStack(Material.AIR)
        }
        if (applyMainHandItem)
            player.updateInventory()
    }

    fun getHotButton(slot: Int): HotButton? {
        if (isFixed(slot)) return null
        return buttonMap[slot] as HotButton?
    }

    fun findFixedButton(): HandItem? {
        return buttonMap[fixedSlot] as HandItem?
    }

    fun getButton(slot: Int): Button? {
        return if (isFixed(slot)) findFixedButton()
        else getHotButton(slot)
    }

    fun isFixed(slot: Int) = when {
        fixedSlot != slot -> false
        buttonMap[fixedSlot] == null -> false
        else -> true
    }

    fun hasFixedSlot() = buttonMap[fixedSlot] != null

    fun getFixedSlot(): Int? {
        buttonMap[fixedSlot] ?: return null
        return fixedSlot
    }

}