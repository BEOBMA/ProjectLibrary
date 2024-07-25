@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.info.SetUp.Companion.abnormalityCardList
import org.beobma.projectlibrary.listener.OnDamageEvent.Companion.happyMemoriesCount
import org.beobma.projectlibrary.listener.OnDamageEvent.Companion.oldHugCount
import org.beobma.projectlibrary.text.TextManager
import org.beobma.projectlibrary.util.Util.enemyTeamPlayer
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.getRandomNumberBetween
import org.beobma.projectlibrary.util.Util.getScore
import org.beobma.projectlibrary.util.Util.isTrueWithProbability
import org.beobma.projectlibrary.util.Util.percentageOf
import org.beobma.projectlibrary.util.Util.shootLaser
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable

class HistoryAbnormalityCardSetUp {
    init {
        abnormalityCardSetUp()
    }

    private fun abnormalityCardSetUp() {
        val textManager = TextManager()

        abnormalityCardList.addAll(
            listOf(
                AbnormalityCard(
                    "재", listOf(
                        "${ChatColor.GRAY}피격 시 공격자에게 ${textManager.redMessagetoGray("화상")} 1~3을 부여한다."
                    ), EmotionType.Affirmation, LibraryFloor.History, 1, null,
                    { _, _, _, _, _, _, burnManager, _, player, _, _, entityBookShelf, _, _ ->
                        burnManager.run {
                            player.addBurn(getRandomNumberBetween(1, 3, entityBookShelf))
                        }
                        return@AbnormalityCard 0.0
                    }
                ),

                AbnormalityCard(
                    "그리웠던 옛날의 포옹", listOf(
                        "${ChatColor.GRAY}적중 시 20% 확률로 5의 ${textManager.goldenMessagetoGray("흐트러짐")} 피해를 입힌다.",
                        "${ChatColor.GRAY}동일한 대상을 공격할 때마다 확률이 증가한다.",
                        "${ChatColor.GRAY}이 효과가 발동한 후, 확률이 초기화된다.",
                    ), EmotionType.Affirmation, LibraryFloor.History, 1,
                    { _, _, _, _, _, _, _, _, player, entity, _, entityBookShelf, _, _ ->
                        val damagerMap = oldHugCount.computeIfAbsent(player) { mutableMapOf() }
                        val hitCount = damagerMap.getOrDefault(entity, 0) + 1

                        if ((hitCount * 20).isTrueWithProbability()) {
                            entityBookShelf.removeDisheveled(3, entity)
                            damagerMap[entity] = 0
                        }

                        for (otherTarget in damagerMap.keys.toList()) {
                            if (otherTarget != entity) {
                                damagerMap[otherTarget] = 0
                            }
                        }
                        return@AbnormalityCard 0.0
                    }
                ),

                AbnormalityCard(
                    "행복한 기억", listOf(
                        "${ChatColor.GRAY}동일한 대상을 공격할 때마다 ${textManager.darkGreenMessagetoGray("긍정 감정")}을 누적한다."
                    ), EmotionType.Affirmation, LibraryFloor.History, 1,
                    { _, _, _, _, _, _, _, _, player, entity, playerBookShelf, _, _, _ ->
                        val previousEntry = happyMemoriesCount[player]

                        if (previousEntry != null && previousEntry == entity) {
                            playerBookShelf.emotion++
                        } else {
                            happyMemoriesCount[player] = entity
                        }
                        return@AbnormalityCard 0.0
                    }
                ),


                AbnormalityCard(
                    "성냥불", listOf(
                        "${ChatColor.GRAY}적중 시 자신에게 ${textManager.redMessagetoGray("불씨")}를 부여한다.",
                        "${ChatColor.GRAY}적중 시 ${textManager.redMessagetoGray("불씨")}의 절반 만큼 추가 피해를 입힌다.",
                        "${ChatColor.GRAY}불씨가 4 이상이면 25% 확률로 자신은 불씨 만큼의 피해를 입는다."
                    ), EmotionType.Negative, LibraryFloor.History, 1,
                    { game, _, _, _, _, _, _, _, player, _, playerBookShelf, _, _, _ ->
                        player.getScore("matchFireCount").score++

                        if (player.getScore("matchFireCount").score >= 4) {
                            if (25.isTrueWithProbability()) {
                                playerBookShelf.paleDamage(player.getScore("matchFireCount").score.toDouble(), player)
                            }
                        }
                        game.stageEndBukkitScheduler.add { player.getScore("matchFireCount").score = 0 }
                        return@AbnormalityCard (player.getScore("matchFireCount").score / 2).toDouble()
                    }
                ),

                AbnormalityCard(
                    "애정 표현", listOf(
                        "${ChatColor.GRAY}적중 시 대상이 자신을 바라보고 있지 않았다면 2의 추가 피해를 입힌다.",
                        "${ChatColor.GRAY}적중 시 대상이 자신을 바라보고 있었다면 가하는 피해가 2 감소한다."
                    ), EmotionType.Negative, LibraryFloor.History, 1,
                    { _, _, _, _, _, _, _, _, player, entity, _, _, _, _ ->
                        val entityShoot = shootLaser(entity)

                        if (entityShoot == player) {
                            return@AbnormalityCard -2.0
                        } else {
                            return@AbnormalityCard 2.0
                        }
                    }
                ),

                AbnormalityCard(
                    "요정들의 보살핌", listOf(
                        "${ChatColor.GRAY}막 시작 시 최대 체력이 30% 증가한다.",
                        "${ChatColor.GRAY}막 시작 20초간 동안 5번 이상 피격 시, 현재 체력의 30%를 잃는다.",
                    ), EmotionType.Negative, LibraryFloor.History, 1
                ) { game, _, _, _, _, _, _, _, player, _, maxHealthManager, _ ->
                    maxHealthManager.run {
                        player.maxHealthPercentage(30, true)
                    }
                    player.scoreboardTags.add("fairyCare")
                    game.stageEndBukkitScheduler.add { player.scoreboardTags.remove("fairyCare") }

                    game.mapBukkitScheduler[player] = mutableMapOf(
                        Pair("요정들의 보살핌", object : BukkitRunnable() {
                            override fun run() {
                                player.scoreboardTags.remove("fairyCare")
                            }
                        }.runTaskLater(ProjectLibrary.instance, 400L))
                    )
                },


                AbnormalityCard(
                    "식탐", listOf(
                        "${ChatColor.GRAY}적중 시 대상의 체력이 50% 이하라면, 피해량이 2 증가하고 자신의 체력을 3 회복한다."
                    ), EmotionType.Affirmation, LibraryFloor.History, 2,
                    { _, _, _, _, _, _, _, _, player, entity, playerBookShelf, _, _, _ ->
                        if (entity.maxHealth <= entity.maxHealth.percentageOf(50)) {
                            playerBookShelf.heal(3.0, player)
                            return@AbnormalityCard 2.0
                        }
                        return@AbnormalityCard 0.0
                    }
                ),

                AbnormalityCard(
                    "포자", listOf(
                        "${ChatColor.GRAY}피격 시 공격자에게 ${textManager.redMessagetoGray("화상")} 1을 부여한다.",
                        "${ChatColor.GRAY}피격 시 공격자에게 ${textManager.darkRedMessagetoGray("출혈")} 1을 부여한다.",
                    ), EmotionType.Affirmation, LibraryFloor.History, 2, null,
                    { _, _, _, _, _, _, burnManager, bleedingManager, player, _, _, _, _, _ ->
                        burnManager.run {
                            player.addBurn(1)
                        }
                        bleedingManager.run {
                            player.addBleeding(1)
                        }
                        return@AbnormalityCard 0.0
                    }
                ),

                AbnormalityCard(
                    "덩굴", listOf(
                        textManager.oneTimeMessage(),
                        "${ChatColor.GRAY}이번 막 시작 시 모든 적의 ${textManager.goldenMessagetoGray("이동 속도")}가 40% 감소한다."
                    ), EmotionType.Affirmation, LibraryFloor.History, 2
                ) { _, _, _, _, moveSpeedManager, _, _, _, player, _, _, _ ->
                    player.enemyTeamPlayer().forEach {
                        moveSpeedManager.run {
                            it.moveSpeedPercentage(40, Int.MAX_VALUE, "덩굴", false)
                        }
                    }
                },


                AbnormalityCard(
                    "발걸음", listOf(
                        "${ChatColor.GRAY}피격 시 체력이 20% 이하인 경우, 공격자에게 자신 최대 체력의 30% 만큼 피해를 준다.",
                        "${ChatColor.GRAY}이후 자신은 사망한다."
                    ), EmotionType.Negative, LibraryFloor.History, 2, null,
                    { _, _, _, _, _, _, _, _, player, entity, playerBookShelf, entityBookShelf, _, _ ->
                        if (entity.health <= entity.maxHealth.percentageOf(20)) {
                            playerBookShelf.paleDamage(entity.maxHealth.percentageOf(30), player)
                            entityBookShelf.death(entity)
                        }
                        return@AbnormalityCard 0.0
                    }
                ),

                AbnormalityCard(
                    "포식", listOf(
                        "${ChatColor.GRAY}막 시작시 체력을 최대 체력의 30% 만큼 소모한다.", "${ChatColor.GRAY}이번 막동안 피해량이 3 증가한다."
                    ), EmotionType.Negative, LibraryFloor.History, 2,
                    { _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
                        return@AbnormalityCard 3.0
                    }
                ) {_, _, _, _, _, _, _, _, player, playerBookShelf, _, _ ->
                    playerBookShelf.paleDamage(player.maxHealth.percentageOf(30), player)
                },

                AbnormalityCard(
                    "일벌", listOf(
                        "${ChatColor.GRAY}피격 시 대상에게 표적이 찍힌다.",
                        "${ChatColor.GRAY}해당 적을 아군이 공격할 때 피해량이 2 증가한다.",
                        "${ChatColor.GRAY}효과 적용 후, 표적이 제거된다."
                    ), EmotionType.Negative, LibraryFloor.History, 2, null,
                    { game, _, _, _, _, _, _, _, player, entity, _, _, _, _ ->
                        player.scoreboardTags.add("일벌 표식")
                        game.stageEndBukkitScheduler.add { entity.scoreboardTags.remove("일벌 표식") }
                        return@AbnormalityCard 0.0
                    }
                ),


                AbnormalityCard(
                    "충성심", listOf(
                        "${ChatColor.GRAY}자신의 잃은 체력에 비례하여 피해량이 증가한다."
                    ), EmotionType.Negative, LibraryFloor.History, 3,
                    { _, _, _, _, _, _, _, _, player, _, _, _, _, _ ->
                        return@AbnormalityCard when {
                            player.health >= player.maxHealth.percentageOf(80) -> 1.0
                            player.health >= player.maxHealth.percentageOf(60) -> 2.0
                            player.health >= player.maxHealth.percentageOf(40) -> 3.0
                            else -> 4.0
                        }
                    }
                ),

                AbnormalityCard(
                    "4번째 성냥불", listOf(
                        "${ChatColor.GRAY}적중 시 ${textManager.redMessagetoGray("화상")} 1을 부여한다.",
                        "${ChatColor.GRAY}50% 확률로 ${textManager.redMessagetoGray("화상")} 1을 얻는다.",
                    ), EmotionType.Negative, LibraryFloor.History, 3,
                    { _, _, _, _, _, _, burnManager, _, player, entity, _, _, _, _ ->
                        burnManager.run {
                            entity.addBurn(1)
                        }
                        if (50.isTrueWithProbability()) {
                            burnManager.run {
                                player.addBurn(1)
                            }
                        }
                        return@AbnormalityCard 0.0
                    }
                ),

                AbnormalityCard(
                    "잊혀짐", listOf(
                        "${ChatColor.GRAY}막 시작 시 ${textManager.redMessagetoGray("부정 감정")} 5를 누적한다."
                    ), EmotionType.Negative, LibraryFloor.History, 3
                ) { _, _, _, _, _, _, _, _, _, playerBookShelf, _ , _->
                    playerBookShelf.emotion -= 5
                },


                AbnormalityCard(
                    "가시 장벽", listOf(
                        "${ChatColor.GRAY}피격 시 공격자에게 2의 피해를 준다."
                    ), EmotionType.Affirmation, LibraryFloor.History, 3, null,
                    { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, _, _ ->
                        playerBookShelf.paleDamage(2.0, player)
                        return@AbnormalityCard 0.0
                    }
                ),

                AbnormalityCard(
                    "악의", listOf(
                        textManager.oneTimeMessage(), "${ChatColor.GRAY}이번 막 시작 시 모든 적에게 10의 피해를 입힌다."
                    ), EmotionType.Affirmation, LibraryFloor.History, 3
                ) { _, _, _, _, _, _, _, _, player, _, _, _ ->
                    player.enemyTeamPlayer().forEach {
                        it.getMainBookShelf()?.paleDamage(10.0, player)
                    }
                },

                AbnormalityCard(
                    "날갯짓", listOf(
                        "${ChatColor.GRAY}적중 시 순수 피해량의 25% 만큼 회복한다."
                    ), EmotionType.Negative, LibraryFloor.History, 3,
                    { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, damage, _ ->
                        playerBookShelf.heal(damage.percentageOf(25), player)
                        return@AbnormalityCard 0.0
                    }
                )
            )
        )
    }
}