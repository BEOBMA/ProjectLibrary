package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.abnormalstatus.AbnormalStatusManager
import org.beobma.projectlibrary.info.Info
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class OnMoveEvent : Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        if (!Info.isGaming() && !Info.isStarting()) return

        AbnormalStatusManager().run {
            if (!player.isUnableMove()) return
            event.isCancelled = true
        }
    }
}