@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.info.SetUp.Companion.abnormalityCardList
import org.beobma.projectlibrary.listener.OnDamageEvent.Companion.friendMarker
import org.beobma.projectlibrary.manager.AbnormalStatusManager
import org.beobma.projectlibrary.text.TextManager
import org.beobma.projectlibrary.util.Util.enemyTeamPlayer
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.getRandomNumberBetween
import org.beobma.projectlibrary.util.Util.getScore
import org.beobma.projectlibrary.util.Util.isTrueWithProbability
import org.beobma.projectlibrary.util.Util.myTeamPlayer
import org.beobma.projectlibrary.util.Util.percentageOf
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class NaturalSciencesAbnormalityCardSetUp {
    init {
        abnormalityCardSetUp()
    }

    private fun abnormalityCardSetUp() {
        val textManager = TextManager()

        abnormalityCardList.addAll(listOf(AbnormalityCard("사랑",
            listOf(
                "${ChatColor.GRAY}공격 적중 시 무작위 아군의 체력이 3~5 회복된다."
            ),
            EmotionType.Affirmation,
            LibraryFloor.Art,
            1,
            { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, _, _ ->
                val playerTeam = player.myTeamPlayer().random()
                playerTeam.getMainBookShelf()
                    ?.heal(getRandomNumberBetween(3, 5, playerBookShelf).toDouble(), playerTeam)
                return@AbnormalityCard 0.0
            }),

            AbnormalityCard("정의",
                listOf(
                    "${ChatColor.GRAY}자신에게 피해를 입힌 적에게 악당 표적이 찍힌다.",
                    "${ChatColor.GRAY}악당 표적이 찍힌 적을 공격 시 피해량이 3~5 증가한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Art,
                1,
                { _, _, _, _, _, _, _, _, _, entity, playerBookShelf, _, _, _ ->
                    if (entity.scoreboardTags.contains("악당 표적")) {
                        return@AbnormalityCard getRandomNumberBetween(3, 5, playerBookShelf).toDouble()
                    }
                    return@AbnormalityCard 0.0
                },
                { game, _, _, _, _, _, _, _, player, _, _, _, _, _ ->
                    player.scoreboardTags.add("악당 표적")
                    game.stageEndBukkitScheduler.add { player.scoreboardTags.remove("악당 표적") }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("눈물로 벼려낸 검",
                listOf(
                    "${ChatColor.GRAY}관통 공격 적중 시 피해량이 8 이상이라면 대상의 최대 체력의 10% 만큼 추가 피해를 준다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Art,
                1,
                { _, _, _, _, _, _, _, _, _, entity, playerBookShelf, _, damage, _ ->
                    if (playerBookShelf.attackType == AttackType.Piercing) {
                        if (damage >= 8) {
                            return@AbnormalityCard entity.maxHealth.percentageOf(10)
                        }
                    }
                    return@AbnormalityCard 0.0
                }),


            AbnormalityCard("증오",
                listOf(
                    "${ChatColor.GRAY}피격 시 ${textManager.disheveledMessage()} 피해 2~4를 받는다.",
                    "${ChatColor.GRAY}대신, 피격 시 5초간 ${textManager.powerMessage()} 1을 얻는다."
                ),
                EmotionType.Negative,
                LibraryFloor.Art,
                1,
                null,
                null,
                null,
                { _, _, _, _, _, _, _, _, _, entity, _, entityBookShelf, _, _ ->
                    AbnormalStatusManager().createAttackDamageManager().run {
                        entity.attackDamage(1, 5, "증오", true)
                    }
                    if (entity.scoreboardTags.contains("nihilismMark")) {
                        return@AbnormalityCard 0.0
                    }
                    return@AbnormalityCard getRandomNumberBetween(2, 4, entityBookShelf).toDouble()
                }),

            AbnormalityCard("가호",
                listOf(
                    "${ChatColor.GRAY}피격 시 공격자의 공격 속성이 자신의 속성과 일치하면 받는 피해가 절반으로 감소한다."
                ),
                EmotionType.Negative,
                LibraryFloor.Art,
                1,
                null,
                { _, _, _, _, _, _, _, _, _, _, playerBookShelf, entityBookShelf, damage, _ ->
                    if (playerBookShelf.attackType == entityBookShelf.attackType) {
                        return@AbnormalityCard (damage / 2) - damage
                    }
                    return@AbnormalityCard 5.0
                }),

            AbnormalityCard("절망",
                listOf(
                    "${ChatColor.GRAY}사망 시 모든 아군이 ${textManager.powerMessage()} 3을 얻는다."
                ),
                EmotionType.Negative,
                LibraryFloor.Art,
                1,
                null,
                null,
                null,
                null,
                null,
                { _, _, _, _, _, _, _, _, player, entity, _, _, _, _ ->
                    if (!player.scoreboardTags.contains("nihilismMark")) {
                        AbnormalStatusManager().createAttackDamageManager().run {
                            entity.myTeamPlayer().forEach {
                                it.attackDamage(3, Int.MAX_VALUE, "절망", true)
                            }
                        }
                    }
                }) { _, _, _, _, _, _, _, _, player, _, _, _ ->
                if (player.scoreboardTags.contains("nihilismMark")) {
                    AbnormalStatusManager().createAttackDamageManager().run {
                        player.myTeamPlayer().forEach {
                            it.attackDamage(3, Int.MAX_VALUE, "절망", true)
                        }
                    }
                }
            },


            AbnormalityCard("무절제",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 10% 확률로 3초간 대상을 공격 불능 상태로 만든다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Art,
                2,
                { game, unableAttackManager, _, _, _, _, _, _, _, entity, _, _, _, _ ->
                    if (10.isTrueWithProbability()) {
                        unableAttackManager.run {
                            entity.addUnableAttack()
                        }
                        game.mapBukkitScheduler[entity] = mutableMapOf(
                            Pair("무절제", object : BukkitRunnable() {
                                override fun run() {
                                    unableAttackManager.run {
                                        entity.removeUnableAttack()
                                    }
                                }
                            }.runTaskLater(ProjectLibrary.instance, 60L))
                        )
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("행복의 길", listOf(
                "${ChatColor.GRAY}공격 적중 횟수에 따라 이번 막동안 받는 피해가 최대 5까지 감소한다."
            ), EmotionType.Affirmation, LibraryFloor.Art, 2, { _, _, _, _, _, _, _, _, player, _, _, _, _, _ ->
                if (player.getScore("pathOfHappinessCounter").score >= 5) {
                    player.getScore("pathOfHappinessCounter").score = 5
                } else {
                    player.getScore("pathOfHappinessCounter").score++
                }
                return@AbnormalityCard 0.0
            }, { _, _, _, _, _, _, _, _, _, entity, _, _, _, _ ->
                return@AbnormalityCard -(entity.getScore("pathOfHappinessCounter").score.toDouble())
            }),

            AbnormalityCard("벗",
                listOf(
                    "${ChatColor.GRAY}생존한 벗이 없을 경우, 처음 공격하는 적에게 벗 표식을 부여한다.",
                    "${ChatColor.GRAY}벗 표식을 가진 대상을 처치하면 모든 아군의 빛/체력/흐트러짐이 3/10/10 만큼 회복된다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Art,
                2,
                { _, _, _, _, _, _, _, _, player, entity, _, _, _, _ ->
                    if (friendMarker[player] !is Player) {
                        friendMarker[player] = entity
                    } else {
                        if (friendMarker[player]?.gameMode != GameMode.ADVENTURE) {
                            friendMarker[player] = entity
                        }
                    }

                    return@AbnormalityCard 0.0
                },
                null,
                null,
                null,
                { _, _, _, _, _, _, _, _, player, entity, playerBookShelf, _, _, _ ->
                    if (friendMarker[player] == entity) {
                        playerBookShelf.light(3)
                        playerBookShelf.heal(10.0, player)
                        playerBookShelf.addDisheveled(10)
                    }
                }),


            AbnormalityCard("탐욕",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 피해량이 10 이상이면 피해량이 3~7 증가하고, 체력을 2~5 회복한다."
                ),
                EmotionType.Negative,
                LibraryFloor.Art,
                2,
                { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, damage, _ ->
                    if (player.scoreboardTags.contains("nihilismMark")) {
                        playerBookShelf.heal(getRandomNumberBetween(2, 5, playerBookShelf).toDouble(), player)
                        return@AbnormalityCard getRandomNumberBetween(3, 7, playerBookShelf).toDouble()
                    } else {
                        if (damage >= 10) {
                            playerBookShelf.heal(getRandomNumberBetween(2, 5, playerBookShelf).toDouble(), player)
                            return@AbnormalityCard getRandomNumberBetween(3, 7, playerBookShelf).toDouble()
                        }
                    }

                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("분노", listOf(
                "${ChatColor.GRAY}매 막마다 ${textManager.powerMessage()} 2를 얻고, ${textManager.lightMessage()} 2를 회복한다.",
                "${ChatColor.GRAY}단, 공격 적중 시 무작위 아군이 2의 피해를 입는다."
            ), EmotionType.Negative, LibraryFloor.Art, 2, { _, _, _, _, _, _, _, _, player, _, _, _, _, _ ->
                if (!player.scoreboardTags.contains("nihilismMark")) {
                    val playerT = player.myTeamPlayer().random()
                    playerT.getMainBookShelf()?.paleDamage(2.0, playerT)
                }
                return@AbnormalityCard 0.0
            }) { _, _, _, _, _, _, _, _, player, playerBookShelf, _, attackDamageManager ->
                attackDamageManager.run {
                    player.attackDamage(2, Int.MAX_VALUE, "분노", true)
                }
                playerBookShelf.light(2)
            },

            AbnormalityCard(
                "마법소녀",
                listOf(
                    textManager.oneTimeMessage(),
                    "${ChatColor.GRAY}막 종료 시, 자연과학의 층 1막부터 다시 시작한다.",
                    "${ChatColor.GRAY}이 환상체 책장은 한 게임에 최대 3번까지 등장한다."
                ),
                EmotionType.Negative,
                LibraryFloor.Art,
                2,
            ) { game, _, _, _, _, _, _, _, player, _, _, _ ->
                game.act = 0
                if (game.players.find { it.scoreboardTags.contains("magicalGirl1") } == null) {
                    player.scoreboardTags.add("magicalGirl1")
                    player.scoreboard.getObjective("magicalGirlCounter")!!.getScore("1").score++
                    if (player.scoreboard.getObjective("magicalGirlCounter")!!.getScore("1").score >= 3) {
                        abnormalityCardList.remove(abnormalityCardList.find { it.name == "마법소녀" })
                    }
                    game.stageEndBukkitScheduler.add { player.scoreboardTags.remove("magicalGirl1") }
                }
            },


            AbnormalityCard(
                "독액", listOf(
                    "${ChatColor.GRAY}막 시작 시 모든 적에게 부식 5를 부여한다.",
                    "${ChatColor.GRAY}부식: 피격 시 수치만큼 피해를 입고 수치가 1 감소한다."
                ), EmotionType.Affirmation, LibraryFloor.Art, 3
            ) { game, _, _, _, _, _, _, _, player, _, _, _ ->
                player.enemyTeamPlayer().forEach {
                    it.getScore("corrosion").score = 5
                    game.stageEndBukkitScheduler.add { it.getScore("corrosion").score = 0 }
                }
            },

            AbnormalityCard("d",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 적이 ${textManager.disheveledMessage()} 상태라면 만들면 대상의 ${textManager.lightMessage()}을 1 감소시킨다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Art,
                3,
                { _, _, disheveledManager, _, _, _, _, _, _, entity, _, entityBookShelf, _, _ ->
                    disheveledManager.run {
                        if (entity.isDisheveled()) {
                            entityBookShelf.light(-1)
                        }
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("d",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 아군의 수 만큼 추가 피해를 입히고 체력을 회복한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Art,
                3,
                { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, _, _ ->
                    val count = player.myTeamPlayer().size

                    playerBookShelf.heal(count.toDouble(), player)
                    return@AbnormalityCard count.toDouble()
                }),


            AbnormalityCard(
                "공허", listOf(
                    "${ChatColor.GRAY}막 시작 시 보유중인 모든 환상체 책장을 소멸시킨다.",
                    "${ChatColor.GRAY}막 시작 시 현재까지 소멸시킨 환상체 책장의 수 만큼 ${textManager.powerMessage()}을 얻는다.",
                ), EmotionType.Negative, LibraryFloor.Art, 3
            ) { _, _, _, _, _, _, _, _, player, playerBookShelf, _, attackDamageManager ->
                val newCrads = playerBookShelf.abnormalityCards.filter { it.name != "공허" }

                player.getScore("voidCount").score += newCrads.size
                playerBookShelf.abnormalityCards.removeAll(newCrads)

                attackDamageManager.run {
                    player.attackDamage(player.getScore("voidCount").score, Int.MAX_VALUE, "공허", true)
                }
            },

            AbnormalityCard(
                "허무", listOf(
                    "${ChatColor.GRAY}막 시작 시 탐욕, 증오, 절망, 분노 환상체 책장을 가졌다면, 자신에게 이하의 효과를 적용한다.",
                    "${ChatColor.GRAY}탐욕, 증오, 절망, 분노 환상체 책장의 패널티가 사라진다.",
                    "",
                    "${ChatColor.GRAY}위 효과가 발동했다면, 매 막마다 모든 적에게 이하의 효과를 적용한다.",
                    "${textManager.moveSpeedMessage()} 20% 감소",
                    "${textManager.weaknessMessage()} 6 부여"
                ), EmotionType.Negative, LibraryFloor.Art, 3
            ) { game, _, _, _, moveSpeedManager, _, _, _, player, playerBookShelf, _, attackDamagemanager ->
                val card1 = playerBookShelf.abnormalityCards.find { it.name == "탐욕" }
                val card2 = playerBookShelf.abnormalityCards.find { it.name == "증오" }
                val card3 = playerBookShelf.abnormalityCards.find { it.name == "절망" }
                val card4 = playerBookShelf.abnormalityCards.find { it.name == "분노" }

                if (card1 is AbnormalityCard && card2 is AbnormalityCard && card3 is AbnormalityCard && card4 is AbnormalityCard) {
                    player.scoreboardTags.add("nihilismMark")
                    val enemyTeam = player.enemyTeamPlayer()
                    moveSpeedManager.run {
                        enemyTeam.forEach {
                            it.moveSpeedPercentage(20, Int.MAX_VALUE, "허무", false)
                        }
                    }
                    attackDamagemanager.run {
                        enemyTeam.forEach {
                            it.attackDamage(6, Int.MAX_VALUE, "허무", false)
                        }
                    }

                    game.stageEndBukkitScheduler.add { player.scoreboardTags.remove("nihilismMark") }
                }
            },

            AbnormalityCard(
                "다카포", listOf(
                    textManager.oneTimeMessage(), "${ChatColor.GRAY}이번 막 종료 시, 총류의 층 1막부터 다시 시작한다."
                ), EmotionType.Negative, LibraryFloor.Art, 3
            ) { game, _, _, _, _, _, _, _, _, _, _, _ ->
                game.floor = LibraryFloor.GeneralWorks
                game.act = 0

                abnormalityCardList.remove(
                    AbnormalityCard(
                        "다카포", listOf(
                            TextManager().oneTimeMessage(), "${ChatColor.GRAY}이번 막 종료 시, 총류의 층 1막부터 다시 시작한다."
                        ), EmotionType.Negative, LibraryFloor.Art, 3
                    )
                )
            })
        )
    }
}
