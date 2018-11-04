package click.seichi.gigantic.button.buttons

import click.seichi.gigantic.belt.Belt
import click.seichi.gigantic.boss.Boss
import click.seichi.gigantic.button.Button
import click.seichi.gigantic.button.PlayerHeadButton
import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.cache.manipulator.catalog.CatalogPlayerCache
import click.seichi.gigantic.extension.*
import click.seichi.gigantic.head.Head
import click.seichi.gigantic.menu.menus.*
import click.seichi.gigantic.message.messages.MenuMessages
import click.seichi.gigantic.player.LockedFunction
import click.seichi.gigantic.raid.RaidManager
import click.seichi.gigantic.relic.Relic
import click.seichi.gigantic.relic.RelicRarity
import click.seichi.gigantic.sound.sounds.PlayerSounds
import click.seichi.gigantic.sound.sounds.SkillSounds
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @author tar0ss
 */
object MenuButtons {

    val PLAYER_HEAD = { uuid: UUID ->
        PlayerHeadButton(uuid)
    }

    val PROFILE_PROFILE = object : Button {
        override fun getItemStack(player: Player): ItemStack? {
            return player.getHead().apply {
                setDisplayName("${ChatColor.AQUA}" + MenuMessages.PROFILE_PROFILE.asSafety(player.wrappedLocale))
                val level = player.find(CatalogPlayerCache.LEVEL) ?: return@apply
                val aptitude = player.find(CatalogPlayerCache.APTITUDE) ?: return@apply
                val health = player.find(CatalogPlayerCache.HEALTH) ?: return@apply
                val mineCombo = player.find(CatalogPlayerCache.MINE_COMBO) ?: return@apply
                val mana = player.find(CatalogPlayerCache.MANA) ?: return@apply
                val lore = mutableListOf(
                        MenuMessages.PROFILE_LEVEL(level).asSafety(player.wrappedLocale),
                        MenuMessages.PROFILE_EXP(level).asSafety(player.wrappedLocale),
                        MenuMessages.PROFILE_HEALTH(health).asSafety(player.wrappedLocale)
                )
                if (LockedFunction.MANA_STONE.isUnlocked(player)) {
                    lore.add(MenuMessages.PROFILE_MANA(mana).asSafety(player.wrappedLocale))
                }

                lore.addAll(listOf(
                        MenuMessages.PROFILE_MAX_COMBO(mineCombo).asSafety(player.wrappedLocale),
                        *MenuMessages.PROFILE_WILL_APTITUDE(aptitude).map { it.asSafety(player.wrappedLocale) }.toTypedArray())
                )

                setLore(*lore.toTypedArray())
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
        }

    }

    val PROFILE_RAID_BOSS = object : Button {
        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.RAID_BATTLE.isUnlocked(player)) return null

            return ItemStack(Material.ENDER_EYE).apply {
                setDisplayName("${ChatColor.AQUA}" + MenuMessages.PROFILE_RAID_BOSS.asSafety(player.wrappedLocale))
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
            if (event.inventory.holder === ProfileBossMenu) return
            if (!LockedFunction.RAID_BATTLE.isUnlocked(player)) return

            ProfileBossMenu.open(player)
        }

    }

