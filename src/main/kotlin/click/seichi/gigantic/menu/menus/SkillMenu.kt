package click.seichi.gigantic.menu.menus

import click.seichi.gigantic.button.buttons.menu.SkillButtons
import click.seichi.gigantic.extension.wrappedLocale
import click.seichi.gigantic.menu.Menu
import click.seichi.gigantic.message.messages.menu.SkillMenuMessages
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * @author tar0ss
 */
object SkillMenu : Menu() {

    override val size: Int
        get() = 9

    override fun getTitle(player: Player): String {
        return "${ChatColor.BLACK}" +
                SkillMenuMessages.TITLE.asSafety(player.wrappedLocale)
    }

    init {
        registerButton(0, SkillButtons.HEAL)
        registerButton(1, SkillButtons.FLASH)
        registerButton(2, SkillButtons.MINE_BURST)
    }

}