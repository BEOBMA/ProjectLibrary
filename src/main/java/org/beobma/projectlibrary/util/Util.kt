package org.beobma.projectlibrary.util

import org.beobma.projectlibrary.bookshelf.MainBookShelf
import org.beobma.projectlibrary.game.GameManager
import org.beobma.projectlibrary.info.Info
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player

object Util {
    fun Player.isParticipation(): Boolean {
        if (!Info.isGaming()) return false
        if (player == null) return false
        if (!player!!.isDead) return false
        if (player !in Info.game!!.players) return false
        if (player!!.isTeam("SpectatorTeam")) return false
        if (player !in Bukkit.getOnlinePlayers()) return false
        if (player!!.gameMode != GameMode.ADVENTURE) return false

        return true
    }

    fun Player.isTeam(team: String): Boolean {
        if (player != null) {
            return GameManager.teams[team]?.hasEntry(player!!.name) == true
        }
        return false
    }

    fun Player.getMainBookShelf(): MainBookShelf? {
        return Info.game!!.playerMainBookShelf[player]
    }
}