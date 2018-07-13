package click.seichi.gigantic.event.events

import click.seichi.gigantic.event.CustomEvent
import org.bukkit.entity.Player

/**
 * @author tar0ss
 */
class LevelUpEvent(
        val level: Int,
        val player: Player
) : CustomEvent()