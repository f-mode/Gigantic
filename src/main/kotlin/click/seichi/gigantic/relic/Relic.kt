package click.seichi.gigantic.relic

import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.extension.getOrPut
import click.seichi.gigantic.extension.transform
import click.seichi.gigantic.head.Head
import click.seichi.gigantic.message.LocalizedText
import click.seichi.gigantic.message.messages.RelicMessages
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @author tar0ss
 */
enum class Relic(
        val id: Int,
        private val localizedName: LocalizedText,
        private val localizedLore: List<LocalizedText>?,
        val maxAmount: Long = Long.MAX_VALUE,
        private val icon: ItemStack = Head.RUBY_JEWELLERY.toItemStack()
) {
    CHICKEN_KING_CROWN(
            2,
            RelicMessages.CHICKEN_KING_CROWN,
            RelicMessages.CHICKEN_KING_CROWN_LORE,
            icon = Head.KING_CROWN.toItemStack()
    ),
    TURTLE_KING_CROWN(
            6,
            RelicMessages.TURTLE_KING_CROWN,
            RelicMessages.TURTLE_KING_CROWN_LORE,
            icon = Head.KING_CROWN.toItemStack()
    ),
    SPIDER_KING_CROWN(
            7,
            RelicMessages.SPIDER_KING_CROWN,
            RelicMessages.SPIDER_KING_CROWN_LORE,
            icon = Head.KING_CROWN.toItemStack()
    ),
    ZOMBIE_KING_CROWN(
            8,
            RelicMessages.ZOMBIE_KING_CROWN,
            RelicMessages.ZOMBIE_KING_CROWN_LORE,
            icon = Head.KING_CROWN.toItemStack()
    ),
    SKELETON_KING_CROWN(
            9,
            RelicMessages.SKELETON_KING_CROWN,
            RelicMessages.SKELETON_KING_CROWN_LORE,
            icon = Head.KING_CROWN.toItemStack()
    ),
    ORC_KING_CROWN(
            10,
            RelicMessages.ORC_KING_CROWN,
            RelicMessages.ORC_KING_CROWN_LORE,
            icon = Head.KING_CROWN.toItemStack()
    ),
    GHOST_KING_CROWN(
            11,
            RelicMessages.GHOST_KING_CROWN,
            RelicMessages.GHOST_KING_CROWN_LORE,
            icon = Head.KING_CROWN.toItemStack()
    ),
    CHIP_OF_WOOD(
            12,
            RelicMessages.CHIP_OF_WOOD,
            RelicMessages.CHIP_OF_WOOD_LORE,
            icon = ItemStack(Material.OAK_WOOD)
    ),
    MOISTENED_SLIME_BOLL(
            13,
            RelicMessages.MOISTENED_SLIME_BOLL,
            RelicMessages.MOISTENED_SLIME_BOLL_LORE,
            icon = ItemStack(Material.SLIME_BALL)
    ),
    FADING_ENDER_PEARL(
            14,
            RelicMessages.FADING_ENDER_PEARL,
            RelicMessages.FADING_ENDER_PEARL_LORE,
            icon = ItemStack(Material.ENDER_PEARL)
    ),
    SPELL_BOOK_EXPLOSION(100, RelicMessages.SPELL_BOOK_EXPLOSION, null, 1),
    GOLDEN_APPLE(150, RelicMessages.GOLDEN_APPLE, null),
    WILL_CRYSTAL_SAPPHIRE(200, RelicMessages.WILL_CRYSTAL_SAPPHIRE, null),
    WILL_CRYSTAL_RUBY(300, RelicMessages.WILL_CRYSTAL_RUBY, null),
    WILL_CRYSTAL_FLUORITE(400, RelicMessages.WILL_CRYSTAL_FLUORITE, null),
    WILL_CRYSTAL_ANDALUSITE(500, RelicMessages.WILL_CRYSTAL_ANDALUSITE, null),
    WILL_CRYSTAL_JADE(600, RelicMessages.WILL_CRYSTAL_JADE, null),
    ;

    companion object {
        fun getDroppedList(player: Player): List<Relic> {
            return values().filter { it.has(player) }
        }
    }

    fun getName(locale: Locale) = localizedName.asSafety(locale)

    fun getLore(locale: Locale) = localizedLore?.map { it.asSafety(locale) }

    fun getIcon() = icon.clone()

    fun has(player: Player) = getDroppedNum(player) > 0

    fun getDroppedNum(player: Player) = Keys.RELIC_MAP[this]?.let { player.getOrPut(it) } ?: 0L

    fun dropTo(player: Player) {
        player.transform(Keys.RELIC_MAP[this] ?: return) { if (it < maxAmount) it + 1 else maxAmount }
    }

}