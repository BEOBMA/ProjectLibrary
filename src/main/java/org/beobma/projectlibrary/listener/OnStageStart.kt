@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.event.OnStageStartEvent
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.manager.AbnormalStatusManager
import org.beobma.projectlibrary.util.Util.enemyTeamPlayer
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.getScore
import org.beobma.projectlibrary.util.Util.isTrueWithProbability
import org.beobma.projectlibrary.util.Util.myTeamPlayer
import org.beobma.projectlibrary.util.Util.percentageOf
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable

class OnStageStart : Listener {
    @EventHandler
    fun onStageStartEvent(event: OnStageStartEvent) {
        val game = Info.game ?: return

        val abnormalStatusManager = AbnormalStatusManager()
        val attackSpeedManager = abnormalStatusManager.createAttackSpeedManager()
        val maxHealthManager = abnormalStatusManager.createMaxHealthManager()
        val moveSpeedManager = abnormalStatusManager.createMoveSpeedManager()
        val unableAttackManager = abnormalStatusManager.createUnableAttackManager()
        val unableMoveManager = abnormalStatusManager.createUnableMoveManager()
        val attackDamageManager = abnormalStatusManager.createAttackDamageManager()
        val disheveledManager = abnormalStatusManager.createDisheveledManager()
        val burnManager = abnormalStatusManager.createBurnManager()
        val bleedingManager = abnormalStatusManager.createBleedingManager()

        Info.game!!.players.forEach { player ->
            val playerBookShelfUniqueAbilitiesList = player.getMainBookShelf()!!.uniqueAbilities
            val playerBookShelfList = player.getMainBookShelf()!!.abnormalityCards
            val playerBookShelf = player.getMainBookShelf()!!

            if (playerBookShelfList.isNotEmpty()) {
                playerBookShelfList.forEach { abnormalityCard ->
                    abnormalityCard.stageStartUnit?.invoke(game, unableAttackManager, disheveledManager, unableMoveManager, moveSpeedManager, attackSpeedManager, burnManager, bleedingManager, player, playerBookShelf, maxHealthManager, attackDamageManager)
                }
            }

            if (playerBookShelfUniqueAbilitiesList.isNotEmpty()) {
                playerBookShelfUniqueAbilitiesList.forEach { uniqueAbilities ->
                    when (uniqueAbilities.name) {
                        "어설픈 용기" -> {
                            if (50.isTrueWithProbability()) {
                                attackDamageManager.run {
                                    player.attackDamage(1, Int.MAX_VALUE, "어설픈 용기", true)
                                }
                            }
                        }

                        "비상 식량" -> {
                            game.mapBukkitScheduler[player]?.get("비상 식량")?.cancel()
                            game.mapBukkitScheduler[player] = mutableMapOf(
                                Pair("비상 식량", object : BukkitRunnable() {
                                    override fun run() {
                                        playerBookShelf.heal(5.0, player)
                                    }
                                }.runTaskTimer(ProjectLibrary.instance, 0L, 100L))
                            )
                        }

                        "속도" -> {
                            moveSpeedManager.run {
                                player.moveSpeedPercentage(10, Int.MAX_VALUE, "속도", true)
                            }
                        }

                        "심호흡" -> {
                            if (25.isTrueWithProbability()) {
                                playerBookShelf.light(1)
                            }
                        }

                        "휴식" -> {
                            game.stageBukkitScheduler.add(
                                object : BukkitRunnable() {
                                    override fun run() {
                                        player.getScore("restCounter").score++
                                        if (player.getScore("restCounter").score >= 200) {
                                            playerBookShelf.light(1)
                                            player.getScore("restCounter").score = 0
                                        }
                                    }
                                }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                            )
                        }

                        "난사" -> {
                            attackDamageManager.run {
                                player.attackDamage(2, 20, "난사", true)
                            }
                        }

                        "저격" -> {
                            attackDamageManager.run {
                                player.attackDamage(3, 20, "저격", true)
                            }
                        }

                        "집중" -> {
                            attackDamageManager.run {
                                player.attackDamage(1, 20, "집중", true)
                            }
                        }

                        "고독한 해결사" -> {
                            val playerTeam = player.myTeamPlayer()

                            if (playerTeam.size == 1) {
                                attackDamageManager.run {
                                    player.attackDamage(3, Int.MAX_VALUE, "고독한 해결사", true)
                                }
                            }
                        }

                        "수호자" -> {
                            val playerTeam = player.myTeamPlayer()
                            val newTeam = playerTeam.shuffled()

                            newTeam[1].scoreboardTags.add("수호자")
                            newTeam[2].scoreboardTags.add("수호자")
                            newTeam[3].scoreboardTags.add("수호자")

                            game.stageBukkitScheduler.add(
                                object : BukkitRunnable() {
                                    override fun run() {
                                        newTeam[1].scoreboardTags.remove("수호자")
                                        newTeam[2].scoreboardTags.remove("수호자")
                                        newTeam[3].scoreboardTags.remove("수호자")
                                    }
                                }.runTaskLater(ProjectLibrary.instance, 600L)
                            )

                            game.stageEndBukkitScheduler.add {
                                newTeam[1].scoreboardTags.remove("수호자")
                                newTeam[2].scoreboardTags.remove("수호자")
                                newTeam[3].scoreboardTags.remove("수호자")
                            }
                        }

                        "불안정한 충전" -> {
                            if (50.isTrueWithProbability()) {
                                attackDamageManager.run {
                                    player.attackDamage(1, Int.MAX_VALUE, "불안정한 충전", true)
                                }
                            }
                        }

                        "찌릿찌릿" -> {
                            val playerTeam = player.enemyTeamPlayer()
                            val newTeam = playerTeam.shuffled()

                            moveSpeedManager.run {
                                newTeam[1].moveSpeedPercentage(10, 30, "찌릿찌릿", false)
                                newTeam[2].moveSpeedPercentage(10, 30, "찌릿찌릿", false)
                                newTeam[3].moveSpeedPercentage(10, 30, "찌릿찌릿", false)
                            }
                        }

                        "임전" -> {
                            playerBookShelf.light(1)
                        }

                        "야옹야옹~" -> {
                            val playerTeam = player.myTeamPlayer()
                            val newTeam = playerTeam.shuffled()

                            attackDamageManager.run {
                                newTeam[1].attackDamage(1, Int.MAX_VALUE, "야옹야옹~", true)
                                newTeam[2].attackDamage(1, Int.MAX_VALUE, "야옹야옹~", true)
                            }
                        }

                        "호흡" -> {
                            if (playerBookShelf.light == 0) {
                                playerBookShelf.light(1)
                            }
                        }

                        "키즈나 / 극한의 피로" -> {
                            playerBookShelf.paleDamage(playerBookShelf.maxHealth.percentageOf(25), player)
                        }

                        "속도 2" -> {
                            moveSpeedManager.run {
                                player.moveSpeedPercentage(20, Int.MAX_VALUE, "속도 2", true)
                            }
                        }

                        "과호흡 / 탈진" -> {
                            moveSpeedManager.run {
                                player.moveSpeedPercentage(10, Int.MAX_VALUE, "과호흡 / 탈진", false)
                            }
                        }

                        "키즈나 / 사의 피로" -> {
                            playerBookShelf.paleDamage(playerBookShelf.maxHealth.percentageOf(75), player)
                        }
                    }
                }
            }
        }
    }
}