    val PROFILE_RELIC = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.RAID_BATTLE.isUnlocked(player)) return null

            return Head.JEWELLERY_BOX.toItemStack().apply {
                setDisplayName("${ChatColor.AQUA}" + MenuMessages.PROFILE_RAID_RELIC.asSafety(player.wrappedLocale))
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
            if (event.inventory.holder === ProfileRelicMenu) return
            if (!LockedFunction.RAID_BATTLE.isUnlocked(player)) return

            ProfileRelicMenu.open(player)
        }
    }

    val PROFILE_RAID_BOSS_INFO: (Boss, Long) -> Button = { boss: Boss, defeatCount: Long ->
        object : Button {
            override fun getItemStack(player: Player): ItemStack? {
                val color = when (defeatCount) {
                    0L -> ChatColor.DARK_GRAY
                    in 1..29 -> ChatColor.WHITE
                    in 30..99 -> ChatColor.YELLOW
                    else -> ChatColor.LIGHT_PURPLE
                }
                return when (defeatCount) {
                    0L -> ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1)
                    else -> boss.head.toItemStack()
                }.apply {
                    setDisplayName("$color${boss.localizedName.asSafety(player.wrappedLocale)}")
                    setLore("$color${MenuMessages.PROFILE_RAID_BOSS_DEFEATED(defeatCount).asSafety(player.wrappedLocale)}")
                }
            }

            override fun onClick(player: Player, event: InventoryClickEvent) {
            }

        }
    }

    val PROFILE_RAID_RELIC_INFO: (Relic, Long) -> Button = { relic: Relic, amount: Long ->
        object : Button {
            override fun getItemStack(player: Player): ItemStack? {
                val color = when (amount) {
                    0L -> ChatColor.DARK_GRAY
                    in 1..29 -> ChatColor.WHITE
                    in 30..99 -> ChatColor.YELLOW
                    else -> ChatColor.LIGHT_PURPLE
                }
                return when {
                    amount == 0L -> ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1)
                    relic.rarity == RelicRarity.NORMAL -> Head.RUBY_JEWELLERY.toItemStack()
                    relic.rarity == RelicRarity.RARE -> Head.SAPPHIRE_JEWELLERY.toItemStack()
                    else -> ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1)
                }.apply {
                    setDisplayName("$color${relic.localizedName.asSafety(player.wrappedLocale)}")
                    setLore(*relic.description.map { "$color${it.asSafety(player.wrappedLocale)}" }.toTypedArray(),
                            "$color${MenuMessages.PROFILE_RAID_RELIC_AMOUNT(amount).asSafety(player.wrappedLocale)}")
                }
            }

            override fun onClick(player: Player, event: InventoryClickEvent) {
            }

        }
    }

    val RAID_BATTLE_BOSS: (Int) -> Button = { rank: Int ->
        object : Button {
            override fun getItemStack(player: Player): ItemStack? {
                val battle = RaidManager.getBattle(rank) ?: return ItemStack(Material.AIR)
                val boss = battle.boss
                val isJoinedOtherRaid = RaidManager
                        .getBattleList()
                        .firstOrNull { it.isJoined(player) } != null
                return boss.head.toItemStack().apply {
                    setDisplayName(MenuMessages.BATTLE_BUTTON_TITLE(boss).asSafety(player.wrappedLocale))
                    setLore(*MenuMessages.BATTLE_BUTTON_LORE(battle, player.find(CatalogPlayerCache.HEALTH)
                            ?: return@apply)
                            .map { it.asSafety(player.wrappedLocale) }
                            .toTypedArray())
                    addLore(MenuMessages.LINE)
                    addLore(
                            when {
                                battle.isDropped(player) ->
                                    MenuMessages.BATTLE_BUTTON_DROPPED
                                battle.isJoined(player) ->
                                    MenuMessages.BATTLE_BUTTON_LEFT
                                isJoinedOtherRaid ->
                                    MenuMessages.BATTLE_BUTTON_JOINED
                                else -> MenuMessages.BATTLE_BUTTON_JOIN
                            }.asSafety(player.wrappedLocale)
                    )
                }
            }

            override fun onClick(player: Player, event: InventoryClickEvent) {
                val battle = RaidManager.getBattle(rank) ?: return
                val isJoinedOtherRaid = RaidManager
                        .getBattleList()
                        .firstOrNull { it.isJoined(player) } != null
                when {
                    battle.isDropped(player) -> return
                    battle.isJoined(player) -> {
                        battle.drop(player)
                    }
                    isJoinedOtherRaid -> return
                    else -> {
                        battle.join(player)
                        player.closeInventory()
                    }
                }
                RaidBattleMenu.reopen(player)
            }

        }
    }

    val BELT_SWITCHER_SETTING: (Belt) -> Button = { belt ->
        object : Button {
            override fun getItemStack(player: Player): ItemStack? {
                if (!LockedFunction.SKILL_SWITCH.isUnlocked(player)) return null
                val switcher = player.find(CatalogPlayerCache.BELT_SWITCHER) ?: return null
                return belt.findFixedButton()?.getItemStack(player)?.apply {
                    setDisplayName(belt.localizedName.asSafety(player.wrappedLocale))
                    setLore(
                            *MenuMessages.BELT_SWITCHER_SETTING_BUTTON_LORE(switcher.canSwitch(belt))
                                    .map { it.asSafety(player.wrappedLocale) }
                                    .toTypedArray()
                    )
                }
            }

            override fun onClick(player: Player, event: InventoryClickEvent) {
                if (!LockedFunction.SKILL_SWITCH.isUnlocked(player)) return
                player.manipulate(CatalogPlayerCache.BELT_SWITCHER) {
                    it.setCanSwitch(belt, !it.canSwitch(belt))
                    if (!it.canSwitch(belt)) {
                        it.switch()
                        SkillSounds.SWITCH.playOnly(player)
                    }
                }
                player.getOrPut(Keys.BELT).wear(player)
                BeltSwitchSettingMenu.reopen(player)
            }

        }
    }

    val PROFILE_SKILL = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            return ItemStack(Material.BLAZE_POWDER).apply {
                setDisplayName("${ChatColor.AQUA}" + MenuMessages.PROFILE_SKILL.asSafety(player.wrappedLocale))
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
            if (event.inventory.holder === ProfileSkillMenu) return
            ProfileSkillMenu.open(player)
        }

    }

    val PROFILE_SKILL_MINE_BURST = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.SKILL_MINE_BURST.isUnlocked(player)) return null
            return ItemStack(Material.BLAZE_POWDER).apply {
                setDisplayName(MenuMessages.MINE_BURST_TITLE.asSafety(player.wrappedLocale))
                setLore(*MenuMessages.MINE_BURST
                        .map { it.asSafety(player.wrappedLocale) }
                        .toTypedArray())
                setEnchanted(true)
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
        }

    }

    val PROFILE_SKILL_FLASH = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.SKILL_FLASH.isUnlocked(player)) return null
            return ItemStack(Material.FEATHER).apply {
                setDisplayName(MenuMessages.FLASH_TITLE.asSafety(player.wrappedLocale))
                setLore(*MenuMessages.FLASH
                        .map { it.asSafety(player.wrappedLocale) }
                        .toTypedArray())
                setEnchanted(true)
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
        }

    }

    val PROFILE_SKILL_HEAL = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.SKILL_HEAL.isUnlocked(player)) return null
            return ItemStack(Material.APPLE).apply {
                setDisplayName(MenuMessages.HEAL_TITLE.asSafety(player.wrappedLocale))
                setLore(*MenuMessages.HEAL
                        .map { it.asSafety(player.wrappedLocale) }
                        .toTypedArray())
                setEnchanted(true)
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
        }

    }

    val PROFILE_SKILL_SWITCH = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.SKILL_SWITCH.isUnlocked(player)) return null
            return ItemStack(Material.LEVER).apply {
                setDisplayName(MenuMessages.SWITCH_TITLE.asSafety(player.wrappedLocale))
                setLore(*MenuMessages.SWITCH
                        .map { it.asSafety(player.wrappedLocale) }
                        .toTypedArray())
                setEnchanted(true)
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
        }

    }

    val PROFILE_SKILL_WILL_O_THE_WISP = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.SKILL_WILL_O_THE_WISP.isUnlocked(player)) return null
            return ItemStack(Material.IRON_NUGGET).apply {
                setDisplayName(MenuMessages.WILL_O_THE_WISP_TITLE.asSafety(player.wrappedLocale))
                setLore(*MenuMessages.WILL_O_THE_WISP
                        .map { it.asSafety(player.wrappedLocale) }
                        .toTypedArray())
                setEnchanted(true)
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
        }

    }


    val PROFILE_SPELL_STELLA_CLAIR = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.SPELL_STELLA_CLAIR.isUnlocked(player)) return null
            return ItemStack(Material.LAPIS_LAZULI).apply {
                setDisplayName(MenuMessages.STELLA_CLAIR_TITLE.asSafety(player.wrappedLocale))
                setLore(*MenuMessages.STELLA_CLAIR
                        .map { it.asSafety(player.wrappedLocale) }
                        .toTypedArray())
                setEnchanted(true)
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
        }

    }

    val PROFILE_SPELL_TERRA_DRAIN = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.SPELL_TERRA_DRAIN.isUnlocked(player)) return null
            val toggle = player.getOrPut(Keys.TERRA_DRAIN_TOGGLE)
            return ItemStack(Material.WHEAT_SEEDS).apply {
                if (toggle)
                    setDisplayName(MenuMessages.TERRA_DRAIN_TITLE_ON.asSafety(player.wrappedLocale))
                else
                    setDisplayName(MenuMessages.TERRA_DRAIN_TITLE_OFF.asSafety(player.wrappedLocale))
                setLore(*MenuMessages.TERRA_DRAIN
                        .map { it.asSafety(player.wrappedLocale) }
                        .toTypedArray())
                if (toggle)
                    setEnchanted(true)
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
            if (!LockedFunction.SPELL_TERRA_DRAIN.isUnlocked(player)) return
            player.transform(Keys.TERRA_DRAIN_TOGGLE) {
                if (it)
                    PlayerSounds.SPELL_TOGGLE_OFF.playOnly(player)
                else
                    PlayerSounds.SPELL_TOGGLE_ON.playOnly(player)
                !it
            }
            ProfileSpellMenu.reopen(player)
        }

    }

    val PROFILE_SPELL_GRAND_NATURA = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.SPELL_GRAND_NATURA.isUnlocked(player)) return null
            val toggle = player.getOrPut(Keys.GRAND_NATURA_TOGGLE)
            return ItemStack(Material.SEA_PICKLE).apply {
                if (toggle)
                    setDisplayName(MenuMessages.GRAND_NATURA_TITLE_ON.asSafety(player.wrappedLocale))
                else
                    setDisplayName(MenuMessages.GRAND_NATURA_TITLE_OFF.asSafety(player.wrappedLocale))
                setLore(*MenuMessages.GRAND_NATURA
                        .map { it.asSafety(player.wrappedLocale) }
                        .toTypedArray())
                if (toggle)
                    setEnchanted(true)
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
            if (!LockedFunction.SPELL_GRAND_NATURA.isUnlocked(player)) return
            player.transform(Keys.GRAND_NATURA_TOGGLE) {
                if (it)
                    PlayerSounds.SPELL_TOGGLE_OFF.playOnly(player)
                else
                    PlayerSounds.SPELL_TOGGLE_ON.playOnly(player)
                !it
            }
            ProfileSpellMenu.reopen(player)
        }

    }

    val PROFILE_SPELL_AQUA_LINEA = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            if (!LockedFunction.SPELL_AQUA_LINEA.isUnlocked(player)) return null
            val toggle = player.getOrPut(Keys.AQUA_LINEA_TOGGLE)
            return ItemStack(Material.PRISMARINE_CRYSTALS).apply {
                if (toggle)
                    setDisplayName(MenuMessages.AQUA_LINEA_TITLE_ON.asSafety(player.wrappedLocale))
                else
                    setDisplayName(MenuMessages.AQUA_LINEA_TITLE_OFF.asSafety(player.wrappedLocale))
                setLore(*MenuMessages.AQUA_LINEA
                        .map { it.asSafety(player.wrappedLocale) }
                        .toTypedArray())
                if (toggle)
                    setEnchanted(true)
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
            if (!LockedFunction.SPELL_AQUA_LINEA.isUnlocked(player)) return
            player.transform(Keys.AQUA_LINEA_TOGGLE) {
                if (it)
                    PlayerSounds.SPELL_TOGGLE_OFF.playOnly(player)
                else
                    PlayerSounds.SPELL_TOGGLE_ON.playOnly(player)

                !it
            }
            ProfileSpellMenu.reopen(player)
        }

    }

    val PROFILE_SPELL = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            return ItemStack(Material.LAPIS_LAZULI).apply {
                setDisplayName("${ChatColor.AQUA}" + MenuMessages.PROFILE_SPELL.asSafety(player.wrappedLocale))
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
            if (event.inventory.holder === ProfileSpellMenu) return
            ProfileSpellMenu.open(player)
        }

    }

    val TELEPORT_TO_PLAYER = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            return ItemStack(Material.PLAYER_HEAD).apply {
                setDisplayName("${ChatColor.AQUA}" + MenuMessages.TELEPORT_TO_PLAYER.asSafety(player.wrappedLocale))
            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
            if (event.inventory.holder === TeleportToPlayerMenu) return
            TeleportToPlayerMenu.open(player)
        }

    }

    val TELEPORT_TOGGLE = object : Button {

        override fun getItemStack(player: Player): ItemStack? {
            val toggle = player.getOrPut(Keys.TELEPORT_TOGGLE)
            return ItemStack(Material.DAYLIGHT_DETECTOR).apply {
                if (toggle)
                    setDisplayName(MenuMessages.TELEPORT_TOGGLE_ON.asSafety(player.wrappedLocale))
                else
                    setDisplayName(MenuMessages.TELEPORT_TOGGLE_OFF.asSafety(player.wrappedLocale))

                setLore(*MenuMessages.TELEPORT_TOGGLE_LORE
                        .map { it.asSafety(player.wrappedLocale) }
                        .toTypedArray())

            }
        }

        override fun onClick(player: Player, event: InventoryClickEvent) {
            player.transform(Keys.TELEPORT_TOGGLE) { !it }
            PlayerSounds.TOGGLE.playOnly(player)
            TeleportMenu.reopen(player)
        }

    }

    val TELEPORT_PLAYER: (Player) -> Button = { to: Player ->
        object : Button {
            override fun getItemStack(player: Player): ItemStack? {
                return when {
                    !to.isValid -> ItemStack(Material.BLACK_STAINED_GLASS_PANE).apply {
                        setDisplayName("${ChatColor.RED}${to.name}")
                        setLore(*MenuMessages.TELEPORT_PLAYER_INVALID_LORE
                                .map { it.asSafety(player.wrappedLocale) }
                                .toTypedArray())
                    }
                    !to.getOrPut(Keys.TELEPORT_TOGGLE) -> ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE).apply {
                        setDisplayName("${ChatColor.RED}${to.name}")
                        setLore(*MenuMessages.TELEPORT_PLAYER_TOGGLE_OFF_LORE
                                .map { it.asSafety(player.wrappedLocale) }
                                .toTypedArray())
                    }
                    to.gameMode == GameMode.SPECTATOR -> ItemStack(Material.YELLOW_STAINED_GLASS_PANE).apply {
                        setDisplayName("${ChatColor.RED}${to.name}")
                        setLore(*MenuMessages.TELEPORT_PLAYER_AFK_LORE
                                .map { it.asSafety(player.wrappedLocale) }
                                .toTypedArray())
                    }
                    to.gameMode != GameMode.SURVIVAL -> ItemStack(Material.BROWN_STAINED_GLASS_PANE).apply {
                        setDisplayName("${ChatColor.RED}${to.name}")
                        setLore(*MenuMessages.TELEPORT_PLAYER_NOT_SURVIVAL_LORE
                                .map { it.asSafety(player.wrappedLocale) }
                                .toTypedArray())
                    }
                    to.world != player.world -> ItemStack(Material.CYAN_STAINED_GLASS_PANE).apply {
                        setDisplayName("${ChatColor.RED}${to.name}")
                        setLore(*MenuMessages.TELEPORT_PLAYER_INVALID_WORLD_LORE(to.world)
                                .map { it.asSafety(player.wrappedLocale) }
                                .toTypedArray())
                    }
                    to.isFlying -> ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE).apply {
                        setDisplayName("${ChatColor.RED}${to.name}")
                        setLore(*MenuMessages.TELEPORT_PLAYER_FLYING_LORE
                                .map { it.asSafety(player.wrappedLocale) }
                                .toTypedArray())
                    }
                    else -> to.getHead().apply {
                        setDisplayName("${ChatColor.GREEN}${to.name}")
                        setLore(*MenuMessages.TELEPORT_PLAYER_LORE(player)
                                .map { it.asSafety(player.wrappedLocale) }
                                .toTypedArray())
                    }
                }
            }

            override fun onClick(player: Player, event: InventoryClickEvent) {
                if (!to.isValid) return
                if (!to.getOrPut(Keys.TELEPORT_TOGGLE)) return
                if (to.gameMode != GameMode.SURVIVAL) return
                if (to.world != player.world) return
                if (to.isFlying) return
                player.teleport(to)
                PlayerSounds.TELEPORT.play(to.location)
            }

        }
    }

}