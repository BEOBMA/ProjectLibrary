@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.info.SetUp.Companion.abnormalityCardList
import org.beobma.projectlibrary.listener.OnDamageEvent.Companion.surpriseGiftMarker
import org.beobma.projectlibrary.text.TextManager
import org.beobma.projectlibrary.util.Util.enemyTeamPlayer
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.getRandomNumberBetween
import org.beobma.projectlibrary.util.Util.getScore
import org.beobma.projectlibrary.util.Util.isTrueWithProbability
import org.beobma.projectlibrary.util.Util.myTeamPlayer
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.sqrt
import kotlin.random.Random

class LiteratureAbnormalityCardSetUp {
    init {
        abnormalityCardSetUp()
    }

    private fun abnormalityCardSetUp() {
        val textManager = TextManager()

        abnormalityCardList.addAll(listOf(AbnormalityCard("사회적 거리두기",
            listOf(
                "${ChatColor.GRAY}받는 피해가 1~2 감소한다.",
                "${ChatColor.GRAY}피해를 입힌 대상에게 1~2의 ${textManager.goldenMessagetoGray("흐트러짐")} 피해를 준다."
            ),
            EmotionType.Affirmation,
            LibraryFloor.Literature,
            1,
            null,
            { _, _, _, _, _, _, _, _, player, _, playerBookShelf, entityBookShelf, _, _ ->
                playerBookShelf.removeDisheveled(getRandomNumberBetween(1, 2, entityBookShelf), player)
                return@AbnormalityCard -(getRandomNumberBetween(1, 2, entityBookShelf)).toDouble()
            }),

            AbnormalityCard("고치",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 1초간 대상의 ${textManager.goldenMessagetoGray("이동 속도")}가 10% 감소한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Literature,
                1,
                { _, _, _, _, moveSpeedManager, _, _, _, _, entity, _, _, _, _ ->
                    moveSpeedManager.run {
                        entity.moveSpeedPercentage(10, 1, "고치", false)
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("깜짝 선물",
                listOf(
                    "${ChatColor.GRAY}처음으로 공격 적중 시, 대상에게 ${textManager.goldenMessagetoGray("선물")}을 부여한다.",
                    "${textManager.goldenMessagetoGray("선물")}을 받은 대상이 ${textManager.goldenMessagetoGray("선물")}을 준 대상이 아닌, 다른 대상을 공격 시 선물이 40% 확률로 폭발한다.",
                    "${ChatColor.GRAY}폭발 시 대상에게 2~7의 피해를 주고 ${textManager.darkRedMessagetoGray("출혈")} 2를 부여한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Literature,
                1,
                { game, _, _, _, _, _, _, _, player, entity, _, _, _, _ ->
                    if (surpriseGiftMarker[player] == null) {
                        surpriseGiftMarker[player] = entity
                        entity.scoreboardTags.add("깜짝 선물")
                        game.stageEndBukkitScheduler.add { entity.scoreboardTags.remove("깜짝 선물") }
                    } else {
                        if (surpriseGiftMarker[player]?.gameMode == GameMode.SPECTATOR) {
                            surpriseGiftMarker[player] = entity
                            entity.scoreboardTags.add("깜짝 선물")
                            game.stageEndBukkitScheduler.add { entity.scoreboardTags.remove("깜짝 선물") }
                        }
                    }
                    return@AbnormalityCard 0.0
                }),


            AbnormalityCard(
                "오늘의 표정", listOf(
                    "${ChatColor.GRAY}막 시작 시 무작위 ${textManager.goldenMessagetoGray("표정")}으로 변경된다.",
                    "${textManager.goldenMessagetoGray("표정")}에 따라 공격 시 피해량이 변한다.",
                    "${ChatColor.GRAY}화난 얼굴일 때 최대, 웃는 얼굴일 때 최소가 된다."
                ), EmotionType.Negative, LibraryFloor.Literature, 1
            ) { game, _, _, _, _, _, _, _, player, _, _, _ ->
                val random = Random.nextInt(1, 101)
                val pace = when (random) {
                    in 1..20 -> {
                        "오늘의 표정 - 화남"
                    }

                    in 21..40 -> {
                        "오늘의 표정 - 약간 화남"
                    }

                    in 41..60 -> {
                        "오늘의 표정 - 보통"
                    }

                    in 61..80 -> {
                        "오늘의 표정 - 약간 웃음"
                    }

                    in 81..100 -> {
                        "오늘의 표정 - 웃음"
                    }

                    else -> {
                        "오늘의 표정 - 보통"
                    }
                }
                player.scoreboardTags.add(pace)
                game.stageEndBukkitScheduler.add { player.scoreboardTags.remove(pace) }
            },

            AbnormalityCard("반짝임",
                listOf(
                    "${ChatColor.GRAY}막 시작 시 적들이 각각 50%의 확률로 ${textManager.goldenMessagetoGray("유혹")}에 걸린다.",
                    "${textManager.goldenMessagetoGray("유혹")}에 걸린 적들은 피해량이 2~4 증가하고 공격 대상이 적의 아군을 제외, 무작위로 지정된다.",
                    "${ChatColor.GRAY}자신은 ${textManager.goldenMessagetoGray("유혹")}에 걸린 적을 공격할 때, 1~2의 피해를 추가로 준다."
                ),
                EmotionType.Negative,
                LibraryFloor.Literature,
                1,
                { _, _, _, _, _, _, _, _, _, entity, playerBookShelf, _, _, _ ->
                    if (entity.scoreboardTags.contains("유혹")) {
                        return@AbnormalityCard getRandomNumberBetween(1, 2, playerBookShelf).toDouble()
                    }
                    return@AbnormalityCard 0.0
                }) { game, _, _, _, _, _, _, _, player, _, _, _ ->
                player.enemyTeamPlayer().forEach {
                    if (50.isTrueWithProbability()) {
                        it.scoreboardTags.add("유혹")
                        game.stageEndBukkitScheduler.add { it.scoreboardTags.remove("유혹") }
                    }
                }
            },

            AbnormalityCard("도끼",
                listOf(
                    "${ChatColor.GRAY}참격 공격이 아닌 다른 공격을 하면 ${textManager.goldenMessagetoGray("흐트러짐")} 피해 2~5를 받는다.",
                    "${ChatColor.GRAY}참격 공격의 피해량이 1~3 증가한다."
                ),
                EmotionType.Negative,
                LibraryFloor.Literature,
                1,
                { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, _, _ ->
                    if (playerBookShelf.attackType == AttackType.Slashing) {
                        return@AbnormalityCard getRandomNumberBetween(1, 3, playerBookShelf).toDouble()
                    } else {
                        playerBookShelf.removeDisheveled(getRandomNumberBetween(2, 5, playerBookShelf), player)
                        return@AbnormalityCard 0.0
                    }
                }),


            AbnormalityCard("수줍음",
                listOf(
                    "${ChatColor.GRAY}피격 시 현재 막의 수에 반비례하여 피해량이 줄어든다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Literature,
                2,
                null,
                { game, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
                    return@AbnormalityCard when (game.act) {
                        1 -> 3.0
                        2 -> 2.0
                        3 -> 1.0
                        else -> {
                            0.0
                        }
                    }
                }),

            AbnormalityCard("식사",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 대상이 ${textManager.disheveledMessage()} 상태면 모든 아군이 체력을 2~4 회복한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Literature,
                2,
                { _, _, disheveledManager, _, _, _, _, _, player, entity, _, _, _, _ ->
                    disheveledManager.run {
                        if (entity.isDisheveled()) {
                            player.myTeamPlayer().forEach {
                                val mainBookShelf = it.getMainBookShelf()
                                if (mainBookShelf is MainBookShelf) {
                                    mainBookShelf.heal(getRandomNumberBetween(2, 4, mainBookShelf).toDouble(), it)
                                }
                            }
                        }
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard(
                "경계", listOf(
                    "${ChatColor.GRAY}막 시작 시 무작위 적 하나의 피해량이 1 감소한다."
                ), EmotionType.Affirmation, LibraryFloor.Literature, 2
            ) { _, _, _, _, _, _, _, _, player, _, _, attackDamageManager ->
                attackDamageManager.run {
                    player.enemyTeamPlayer().random().attackDamage(1, Int.MAX_VALUE, "경계", false)
                }
            },


            AbnormalityCard(
                "집착", listOf(
                    "${ChatColor.GRAY}모든 대상이 어떤 경로로든, ${textManager.darkRedMessagetoGray("출혈")}을 얻을 때 2배로 얻음."
                ), EmotionType.Negative, LibraryFloor.Literature, 2
            ),

            AbnormalityCard(
                "친구", listOf(
                    "${ChatColor.GRAY}막 시작 시 무작위 아군 하나를 ${textManager.goldenMessagetoGray("친구")}로 만든다.",
                    "${textManager.goldenMessagetoGray("친구")}가 입히는 피해량이 2~4 증가한다.",
                    "${ChatColor.GRAY}단, ${textManager.goldenMessagetoGray("친구")}와 자신의 거리가 5칸 이상 멀어지면 양쪽 모두 2~7의 피해를 입는다.",
                    "${ChatColor.GRAY}이 효과가 적용되었다면, 이 환상체 책장이 소멸한다.",
                    "${ChatColor.GRAY}아군이 자신 혼자라면 이 효과는 적용되지 않는다.",
                ), EmotionType.Negative, LibraryFloor.Literature, 2
            ) { game, _, _, _, _, _, _, _, player, playerBookShelf, _, _ ->
                val entityList = player.myTeamPlayer().filter { it != player }

                if (entityList.isNotEmpty()) {
                    val entity = entityList.random()
                    entity.scoreboardTags.add("친구")
                    game.stageEndBukkitScheduler.add { entity.scoreboardTags.remove("친구") }

                    game.stageBukkitScheduler.add(
                        object : BukkitRunnable() {
                            override fun run() {
                                if (entity.gameMode != GameMode.ADVENTURE || player.gameMode != GameMode.ADVENTURE) {
                                    this.cancel()
                                }
                                val loc1 = player.location
                                val loc2 = entity.location

                                val deltaX = loc1.x - loc2.x
                                val deltaY = loc1.y - loc2.y
                                val deltaZ = loc1.z - loc2.z
                                if (sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) >= 5) {
                                    val entityBookShelf = entity.getMainBookShelf()!!
                                    playerBookShelf.paleDamage(
                                        getRandomNumberBetween(
                                            2, 7, playerBookShelf
                                        ).toDouble(), player
                                    )
                                    entityBookShelf.paleDamage(
                                        getRandomNumberBetween(
                                            2, 7, entityBookShelf
                                        ).toDouble(), entity
                                    )
                                    game.stageEndBukkitScheduler.add {
                                        playerBookShelf.abnormalityCards.remove(
                                            AbnormalityCard(
                                                "친구", listOf(
                                                    "${ChatColor.GRAY}막 시작 시 무작위 아군 하나를 ${
                                                        TextManager().goldenMessagetoGray(
                                                            "친구"
                                                        )
                                                    }로 만든다.",
                                                    "${TextManager().goldenMessagetoGray("친구")}가 입히는 피해량이 2~4 증가한다.",
                                                    "${ChatColor.GRAY}단, ${
                                                        TextManager().goldenMessagetoGray("친구")
                                                    }와 자신의 거리가 5칸 이상 멀어지면 양쪽 모두 2~7의 피해를 입는다.",
                                                    "${ChatColor.GRAY}이 효과가 적용되었다면, 이 환상체 책장이 소멸한다.",
                                                    "${ChatColor.GRAY}아군이 자신 혼자라면 이 효과는 적용되지 않는다.",
                                                ), EmotionType.Negative, LibraryFloor.Literature, 2
                                            )
                                        )
                                    }
                                }
                            }
                        }.runTaskTimer(ProjectLibrary.instance, 0L, 20L)
                    )
                }
            },

            AbnormalityCard("재미난 장난",
                listOf(
                    "${ChatColor.GRAY}매 막마다의 첫 공격의 피해량은 반드시 12 감소하거나, 증가한다.",
                    "${ChatColor.GRAY}감소되었을 경우, 자신은 피해 2~7을 받는다.",
                ),
                EmotionType.Negative,
                LibraryFloor.Literature,
                2,
                { game, _, _, _, _, _, _, _, player, entity, playerBookShelf, _, _, _ ->
                    if (!player.scoreboardTags.contains("재미난 장난")) {
                        player.scoreboardTags.add("재미난 장난")
                        game.stageEndBukkitScheduler.add { entity.scoreboardTags.remove("재미난 장난") }
                        if (50.isTrueWithProbability()) {
                            return@AbnormalityCard 12.0
                        } else {
                            playerBookShelf.paleDamage(
                                getRandomNumberBetween(2, 7, playerBookShelf).toDouble(),
                                player
                            )
                            return@AbnormalityCard 12.0
                        }
                    }
                    return@AbnormalityCard 0.0
                }),


            AbnormalityCard("오물",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 ${textManager.darkRedMessagetoGray("출혈")} 1을 부여한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Literature,
                3,
                { _, _, _, _, _, _, _, bleedingManager, _, entity, _, _, _, _ ->
                    bleedingManager.run {
                        entity.addBleeding(1)
                    }
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("사랑하는 가족",
                listOf(
                    "${ChatColor.GRAY}막 시작 시 ${textManager.goldenMessagetoGray("쐐기옷")} 6을 얻는다.",
                    "${ChatColor.GRAY}피격 시 ${textManager.goldenMessagetoGray("쐐기옷")}이 있다면 피해를 수치만큼 줄여 받고, ${
                        textManager.goldenMessagetoGray("쐐기옷")
                    }이 1 감소한다.",
                ),
                EmotionType.Affirmation,
                LibraryFloor.Literature,
                3,
                null,
                { _, _, _, _, _, _, _, _, _, entity, _, _, _, _ ->
                    var finalDamage = 0.0
                    if (entity.getScore("wedgeClothCount").score > 0) {
                        finalDamage -= entity.getScore("wedgeClothCount").score.toDouble()
                        entity.getScore("wedgeClothCount").score--
                    }
                    return@AbnormalityCard finalDamage
                }) { game, _, _, _, _, _, _, _, player, _, _, _ ->
                player.getScore("wedgeClothingCount").score = 6
                game.stageEndBukkitScheduler.add {
                    player.getScore("wedgeClothingCount").score = 0
                }
            },

            AbnormalityCard("적안",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 25% 확률로 대상에게 ${textManager.redMessagetoGray("부정 감정")} 1을 누적한다."
                ),
                EmotionType.Affirmation,
                LibraryFloor.Literature,
                3,
                { _, _, _, _, _, _, _, _, _, _, _, entityBookShelf, _, _ ->
                    if (25.isTrueWithProbability()) {
                        entityBookShelf.emotion--
                    }
                    return@AbnormalityCard 0.0
                }),


            AbnormalityCard("낡은 양산",
                listOf(
                    "${ChatColor.GRAY}막 시작 시 ${textManager.goldenMessagetoGray("이동 속도")}가 20% 증가한다.",
                    "${ChatColor.GRAY}피격 시 30% 확률로 받는 피해를 0으로 만들고, 받은 피해의 2배를 ${textManager.goldenMessagetoGray("흐트러짐")} 피해로 반사한다.",
                ),
                EmotionType.Negative,
                LibraryFloor.Literature,
                3,
                null,
                { _, _, _, _, _, _, _, _, player, _, playerBookShelf, _, damage, _ ->
                    if (30.isTrueWithProbability()) {
                        playerBookShelf.removeDisheveled(damage.toInt() * 2, player)
                        return@AbnormalityCard -Double.MAX_VALUE
                    }
                    return@AbnormalityCard 0.0
                }) { _, _, _, _, moveSpeedManager, _, _, _, player, _, _, _ ->
                moveSpeedManager.run {
                    player.moveSpeedPercentage(20, Int.MAX_VALUE, "낡은 양산", true)
                }
            },

            AbnormalityCard("핏빛 욕망",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 ${textManager.darkRedMessagetoGray("출혈")} 3을 부여한다.",
                    "${ChatColor.GRAY}공격이 적중할 때마다 ${textManager.goldenMessagetoGray("이동 속도")}가 감소하고 2의 피해를 입는다."
                ),
                EmotionType.Negative,
                LibraryFloor.Literature,
                3,
                { _, _, _, _, moveSpeedManager, _, _, bleedingManager, player, entity, playerBookShelf, _, _, _ ->
                    bleedingManager.run {
                        entity.addBleeding(3)
                    }
                    moveSpeedManager.run {
                        player.moveSpeedPercentage(5, Int.MAX_VALUE, "핏빛 욕망", false)
                    }
                    playerBookShelf.paleDamage(2.0, player)
                    return@AbnormalityCard 0.0
                }),

            AbnormalityCard("흑조",
                listOf(
                    "${ChatColor.GRAY}공격 적중 시 10% 확률로 대상의 ${textManager.yellowMessagetoGray("빛")} 1 감소."
                ),
                EmotionType.Negative,
                LibraryFloor.Literature,
                3,
                { _, _, _, _, _, _, _, _, _, _, _, entityBookShelf, _, _ ->
                    if (10.isTrueWithProbability()) {
                        entityBookShelf.light(-1)
                    }
                    return@AbnormalityCard 0.0
                })))
    }
}