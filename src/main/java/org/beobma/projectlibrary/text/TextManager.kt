@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.ChatColor

class TextManager {
    /**
     * @param msg 황금색으로 강조할 메시지
     * @return 황금색으로 강조된 메시지를 반환 후, ChatColor 리셋
     */
    fun goldenMessage(msg: String): String {
        return "${ChatColor.RED}${ChatColor.BOLD}${msg}${ChatColor.RESET}"
    }

    /**
     * @param i 보호막 수치
     * @return "i만큼의 피해를 흡수하는 보호막을 얻습니다."
     */
    fun shieldText(i: Int): String {
        return "${ChatColor.AQUA}${ChatColor.BOLD}${i}만큼의 피해를 흡수하는 보호막${ChatColor.RESET}${ChatColor.GRAY}을 얻습니다."
    }

    /**
     * @param i 피해 수치
     * @return "3칸 내 바라보는 적에게 i의 피해를 입힙니다."
     */
    fun closeRangeDamageText(i: Int): String {
        return "${ChatColor.GRAY}3칸 내 바라보는 적에게 ${i}의 피해를 입힙니다."
    }

    fun hoverMessage(msg: String, hoverText: String): TextComponent {
        val component = Component.text(msg)
        val hoverComponent = Component.text(hoverText)
        component.hoverEvent(HoverEvent.showText(hoverComponent))
        return component
    }
}