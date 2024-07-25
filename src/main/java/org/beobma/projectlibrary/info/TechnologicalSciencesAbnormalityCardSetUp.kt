@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.info.SetUp.Companion.abnormalityCardList
import org.beobma.projectlibrary.listener.OnDamageEvent.Companion.patternRepeatRecognitionCount
import org.beobma.projectlibrary.listener.OnDamageEvent.Companion.requestMarker
import org.beobma.projectlibrary.localization.Localization
import org.beobma.projectlibrary.text.TextManager
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.getRandomNumberBetween
import org.beobma.projectlibrary.util.Util.getScore
import org.beobma.projectlibrary.util.Util.isTrueWithProbability
import org.beobma.projectlibrary.util.Util.myTeamPlayer
import org.beobma.projectlibrary.util.Util.percentageOf
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class TechnologicalSciencesAbnormalityCardSetUp {
    init {
        abnormalityCardSetUp()
    }

    private fun abnormalityCardSetUp() {
        val textManager = TextManager()

        abnormalityCardList.addAll(listOf(AbnormalityCard("금속성 울림",
            listOf(
                "${ChatColor.GRAY}타격 공격 적중 시 1초간 대상의 ${textManager.goldenMessagetoGray("이동 속도")}가 10% 감소한다."
            ),
            EmotionType.Affirmation,
            LibraryFloor.TechnologicalSciences,
            1,
            { _, _, _, _, moveSpeedManager, _, _, _, _, entity, playerBookShelf, _, _, _ ->
                if (playerBookShelf.attackType == AttackType.Striking) {
                    moveSpeedManager.run {
                        entity.moveSpeedPercentage(10, 1, "금속성 울림", false)
                    }
                }
                return@AbnormalityCard 0.0
            }),

            AbnormalityCard("패턴 반복 인식 기능",
                listOf(
                    "${ChatColor.GRAY}3번째 공격마다 ${textManager.yellowMessagetoGray("빛")}을 1 회복한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.TechnologicalSciences,
                1,
                { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, _, _ ->
                    patternRepeatRecognitionCount[player] = patternRepeatRecognitionCount[player]!! + 1
                    if (patternRepeatRecognitionCount[player]!! >= 3) {
                        playerBookShelf.light(1)
                        patternRepeatRecognitionCount[player] = 0
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("의뢰",
                listOf(
                    "${ChatColor.GRAY}생존한 ${textManager.goldenMessagetoGray("의뢰 대상")}이 없을 경우, 처음 공격하는 적에게 ${
                        textManager.goldenMessagetoGray(
                            "의뢰 대상"
                        )
                    }을 부여한다.",
                    "${textManager.goldenMessagetoGray("의뢰 대상")}은 받는 피해량이 3 증가한다.",
                    "${textManager.goldenMessagetoGray("의뢰 대상")}을 처치한 대상은 ${textManager.goldenMessagetoGray("흐트러짐")}을 최대로 회복하고, ${
                        textManager.yellowMessagetoGray("빛")
                    }을 최대로 회복한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.TechnologicalSciences,
                1,
                { game, _, _, _, _, _, _, _, player, entity, _, _, _, _ ->
                    if (requestMarker[player] == null) {
                        requestMarker[player] = entity
                        entity.scoreboardTags.add("의뢰 대상")
                        game.stageEndBukkitScheduler.add { entity.scoreboardTags.remove("의뢰 대상") }
                    } else {
                        if (requestMarker[player]?.gameMode == GameMode.SPECTATOR) {
                            requestMarker[player] = entity
                            entity.scoreboardTags.add("의뢰 대상")
                            game.stageEndBukkitScheduler.add { entity.scoreboardTags.remove("의뢰 대상") }
                        }
                    }
                    return@AbnormalityCard 0.0
                }),


            AbnormalityCard(
                "폭력성", listOf(
                    "${ChatColor.GRAY}막 시작 시 모든 효과 위력 사이의 최솟값이 1 감소하고 최댓값이 3 증가한다."
                ), EmotionType.Negative, LibraryFloor.TechnologicalSciences, 1
            ) { _, _, _, _, _, _, _, _, _, playerBookShelf, _, _ ->
                playerBookShelf.minDiceWeight -= 1
                playerBookShelf.maxDiceWeight += 3
            },

            AbnormalityCard("리듬",
                listOf(
                    "${ChatColor.GRAY}막 시작 시 리듬을 1 얻는다.",
                    "${ChatColor.GRAY}공격 적중 시 25% 확률로 리듬이 1 증가한다.",
                    "",
                    "${ChatColor.GRAY}리듬을 보유한 대상의 공격 적중 시 피해량이 수치만큼 증가한다.",
                    "${ChatColor.GRAY}리듬을 보유한 대상은 피격 시 ${textManager.goldenMessagetoGray("흐트러짐")} 피해량이 수치 x 2만큼 증가한다.",
                    "${ChatColor.GRAY}리듬은 매 막마다 초기화된다.",
                ),
                EmotionType.Negative,
                LibraryFloor.TechnologicalSciences,
                1,
                { game, _, _, _, _, _, _, _, player, _, _, _, _, _ ->
                    if (25.isTrueWithProbability()) {
                        player.getScore("rhythmCounter").score++
                    }

                    game.stageEndBukkitScheduler.add { player.getScore("rhythmCounter").score = 0 }
                    return@AbnormalityCard player.getScore("rhythmCounter").score.toDouble()
                }) { game, _, _, _, _, _, _, _, player, _, _, _ ->
                player.getScore("rhythmCounter").score++
                game.stageEndBukkitScheduler.add { player.getScore("rhythmCounter").score = 0 }
            },

            AbnormalityCard("애도",
                listOf(
                    "${ChatColor.GRAY}상태이상이 있는 대상에게 가하는 피해량이 2 증가한다."
                ),
                EmotionType.Negative,
                LibraryFloor.TechnologicalSciences,
                1,
                { _, _, _, _, _, _, _, bleedingManager, _, entity, _, _, _, _ ->
                    val bleeding = bleedingManager.run {
                        entity.getBleeding()
                    }
                    if (entity.fireTicks > 0 || bleeding > 0) {
                        return@AbnormalityCard +2.0
                    }
                    return@AbnormalityCard 0.0
                }),


            AbnormalityCard(
                "충전", listOf(
                    "${ChatColor.GRAY}막 시작 시 ${textManager.yellowMessagetoGray("빛")}을 최대로 가지고 있으면 이하의 효과를 얻는다.",
                    "${textManager.goldenMessagetoGray("이동 속도")}가 10% 증가하고, 이번 막동안 ${textManager.redMessagetoGray("힘")} 3을 얻는다."
                ), EmotionType.Affirmation, LibraryFloor.TechnologicalSciences, 2
            ) { _, _, _, _, moveSpeedManager, _, _, _, player, playerBookShelf, _, attackDamageManager ->
                if (playerBookShelf.light == playerBookShelf.maxLight) {
                    moveSpeedManager.run {
                        player.moveSpeedPercentage(10, Int.MAX_VALUE, "충전", true)
                    }

                    attackDamageManager.run {
                        player.attackDamage(3, Int.MAX_VALUE, "충전", true)
                    }
                }
            },

            AbnormalityCard("청소",
                listOf(
                    "${ChatColor.GRAY}자신의 ${textManager.goldenMessagetoGray("이동 속도")}가 상대보다 높을 때.",
                    "${ChatColor.GRAY}상대와의 ${textManager.goldenMessagetoGray("이동 속도")} 차이에 비례하여 피해량이 증가한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.TechnologicalSciences,
                2,
                { _, _, _, _, _, _, _, _, player, entity, _, _, _, _ ->
                    val playerMoveSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue
                    val entityMoveSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue
                    if (playerMoveSpeed > entityMoveSpeed) {
                        return@AbnormalityCard (playerMoveSpeed - entityMoveSpeed)
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("안식",
                listOf(
                    "${ChatColor.GRAY}현재 체력이 가장 낮은 적을 공격할 때 피해량 2, ${textManager.goldenMessagetoGray("흐트러짐")} 피해량 2 증가."
                ),
                EmotionType.Affirmation,
                LibraryFloor.TechnologicalSciences,
                2,
                { game, _, _, _, _, _, _, _, _, entity, _, _, _, _ ->
                    var lowestHealthPlayer: Player? = null
                    var lowestHealth = Double.MAX_VALUE

                    for (playerT in game.players) {
                        val health = playerT.health
                        if (health < lowestHealth) {
                            lowestHealth = health
                            lowestHealthPlayer = playerT
                        }
                    }

                    if (lowestHealthPlayer == entity) {
                        return@AbnormalityCard +2.0
                    }
                    return@AbnormalityCard 0.0
                }),


            AbnormalityCard("구속당한 분노",
                listOf(
                    "${textManager.goldenMessagetoGray("이동 속도")}가 0으로 고정된다.",
                    "${ChatColor.GRAY}타격 공격 적중 시 피해량이 1~3 증가한다."
                ),
                EmotionType.Negative,
                LibraryFloor.TechnologicalSciences,
                2,
                { _, _, _, _, _, _, _, _, _, _, playerBookShelf, _, _, _ ->
                    if (playerBookShelf.attackType == AttackType.Striking) {
                        return@AbnormalityCard getRandomNumberBetween(1, 3, playerBookShelf).toDouble()
                    }
                    return@AbnormalityCard 0.0
                }) { game, _, _, _, _, _, _, _, player, _, _, _ ->
                game.mapBukkitScheduler[player] = mutableMapOf(
                    Pair(
                        "구속당한 분노", object : BukkitRunnable() {
                            override fun run() {
                                player.velocity = Vector(0, 0, 0)
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 1L)
                    )
                )
            },

            AbnormalityCard("음악 중독",
                listOf(
                    "${ChatColor.GRAY}가하는 피해량이 1 증가한다.", "${ChatColor.GRAY}받는 피해량이 1 증가한다."
                ),
                EmotionType.Negative,
                LibraryFloor.TechnologicalSciences,
                2,
                { _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
                    return@AbnormalityCard 1.0
                },
                { _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
                    return@AbnormalityCard 1.0
                }),

            AbnormalityCard(
                "일곱 번째 탄환",
                listOf(
                    "${ChatColor.GRAY}가하는 피해량이 1~7 증가한다.", "${ChatColor.GRAY}일곱 번째 공격의 대상이 아군을 포함 무작위 대상으로 결정된다."
                ),
                EmotionType.Negative, LibraryFloor.TechnologicalSciences, 2,
            ),


            AbnormalityCard("관",
                listOf(
                    "${ChatColor.GRAY}체력이 50% 이하인 적에게 공격이 적중하면 이번 막동안 대상의 효과 위력 사이의 최소값이 1 감소한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.TechnologicalSciences,
                3,
                { _, _, _, _, _, _, _, _, _, entity, _, entityBookShelf, _, _ ->
                    if (entity.health <= entity.maxHealth.percentageOf(50)) {
                        entityBookShelf.minDiceWeight -= 1
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("엄숙한 애도",
                listOf(
                    "${ChatColor.GRAY}사망 시 모든 아군에게 ${textManager.redMessagetoGray("힘")} 1을 부여한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.TechnologicalSciences,
                3,
                null,
                null,
                null,
                null,
                null,
                { _, _, _, _, _, _, _, _, _, entity, _, _, _, attackDamageManager ->
                    entity.myTeamPlayer().forEach {
                        attackDamageManager.run {
                            it.attackDamage(1, Int.MAX_VALUE, "엄숙한 애도", true)
                        }
                    }
                }),

            AbnormalityCard(
                "하모니", listOf(
                    "${ChatColor.GRAY}막 시작 후 10초마다 아군 전체의 체력이 2 회복된다."
                ), EmotionType.Affirmation, LibraryFloor.TechnologicalSciences, 3
            ) { game, _, _, _, _, _, _, _, player, _, _, _ ->
                val playerTeam = player.myTeamPlayer()

                game.mapBukkitScheduler[player] = mutableMapOf(
                    Pair(
                        "하모니", object : BukkitRunnable() {
                            override fun run() {
                                playerTeam.forEach {
                                    it.getMainBookShelf()?.heal(2.0, it)
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 200L)
                    )
                )
            },


            AbnormalityCard(
                "음악", listOf(
                    textManager.oneTimeMessage(),
                    "${ChatColor.GRAY}이번 막동안 아군, 적군이 가하는 피해가 4~8 증가한다.",
                    "${ChatColor.GRAY}또한, 피격 시 ${textManager.goldenMessagetoGray("흐트러짐")} 피해를 4~8 추가로 받는다.",
                    "${ChatColor.GRAY}자신 한정으로 적을 처치할 때마다 체력과 ${textManager.goldenMessagetoGray("흐트러짐")} 저항을 15 회복한다.",
                ), EmotionType.Negative, LibraryFloor.TechnologicalSciences, 3
            ) { game, _, _, _, _, _, _, _, _, _, _, _ ->
                game.stageEvent.add("음악")
            },

            AbnormalityCard(
                "흑염", listOf(
                    textManager.oneTimeMessage(), "${ChatColor.GRAY}이번 막동안 아군, 적군은 서로 2배의 피해를 입는다."
                ), EmotionType.Negative, LibraryFloor.TechnologicalSciences, 3
            ) { game, _, _, _, _, _, _, _, _, _, _, _ ->
                game.stageEvent.add("흑염")
            },

            AbnormalityCard(
                "마탄", listOf(
                    textManager.oneTimeMessage(),
                    "${ChatColor.GRAY}이번 막동안 마탄의 사수로 ${textManager.goldenMessagetoGray("동화")}한다.",
                    "${ChatColor.GRAY}마탄의 사수는 원거리 무기를 사용하며 우클릭 시 조준, 양손들기 시 사격한다."
                ), EmotionType.Negative, LibraryFloor.TechnologicalSciences, 3
            ) { _, _, _, _, _, _, _, _, player, _, _, _ ->
                player.run {
                    inventory.clear()
                    inventory.setItem(0, Localization().magicMarksmanWeapon)
                }
            }))
    }
}