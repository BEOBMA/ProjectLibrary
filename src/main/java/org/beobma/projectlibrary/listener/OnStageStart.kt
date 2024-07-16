@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalstatus.AbnormalStatusManager
import org.beobma.projectlibrary.event.OnStageStartEvent
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.increaseByPercentage
import org.beobma.projectlibrary.util.Util.percentageOf
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

class OnStageStart : Listener {
    @EventHandler
    fun onStageStartEvent(event: OnStageStartEvent) {
        Info.game!!.players.forEach { player ->
            val playerBookShelfList = player.getMainBookShelf()!!.abnormalityCards
            val playerBookShelf = player.getMainBookShelf()!!

            if (playerBookShelfList.isNotEmpty()) {
                playerBookShelfList.forEach { abnormalityCard ->
                    if (abnormalityCard.name == "고동") {
                        Info.game!!.stageBukkitScheduler.add(
                            object : BukkitRunnable() {
                                override fun run() {
                                    player.scoreboard.getObjective("beatingCounter")!!.getScore(player.name).score++

                                    if (player.scoreboard.getObjective("beatingCounter")!!.getScore(player.name).score >= 200) {
                                        playerBookShelf.paleDamage(playerBookShelf.maxHealth.percentageOf(25), player)
                                        player.scoreboard.getObjective("beatingCounter")!!.getScore(player.name).score = 0
                                    }
                                }
                            }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                        )
                    }
                    if (abnormalityCard.name == "동맥") {
                        val original = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)!!.value
                        val final = original + original.percentageOf(10)
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.attack_speed base set $final")
                        val healthInt = playerBookShelf.maxHealth.percentageOf(10)
                        player.maxHealth -= healthInt
                    }
                    if (abnormalityCard.name == "열망") {
                        val healthInt = playerBookShelf.maxHealth.percentageOf(15)
                        player.maxHealth += healthInt
                        player.health = player.maxHealth

                        val original = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.value
                        val final = original + original.percentageOf(20)
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.movement_speed base set $final")
                    }
                    if (abnormalityCard.name == "몰아치는 박동") {
                        val original = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.value
                        val final = original + original.percentageOf(20)
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.movement_speed base set $final")

                        Info.game!!.mapBukkitScheduler[player]?.get("몰아치는 박동")?.cancel()
                        Info.game!!.mapBukkitScheduler[player] = mutableMapOf(Pair("몰아치는 박동",
                            object : BukkitRunnable() {
                                override fun run() {
                                    playerBookShelf.death(player)
                                }
                            }.runTaskLater(ProjectLibrary.instance, 400L))
                        )
                    }
                    if (abnormalityCard.name == "거짓말") {
                        val randomPercentage = Random.nextInt(50, 151) / 100.0
                        val newMaxHealth = (playerBookShelf.maxHealth * randomPercentage)
                        player.maxHealth = newMaxHealth
                        player.health = player.maxHealth
                    }
                    if (abnormalityCard.name == "눈보라") {
                        if (playerBookShelfList.contains(playerBookShelf.abnormalityCards.find { it.name == "서리검" })) {
                            Info.game!!.players.forEach { players ->
                                AbnormalStatusManager().run {
                                    if (player != players) {
                                        players.addUnableMove()
                                        players.addUnableAttack()

                                        Info.game!!.mapBukkitScheduler[players]?.get("눈보라")?.cancel()
                                        Info.game!!.mapBukkitScheduler[players] = mutableMapOf(Pair("눈보라",
                                            object : BukkitRunnable() {
                                                override fun run() {
                                                    players.removeUnableMove()
                                                    players.removeUnableAttack()
                                                }
                                            }.runTaskLater(ProjectLibrary.instance, 200L))
                                        )
                                    }
                                }
                            }
                        }
                        else {
                            Info.game!!.players.forEach { players ->
                                AbnormalStatusManager().run {
                                    if (player != players) {
                                        players.addUnableMove()

                                        Info.game!!.mapBukkitScheduler[players]?.get("눈보라")?.cancel()
                                        Info.game!!.mapBukkitScheduler[players] = mutableMapOf(
                                            Pair("눈보라", object : BukkitRunnable() {
                                                override fun run() {
                                                    players.removeUnableMove()
                                                }
                                            }.runTaskLater(ProjectLibrary.instance, 200L))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            playerBookShelf.abnormalityCards.remove(playerBookShelf.abnormalityCards.find { it.name == "눈보라" })
        }
    }
}