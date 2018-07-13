package click.seichi.gigantic.listener

import click.seichi.gigantic.extension.gPlayer
import click.seichi.gigantic.extension.isBeltSlot
import click.seichi.gigantic.menu.Menu
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

/**
 * @author tar0ss
 */
class Menu : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val gPlayer = player.gPlayer ?: return
        val holder = event.inventory.holder

        if (holder is Menu) {
            event.isCancelled = true
            if (event.clickedInventory == event.view.topInventory) {
                holder.getButton(event.slot)?.onClick(player, event)
            } else if (event.clickedInventory == event.view.bottomInventory) {
                if (event.isBeltSlot) {
                    gPlayer.belt.getHookedItem(event.slot)?.onClick(player, event)
                } else {
                    gPlayer.defaultInventory.getSlotItem(event.slot)?.onClick(player, event)
                }
            }
        } else if (player.inventory.holder == holder) {
            // Eで開くインベントリの場合
            event.isCancelled = true
            if (event.isBeltSlot) {
                gPlayer.belt.getHookedItem(event.slot)?.onClick(player, event)
            } else {
                gPlayer.defaultInventory.getSlotItem(event.slot)?.onClick(player, event)
            }
        }
    }

}