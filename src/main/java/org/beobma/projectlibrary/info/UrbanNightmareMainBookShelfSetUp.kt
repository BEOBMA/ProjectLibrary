@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.bookshelf.AttackType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.bookshelf.UniqueAbilities
import org.beobma.projectlibrary.info.SetUp.Companion.mainBookShelfList
import org.beobma.projectlibrary.info.SetUp.Companion.urbanDiseaseMainBookShelfList
import org.beobma.projectlibrary.info.SetUp.Companion.urbanNightmareMainBookShelfList
import org.beobma.projectlibrary.localization.Localization
import org.beobma.projectlibrary.text.TextManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class UrbanNightmareMainBookShelfSetUp {

    init {
        mainBookShelfSetUp()
    }

    private fun mainBookShelfSetUp() {
        val textManager = TextManager()

        urbanNightmareMainBookShelfList.addAll(
            listOf(
                MainBookShelf(
                    "라일라의 책장", Rating.Advanced, 77.0, 77.0, 40, 40, 0, 3, mutableListOf(
                        UniqueAbilities("체력 수거", listOf("${ChatColor.GRAY}공격 적중 시 체력 2 회복"), Rating.Advanced),
                        UniqueAbilities("액화 육체", listOf("${ChatColor.GRAY}받는 피해량 -1"), Rating.Supply),
                        UniqueAbilities("시체 청소", listOf("${ChatColor.GRAY}적 처치 시 체력 5% 회복"), Rating.Advanced)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}칼날")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}날카로운 칼날이다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ),
                MainBookShelf(
                    "안톤의 책장", Rating.Advanced, 77.0, 77.0, 40, 40, 0, 3, mutableListOf(
                        UniqueAbilities("정신 수거", listOf("${ChatColor.GRAY}공격 적중 시 ${textManager.disheveledMessage()} 저항 2 회복"), Rating.Advanced),
                        UniqueAbilities("액화 육체", listOf("${ChatColor.GRAY}받는 피해량 -1"), Rating.Supply),
                        UniqueAbilities("시체 청소", listOf("${ChatColor.GRAY}적 처치 시 체력 5% 회복"), Rating.Advanced)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}칼날")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}날카로운 칼날이다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ),

                MainBookShelf(
                    "텐마의 책장", Rating.Limit, 96.0, 96.0, 41, 41, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.moveSpeedMessage()} 10% 증가"),
                            Rating.Limit
                        ),
                        UniqueAbilities("호흡", listOf("${ChatColor.GRAY}막 시작 시 ${textManager.lightMessage()}이 0이면 ${textManager.lightMessage()} 1 회복"), Rating.Advanced),
                        UniqueAbilities("키즈나 / 극한의 피로", listOf("${ChatColor.GRAY}적 처치 시 ${textManager.powerMessage()} 1을 얻음 / 막 시작 시 체력 25% 감소"), Rating.Advanced)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}일본도")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}암살용 일본도."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ),

                MainBookShelf(
                    "유진의 책장", Rating.Art, 268.0, 268.0, 44, 44, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도 2",
                            listOf("${textManager.moveSpeedMessage()} 20% 증가"),
                            Rating.Limit
                        ),
                        UniqueAbilities("과호흡 / 탈진", listOf("${ChatColor.GRAY}적중 시 원래 피해량이 10 이상이면 ${textManager.lightMessage()} 2 회복 / ${textManager.moveSpeedMessage()} 10% 감소"), Rating.Art),
                        UniqueAbilities("받아내기", listOf("${ChatColor.GRAY}받는 피해량 2~6 감소"), Rating.Limit),
                        UniqueAbilities("사의 눈 / 탈력", listOf("${ChatColor.GRAY}가하는 피해량 +4 / 가하는 피해량 -3"), Rating.Art),
                        UniqueAbilities("키즈나 / 사의 피로", listOf("${ChatColor.GRAY}적 처치 시 ${textManager.powerMessage()} 1을 얻음 / 막 시작 시 체력 75% 감소"), Rating.Advanced)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}일본도")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}암살용 일본도."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ),

                MainBookShelf(
                    "왕의 책장", Rating.Limit, 81.0, 81.0, 43, 43, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.moveSpeedMessage()} 10% 증가"),
                            Rating.Limit
                        ),
                        UniqueAbilities("연기 후리기", listOf("${ChatColor.GRAY}자신에게 연기가 있다면 공격으로 주는 ${textManager.disheveledMessage()} 피해량이 (연기x10)% 만큼 증가"), Rating.Limit),
                        UniqueAbilities("뭉게뭉게", listOf("${ChatColor.GRAY}자신에게 있는 연기의 받는 피해량이 증가하지 않고, 대신 공격으로 주는 피해량 증가."), Rating.Limit),
                        UniqueAbilities("연기", listOf("${ChatColor.GRAY}공격 적중 시 서로에게 연기 1 부여", "${ChatColor.GRAY}연기: 수치에 비례하여 입는 피해가 증가하고 9 이상이면 가하는 피해가 3 증가한다."), Rating.Advanced)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}곰방대")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}맞으면 아프다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Piercing
                ),

                MainBookShelf(
                    "불안정한 우는 아이의 책장", Rating.Art, 87.0, 87.0, 43, 43, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.moveSpeedMessage()} 10% 증가"),
                            Rating.Limit
                        ),
                        UniqueAbilities("이글거리는 검", listOf("${ChatColor.GRAY}공격 적중 시 ${textManager.burnMessage()} 1~2 부여"), Rating.Limit),
                        UniqueAbilities("깃털 방패", listOf("${ChatColor.GRAY}피격 시 공격자에게 화상 1~2 부여"), Rating.Limit),
                        UniqueAbilities("불안정한 격정", listOf("${ChatColor.GRAY}대상에게 화상이 있다면 피해량 +1"), Rating.Limit)
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}이글거리는 검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}이글거리고 있다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                ),

                MainBookShelf(
                    "로즈의 책장", Rating.Art, 87.0, 87.0, 43, 43, 0, 3, mutableListOf(
                        UniqueAbilities(
                            "속도",
                            listOf("${textManager.moveSpeedMessage()} 10% 증가"),
                            Rating.Limit
                        ),
                        UniqueAbilities("양자도약", listOf("${textManager.moveSpeedMessage()}가 일정 수치 이상이면 공격 적중 시 10% 확률로 대상에게 피해 13"), Rating.Limit),
                        UniqueAbilities("굴절", listOf("${ChatColor.GRAY}공격 적중 시 5% 확률로 대상에게 흐트러짐 피해 13"), Rating.Limit),
                        UniqueAbilities("차원절단", listOf("${ChatColor.GRAY}공격 적중 시 10% 확률로 대상에게 피해 10"), Rating.Limit),
                        UniqueAbilities("충전", listOf("${ChatColor.GRAY}공격 적중 시 ${textManager.chargingMessage()} 1을 얻음"), Rating.Advanced),
                        UniqueAbilities("과충전", listOf("${textManager.chargingMessage()}이 10 이상이면 피해량 +5"), Rating.Art),
                    ), ItemStack(Material.IRON_SWORD, 1).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName("${ChatColor.BOLD}절단검")
                            lore = arrayListOf(
                                "${ChatColor.GRAY}절삭력이 뛰어나며, 충전할 수 있다."
                            )
                            isUnbreakable = true
                        }
                    }, AttackType.Slashing
                )
            )
        )
        mainBookShelfList.addAll(urbanNightmareMainBookShelfList)
    }
}