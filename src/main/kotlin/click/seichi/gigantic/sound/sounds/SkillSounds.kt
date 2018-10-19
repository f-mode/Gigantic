package click.seichi.gigantic.sound.sounds

import click.seichi.gigantic.sound.DetailedSound
import click.seichi.gigantic.util.Random
import org.bukkit.Sound
import org.bukkit.SoundCategory

/**
 * @author tar0ss
 */
object SkillSounds {

    val MINE_BURST_ON_BREAK = { combo: Long ->
        DetailedSound(
                Sound.BLOCK_NOTE_BLOCK_CHIME,
                SoundCategory.BLOCKS,
                pitchLevel = (combo % 25).toInt(),
                volume = 0.3F
        )
    }


    val MINE_BURST_ON_FIRE = DetailedSound(
            Sound.BLOCK_ENCHANTMENT_TABLE_USE,
            SoundCategory.BLOCKS,
            pitch = 1.6F,
            volume = 0.3F
    )

    val FLASH_FIRE = DetailedSound(
            Sound.ENTITY_SNOWBALL_THROW,
            SoundCategory.BLOCKS,
            pitch = 4.0F,
            volume = 1.0F
    )

    val FLASH_MISS = DetailedSound(
            Sound.ENTITY_PIG_AMBIENT,
            SoundCategory.BLOCKS,
            pitch = 4.0F,
            volume = 1.0F
    )

    val SWITCH = DetailedSound(
            Sound.ITEM_ARMOR_EQUIP_DIAMOND,
            SoundCategory.BLOCKS,
            pitch = 1.4F,
            volume = 1.0F
    )

    val TERRA_DRAIN = DetailedSound(
            Sound.ITEM_HOE_TILL,
            SoundCategory.BLOCKS,
            pitch = (1.5 + Random.nextGaussian(variance = 0.4)).toFloat(),
            volume = 0.2F
    )

}