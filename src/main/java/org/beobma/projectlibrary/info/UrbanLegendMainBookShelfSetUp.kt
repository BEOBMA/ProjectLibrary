@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.bookshelf.UniqueAbilities
import org.beobma.projectlibrary.info.SetUp.Companion.mainBookShelfList
import org.beobma.projectlibrary.info.SetUp.Companion.urbanLegendMainBookShelfList
import org.beobma.projectlibrary.text.TextManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class UrbanLegendMainBookShelfSetUp {

    init {
        mainBookShelfSetUp()
    }

    private fun mainBookShelfSetUp() {
        val textManager = TextManager()

        urbanLegendMainBookShelfList.addAll(
            listOf(
                MainBookShelf(
                    "이사도라의 책장", Rating.Advanced, 57.0, 57.0, 29, 29, 0, 3, mutableListOf(
                        UniqueAbilities("최소한의 공격", listOf("${ChatColor.GRAY}참격 흐트러짐 피해량 +1"), Rating.Supply)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}츠바이헨더")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}평범한 츠바이헨더."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ),
                MainBookShelf(
                    "줄리아의 책장", Rating.Advanced, 57.0, 57.0, 29, 29, 0, 3, mutableListOf(
                        UniqueAbilities("츠바이 검술1", listOf("${ChatColor.GRAY}참격 피해량 +1"), Rating.Supply)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}츠바이헨더")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}평범한 츠바이헨더."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ),
                MainBookShelf(
                    "월터의 책장", Rating.Limit, 63.0, 63.0, 29, 29, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.goldenMessagetoGray("이동 속도")} 10% 증가"),
                            Rating.Limit
                        ), UniqueAbilities("당신의 방패", listOf("${ChatColor.GRAY}받는 피해량 -1"), Rating.Limit)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}츠바이헨더")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}평범한 츠바이헨더."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ),
                MainBookShelf(
                    "디노의 책장", Rating.Advanced, 57.0, 57.0, 29, 29, 0, 3, mutableListOf(
                        UniqueAbilities("2단 차기", listOf("${ChatColor.GRAY}공격 적중 시 피해량 +1"), Rating.Supply)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}너클")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}검처럼 보이지만, 검이 맞다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Striking
                ),
                MainBookShelf(
                    "줄루의 책장", Rating.Advanced, 57.0, 57.0, 29, 29, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "심호흡",
                            listOf("${ChatColor.GRAY}막 시작 시 25% 확률로 ${textManager.yellowMessagetoGray("빛")} 1 회복"),
                            Rating.Supply
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}너클")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}검처럼 보이지만, 검이 맞다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Striking
                ),
                MainBookShelf(
                    "경미의 책장", Rating.Limit, 64.0, 64.0, 32, 32, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.goldenMessagetoGray("이동 속도")} 10% 증가"),
                            Rating.Limit
                        ),
                        UniqueAbilities(
                            "휴식",
                            listOf("${ChatColor.GRAY}10초 동안 공격하지 않으면 ${textManager.yellowMessagetoGray("빛")} 1 회복"),
                            Rating.Advanced
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}너클")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}검처럼 보이지만, 검이 맞다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Striking
                ),
                MainBookShelf(
                    "미카의 책장",
                    Rating.Advanced,
                    57.0,
                    57.0,
                    29,
                    29,
                    0,
                    3,
                    mutableListOf(),
                    ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}일반적인 검."
                            )
                            isUnbreakable = true
                        }
                    },
                    AttackType.Piercing
                ),
                MainBookShelf(
                    "레인의 책장",
                    Rating.Advanced,
                    57.0,
                    57.0,
                    29,
                    29,
                    0,
                    3,
                    mutableListOf(),
                    ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}일반적인 검."
                            )
                            isUnbreakable = true
                        }
                    },
                    AttackType.Piercing
                ),
                MainBookShelf(
                    "올가의 책장", Rating.Art, 67.0, 67.0, 32, 32, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.goldenMessagetoGray("이동 속도")} 10% 증가"),
                            Rating.Limit
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}일반적인 검."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Piercing
                ),
            )
        )
        mainBookShelfList.addAll(urbanLegendMainBookShelfList)
    }
}