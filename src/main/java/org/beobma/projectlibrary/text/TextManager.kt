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
        return "${ChatColor.GOLD}${ChatColor.BOLD}${msg}${ChatColor.RESET}"
    }

    /**
     * @param msg 황금색으로 강조할 메시지
     * @return 황금색으로 강조된 메시지를 반환 후, ChatColor 리셋
     */
    fun goldenMessagetoGray(msg: String): String {
        return "${ChatColor.GOLD}${ChatColor.BOLD}${msg}${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @param msg 빨간색으로 강조할 메시지
     * @return 빨간색으로 강조된 메시지를 반환 후, ChatColor 리셋
     */
    fun redMessagetoGray(msg: String): String {
        return "${ChatColor.RED}${ChatColor.BOLD}${msg}${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @param msg 짙은 빨간색으로 강조할 메시지
     * @return 짙은 빨간색으로 강조된 메시지를 반환 후, ChatColor 리셋
     */
    fun darkRedMessagetoGray(msg: String): String {
        return "${ChatColor.DARK_RED}${ChatColor.BOLD}${msg}${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @param msg 짙은 초록색으로 강조할 메시지
     * @return 짙은 초록색으로 강조된 메시지를 반환 후, ChatColor 리셋
     */
    fun darkGreenMessagetoGray(msg: String): String {
        return "${ChatColor.DARK_GREEN}${ChatColor.BOLD}${msg}${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @param msg 노란색으로 강조할 메시지
     * @return 노란색으로 강조된 메시지를 반환 후, ChatColor 리셋
     */
    fun yellowMessagetoGray(msg: String): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}${msg}${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @return 일회성
     */
    fun oneTimeMessage(): String {
        return "${ChatColor.GOLD}${ChatColor.BOLD}일회성${ChatColor.RESET}${ChatColor.GRAY}"
    }


    /**
     * @return 힘
     */
    fun powerMessage(): String {
        return "${ChatColor.RED}${ChatColor.BOLD}힘${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @return 허약
     */
    fun weaknessMessage(): String {
        return "${ChatColor.BLUE}${ChatColor.BOLD}허약${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @return 이동 속도
     */
    fun moveSpeedMessage(): String {
        return "${ChatColor.GOLD}${ChatColor.BOLD}이동 속도${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @return 흐트러짐
     */
    fun disheveledMessage(): String {
        return "${ChatColor.GOLD}${ChatColor.BOLD}흐트러짐${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @return 잔향
     */
    fun reverberationMessage(): String {
        return "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}잔향${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @return 빛
     */
    fun lightMessage(): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}빛${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @return 화상
     */
    fun burnMessage(): String {
        return "${ChatColor.RED}${ChatColor.BOLD}화상${ChatColor.RESET}${ChatColor.GRAY}"
    }

    /**
     * @return 충전
     */
    fun chargingMessage(): String {
        return "${ChatColor.BLUE}${ChatColor.BOLD}충전${ChatColor.RESET}${ChatColor.GRAY}"
    }

    fun hoverMessage(msg: String, hoverText: String): TextComponent {
        val component = Component.text(msg)
        val hoverComponent = Component.text(hoverText)
        component.hoverEvent(HoverEvent.showText(hoverComponent))
        return component
    }
}