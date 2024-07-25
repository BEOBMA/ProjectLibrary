@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.manager

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCardManager
import org.beobma.projectlibrary.abnormalitycard.DefaultAbnormalityCardConverter
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.game.LibraryFloor.*
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.info.SetUp
import org.beobma.projectlibrary.localization.Localization
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitRunnable

interface CompensationHandler {
    fun compensationCheck()
    fun mainBookShelfCompensation(player: Player)
    fun abnormalityCardCompensation(player: Player)
    fun fillInventoryWithNullPane(inventory: Inventory)
}

class DefaultCompensationHandler : CompensationHandler {
    override fun compensationCheck() {
        if (Info.game!!.floor == Kether && Info.game!!.act == 3) {
            ProjectLibrary.instance.loggerInfo("[ProjectLibrary] compensation cancle")
            GameManager(DefaultGameService()).preparationGame()
            return
        }
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 잠시 후, 보상을 획득합니다.")
                Bukkit.broadcastMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 보상 획득 창을 끄면 보상을 포기합니다.")
                object : BukkitRunnable() {
                    override fun run() {
                        Info.game!!.players.forEach { player ->
                            ProjectLibrary.instance.loggerInfo("[ProjectLibrary] compensation ready")
                            abnormalityCardCompensation(player)
                        }
                    }
                }.runTaskLater(ProjectLibrary.instance, 60L)
            }
        }.runTaskLater(ProjectLibrary.instance, 40L)
    }

    override fun mainBookShelfCompensation(player: Player) {
        val inventory = Bukkit.createInventory(null, 27, "핵심 책장을 교체하세요.")
        fillInventoryWithNullPane(inventory)

        val mainBookShelfList = when (Info.game!!.floor) {
            GeneralWorks -> SetUp.rumorMainBookShelfList.toList()
            History -> SetUp.urbanGhostMainBookShelfList.toList()
            TechnologicalSciences -> SetUp.urbanLegendMainBookShelfList.toList()
            Literature, Art -> SetUp.urbanDiseaseMainBookShelfList.toList()
            NaturalSciences, Language -> SetUp.urbanNightmareMainBookShelfList.toList()
            SocialSciences, Philosophy -> SetUp.cityStarMainBookShelfList.toList()
            Religion, Kether -> SetUp.ImpuritiesMainBookShelfList.toList()
        }

        mainBookShelfList.shuffled().take(3).forEachIndexed { index, bookShelf ->
            val newBookShelf = bookShelf.toItem()
            inventory.setItem(11 + index * 2, newBookShelf)
        }

        player.scoreboardTags.add("rewardChose")
        player.getMainBookShelf()?.loadToInventory(player)
        player.openInventory(inventory)
    }

    override fun abnormalityCardCompensation(player: Player) {
        val inventory = Bukkit.createInventory(null, 27, "환상체 책장을 선택하세요.")
        val game = Info.game ?: return
        val converter = DefaultAbnormalityCardConverter()
        val abnormalityCardManager = AbnormalityCardManager(converter)

        fillInventoryWithNullPane(inventory)
        val cardList = SetUp.abnormalityCardList.filter { it.floor == game.floor && it.act == game.act }.toMutableList()

        cardList.shuffle()

        val finalCardSet = mutableSetOf<AbnormalityCard>().apply {
            if (player.getMainBookShelf()!!.emotion > 5) {
                cardList.filter { it.emotion == EmotionType.Affirmation }.forEach { add(it) }
            } else if (player.getMainBookShelf()!!.emotion > 0) {
                cardList.find { it.emotion == EmotionType.Affirmation }?.let { add(it) }
                cardList.forEach { add(it) }
            } else if (player.getMainBookShelf()!!.emotion == 0) {
                cardList.forEach { add(it) }
            } else if (player.getMainBookShelf()!!.emotion < -5) {
                cardList.find { it.emotion == EmotionType.Negative }?.let { add(it) }
                cardList.forEach { add(it) }
            } else {
                cardList.filter { it.emotion == EmotionType.Negative }.forEach { add(it) }
            }
        }

        val finalCardList = finalCardSet.toList()

        finalCardList.take(3).forEachIndexed { index, card ->
            inventory.setItem(11 + index * 2, abnormalityCardManager.toItem(card))
        }

        player.scoreboardTags.add("rewardChose")
        player.getMainBookShelf()?.loadToInventory(player)
        player.openInventory(inventory)
    }

    override fun fillInventoryWithNullPane(inventory: Inventory) {
        for (i in 0 until inventory.size) {
            inventory.setItem(i, Localization().nullPane)
        }
    }
}

class CompensationManager(private val converter: CompensationHandler) {
    fun compensationCheck() {
        converter.compensationCheck()
    }

    fun mainBookShelfCompensation(player: Player) {
        converter.mainBookShelfCompensation(player)
    }

    fun abnormalityCardCompensation(player: Player) {
        converter.abnormalityCardCompensation(player)
    }

    fun fillInventoryWithNullPane(inventory: Inventory) {
        converter.fillInventoryWithNullPane(inventory)
    }
}