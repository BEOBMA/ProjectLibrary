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
     * @return 일회성
     */
    fun oneTimeMessage(): String {
        return "${ChatColor.GOLD}${ChatColor.BOLD}일회성${ChatColor.RESET}${ChatColor.GRAY}"
    }

    fun hoverMessage(msg: String, hoverText: String): TextComponent {
        val component = Component.text(msg)
        val hoverComponent = Component.text(hoverText)
        component.hoverEvent(HoverEvent.showText(hoverComponent))
        return component
    }
}