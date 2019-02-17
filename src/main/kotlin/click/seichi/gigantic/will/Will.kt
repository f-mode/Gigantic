package click.seichi.gigantic.will

import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.extension.*
import click.seichi.gigantic.message.LocalizedText
import click.seichi.gigantic.message.messages.WillMessages
import click.seichi.gigantic.util.SideBarRow
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*

/**
 * @author unicroak
 * @author tar0ss
 */
enum class Will(
        // 変更不可
        val id: Int,
        val color: Color,
        val chatColor: ChatColor,
        val material: Material,
        val grade: WillGrade,
        private val localizedName: LocalizedText,
        val defaultRow: SideBarRow,
        val displayPriority: Int
) {

    /**
     * スポーン条件は以下のいずれかによって分けること
     * * バイオーム
     * * 気温
     * * 高度
     *
     */
    AQUA(
            1,
            Color.fromRGB(0, 0, 128),
            ChatColor.BLUE,
            Material.BLUE_GLAZED_TERRACOTTA,
            WillGrade.BASIC,
            WillMessages.AQUA,
            SideBarRow.TWO,
            2

    ) {
        // 高度が30以上62以下であり，かつ海，川等のバイオームであること
        override fun canSpawn(player: Player, block: Block): Boolean {
            val biome = block.biome ?: return false
            if (!biome.isOcean && !biome.isRiver) return false
            return block.y in 30..62
        }
    },
    IGNIS(
            2,
            Color.fromRGB(139, 0, 0),
            ChatColor.RED,
            Material.RED_GLAZED_TERRACOTTA,
            WillGrade.BASIC,
            WillMessages.IGNIS,
            SideBarRow.THREE,
            3
    ) {
        // 高度が29以下であること
        override fun canSpawn(player: Player, block: Block): Boolean {
            return block.y <= 29
        }
    },
    AER(
            3,
            Color.fromRGB(255, 255, 204),
            ChatColor.WHITE,
            Material.WHITE_GLAZED_TERRACOTTA,
            WillGrade.BASIC,
            WillMessages.AER,
            SideBarRow.FIVE,
            5
    ) {
        // 高度が85以上であること
        override fun canSpawn(player: Player, block: Block): Boolean {
            return block.y >= 85
        }
    },
    TERRA(
            4,
            Color.fromRGB(124, 83, 53),
            ChatColor.GOLD,
            Material.BROWN_GLAZED_TERRACOTTA,
            WillGrade.BASIC,
            WillMessages.TERRA,
            SideBarRow.ONE,
            1
    ) {
        // 高度が30以上62以下であり，かつ海，川等のバイオームではないこと
        override fun canSpawn(player: Player, block: Block): Boolean {
            val biome = block.biome ?: return false
            if (biome.isOcean || biome.isRiver) return false
            return block.y in 30..62
        }
    },
    NATURA(
            5,
            Color.fromRGB(0, 255, 0),
            ChatColor.DARK_GREEN,
            Material.LIME_GLAZED_TERRACOTTA,
            WillGrade.BASIC,
            WillMessages.NATURA,
            SideBarRow.FOUR,
            4
    ) {
        // 高度が63以上84以下であること
        override fun canSpawn(player: Player, block: Block): Boolean {
            return block.y in 63..84
        }
    },
    GLACIES(
            6,
            Color.fromRGB(127, 255, 255),
            ChatColor.AQUA,
            Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
            WillGrade.ADVANCED,
            WillMessages.GLACIES,
            SideBarRow.SIX,
            6
    ) {
        // 温度が0以下であること
        override fun canSpawn(player: Player, block: Block): Boolean {
            return block.temperature <= 0
        }
    },
    LUX(
            7,
            Color.fromRGB(255, 234, 0),
            ChatColor.YELLOW,
            Material.YELLOW_GLAZED_TERRACOTTA,
            WillGrade.ADVANCED,
            WillMessages.LUX,
            SideBarRow.NINE,
            9
    ) {
        // 温度が1.2以上であること
        override fun canSpawn(player: Player, block: Block): Boolean {
            return block.temperature >= 1.2
        }
    },
    SOLUM(
            8,
            Color.fromRGB(80, 73, 70),
            ChatColor.GRAY,
            Material.GRAY_GLAZED_TERRACOTTA,
            WillGrade.ADVANCED,
            WillMessages.SOLUM,
            SideBarRow.SEVEN,
            7
    ) {
        // 山岳バイオームであること
        override fun canSpawn(player: Player, block: Block): Boolean {
            val biome = block.biome ?: return false
            return biome.isMountain
        }
    },
    UMBRA(
            9,
            Color.fromRGB(148, 0, 211),
            ChatColor.DARK_PURPLE,
            Material.PURPLE_GLAZED_TERRACOTTA,
            WillGrade.ADVANCED,
            WillMessages.UMBRA,
            SideBarRow.TEN,
            10
    ) {
        // 森林バイオームであること
        override fun canSpawn(player: Player, block: Block): Boolean {
            val biome = block.biome ?: return false
            return biome.isForest
        }
    },
    VENTUS(
            10,
            Color.fromRGB(224, 193, 255),
            ChatColor.LIGHT_PURPLE,
            Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
            WillGrade.ADVANCED,
            WillMessages.VENTUS,
            SideBarRow.EIGHT,
            8
    ) {
        // 丘陵
        override fun canSpawn(player: Player, block: Block): Boolean {
            val biome = block.biome ?: return false
            return biome.isHill
        }
    },
    ;

    abstract fun canSpawn(player: Player, block: Block): Boolean

    fun addEthel(player: Player, amount: Long) = player.transform(Keys.ETHEL_MAP.getValue(this)) { it + amount }

    override fun toString(): String = name.toLowerCase()

    fun getName(locale: Locale) = localizedName.asSafety(locale)

    companion object {
        private val idMap = values().map { it.id to it }.toMap()

        fun findById(id: Int) = idMap[id]
    }
}