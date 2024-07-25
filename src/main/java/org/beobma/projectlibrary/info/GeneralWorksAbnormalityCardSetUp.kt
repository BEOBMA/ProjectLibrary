@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.info.SetUp.Companion.abnormalityCardList
import org.beobma.projectlibrary.listener.OnDamageEvent.Companion.paleHandCount
import org.beobma.projectlibrary.manager.AbnormalStatusManager
import org.beobma.projectlibrary.text.TextManager
import org.beobma.projectlibrary.util.Util.getRandomNumberBetween
import org.beobma.projectlibrary.util.Util.getScore
import org.beobma.projectlibrary.util.Util.isTrueWithProbability
import org.beobma.projectlibrary.util.Util.percentageOf
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random

class GeneralWorksAbnormalityCardSetUp {
    init {
        abnormalityCardSetUp()
    }

    private fun abnormalityCardSetUp() {
        val textManager = TextManager()

        abnormalityCardList.addAll(listOf(
            AbnormalityCard("흉터",
                listOf(
                    "${ChatColor.GRAY}참격에 의해 받는 피해가 2~5 감소한다.", "${ChatColor.GRAY}피격 시 20% 확률로 피해를 0으로 만든다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.GeneralWorks,
                1,
                null,
                { _, _, _, _, _, _, _, _, _, _, playerBookShelf, entityBookShelf, _, _ ->
                    var finalDamage = 0.0
                    if (playerBookShelf.attackType == AttackType.Slashing) {
                        finalDamage -= getRandomNumberBetween(2, 5, entityBookShelf)
                    }
                    if (10.isTrueWithProbability()) {
                        finalDamage -= Int.MAX_VALUE
                    }
                    return@AbnormalityCard finalDamage
                }),

            AbnormalityCard("창백한 손",
                listOf(
                    "${ChatColor.GRAY}공격이 같은 대상에게 3번 명중할 때마다 ${textManager.goldenMessagetoGray("흐트러짐")} 피해 3~10을 추가로 준다.",
                    "${ChatColor.GRAY}다른 대상 공격시 초기화"
                ),
                EmotionType.Affirmation,
                LibraryFloor.GeneralWorks,
                1,
                null,
                null,
                { _, _, _, _, _, _, _, _, player, entity, playerBookShelf, _, _, _ ->
                    var finalDamage = 0.0
                    val damagerMap = paleHandCount.computeIfAbsent(player) { mutableMapOf() }
                    val hitCount = damagerMap.getOrDefault(entity, 0) + 1

                    if (hitCount >= 3) {
                        finalDamage += getRandomNumberBetween(3, 10, playerBookShelf)
                        damagerMap[entity] = 0
                    } else {
                        damagerMap[entity] = hitCount
                    }

                    for (otherTarget in damagerMap.keys.toList()) {
                        if (otherTarget != entity) {
                            damagerMap[otherTarget] = 0
                        }
                    }

                    return@AbnormalityCard finalDamage
                }),

            AbnormalityCard(
                "열망", listOf(
                    "${ChatColor.GRAY}최대 체력이 15% 증가한다.",
                    "${ChatColor.GRAY}${textManager.goldenMessagetoGray("이동 속도")}가 10%~20% 증가한다."
                ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 1, null, null, null, null
            ) { _, _, _, _, moveSpeedManager, _, _, _, player, playerBookShelf, maxHealthManager, _ ->
                maxHealthManager.run {
                    player.maxHealthPercentage(15, true)
                }

                moveSpeedManager.run {
                    player.moveSpeedPercentage(
                        getRandomNumberBetween(10, 20, playerBookShelf), Int.MAX_VALUE, "열망", true
                    )
                }
            },


            AbnormalityCard(
                "고동",
                listOf(
                    "${ChatColor.GRAY}가하는 피해가 1~2 증가한다.",
                    "${ChatColor.GRAY}10초마다 적에게 피해를 입히지 못하면 체력을 최대 체력의 25% 만큼 잃는다."
                ),
                EmotionType.Negative, LibraryFloor.GeneralWorks, 1,
                { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, _, _ ->
                    var finalDamage = 0.0
                    finalDamage += getRandomNumberBetween(1, 2, playerBookShelf)
                    player.getScore("beatingCounter").score = 0
                    return@AbnormalityCard finalDamage
                },
                ) { game, _, _, _, _, _, _, _, player, playerBookShelf, _, _->
                game.stageBukkitScheduler.add(
                    object : BukkitRunnable() {
                        override fun run() {
                            player.getScore("beatingCounter").score++
                            if (player.getScore("beatingCounter").score >= 200) {
                                playerBookShelf.paleDamage(player.maxHealth.percentageOf(25), player)
                                player.getScore("beatingCounter").score = 0
                            }
                        }
                    }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                )
            },
            AbnormalityCard(
                "거짓말", listOf(
                    "${ChatColor.GRAY}막 시작 시 자신의 체력을 이하와 같이 변경한다.",
                    "${ChatColor.GRAY}현재 체력을 최대 체력의 50% ~ 최대 체력의 150% 사이의 값으로 변경"
                ), EmotionType.Negative, LibraryFloor.GeneralWorks, 1, null, null
            ) { _, _, _, _, _, _, _, _, player, _, _, _->
                val randomPercentage = Random.nextInt(50, 151) / 100.0
                val newMaxHealth = (player.maxHealth * randomPercentage)
                player.maxHealth = newMaxHealth
                player.health = player.maxHealth
            },
            AbnormalityCard(
                "호기심", listOf(
                    "${ChatColor.GRAY}점프 시 ${textManager.goldenMessagetoGray("이동 속도")}가 2% 증가한다.",
                    "${ChatColor.GRAY}이 효과는 매 막마다 초기화된다."
                ), EmotionType.Negative, LibraryFloor.GeneralWorks, 1
            ),


            AbnormalityCard("학습",
                listOf(
                    "${ChatColor.GRAY}적중 시 현재 막의 수만큼 추가 피해를 입힌다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.GeneralWorks,
                2,
                { game, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
                    return@AbnormalityCard (game.act.toDouble())
                }),
            AbnormalityCard("서리검",
                listOf(
                    "${ChatColor.GRAY}적중 시 50% 확률로 1초간 대상의 ${textManager.goldenMessagetoGray("이동 속도")}를 10% 감소시킨다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.GeneralWorks,
                2,
                { _, _, _, _, moveSpeedManager, _, _, _, _, entity, _, _, _, _ ->
                    if (50.isTrueWithProbability()) {
                        moveSpeedManager.run {
                            entity.moveSpeedPercentage(10, 1, "서리검", false)
                        }
                    }
                    return@AbnormalityCard 0.0
                }),
            AbnormalityCard("못과 망치",
                listOf(
                    "${ChatColor.GRAY}관통 공격 적중 시 대상에게 ${textManager.goldenMessagetoGray("못")}을 부여한다.",
                    "${ChatColor.GRAY}못이 있는 대상이 타격에 공격당할 경우 5만큼 피해를 받는다.",
                    "${ChatColor.GRAY}이 효과 적용 후, 못을 제거한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.GeneralWorks,
                2,
                { game, _, _, _, _, _, _, _, _, entity, playerBookShelf, _, _, _ ->
                    if (playerBookShelf.attackType == AttackType.Piercing) {
                        entity.scoreboardTags.add("못")
                        game.stageEndBukkitScheduler.add { entity.scoreboardTags.remove("못") }
                    }
                    return@AbnormalityCard 0.0
                }),


            AbnormalityCard(
                "피", listOf(
                    "${ChatColor.GRAY}피격 시 받는 피해가 1~2 감소한다.",
                    "${ChatColor.GRAY}피격 시 ${textManager.goldenMessagetoGray("흐트러짐")} 피해 1~3을 추가로 받는다."
                ), EmotionType.Negative, LibraryFloor.GeneralWorks, 2, null,
                { _, _, _, _, _, _, _, _, _, entity, _, entityBookShelf, _, _ ->
                    var finalDamage = 0.0
                    finalDamage -= getRandomNumberBetween(1, 2, entityBookShelf)
                    entityBookShelf.removeDisheveled(getRandomNumberBetween(1, 3, entityBookShelf), entity)
                    return@AbnormalityCard finalDamage
                }
            ),

            AbnormalityCard(
                "몰아치는 박동", listOf(
                    "${ChatColor.GRAY}막 시작 시 이하의 효과를 얻으나, 매 막 시작 20초 후 사망한다.",
                    "${ChatColor.GRAY}가하는 피해가 20% 증가한다.",
                    "${textManager.goldenMessagetoGray("이동 속도")}가 20% 증가한다.",
                    "${textManager.goldenMessagetoGray("받는 피해")}가 20% 감소한다.",
                ), EmotionType.Negative, LibraryFloor.GeneralWorks, 2,
                { _, _, _, _, _, _, _, _, _, _, _, _, damage, _ ->
                    return@AbnormalityCard damage.percentageOf(20)
                },
                {_, _, _, _, _, _, _, _, _, _, _, _, damage, _ ->
                    return@AbnormalityCard -damage.percentageOf(20)
                }
            ) {game, _, _, _, moveSpeedManager, _, _, _, player, playerBookShelf, _, _ ->
                moveSpeedManager.run {
                    player.moveSpeedPercentage(20, Int.MAX_VALUE, "몰아치는 박동", true)
                }
                game.mapBukkitScheduler[player]?.get("몰아치는 박동")?.cancel()
                game.mapBukkitScheduler[player] = mutableMapOf(
                    Pair("몰아치는 박동", object : BukkitRunnable() {
                        override fun run() {
                            playerBookShelf.death(player)
                        }
                    }.runTaskLater(ProjectLibrary.instance, 400L))
                )
            },

            AbnormalityCard(
                "죄책감", listOf(
                    "${ChatColor.GRAY}받는 ${textManager.goldenMessagetoGray("흐트러짐")} 피해가 1~3 감소한다.",
                    "${ChatColor.GRAY}공격으로 받은 피해만큼 공격자에게 ${textManager.goldenMessagetoGray("흐트러짐")} 피해를 준다.",
                ), EmotionType.Negative, LibraryFloor.GeneralWorks, 2, null,
                { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, damage, _ ->
                    playerBookShelf.removeDisheveled(damage.toInt(), player)
                    return@AbnormalityCard 0.0
                }
                , null,
                { _, _, _, _, _, _, _, _, _, _, _, entityBookShelf, _, _ ->
                    return@AbnormalityCard -(getRandomNumberBetween(1, 3, entityBookShelf)).toDouble()
                }
            ),


            AbnormalityCard(
                "입맞춤", listOf(
                    "${ChatColor.GRAY}적중 시 50% 확률로 1초간 대상의 ${textManager.goldenMessagetoGray("공격 속도")}를 10% 감소시킨다."
                ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 3,
                { _, _, _, _, _, attackSpeedManager, _, _, _, entity, _, _, _, _ ->
                    if (50.isTrueWithProbability()) {
                        attackSpeedManager.run {
                            entity.attackSpeedPercentage(10, 1, "입맞춤", false)
                        }
                    }
                    return@AbnormalityCard 0.0
                }
            ),

            AbnormalityCard(
                "꼭두각시", listOf(
                    "${ChatColor.GRAY}피격 시 받는 ${textManager.goldenMessagetoGray("흐트러짐")} 피해가 1 감소한다."
                ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 3, null, null,null,
                { _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
                    return@AbnormalityCard -1.0
                }
            ),

            AbnormalityCard(
                "동맥", listOf(
                    "${textManager.goldenMessagetoGray("공격 속도")}가 10% 증가한다."
                ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 3
            ) { _, _, _, _, _, attackSpeedManager, _, _, player, _, _, _->
                attackSpeedManager.run {
                    player.attackSpeedPercentage(10, Int.MAX_VALUE, "동맥", true)
                }
            },

            AbnormalityCard(
                "서리조각", listOf(
                    "${ChatColor.GRAY}피격 시 1초간 대상의 ${textManager.goldenMessagetoGray("이동 속도")}를 15% 감소시킨다."
                ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 3, null,
                { _, _, _, _, moveSpeedManager, _, _, _, player, _, _, _, _, _ ->
                    moveSpeedManager.run {
                        player.moveSpeedPercentage(15, 1, "서리조각", false)
                    }
                    return@AbnormalityCard 0.0
                }
            ),


            AbnormalityCard(
                "눈보라", listOf(
                    textManager.oneTimeMessage(),
                    "${ChatColor.GRAY}이번 막 시작시 30초간 자신을 제외한 모든 대상에게 ${textManager.goldenMessagetoGray("이동 불가")} 효과를 적용한다.",
                    "${ChatColor.GRAY}만약, 서리검 환상체 책장을 보유했다면 추가로 ${textManager.goldenMessagetoGray("공격 불가")} 효과를 적용한다.",
                ), EmotionType.Negative, LibraryFloor.GeneralWorks, 3
            ) { game, unableAttackManager, _, unableMoveManager, _, _, _, _, player, playerBookShelf, _, _->
                if (playerBookShelf.abnormalityCards.contains(playerBookShelf.abnormalityCards.find { it.name == "서리검" })) {
                    game.players.forEach { players ->
                        if (player != players) {
                            unableMoveManager.run { players.addUnableMove() }
                            unableAttackManager.run { players.addUnableAttack() }
                            game.mapBukkitScheduler[players]?.get("눈보라")?.cancel()
                            game.mapBukkitScheduler[players] = mutableMapOf(
                                Pair("눈보라", object : BukkitRunnable() {
                                    override fun run() {
                                        unableMoveManager.run { players.removeUnableMove() }
                                        unableAttackManager.run { players.removeUnableAttack() }
                                    }
                                }.runTaskLater(ProjectLibrary.instance, 600L))
                            )
                        }
                    }
                } else {
                    Info.game!!.players.forEach { players ->
                        AbnormalStatusManager().run {
                            if (player != players) {
                                unableMoveManager.run { players.addUnableMove() }

                                Info.game!!.mapBukkitScheduler[players]?.get("눈보라")?.cancel()
                                Info.game!!.mapBukkitScheduler[players] = mutableMapOf(
                                    Pair("눈보라", object : BukkitRunnable() {
                                        override fun run() {
                                            unableMoveManager.run { players.removeUnableMove() }
                                        }
                                    }.runTaskLater(ProjectLibrary.instance, 200L))
                                )
                            }
                        }
                    }
                }
            },

            AbnormalityCard(
                "눈빛", listOf(
                    "${ChatColor.GRAY}가하는 피해가 1.25배가 된다.", "${ChatColor.GRAY}받는 피해가 1.25배가 된다."
                ), EmotionType.Negative, LibraryFloor.GeneralWorks, 3,
                { _, _, _, _, _, _, _, _, _, _, _, _, damage, _ ->
                    return@AbnormalityCard damage * 1.25
                },
                { _, _, _, _, _, _, _, _, _, _, _, _, damage, _ ->
                    return@AbnormalityCard damage * 1.25
                }
            ),

            AbnormalityCard(
                "손목긋개", listOf(
                    "${ChatColor.GRAY}공격 적중시 적에게 2의 피해를 추가로 입힌다.", "${ChatColor.GRAY}공격 적중시 자신은 1의 피해를 입는다."
                ), EmotionType.Negative, LibraryFloor.GeneralWorks, 3,
                { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, _, _ ->
                    playerBookShelf.paleDamage(1.0, player)
                    return@AbnormalityCard 2.0
                }
            ),
        ))
    }
}