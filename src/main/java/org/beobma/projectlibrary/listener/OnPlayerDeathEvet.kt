package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.info.Info
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.util.Vector

class OnPlayerDeathEvet : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity

        if (Info.isGaming()) {
            Info.game!!.playerMainBookShelf[player]!!.death(player)
            player.velocity = Vector(0, 0, 0)
            event.isCancelled = true
        }
    }
}