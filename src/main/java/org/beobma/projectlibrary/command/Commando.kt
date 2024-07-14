@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.command

import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.util.Util.isTeam
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

class Commando : Listener, CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (cmd.name.equals("pl", ignoreCase = true) && args.isNotEmpty()) {
            if (sender !is Player) {
                sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이 명령어는 플레이어만 사용할 수 있습니다.")
                return false
            }

            val commandManager = Commands()

            when (args[0].lowercase(Locale.getDefault())) {
                "start" -> {
                    if (!sender.isOp) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이 명령어를 사용할 권한이 없습니다.")
                        return false
                    }

                    val world = Bukkit.getWorld("world")
                    if (world?.seed != 8971917449433682803) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이 플러그인은 전용 맵과 함께 사용해야 합니다.")
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 게임을 강제로 종료합니다.")
                        return true
                    }

                    commandManager.run {
                        sender.gameStart(Bukkit.getOnlinePlayers().toList())
                    }
                    return true
                }

                "stop" -> {
                    if (!sender.isOp) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이 명령어를 사용할 권한이 없습니다.")
                        return false
                    }
                    commandManager.run { sender.gameStop() }
                    return true
                }

                "info" -> {
                    if (!Info.isGaming()) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 현재 게임중이 아닙니다.")
                        return false
                    }
                    if (sender !in Info.game!!.players || sender.isTeam("SpectatorTeam")) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 게임 참가자가 아닙니다.")
                        return false
                    }

                    commandManager.run { sender.info() }
                    return true
                }

                else -> {
                    sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 알 수 없는 명령어: ${args[0]}.")
                    return false
                }
            }
        }
        return false
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<String>
    ): List<String> {
        if (command.name.equals("pl", ignoreCase = true)) {
            return when (args.size) {
                1 -> listOf("start", "stop", "info")
                else -> emptyList()
            }
        }
        return emptyList()
    }
}