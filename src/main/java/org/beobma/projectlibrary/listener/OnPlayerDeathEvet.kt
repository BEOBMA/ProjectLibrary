@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.manager.AbnormalStatusManager
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.manager.DefaultTeamService.Companion.teams
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.getTeam
import org.beobma.projectlibrary.util.Util.myTeamPlayer
import org.beobma.projectlibrary.util.Util.percentageOf
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.util.Vector

class OnPlayerDeathEvet : Listener {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val game = Info.game ?: return
        val abnormalStatusManager = AbnormalStatusManager()
        val attackDamageManager = abnormalStatusManager.createAttackDamageManager()
        val unableAttackManager = abnormalStatusManager.createUnableAttackManager()
        val unableMoveManager = abnormalStatusManager.createUnableMoveManager()
        val disheveledManager = abnormalStatusManager.createDisheveledManager()
        val moveSpeedManager = abnormalStatusManager.createMoveSpeedManager()
        val attackSpeedManager = abnormalStatusManager.createAttackSpeedManager()
        val burnManager = abnormalStatusManager.createBurnManager()
        val bleedingManager = abnormalStatusManager.createBleedingManager()
        val maxHealthManager = abnormalStatusManager.createMaxHealthManager()

        val player = event.player
        val entity = event.entity

        if (Info.isGaming()) {
            val playerBookShelf = player.getMainBookShelf()!!
            val entityBookShelf = entity.getMainBookShelf()!!

            entityBookShelf.death(entity)
            entity.velocity = Vector(0, 0, 0)
            event.isCancelled = true

            val playerBookShelfAbnormalityCardsList = playerBookShelf.abnormalityCards
            val entityBookShelfAbnormalityCardsList = entityBookShelf.abnormalityCards

            val playerBookShelfUniqueAbilitiesList = playerBookShelf.uniqueAbilities
            val entityBookShelfUniqueAbilitiesList = entityBookShelf.uniqueAbilities


            if (playerBookShelfAbnormalityCardsList.isNotEmpty()) {
                playerBookShelfAbnormalityCardsList.forEach { abnormalityCard ->
                    abnormalityCard.killUnit?.invoke(game, unableAttackManager, disheveledManager, unableMoveManager, moveSpeedManager, attackSpeedManager, burnManager, bleedingManager, player, entity, playerBookShelf, entityBookShelf, maxHealthManager, attackDamageManager)
                }
            }
            if (entityBookShelfAbnormalityCardsList.isNotEmpty()) {
                entityBookShelfAbnormalityCardsList.forEach { abnormalityCard ->
                    abnormalityCard.deathUnit?.invoke(game, unableAttackManager, disheveledManager, unableMoveManager, moveSpeedManager, attackSpeedManager, burnManager, bleedingManager, player, entity, playerBookShelf, entityBookShelf, maxHealthManager, attackDamageManager)
                }
            }

            if (playerBookShelfUniqueAbilitiesList.isNotEmpty()) {
                playerBookShelfUniqueAbilitiesList.forEach { uniqueAbilities ->
                    when (uniqueAbilities.name) {
                        "갈고리" -> {
                            attackDamageManager.run {
                                player.attackDamage(1, Int.MAX_VALUE, "갈고리", true)
                            }
                        }

                        "즉석 조리" -> {
                            playerBookShelf.heal(playerBookShelf.maxHealth.percentageOf(10), player)
                        }

                        "시체 청소" -> {
                            playerBookShelf.heal(player.maxHealth.percentageOf(5), player)
                        }

                        "키즈나 / 극한의 피로" -> {
                            attackDamageManager.run {
                                player.attackDamage(1, Int.MAX_VALUE, "키즈나 / 극한의 피로", true)
                            }
                        }

                        "키즈나 / 사의 피로" -> {
                            attackDamageManager.run {
                                player.attackDamage(1, Int.MAX_VALUE, "키즈나 / 사의 피로", true)
                            }
                        }
                    }
                }
            }
            if (entityBookShelfUniqueAbilitiesList.isNotEmpty()) {
                entityBookShelfUniqueAbilitiesList.forEach { uniqueAbilities ->
                    when (uniqueAbilities.name) {
                        "추모" -> {
                            entity.myTeamPlayer().forEach {
                                attackDamageManager.run {
                                    it.attackDamage(2, Int.MAX_VALUE, "추모", true)
                                }
                            }
                        }
                    }
                }
            }

            if (entity.scoreboardTags.contains("의뢰 대상")) {
                playerBookShelf.addDisheveled(playerBookShelf.maxDisheveled)
                playerBookShelf.light(playerBookShelf.maxLight)
                entity.scoreboardTags.remove("의뢰 대상")
            }
        }
    }
}