@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalstatus.AbnormalStatusManager
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.increaseByPercentage
import org.beobma.projectlibrary.util.Util.isTrueWithProbability
import org.beobma.projectlibrary.util.Util.percentageOf
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scheduler.BukkitRunnable

class OnDamageEvent : Listener {
    @EventHandler
    fun onPlayerAttack(event: EntityDamageByEntityEvent) {
        val player = event.damager as? Player ?: return
        val entity = event.entity as? Player ?: return

        if (!Info.isGaming() && !Info.isStarting()) return

        AbnormalStatusManager().run {
            if (player.isUnableAttack()) {
                event.isCancelled = true
                return
            }
        }

        event.damage += damageHandle(player, entity, event.damage)

        if (event.damage <= 0) {
            event.isCancelled = true
            return
        }

        var disheveledDamage = event.damage / 4
        disheveledDamage += disheveledDamageHandle(player, entity, disheveledDamage)

        if (disheveledDamage <= 0) {
            return
        }
        val mainBookShelf = Info.game!!.playerMainBookShelf[entity] ?: return
        if (mainBookShelf.disheveled - disheveledDamage <= 0) {
            AbnormalStatusManager().run {
                if (player.isDisheveled()) {
                    event.damage += disheveledDamage
                } else {
                    mainBookShelf.disheveled(entity)
                }
            }
        } else {
            mainBookShelf.disheveled -= disheveledDamage.toInt()
        }

        if (entity.health - event.damage <= 0) {
            updateEmotionOnDeath(player, entity)
            entity.health = 0.0
        } else {
            updateEmotionOnDamage(player, entity, event.damage)
            Info.game!!.playerMainBookShelf[entity]!!.health = entity.health - event.damage
        }
    }

    private fun damageHandle(player: Player, entity: Player, damage: Double): Double {
        val playerBookShelfList = player.getMainBookShelf()!!.abnormalityCards
        val entityBookShelfList = entity.getMainBookShelf()!!.abnormalityCards
        val playerBookShelf = player.getMainBookShelf()!!
        val entityBookShelf = entity.getMainBookShelf()!!
        val game = Info.game ?: return 0.0
        var finalDamage = 0.0

        if (playerBookShelfList.isNotEmpty()) {
            playerBookShelfList.forEach { abnormalityCard ->
                if (abnormalityCard.name == "손목긋개") {
                    finalDamage += 2
                    player.damage(1.0, entity)
                }
                if (abnormalityCard.name == "창백한 손") {
                    if (entity.scoreboard.getObjective("paleHandCount")!!
                            .getScore(entity.name).score == 0
                    ) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players reset @a paleHandCount")
                    entity.scoreboard.getObjective("paleHandCount")!!.getScore(entity.name).score++

                    if (entity.scoreboard.getObjective("paleHandCount")!!.getScore(entity.name).score >= 3) {
                        entityBookShelf.removeDisheveled(3, entity)
                        entity.scoreboard.getObjective("paleHandCount")!!.getScore(entity.name).score = 0
                    }
                }
                if (abnormalityCard.name == "고동") {
                    finalDamage += damage.percentageOf(20)
                    player.scoreboard.getObjective("beatingCounter")!!.getScore(player.name).score = 0
                }
                if (abnormalityCard.name == "몰아치는 박동") {
                    finalDamage += damage.percentageOf(20)
                }
                if (abnormalityCard.name == "학습") {
                    finalDamage += game.act
                }
                if (abnormalityCard.name == "서리검") {
                    val original = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.value
                    val int = original * 0.15
                    val final = original - int
                    Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "attribute ${player.name} minecraft:generic.movement_speed base set $final"
                    )

                    Info.game!!.mapBukkitScheduler[entity]?.get("서리검")?.cancel()
                    Info.game!!.mapBukkitScheduler[entity] = mutableMapOf(
                        Pair("서리검", object : BukkitRunnable() {
                            override fun run() {
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "attribute ${player.name} minecraft:generic.movement_speed base set $original"
                                )
                            }
                        }.runTaskLater(ProjectLibrary.instance, 20L))
                    )
                }
                if (abnormalityCard.name == "입맞춤") {
                    val original = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)!!.value
                    val int = original * 0.15
                    val final = original - int
                    Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "attribute ${player.name} minecraft:generic.attack_speed base set $final"
                    )

