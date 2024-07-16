@file:Suppress("DEPRECATION")

package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.ProjectLibrary
import org.beobma.projectlibrary.game.Game
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.info.Info.world
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent


class OnPlayerQuickEvent : Listener {

    @EventHandler
    fun onPlayerQuick(event: PlayerQuitEvent) {
        val player = event.player

        if (!Info.isGaming() && !Info.isStarting()) return
        if (player !in Info.game!!.players) return

        val playerList = Info.game!!.players.toMutableList()
        playerList.remove(player)
        Info.game!!.players = playerList.toList()
        val redTeamList = teams["RedTeam"]?.players?
        val blueTeamList = teams["BlueTeam"]?.players?

        if (redTeamList.isNullOrEmpty() || blueTeamList.isNullOrEmpty()) {
            Info.game!!.stop()
            ProjectLibrary.instance.loggerInfo("[ProjectLibrary] game stop at team null")
        }

        if (Info.game!!.players.isEmpty() || Info.game!!.players.size == 1) {
            Info.game!!.stop()
            ProjectLibrary.instance.loggerInfo("[ProjectLibrary] game stop at player null")
        }
        else {
            Info.game!!.resetPlayer(player)
        }
    }
}