@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.info

import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.abnormalitycard.EmotionType
import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.bookshelf.Rating
import org.beobma.projectlibrary.game.LibraryFloor
import org.beobma.projectlibrary.text.TextManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SetUp {

    companion object {
        val rumorMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val urbanGhostMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val urbanLegendMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val urbanDiseaseMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val urbanNightmareMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val cityStarMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
        val ImpuritiesMainBookShelfList: MutableList<MainBookShelf> = mutableListOf()

        val abnormalityCardList: MutableList<AbnormalityCard> = mutableListOf()
        val mainBookShelfList: MutableList<MainBookShelf> = mutableListOf()
    }

    init {
        abnormalityCardSetUp()
        mainBookShelfSetUp()
    }

    private fun abnormalityCardSetUp() {
        val textManager = TextManager()
        abnormalityCardList.addAll(
            listOf(
                AbnormalityCard(
                    "피", listOf(
                        "${ChatColor.GRAY}피격 시 받는 피해가 2 감소한다.",
                        "${ChatColor.GRAY}피격 시 ${textManager.goldenMessagetoGray("흐트러짐")} 피해 1을 추가로 받는다."
                    ), EmotionType.Negative, LibraryFloor.GeneralWorks, 1
                ),
                AbnormalityCard(
                    "손목긋개", listOf(
                        "${ChatColor.GRAY}공격 적중시 적에게 2의 피해를 추가로 입힌다.",
                        "${ChatColor.GRAY}공격 적중시 자신은 1의 피해를 입는다."
                    ), EmotionType.Negative, LibraryFloor.GeneralWorks, 1
                ),
                AbnormalityCard(
                    "흉터", listOf(
                        "${ChatColor.GRAY}피격 시 받는 피해가 1 감소한다.",
                        "${ChatColor.GRAY}피격 시 10% 확률로 ${textManager.goldenMessagetoGray("피해를 0으로 만든다.")}"
                    ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 1
                ),
                AbnormalityCard(
                    "창백한 손", listOf(
                        "${ChatColor.GRAY}공격이 같은 대상에게 3번 명중할 때마다 ${textManager.goldenMessagetoGray("흐트러짐")} 피해 3을 추가로 준다.",
                        "${ChatColor.GRAY}다른 대상 공격시 초기화"
                    ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 1
                ),
                AbnormalityCard(
                    "고동", listOf(
                        "${ChatColor.GRAY}가하는 피해가 20% 증가한다.",
                        "${ChatColor.GRAY}10초마다 적에게 피해를 입히지 못하면 체력을 최대 체력의 25% 만큼 잃는다."
                    ), EmotionType.Negative, LibraryFloor.GeneralWorks, 1
                ),
                AbnormalityCard(
                    "동맥", listOf(
                        "${textManager.goldenMessagetoGray("공격 속도")}가 10% 증가한다.",
                        "${ChatColor.GRAY}최대 체력이 10% 감소한다."
                    ), EmotionType.Negative, LibraryFloor.GeneralWorks, 1
                ),
                AbnormalityCard(
                    "열망", listOf(
                        "${ChatColor.GRAY}최대 체력이 15% 증가한다.",
                        "${ChatColor.GRAY}${textManager.goldenMessagetoGray("이동 속도")}가 20% 증가한다."
                    ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 1
                ),
                AbnormalityCard(
                    "몰아치는 박동", listOf(
                        "${ChatColor.GRAY}매 막마다 이하의 효과를 얻으나, 매 막 시작 20초 후 사망한다.",
                        "${ChatColor.GRAY}가하는 피해가 20% 증가한다.",
                        "${textManager.goldenMessagetoGray("이동 속도")}가 20% 증가한다.",
                        "${textManager.goldenMessagetoGray("받는 피해")}가 20% 감소한다.",
                    ), EmotionType.Negative, LibraryFloor.GeneralWorks, 2
                ),
                AbnormalityCard(
                    "학습", listOf(
                        "${ChatColor.GRAY}현재 막의 수만큼 추가 피해를 입힌다."
                    ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 2
                ),
                AbnormalityCard(
                    "거짓말", listOf(
                        "${ChatColor.GRAY}매 막마다 자신의 체력을 이하와 같이 변경한다.",
                        "${ChatColor.GRAY}현재 체력을 최대 체력의 50% ~ 최대 체력의 150% 사이의 값으로 변경"
                    ), EmotionType.Negative, LibraryFloor.GeneralWorks, 2
                ),
                AbnormalityCard(
                    "호기심", listOf(
                        "${ChatColor.GRAY}점프할 때마다 ${textManager.goldenMessagetoGray("이동 속도")}가 증가한다.",
                        "${ChatColor.GRAY}이 효과는 매 막마다 초기화된다."
                    ), EmotionType.Negative, LibraryFloor.GeneralWorks, 2
                ),
                AbnormalityCard(
                    "꼭두각시", listOf(
                        "${ChatColor.GRAY}피격 시 받는 ${textManager.goldenMessagetoGray("흐트러짐")} 피해가 1 감소한다."
                    ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 2
                ),
                AbnormalityCard(
                    "서리검", listOf(
                        "${ChatColor.GRAY}적중 시 1초간 대상의 ${textManager.goldenMessagetoGray("이동 속도")}를 15% 감소시킨다."
                    ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 2
                ),
                AbnormalityCard(
                    "입맞춤", listOf(
                        "${ChatColor.GRAY}적중 시 1초간 대상의 ${textManager.goldenMessagetoGray("공격 속도")}를 10% 감소시킨다."
                    ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 3
                ),
                AbnormalityCard(
                    "눈보라", listOf(
                        textManager.oneTimeMessage(),
                        "${ChatColor.GRAY}이번 막 시작시 10초간 자신을 제외한 모든 대상에게 ${textManager.goldenMessagetoGray("이동 불가")} 효과를 적용한다.",
                        "${ChatColor.GRAY}만약, 서리검 환상체 책장을 보유했다면 추가로 ${textManager.goldenMessagetoGray("공격 불가")} 효과를 적용한다.",
                    ), EmotionType.Negative, LibraryFloor.GeneralWorks, 3
                ),
                AbnormalityCard(
                    "서리조각", listOf(
                        "${ChatColor.GRAY}피격 시 1초간 대상의 ${textManager.goldenMessagetoGray("이동 속도")}를 15% 감소시킨다."
                    ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 3
                ),
                AbnormalityCard(
                    "못과 망치", listOf(
                        "${ChatColor.GRAY}적중 시 대상에게 ${textManager.goldenMessagetoGray("못")}을 부여한다.",
                        "${ChatColor.GRAY}못이 있는 대상은 모든 ${textManager.goldenMessagetoGray("흐트러짐")} 피해를 2 추가로 받는다.",
                    ), EmotionType.Affirmation, LibraryFloor.GeneralWorks, 3
                ),
                AbnormalityCard(
                    "눈빛", listOf(
                        "${ChatColor.GRAY}가하는 피해가 2배가 된다.",
                        "${ChatColor.GRAY}받는 피해가 2배가 된다."
                    ), EmotionType.Negative, LibraryFloor.GeneralWorks, 3
                ),
                AbnormalityCard(
                    "죄책감", listOf(
                        textManager.oneTimeMessage(),
                        "${ChatColor.GRAY}받는 ${textManager.goldenMessagetoGray("흐트러짐")} 피해가 2 감소한다.",
                        "${ChatColor.GRAY}공격으로 받은 피해의 절반만큼 공격자에게 ${textManager.goldenMessagetoGray("흐트러짐")} 피해를 준다.",
                    ), EmotionType.Negative, LibraryFloor.GeneralWorks, 3
                ),


                AbnormalityCard(
                    "재", listOf(
                        "${ChatColor.GRAY}피격 시 공격자에게 ${textManager.redMessagetoGray("화상")} 2를 부여한다."
                    ), EmotionType.Affirmation, LibraryFloor.History, 1
                ),

                AbnormalityCard(
                    "발걸음", listOf(
                        "${ChatColor.GRAY}피격 시 체력이 20% 이하인 경우, 공격자에게 자신 최대 체력의 30% 만큼 피해를 준다."
                        "${ChatColor.GRAY}이후 자신은 사망한다."
                    ), EmotionType.Negative, LibraryFloor.History, 2
                ),

                AbnormalityCard(
                    "성냥불", listOf(
                        "${ChatColor.GRAY}적중 시 자신에게 ${textManager.redMessagetoGray("불씨")}를 부여한다."
                        "${ChatColor.GRAY}적중 시 불씨 만큼 추가 피해를 입힌다."
                        "${ChatColor.GRAY}불씨가 4 이상이면 25% 확률로 자신은 불씨 만큼의 피해를 입는다."
                    ), EmotionType.Negative, LibraryFloor.History, 1
                ),


                AbnormalityCard(
                    "그리웠던 옛날의 포옹", listOf(
                        "${ChatColor.GRAY}적중 시 20% 확률로 5의 ${textManager.goldenMessagetoGray("흐트러짐")} 피해를 받는다.",
                        "${ChatColor.GRAY}동일한 대상을 공격할 때마다 확률이 증가한다.",
                    ), EmotionType.Affirmation, LibraryFloor.History, 1
                ),

                AbnormalityCard(
                    "행복한 기억", listOf(
                        "${ChatColor.GRAY}동일한 대상을 공격할 때마다 ${textManager.DarkGreenMessagetoGray("긍정 감정")}을 누적한다."
                    ), EmotionType.Affirmation, LibraryFloor.History, 1
                ),

                AbnormalityCard(
                    "애정 표현", listOf(
                        "${ChatColor.GRAY}적중 시 대상이 자신을 바라보고 있지 않았다면 2의 추가 피해를 입힌다."
                        "${ChatColor.GRAY}적중 시 대상이 자신을 바라보고 있었다면 가하는 피해가 2 감소한다."
                    ), EmotionType.Negative, LibraryFloor.History, 1
                ),


                AbnormalityCard(
                    "요정들의 보살핌", listOf(
                        "${ChatColor.GRAY}매 막마다 최대 체력이 30% 증가한다.",
                        "${ChatColor.GRAY}이 효과가 적용중인 동안 5번 이상 피격 시, 현재 체력의 30%를 잃는다.",
                    ), EmotionType.Negative, LibraryFloor.History, 1
                ),

                AbnormalityCard(
                    "식탐", listOf(
                        "${ChatColor.GRAY}적중 시 대상의 체력이 50% 이하라면, 피해량이 2 증가하고 체력을 3 회복한다."
                    ), EmotionType.Affirmation, LibraryFloor.History, 2
                ),

                AbnormalityCard(
                    "포식", listOf(
                        "${ChatColor.GRAY}막 시작 시 체력을 최대 체력의 30% 만큼 소모한다."
                        "${ChatColor.GRAY}이번 막동안 피해량이 3 증가한다."
                    ), EmotionType.Negative, LibraryFloor.History, 2
                ),

            )
        )
    }

    private fun mainBookShelfSetUp() {
        val assistantLibrarianBookshelf = MainBookShelf(
            "보조사서 책장",
            Rating.Supply,
            30.0,
            30.0,
            15,
            15,
            mutableListOf(),
            ItemStack(Material.WOODEN_SWORD, 1).apply {
                itemMeta = itemMeta.apply {
                    setDisplayName("${ChatColor.BOLD}목검")
                    lore = arrayListOf("${ChatColor.GRAY}가장 기본적인 검.")
                    isUnbreakable = true
                }
            })

        rumorMainBookShelfList.add(assistantLibrarianBookshelf)
        mainBookShelfList.add(assistantLibrarianBookshelf)
    }
}
