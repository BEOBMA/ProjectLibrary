@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.command

import org.beobma.projectlibrary.game.Game
import org.beobma.projectlibrary.info.Info
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class Commands {
    fun Player.gameStart(players: List<Player>) {
        if (players.size < 2) {
            sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 참가자가 2명 미만이므로 게임을 시작할 수 없습니다.")
            return
        }

        when {
            Info.isStarting() -> {
                sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이미 게임 준비중입니다.")
            }
            Info.isGaming() -> {
                sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이미 게임중입니다.")
            }
            else -> {
                Game(players).start()
            }
        }
    }

    fun Player.gameStop() {
        if (!Info.isStarting() && !Info.isGaming()) {
            sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 게임 진행중이 아닙니다.")
            return
        }

        Info.game?.stop()
    }

    fun Player.info() {
        if (!Info.isStarting() && !Info.isGaming()) {
            sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 게임 진행중이 아닙니다.")
            return
        }

        
    }
}