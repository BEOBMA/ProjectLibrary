@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.info.SetUp.Companion.abnormalityCardList
import org.beobma.projectlibrary.listener.OnDamageEvent.Companion.pebbleMarker
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
import org.bukkit.scheduler.BukkitRunnable

class ArtAbnormalityCardSetUp {
    init {
        abnormalityCardSetUp()
    }

    private fun abnormalityCardSetUp() {
        val textManager = TextManager()

        abnormalityCardList.addAll(listOf(AbnormalityCard(
            "이계 너머의 메아리", listOf(
                textManager.oneTimeMessage(),
                "${ChatColor.GRAY}이번 막 시작 시 모든 적에게 ${textManager.disheveledMessage()} 피해 5~10을 준다."
            ), EmotionType.Affirmation, LibraryFloor.Art, 1
        ) { _, _, _, _, _, _, _, _, player, playerBookShelf, _, _ ->
            val entityTeam = player.enemyTeamPlayer()

            entityTeam.forEach {
                it.getMainBookShelf()?.removeDisheveled(getRandomNumberBetween(5, 10, playerBookShelf), it)
            }
        },

            AbnormalityCard("촉수",
                listOf(
                    "${ChatColor.GRAY}관통 공격 적중 시 대상의 최대 ${textManager.disheveledMessage()} 저항의 5% 만큼, ${textManager.disheveledMessage()} 저항이 감소한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Art,
                1,
                null,
                null,
                { _, _, _, _, _, _, _, _, _, _, playerBookShelf, entityBookShelf, _, _ ->
                    if (playerBookShelf.attackType == AttackType.Piercing) {
                        return@AbnormalityCard entityBookShelf.maxDisheveled.toDouble().percentageOf(5)
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("가시",
                listOf(
                    "${ChatColor.GRAY}관통 공격 적중 시 대상에게 ${textManager.goldenMessagetoGray("쾌감")} 1을 부여한다.",
                    "${textManager.goldenMessagetoGray("쾌감")}이 3 이상인 대상에게 2~7의 피해와 흐트러짐 피해를 준다.",
                    "${ChatColor.GRAY}이 효과 발동 후, 부여된 ${textManager.goldenMessagetoGray("쾌감")}을 없앤다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Art,
                1,
                { _, _, _, _, _, _, _, _, _, entity, playerBookShelf, entityBookShelf, _, _ ->
                    var finalDamage = 0.0
                    if (playerBookShelf.attackType == AttackType.Piercing) {
                        entity.getScore("pleasureCount").score++
                    }

                    if (entity.getScore("pleasureCount").score >= 3) {
                        finalDamage += getRandomNumberBetween(2, 7, playerBookShelf)
                        entityBookShelf.removeDisheveled(getRandomNumberBetween(2, 7, playerBookShelf), entity)
                        entity.getScore("pleasureCount").score = 0
                    }
                    return@AbnormalityCard finalDamage
                }),


            AbnormalityCard("조약돌",
                listOf(
                    "${ChatColor.GRAY}막 시작 시 무작위 적에게 ${textManager.goldenMessagetoGray("조약돌")} 효과가 부여된다.",
                    "${textManager.goldenMessagetoGray("조약돌")} 효과가 있는 대상을 공격하면 체력을 3~7 회복한다.",
                    "${textManager.goldenMessagetoGray("조약돌")} 효과가 없는 대상을 공격하면 체력이 1~3 감소한다.",
                    "${textManager.goldenMessagetoGray("조약돌")}이 부여된 적을 처치하면 이번 막동안 효과가 소멸한다."
                ),
                EmotionType.Negative,
                LibraryFloor.Art,
                1,
                { _, _, _, _, _, _, _, _, player, entity, playerBookShelf, _, _, _ ->
                    if (pebbleMarker[player] == entity) {
                        playerBookShelf.heal(getRandomNumberBetween(3, 7, playerBookShelf).toDouble(), player)
                    } else {
                        if (pebbleMarker[player]?.gameMode == GameMode.ADVENTURE) {
                            playerBookShelf.paleDamage(
                                getRandomNumberBetween(1, 3, playerBookShelf).toDouble(),
                                player
                            )
                        }
                    }
                    return@AbnormalityCard 0.0
                }) { _, _, _, _, _, _, _, _, player, _, _, _ ->
                val entityTeam = player.enemyTeamPlayer()
                pebbleMarker[player] = entityTeam.random()
            },

            AbnormalityCard("쾌락", listOf(
                "${ChatColor.GRAY}가하는 피해량 +5",
                "${ChatColor.GRAY}막 시작 시 ${textManager.disheveledMessage()} 저항이 50% 감소한다."
            ), EmotionType.Negative, LibraryFloor.Art, 1, { _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
                return@AbnormalityCard 5.0
            }) { _, _, _, _, _, _, _, _, player, playerBookShelf, _, _ ->
                playerBookShelf.removeDisheveled((playerBookShelf.maxDisheveled / 2), player)
            },

            AbnormalityCard("웃음 가루",
                listOf(
                    "${ChatColor.GRAY}피격 시 ${textManager.disheveledMessage()} 상태가 아니라면 ${textManager.disheveledMessage()} 저항을 2~4 회복한다."
                ),
                EmotionType.Negative,
                LibraryFloor.Art,
                1,
                null,
                null,
                null,
                { _, _, disheveledManager, _, _, _, _, _, _, entity, _, entityBookShelf, _, _ ->
                    disheveledManager.run {
                        if (!entity.isDisheveled()) {
                            entityBookShelf.addDisheveled(getRandomNumberBetween(2, 4, entityBookShelf))
                        }
                    }
                    return@AbnormalityCard 0.0
                }),


            AbnormalityCard("이해 불능", listOf(
                "${ChatColor.GRAY}공격 적중 시 50% 확률로 3초간 ${textManager.weaknessMessage()} 1을 부여한다."
            ), EmotionType.Affirmation, LibraryFloor.Art, 2, { _, _, _, _, _, _, _, _, _, entity, _, _, _, _ ->
                if (50.isTrueWithProbability()) {
                    AbnormalStatusManager().createAttackDamageManager().run {
                        entity.attackDamage(1, 3, "이해 불능", false)
                    }
                }
                return@AbnormalityCard 0.0
            }),

            AbnormalityCard(
                "친구의 증표", listOf(
                    "${ChatColor.GRAY}막 시작 시 자신의 최대 체력이 10% 증가한다."
                ), EmotionType.Affirmation, LibraryFloor.Art, 2
            ) { _, _, _, _, _, _, _, _, player, _, maxHealthManager, _ ->
                maxHealthManager.run {
                    player.maxHealthPercentage(10, true)
                }
            },

            AbnormalityCard(
                "반복되는 연주", listOf(
                    textManager.oneTimeMessage(), "${ChatColor.GRAY}이번 막 종료 시, 예술의 층 1막부터 다시 시작한다.",
                    "${ChatColor.GRAY}이 환상체 책장은 한 게임에 최대 1번까지 등장한다."
                ), EmotionType.Affirmation, LibraryFloor.Art, 2
            ) { game, _, _, _, _, _, _, _, _, _, _, _ ->
                game.floor = LibraryFloor.Literature
                game.act = 0

                abnormalityCardList.remove(
                    AbnormalityCard(
                        "반복되는 연주", listOf(
                            TextManager().oneTimeMessage(), "${ChatColor.GRAY}이번 막 종료 시, 예술의 층 1막부터 다시 시작한다."
                        ), EmotionType.Affirmation, LibraryFloor.Art, 2
                    )
                )
            },


            AbnormalityCard("눈물", listOf(
                "${ChatColor.GRAY}가하는 피해량 +3", "${ChatColor.GRAY}자신이 사망하면 모든 아군은 자신의 최대 체력의 50% 만큼 피해를 받는다."
            ), EmotionType.Negative, LibraryFloor.Art, 2, { _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
                return@AbnormalityCard 3.0
            }, null, null, null, null, { _, _, _, _, _, _, _, _, player, _, _, _, _, _ ->
                val playerTeam = player.myTeamPlayer()
                val damage = player.maxHealth.percentageOf(50)
                playerTeam.forEach {
                    it.getMainBookShelf()?.paleDamage(damage, it)
                }
            }),

            AbnormalityCard("꽃잎",
                listOf(
                    "${ChatColor.GRAY}매 막마다 처음으로 체력이 50% 이하가 될 때.",
                    "${ChatColor.GRAY}모든 적에게 4~8의 ${textManager.disheveledMessage()} 피해를 주고 자신의 체력을 5~10 회복한다."
                ),
                EmotionType.Negative,
                LibraryFloor.Art,
                2,
                null,
                { game, _, _, _, _, _, _, _, player, entity, playerBookShelf, _, _, _ ->
                    if (!player.scoreboardTags.contains("꽃잎")) {
                        if (player.health <= player.maxHealth.percentageOf(50)) {
                            player.enemyTeamPlayer().forEach {
                                it.getMainBookShelf()
                                    ?.paleDamage(getRandomNumberBetween(4, 8, playerBookShelf).toDouble(), it)
                            }
                            playerBookShelf.heal(getRandomNumberBetween(5, 10, playerBookShelf).toDouble(), player)
                            player.scoreboardTags.add("꽃잎")
                            game.stageEndBukkitScheduler.add { entity.scoreboardTags.remove("꽃잎") }
                        }
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("가을의 저묾",
                listOf(
                    "${ChatColor.GRAY}피격 시 대상에게 ${textManager.reverberationMessage()}을 1 부여한다.",
                    "${textManager.reverberationMessage()}: 피격 시 수치 만큼 흐트러짐 피해를 받고 수치가 절반으로 감소.",
                ),
                EmotionType.Negative,
                LibraryFloor.Art,
                2,
                null,
                { game, _, _, _, _, _, _, _, player, _, _, _, _, _ ->
                    player.getScore("reverberationStack").score++
                    game.stageEndBukkitScheduler.add { player.getScore("reverberationStack").score = 0 }
                    return@AbnormalityCard 0.0
                }),


            AbnormalityCard(
                "봄의 탄생", listOf(
                    "${ChatColor.GRAY}막 시작 시 30초 동안 공격을 받지 않는 상태가 된다.", "${ChatColor.GRAY}자신은 이 시간동안 공격할 수 없다."
                ), EmotionType.Affirmation, LibraryFloor.Art, 3
            ) { game, unableAttackManager, _, _, _, _, _, _, player, _, _, _ ->
                unableAttackManager.run {
                    player.addUnableAttack()
                }
                player.scoreboardTags.add("봄의 탄생")

                game.mapBukkitScheduler[player] = mutableMapOf(
                    Pair(
                        "봄의 탄생", object : BukkitRunnable() {
                            override fun run() {
                                unableAttackManager.run {
                                    player.removeUnableAttack()
                                }
                                player.scoreboardTags.remove("봄의 탄생")
                            }
                        }.runTaskLater(ProjectLibrary.instance, 600L)
                    )
                )

                game.stageEndBukkitScheduler.add { player.scoreboardTags.remove("봄의 탄생") }
            },

            AbnormalityCard("피날레",
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

            AbnormalityCard("우리의 은하수",
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
                "열렬한 감동", listOf(
                    "${ChatColor.GRAY}가하는 피해량 +7",
                    "${ChatColor.GRAY}공격 대상이 피아식별 없이, 무작위 대상으로 결정된다.",
                ), EmotionType.Negative, LibraryFloor.Art, 3
            ),

            AbnormalityCard("잔향",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 대상이 ${textManager.disheveledMessage()} 상태라면 가하는 피해 +5",
                    "${ChatColor.GRAY}피격 시 자신이 ${textManager.disheveledMessage()} 상태라면 받는 피해 +3"
                ),
                EmotionType.Negative,
                LibraryFloor.Art,
                3,
                { _, _, disheveledManager, _, _, _, _, _, _, entity, _, _, _, _ ->
                    disheveledManager.run {
                        if (entity.isDisheveled()) {
                            return@AbnormalityCard 5.0
                        }
                    }
                    return@AbnormalityCard 0.0
                },
                { _, _, disheveledManager, _, _, _, _, _, _, entity, _, _, _, _ ->
                    disheveledManager.run {
                        if (entity.isDisheveled()) {
                            return@AbnormalityCard 3.0
                        }
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard(
                "다카포", listOf(
                    textManager.oneTimeMessage(), "${ChatColor.GRAY}이번 막 종료 시, 총류의 층 1막부터 다시 시작한다.",
                    "${ChatColor.GRAY}이 환상체 책장은 한 게임에 최대 1번까지 등장한다."
                ), EmotionType.Negative, LibraryFloor.Art, 3
            ) { game, _, _, _, _, _, _, _, _, _, _, _ ->
                game.floor = LibraryFloor.GeneralWorks
                game.act = 0

                abnormalityCardList.remove(
                    abnormalityCardList.find { it.name == "다카포" }
                )
            }))
    }
}
