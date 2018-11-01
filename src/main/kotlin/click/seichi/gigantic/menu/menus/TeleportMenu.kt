package click.seichi.gigantic.menu.menus

import click.seichi.gigantic.button.buttons.MenuButtons
import click.seichi.gigantic.extension.wrappedLocale
import click.seichi.gigantic.menu.Menu
import click.seichi.gigantic.message.messages.MenuMessages
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * @author tar0ss
 */
object TeleportMenu : Menu() {

    override val size: Int
        get() = 9

    override fun getTitle(player: Player): String {
        return "${ChatColor.BLACK}" +
                MenuMessages.TELEPORT_TITLE.asSafety(player.wrappedLocale)
    }

    init {
        registerButton(0, MenuButtons.TELEPORT_TO_PLAYER)
        registerButton(8, MenuButtons.TELEPORT_TOGGLE)
    }

}