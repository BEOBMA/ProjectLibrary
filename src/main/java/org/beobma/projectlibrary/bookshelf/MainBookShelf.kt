@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.bookshelf

import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCardManager
import org.beobma.projectlibrary.abnormalitycard.DefaultAbnormalityCardConverter
import org.beobma.projectlibrary.manager.AbnormalStatusManager
import org.beobma.projectlibrary.manager.GameManager
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.manager.DefaultGameService
import org.beobma.projectlibrary.util.Util.isTeam
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.math.max

data class MainBookShelf(
    val name: String,
    val rating: Rating,
    var health: Double,
    var maxHealth: Double,
    var disheveled: Int,
    val maxDisheveled: Int,
    var light: Int,
    val maxLight: Int,
    val uniqueAbilities: MutableList<UniqueAbilities>,
    val weapon: ItemStack,
    val attackType: AttackType,
    val disheveledTime: Int = 3,
    var emotion: Int = 0,
    val abnormalityCards: MutableList<AbnormalityCard> = mutableListOf(),
    var minDiceWeight: Int = 0,
    var maxDiceWeight: Int = 0,
) {

    fun set(player: Player) {
        Info.game?.playerMainBookShelf?.set(player, this)
        player.apply {
            this@MainBookShelf.health = this@MainBookShelf.maxHealth
            maxHealth = this@MainBookShelf.maxHealth
            health = this@MainBookShelf.maxHealth
            inventory.clear()
            activePotionEffects.forEach { removePotionEffect(it.type) }
            inventory.setItem(0, this@MainBookShelf.weapon)
        }
    }

    fun death(player: Player) {
        health = maxHealth
        val players = Info.game!!.players.filter { !it.scoreboardTags.contains("isDeath") }
        if (players.size <= 1) {
            return
        }
        player.apply {
            scoreboardTags.add("isDeath")
            gameMode = GameMode.SPECTATOR
        }
        adjustEmotion(player)
        GameManager(DefaultGameService()).actEndCheck()
    }


    private fun adjustEmotion(player: Player) {
        val team = if (player.isTeam("RedTeam")) "RedTeam" else "BlueTeam"
        val adjustment = if (team == "RedTeam") -2 else 2
        Info.game?.players?.forEach { teammate ->
            if (teammate.isTeam(team)) {
                Info.game!!.playerMainBookShelf[teammate]!!.emotion += adjustment
            } else {
                Info.game!!.playerMainBookShelf[teammate]!!.emotion += -adjustment
            }
        }
    }

    fun loadToInventory(player: Player) {
        val abnormalityCardManager = AbnormalityCardManager(DefaultAbnormalityCardConverter())
        player.inventory.apply {
            setItem(9, this@MainBookShelf.toItem())
            uniqueAbilities.forEachIndexed { index, ability ->
                if (index < 9) setItem(18 + index, ability.toItem())
            }
            abnormalityCards.forEachIndexed { index, card ->
                if (index < 9) setItem(27 + index, abnormalityCardManager.toItem(card))
            }
        }
    }

    fun toItem(): ItemStack {
        val displayName = "${rating.color}${ChatColor.BOLD}$name"
        val uniqueAbilities = uniqueAbilities.map { it.description }.flatten()
        val cardItem = ItemStack(Material.BOOK, 1).apply {
            itemMeta = itemMeta.apply {
                setDisplayName(displayName)
                lore = listOf(name,
                    "${ChatColor.RED}최대 체력: ${maxHealth.toInt()}",
                    "${ChatColor.YELLOW}최대 흐트러짐: $maxDisheveled",
                    "${ChatColor.YELLOW}최대 빛: $maxLight",
                    "${ChatColor.GRAY}무기: ${weapon.itemMeta.displayName}",
                    "${ChatColor.GRAY}공격 유형: ${attackType.string}",
                    "${ChatColor.GRAY}패시브 목록:") + uniqueAbilities
                addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
            }
        }
        return cardItem
    }

    fun removeDisheveled(n: Int, player: Player) {
        this@MainBookShelf.disheveled -= n

        if (this@MainBookShelf.disheveled <= 0) {
            val abnormalStatusManager = AbnormalStatusManager()
            val disheveledManager = abnormalStatusManager.createDisheveledManager()
            disheveledManager.run { player.addDisheveled() }
        }
    }

    fun addDisheveled(n: Int) {
        if (this@MainBookShelf.disheveled + n >= maxDisheveled) {
            disheveled = maxDisheveled
        }
        else {
            disheveled += n
        }
    }

    fun paleDamage(damage: Double, player: Player) {
        player.health -= damage
        if (player.health <= 0) {
            player.health = 0.0
        }
        this@MainBookShelf.health = player.health
    }

    fun heal(damage: Double, player: Player) {
        if (player.health + damage >= player.maxHealth) {
            player.health = player.maxHealth
        }
        else {
            player.health += damage
        }
        this@MainBookShelf.health = player.health
    }

    fun light(damage: Int) {
        if (light + damage >= maxLight) {
            light = maxLight
        }
        else if (light + damage <= 0) {
            light = 0
        }
        else {
            light += damage
        }
    }



    fun deepCopyMainBookShelf(original: MainBookShelf): MainBookShelf {
        val copiedUniqueAbilities = original.uniqueAbilities.map { it.copy() }.toMutableList()
        val copiedAbnormalityCards = original.abnormalityCards.map { it.copy() }.toMutableList()

        val copiedWeapon = original.weapon.clone()

        return MainBookShelf(
            name = original.name,
            rating = original.rating,
            health = original.health,
            maxHealth = original.maxHealth,
            disheveled = original.disheveled,
            maxDisheveled = original.maxDisheveled,
            light = original.light,
            maxLight = original.maxLight,
            uniqueAbilities = copiedUniqueAbilities,
            weapon = copiedWeapon,
            attackType = original.attackType,
            disheveledTime = original.disheveledTime,
            emotion = original.emotion,
            abnormalityCards = copiedAbnormalityCards,
            minDiceWeight = original.minDiceWeight,
            maxDiceWeight = original.maxDiceWeight
        )
    }
}

enum class Rating(val color: ChatColor) {
    Supply(ChatColor.GREEN), Advanced(ChatColor.BLUE), Limit(ChatColor.DARK_PURPLE), Art(ChatColor.YELLOW)
}

enum class AttackType(val string: String) {
    Slashing("참격"), Piercing("관통"), Striking("타격")
}