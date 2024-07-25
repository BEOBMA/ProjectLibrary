@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.manager

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.util.Util.isInsideRegion
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player


interface TeamService {
    fun teamPick()
    fun assignTeam(player: Player, teamName: String, color: ChatColor)
}

class DefaultTeamService : TeamService {
    companion object {
        var teams = mutableMapOf(
            "RedTeam" to Bukkit.getScoreboardManager().mainScoreboard.getTeam("RedTeam"),
            "BlueTeam" to Bukkit.getScoreboardManager().mainScoreboard.getTeam("BlueTeam")
        )
    }

    override fun teamPick() {
        ProjectLibrary.instance.loggerInfo("[ProjectLibrary] team pick time")
        Info.game!!.players.forEach { player ->
            player.teleport(Location(Bukkit.getWorld("world"), -21.5, -47.0, 23.0, -90F, 0F))
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0F, 2.0F)
        }

        Bukkit.broadcastMessage("\n${ChatColor.YELLOW}10초 후, 정면 문을 기준으로 로비를 반으로 나눠 왼쪽에 있는 플레이어는 레드팀, 오른쪽은 블루팀으로 결정됩니다.")
        ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
            Info.game!!.players.forEach { player ->
                if (isInsideRegion(
                        player.location,
                        Location(Bukkit.getWorld("world"), -23.0, -47.0, 23.01),
                        Location(Bukkit.getWorld("world"), 0.0, -40.0, 28.0)
                    )
                ) {
                    assignTeam(player, "BlueTeam", ChatColor.DARK_BLUE)
                } else if (isInsideRegion(
                        player.location,
                        Location(Bukkit.getWorld("world"), -23.0, -47.0, 17.0),
                        Location(Bukkit.getWorld("world"), 0.0, -40.0, 22.99)
                    )
                ) {
                    assignTeam(player, "RedTeam", ChatColor.DARK_RED)
                }
            }

            /*
            * 테스트 용도 주석 처리
            if (teams["RedTeam"]?.players?.isEmpty() == true) {
                Bukkit.getServer().broadcastMessage("\n${ChatColor.YELLOW}상대 팀이 존재하지 않아 게임을 진행할 수 없습니다.")
                Info.game!!.stop()
                return@Runnable
            } else if (teams["BlueTeam"]?.players?.isEmpty() == true) {
                Bukkit.getServer().broadcastMessage("\n${ChatColor.YELLOW}상대 팀이 존재하지 않아 게임을 진행할 수 없습니다.")
                Info.game!!.stop()
                return@Runnable
            }

             */

            Bukkit.broadcastMessage("\n${ChatColor.YELLOW}팀 등록이 완료되었습니다.")
            val broadcastManager = BroadcastManager()
            broadcastManager.broadcastTeamPlayers(teams["RedTeam"], "레드")
            broadcastManager.broadcastTeamPlayers(teams["BlueTeam"], "블루")
            ProjectLibrary.instance.loggerInfo("[ProjectLibrary] team pick end")

            ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
                Bukkit.broadcastMessage("\n${ChatColor.YELLOW}잠시 후 게임이 시작됩니다.")
                ProjectLibrary.instance.server.scheduler.runTaskLater(ProjectLibrary.instance, Runnable {
                    GameManager(DefaultGameService()).firstStart()
                }, 30L)
            }, 30L)
        }, 200L)
    }

    override fun assignTeam(player: Player, teamName: String, color: ChatColor) {
        teams[teamName]?.addEntry(player.name)
        player.setPlayerListName("${color}${player.playerListName}")
    }
}

class TeamManager(private val converter: TeamService) {
    fun teamPick() {
        converter.teamPick()
    }

    fun assignTeam(player: Player, teamName: String, color: ChatColor) {
        converter.assignTeam(player, teamName, color)
    }
}