                    Info.game!!.mapBukkitScheduler[entity]?.get("입맞춤")?.cancel()
                    Info.game!!.mapBukkitScheduler[entity] = mutableMapOf(
                        Pair("입맞춤", object : BukkitRunnable() {
                            override fun run() {
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "attribute ${player.name} minecraft:generic.attack_speed base set $original"
                                )
                            }
                        }.runTaskLater(ProjectLibrary.instance, 20L))
                    )
                }

                if (abnormalityCard.name == "못과 망치") {
                    entity.scoreboardTags.add("못")

                    Info.game!!.stageEndBukkitScheduler.add { entity.scoreboardTags.remove("못") }
                }

                if (abnormalityCard.name == "눈빛") {
                    finalDamage *= 2
                }
            }
        }
        if (entityBookShelfList.isNotEmpty()) {
            entityBookShelfList.forEach { abnormalityCard ->
                if (abnormalityCard.name == "피") {
                    finalDamage -= 2
                    entityBookShelf.removeDisheveled(1, entity)
                }
                if (abnormalityCard.name == "흉터") {
                    finalDamage -= 1
                    if (10.isTrueWithProbability()) {
                        finalDamage -= 9999
                    }
                }
                if (abnormalityCard.name == "몰아치는 박동") {
                    finalDamage -= damage.percentageOf(20)
                }

                if (abnormalityCard.name == "서리조각") {
                    val original = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.value
                    val int = original * 0.15
                    val final = original - int
                    Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "attribute ${player.name} minecraft:generic.movement_speed base set $final"
                    )
                    Info.game!!.mapBukkitScheduler[entity]?.get("서리조각")?.cancel()
                    Info.game!!.mapBukkitScheduler[entity] = mutableMapOf(
                        Pair("서리조각", object : BukkitRunnable() {
                            override fun run() {
                                Bukkit.dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    "attribute ${player.name} minecraft:generic.movement_speed base set $original"
                                )
                            }
                        }.runTaskLater(ProjectLibrary.instance, 20L))
                    )
                }

                if (abnormalityCard.name == "눈빛") {
                    finalDamage *= 2
                }
                if (abnormalityCard.name == "죄책감") {
                    playerBookShelf.removeDisheveled((damage+finalDamage/2).toInt(), player)
                }
            }
        }
        .run {
            val bleending = entity.getBleending()
            if (bleending > 0) {
                finalDamage += bleending
                entity.removeBleending(bleending / 2)
            }
        }
        return finalDamage
    }

    private fun disheveledDamageHandle(player: Player, entity: Player, damage: Double): Double {
        val playerBookShelfList = player.getMainBookShelf()!!.abnormalityCards
        val entityBookShelfList = entity.getMainBookShelf()!!.abnormalityCards
        val playerBookShelf = player.getMainBookShelf()!!
        val entityBookShelf = entity.getMainBookShelf()!!
        val game = Info.game ?: return 0.0
        var finalDamage = 0.0

        if (playerBookShelfList.isNotEmpty()) {
            playerBookShelfList.forEach { abnormalityCard ->
                when (abnormalityCard.name) {

                }
            }
        }
        if (entityBookShelfList.isNotEmpty()) {
            entityBookShelfList.forEach { abnormalityCard ->
                if (abnormalityCard.name == "꼭두각시") {
                    finalDamage -= 1
                }
                if (abnormalityCard.name == "죄책감") {
                    finalDamage -= 2
                }
            }
        }

        if (entity.scoreboardTags.contains("못")) {
            finalDamage += 2
        }
        return finalDamage
    }

    private fun updateEmotionOnDeath(player: Player, entity: Player) {
        val playerBookShelf = Info.game!!.playerMainBookShelf[player] ?: return
        val entityBookShelf = Info.game!!.playerMainBookShelf[entity] ?: return

        playerBookShelf.emotion += 3
        entityBookShelf.emotion -= 3
    }

    private fun updateEmotionOnDamage(player: Player, entity: Player, damage: Double) {
        val playerBookShelf = Info.game!!.playerMainBookShelf[player] ?: return
        val entityBookShelf = Info.game!!.playerMainBookShelf[entity] ?: return

        if (damage > 3) {
            playerBookShelf.emotion += 1
            entityBookShelf.emotion -= 1
        }
    }
}