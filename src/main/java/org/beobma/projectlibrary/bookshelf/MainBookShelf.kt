@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.bookshelf

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalstatus.AbnormalStatusManager
import org.beobma.projectlibrary.bookshelf.Rating.*
import org.beobma.projectlibrary.game.GameManager
import org.beobma.projectlibrary.game.LibraryFloor.*
import org.beobma.projectlibrary.game.LibraryFloor.Art
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.util.Util.isTeam
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.ChatPaginator.ChatPage

data class MainBookShelf(
    val name: String,
    val rating: Rating,
    var health: Double,
    val maxHealth: Double,
    var disheveled: Int,
    val maxDisheveled: Int,
    val uniqueAbilities: MutableList<UniqueAbilities>,
    val weapon: ItemStack,
    val disheveledTime: Int = 3,
    var emotion: Int = 0,
    val abnormalityCards: MutableList<AbnormalityCard> = mutableListOf()
) {

    fun set(player: Player) {
        Info.game!!.playerMainBookShelf[player] = this
        player.maxHealth = this.maxHealth
        player.health = this.health
        player.inventory.clear()
        player.activePotionEffects.forEach { effect ->
            player.removePotionEffect(effect.type)
        }
        player.inventory.setItem(0, this.weapon)
    }

    fun disheveled(player: Player) {
        AbnormalStatusManager().run {
            player.addDisheveled()
        }
        object : BukkitRunnable() {
            override fun run() {
                AbnormalStatusManager().run {
                    player.removeDisheveled()
                }
            }
        }.runTaskLater(ProjectLibrary.instance, (this.disheveledTime * 20).toLong())
    }

    fun death(player: Player) {
        this.health = this.maxHealth
        player.scoreboardTags.add("isDeath")

        player.gameMode = GameMode.SPECTATOR
        if (player.isTeam("RedTeam")) {
            Info.game!!.players.forEach {
                if (it.isTeam("RedTeam")) {
                    Info.game!!.playerMainBookShelf[it]!!.emotion -= 2
                }
                else if (it.isTeam("BlueTeam")) {
                    Info.game!!.playerMainBookShelf[it]!!.emotion += 2
                }
            }
        }
        else {
            Info.game!!.players.forEach {
                if (it.isTeam("RedTeam")) {
                    Info.game!!.playerMainBookShelf[it]!!.emotion += 2
                }
                else if (it.isTeam("BlueTeam")) {
                    Info.game!!.playerMainBookShelf[it]!!.emotion -= 2
                }
            }
        }

        //좌표 수정 필요
        when (Info.game!!.floor) {
            GeneralWorks -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
            History -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
            TechnologicalSciences -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
            Literature -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
            Art -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
            NaturalSciences -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
            Language -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
            SocialSciences -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
            Philosophy -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
            Religion -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
            Kether -> player.teleport(Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f))
        }

        GameManager().actEndCheck()
    }

    fun loadToInventory(player: Player) {
        player.inventory.setItem(9,this.toItem())
        var i1 = 18
        this.uniqueAbilities.forEach {
            player.inventory.setItem(i1, it.toItem())
            i1++
            if (i1 >= 27) {
                return@forEach
            }
        }

        var i2 = 27
        this.abnormalityCards.forEach {
            player.inventory.setItem(i2, it.toItem())
            i2++
            if (i2 >= 36) {
                return@forEach
            }
        }
    }

    fun toItem(): ItemStack {
        val displayName = when (this.rating) {
            Supply -> "${ChatColor.GREEN}${ChatColor.BOLD}$name"
            Advanced -> "${ChatColor.BLUE}${ChatColor.BOLD}$name"
            Limit -> "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}$name"
            Rating.Art -> "${ChatColor.YELLOW}${ChatColor.BOLD}$name"
        }

        val cardItem = ItemStack(Material.BOOK, 1)
        val meta = cardItem.itemMeta.apply {
            setDisplayName(displayName)
            lore = listOf(name, "${ChatColor.RED}최대 체력: ${maxHealth.toInt()}", "${ChatColor.YELLOW}최대 흐트러짐: $maxDisheveled",
                weapon.itemMeta.displayName
            )
            addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        }
        cardItem.itemMeta = meta

        return cardItem
    }
}


enum class Rating {
    Supply, Advanced, Limit, Art
}