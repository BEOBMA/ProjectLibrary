@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.bookshelf.UniqueAbilities
import org.beobma.projectlibrary.info.SetUp.Companion.mainBookShelfList
import org.beobma.projectlibrary.info.SetUp.Companion.urbanDiseaseMainBookShelfList
import org.beobma.projectlibrary.localization.Localization
import org.beobma.projectlibrary.text.TextManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class UrbanDiseaseMainBookShelfSetUp {

    init {
        mainBookShelfSetUp()
    }

    private fun mainBookShelfSetUp() {
        val textManager = TextManager()

        urbanDiseaseMainBookShelfList.addAll(
            listOf(
                MainBookShelf(
                    "사육제의 책장", Rating.Advanced, 64.0, 64.0, 34, 34, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "추모",
                            listOf("${ChatColor.GRAY}사망 시 모든 아군이 ${textManager.redMessagetoGray("힘")} 2를 얻음"),
                            Rating.Supply
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}가시")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}가시"
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Piercing
                ), MainBookShelf(
                    "스테판의 책장", Rating.Advanced, 64.0, 64.0, 34, 34, 0, 3, mutableListOf(
                        UniqueAbilities("난사", listOf("${ChatColor.GRAY}막 시작 20초 동안 모든 피해량 +2"), Rating.Supply)
                    ), Localization().gunWeapon, AttackType.Piercing
                ), MainBookShelf(
                    "타마키의 책장", Rating.Advanced, 64.0, 64.0, 34, 34, 0, 3, mutableListOf(
                        UniqueAbilities("저격", listOf("${ChatColor.GRAY}막 시작 20초 동안 모든 피해량 +3"), Rating.Supply)
                    ), Localization().gunWeapon, AttackType.Piercing
                ), MainBookShelf(
                    "리웨이의 책장", Rating.Limit, 64.0, 64.0, 34, 34, 0, 3, mutableListOf(
                        UniqueAbilities("집중", listOf("${ChatColor.GRAY}막 시작 20초 동안 모든 피해량 +1"), Rating.Supply)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}단검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}평범한 단검"
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Piercing
                ), MainBookShelf(
                    "유나의 책장", Rating.Limit, 64.0, 64.0, 34, 34, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "고독한 해결사",
                            listOf("${ChatColor.GRAY}막 시작 시 다른 아군이 없으면 ${textManager.redMessagetoGray("힘")} 3을 얻음"),
                            Rating.Supply
                        ),
                        UniqueAbilities(
                            "스티그마 공방 무기",
                            listOf("${ChatColor.GRAY}공격 적중 시 대상에게 ${textManager.redMessagetoGray("화상")} 1 부여"),
                            Rating.Supply
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}가방")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}가방으로도 사람을 죽일 수 있다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ), MainBookShelf(
                    "살바도르의 책장", Rating.Art, 76.0, 76.0, 37, 37, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.goldenMessagetoGray("이동 속도")} 10% 증가"),
                            Rating.Limit
                        ),
                        UniqueAbilities(
                            "수호자",
                            listOf("${ChatColor.GRAY}막 시작 시 30초간 무작위 아군 3명이 받는 피해가 2 감소"),
                            Rating.Art
                        ),
                        UniqueAbilities(
                            "새벽불",
                            listOf(
                                "${ChatColor.GRAY}공격 적중 시 대상이 ${textManager.redMessagetoGray("화상")} 상태라면 대상에게 ${
                                    textManager.goldenMessagetoGray("흐트러짐")
                                } 피해 1~2"
                            ),
                            Rating.Art
                        ),
                        UniqueAbilities(
                            "스티그마 공방 무기",
                            listOf("${ChatColor.GRAY}공격 적중 시 대상에게 ${textManager.redMessagetoGray("화상")} 1 부여"),
                            Rating.Supply
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}고급 검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}비싸보이는 검."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ), MainBookShelf(
                    "보노의 책장", Rating.Advanced, 64.0, 64.0, 34, 34, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "불안정한 충전",
                            listOf("${ChatColor.GRAY}막 시작 시 50% 확률로 ${textManager.redMessagetoGray("힘")} 1을 얻음"),
                            Rating.Advanced
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}기계 팔")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}전류가 흐른다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Piercing
                ), MainBookShelf(
                    "알록의 책장", Rating.Limit, 70.0, 70.0, 34, 34, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "찌릿찌릿",
                            listOf("${ChatColor.GRAY}막 시작 시 30초간 무작위 적 3명의 ${textManager.goldenMessagetoGray("이동 속도")}가 10% 감소한다."),
                            Rating.Advanced
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}기계 몽둥이")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}전류가 흐른다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Striking
                ), MainBookShelf(
                    "사요의 책장", Rating.Limit, 70.0, 70.0, 34, 34, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.goldenMessagetoGray("이동 속도")} 10% 증가"),
                            Rating.Limit
                        ),
                        UniqueAbilities(
                            "임전",
                            listOf("${ChatColor.GRAY}막 시작 시 ${textManager.yellowMessagetoGray("빛")}을 1 회복함."),
                            Rating.Limit
                        ),
                        UniqueAbilities("흑운도", listOf("${ChatColor.GRAY}참격 공격 적중 시 피해량 +1"), Rating.Limit),
                        UniqueAbilities(
                            "예리한 일격",
                            listOf("${ChatColor.GRAY}참격 공격 적중 시 ${textManager.darkRedMessagetoGray("출혈")} 1 부여"),
                            Rating.Advanced
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}흑운도")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}날카롭다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ), MainBookShelf(
                    "미야오의 책장", Rating.Limit, 70.0, 70.0, 34, 34, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.goldenMessagetoGray("이동 속도")} 10% 증가"),
                            Rating.Limit
                        ),
                        UniqueAbilities(
                            "야옹야옹~",
                            listOf("${ChatColor.GRAY}막 시작 시 무작위 아군 2명에게 ${textManager.powerMessage()} 1 부여"),
                            Rating.Limit
                        ),
                        UniqueAbilities(
                            "즉흥난타",
                            listOf("${ChatColor.GRAY}타격 공격 적중 시 1초간 대상의 ${textManager.moveSpeedMessage()} 10% 감소"),
                            Rating.Limit
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}방망이")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}타격하면 음악 소리가 난다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Striking
                ), MainBookShelf(
                    "오스카의 책장", Rating.Art, 77.0, 77.0, 38, 38, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.goldenMessagetoGray("이동 속도")} 10% 증가"),
                            Rating.Limit
                        ),
                        UniqueAbilities("불굴", listOf("${ChatColor.GRAY}체력이 0이 되는 피해를 받을 때, 막 당 한 번 저항한다."), Rating.Art),
                        UniqueAbilities("꿰뚫기", listOf("${ChatColor.GRAY}관통 피해량 +1"), Rating.Limit),
                        UniqueAbilities("꿰뚫어 흔들기", listOf("${ChatColor.GRAY}관통 흐트러짐 피해량 +1"), Rating.Limit),
                        UniqueAbilities("쐐기", listOf("${ChatColor.GRAY}관통 피해량 +1"), Rating.Limit)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}장창")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}상당히 긴 창이다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Piercing
                )
            )
        )
        mainBookShelfList.addAll(urbanDiseaseMainBookShelfList)
    }
}