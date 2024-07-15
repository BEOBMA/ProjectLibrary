@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.bookshelf

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalstatus.AbnormalStatusManager
import org.beobma.projectlibrary.bookshelf.Rating.*
import org.beobma.projectlibrary.game.GameManager
import org.beobma.projectlibrary.game.LibraryFloor.*
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
        Info.game?.playerMainBookShelf?.set(player, this)
        player.apply {
            maxHealth = this@MainBookShelf.maxHealth
            health = this@MainBookShelf.health
            inventory.clear()
            activePotionEffects.forEach { removePotionEffect(it.type) }
            inventory.setItem(0, this@MainBookShelf.weapon)
        }
    }

    fun disheveled(player: Player) {
        AbnormalStatusManager().apply {
            player.addDisheveled()
            object : BukkitRunnable() {
                override fun run() {
                    player.removeDisheveled()
                }
            }.runTaskLater(ProjectLibrary.instance, (disheveledTime * 20).toLong())
        }
    }

    fun death(player: Player) {
        health = maxHealth
        player.apply {
            scoreboardTags.add("isDeath")
            gameMode = GameMode.SPECTATOR
        }

        adjustEmotion(player)

        player.teleport(getDeathLocation(Info.game?.floor))

        GameManager().actEndCheck()
    }

    private fun adjustEmotion(player: Player) {
        val team = if (player.isTeam("RedTeam")) "RedTeam" else "BlueTeam"
        val adjustment = if (team == "RedTeam") -2 else 2
        Info.game?.players?.forEach { teammate ->
            if (teammate.isTeam(team)) {
                Info.game?.playerMainBookShelf?.get(teammate)?.emotion = adjustment
            } else {
                Info.game?.playerMainBookShelf?.get(teammate)?.emotion = -adjustment
            }
        }
    }

    private fun getDeathLocation(floor: LibraryFloor?): Location {
        return Location(Info.world, 0.0, 0.0, 0.0, 0f, 0f) // 실제 좌표로 수정 필요
    }

    fun loadToInventory(player: Player) {
        player.inventory.apply {
            setItem(9, this@MainBookShelf.toItem())
            uniqueAbilities.forEachIndexed { index, ability ->
                if (index < 9) setItem(18 + index, ability.toItem())
            }
            abnormalityCards.forEachIndexed { index, card ->
                if (index < 9) setItem(27 + index, card.toItem())
            }
        }
    }

    fun toItem(): ItemStack {
        val displayName = "${rating.color}${ChatColor.BOLD}$name"
        val cardItem = ItemStack(Material.BOOK, 1).apply {
            itemMeta = itemMeta.apply {
                setDisplayName(displayName)
                lore = listOf(name, "${ChatColor.RED}최대 체력: ${maxHealth.toInt()}", "${ChatColor.YELLOW}최대 흐트러짐: $maxDisheveled",
                    weapon.itemMeta.displayName)
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
            }
        }
        return cardItem
    }
}

enum class Rating(val color: ChatColor) {
    Supply(ChatColor.GREEN), Advanced(ChatColor.BLUE), Limit(ChatColor.DARK_PURPLE), Art(ChatColor.YELLOW)
}
