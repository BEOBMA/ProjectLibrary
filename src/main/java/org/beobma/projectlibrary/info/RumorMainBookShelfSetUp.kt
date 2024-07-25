@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.bookshelf.UniqueAbilities
import org.beobma.projectlibrary.info.SetUp.Companion.mainBookShelfList
import org.beobma.projectlibrary.info.SetUp.Companion.rumorMainBookShelfList
import org.beobma.projectlibrary.text.TextManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class RumorMainBookShelfSetUp {

    init {
        mainBookShelfSetUp()
    }

    private fun mainBookShelfSetUp() {
        val textManager = TextManager()

        rumorMainBookShelfList.addAll(
            listOf(
                MainBookShelf(
                    "피트의 책장",
                    Rating.Supply,
                    42.0,
                    42.0,
                    22,
                    22,
                    0,
                    3,
                    mutableListOf(),
                    ItemStack(Material.WOODEN_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}목검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}가장 기본적인 검."
                            )
                            isUnbreakable = true
                        }
                    },
                    AttackType.Piercing
                ), MainBookShelf(
                    "윤의 책장", Rating.Limit, 46.0, 46.0, 22, 22, 0, 3, mutableListOf(
                        UniqueAbilities("윤의 촉", listOf("${ChatColor.GRAY}공격 적중 시 50% 확률로 피해량 +1"), Rating.Supply)
                    ), ItemStack(Material.WOODEN_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}목검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}가장 기본적인 검."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Striking
                ), MainBookShelf(
                    "모의 책장",
                    Rating.Limit,
                    46.0,
                    46.0,
                    22,
                    22,
                    0,
                    3,
                    mutableListOf(
                        UniqueAbilities(
                            "전기충격",
                            listOf("${ChatColor.GRAY}공격 적중 시 25% 확률로 3초간 대상의 ${textManager.goldenMessagetoGray("이동 속도")} 20% 감소"),
                            Rating.Supply
                        )
                    ),
                    ItemStack(Material.WOODEN_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}기계 톱")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}무르지만, 절삭력은 확실하다."
                            )
                            isUnbreakable = true
                        }
                    },
                    AttackType.Slashing,
                ), MainBookShelf(
                    "태인의 책장", Rating.Limit, 43.0, 43.0, 22, 22, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "갈고리",
                            listOf("${ChatColor.GRAY}적 처치시 이번 막동안 ${textManager.redMessagetoGray("힘")} 1을 얻음"),
                            Rating.Supply
                        )
                    ), ItemStack(Material.WOODEN_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}낫")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}다루기 어렵지만, 날카롭다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Piercing
                ), MainBookShelf(
                    "핀의 책장", Rating.Limit, 42.0, 42.0, 22, 22, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "어설픈 용기",
                            listOf("${ChatColor.GRAY}막 시작 시 50% 확률로 ${textManager.redMessagetoGray("힘")} 1을 얻음"),
                            Rating.Supply
                        )
                    ), ItemStack(Material.WOODEN_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}철검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}겉보기에는 나무 검이지만, 실제로 나무 검이 맞다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                )
            )
        )
        mainBookShelfList.addAll(rumorMainBookShelfList)
    }
}