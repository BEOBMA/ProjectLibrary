@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.util

import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.game.GameManager
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.info.SetUp
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Util {
    fun Player.isParticipation(): Boolean {
        if (!Info.isGaming()) return false
        if (player == null) return false
        if (player!!.isDead) return false
        if (player !in Info.game!!.players) return false
        if (player !in Bukkit.getOnlinePlayers()) return false

        return true
    }

    fun Player.isTeam(team: String): Boolean {
        if (player != null) {
            return GameManager.teams[team]?.hasEntry(player!!.name) == true
        }
        return false
    }

    fun Player.getMainBookShelf(): MainBookShelf? {
        return Info.game!!.playerMainBookShelf[player]
    }

    fun ItemStack.toMainBookShelf(): MainBookShelf {
        val lore = this.itemMeta.lore as List<String>

        val mainBookShelf = SetUp.mainBookShelfList.find { it.name.trim() == lore.first().trim() }
        if (mainBookShelf !is MainBookShelf) {
            return MainBookShelf(
                "보조사서 책장",
                Rating.Supply,
                30.0,
                30.0,
                15,
                15,
                mutableListOf(),
                ItemStack(Material.WOODEN_SWORD, 1).apply {
                    itemMeta = itemMeta.apply {
                        setDisplayName("${ChatColor.BOLD}목검")
                        this.lore = arrayListOf(
                            "${ChatColor.GRAY}가장 기본적인 검."
                        )
                        isUnbreakable = true
                    }
                })
        }
        return mainBookShelf
    }

    fun ItemStack.toAbnormalityCard(): AbnormalityCard {
        var abnormalityCard = SetUp.abnormalityCardList.find { "${ChatColor.DARK_GREEN}${ChatColor.BOLD}${it.name}" == this.itemMeta.displayName }

        if (abnormalityCard !is AbnormalityCard)
            abnormalityCard = SetUp.abnormalityCardList.find { "${ChatColor.DARK_RED}${ChatColor.BOLD}${it.name}" == this.itemMeta.displayName }
        }
        if (abnormalityCard !is AbnormalityCard) {
            return AbnormalityCard("오류", listOf("이 환상체는 찾을 수 없는 환상체입니다. 개발자에게 문의해주세요."), EmotionType.Negative, LibraryFloor.Kether, 3)
        }
        return abnormalityCard
    }
}