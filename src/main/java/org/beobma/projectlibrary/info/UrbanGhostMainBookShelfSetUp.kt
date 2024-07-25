@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.bookshelf.UniqueAbilities
import org.beobma.projectlibrary.info.SetUp.Companion.mainBookShelfList
import org.beobma.projectlibrary.info.SetUp.Companion.urbanGhostMainBookShelfList
import org.beobma.projectlibrary.text.TextManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class UrbanGhostMainBookShelfSetUp {

    init {
        mainBookShelfSetUp()
    }

    private fun mainBookShelfSetUp() {
        val textManager = TextManager()

        urbanGhostMainBookShelfList.addAll(
            listOf(
                MainBookShelf(
                    "잭의 책장", Rating.Advanced, 50.0, 50.0, 25, 25, 0, 3, mutableListOf(
                        UniqueAbilities("비상 식량", listOf("${ChatColor.GRAY}5초마다 체력 2 회복"), Rating.Supply)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}중식도")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}용도가 무엇일까?"
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ),
                MainBookShelf(
                    "피에르의 책장", Rating.Limit, 44.0, 44.0, 25, 25, 0, 3, mutableListOf(
                        UniqueAbilities("즉석 조리", listOf("${ChatColor.GRAY}적 처치 시 체력을 최대 체력의 10%만큼 회복"), Rating.Supply)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}식칼")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}무엇을 요리할 수 있을까?"
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Piercing
                ),
                MainBookShelf(
                    "마스의 책장", Rating.Art, 65.0, 65.0, 27, 27, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도", listOf("${textManager.goldenMessagetoGray("이동 속도")} 10% 증가"), Rating.Limit
                        ), UniqueAbilities(
                            "기량",
                            listOf("${ChatColor.GRAY}속도가 더 낮은 적을 공격할때 ${textManager.goldenMessagetoGray("흐트러짐")} 피해량 +1"),
                            Rating.Supply
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}고급 검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}비싸보이는 검이다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ),
                MainBookShelf(
                    "산의 책장", Rating.Advanced, 57.0, 57.0, 29, 29, 0, 3, mutableListOf(
                        UniqueAbilities("침착", listOf("${ChatColor.GRAY}공격 적중 시 25% 확률로 피해량 +3"), Rating.Supply)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}일반적인 검이다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Striking
                ),
                MainBookShelf(
                    "루루의 책장", Rating.Limit, 53.0, 53.0, 26, 26, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "불빠따",
                            listOf("${ChatColor.GRAY}공격 적중 시 50% 확률로 ${textManager.redMessagetoGray("화상")} 1 부여"),
                            Rating.Limit
                        )
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}빠따")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}맞으면 아프다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Striking
                ),
            )
        )
        mainBookShelfList.addAll(urbanGhostMainBookShelfList)
    }
}