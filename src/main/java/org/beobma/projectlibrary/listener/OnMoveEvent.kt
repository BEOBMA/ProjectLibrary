package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.manager.AbnormalStatusManager
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class OnMoveEvent : Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val abnormalStatusManager = AbnormalStatusManager()
        val unableMoveManager = abnormalStatusManager.createUnableMoveManager()

        unableMoveManager.run {
            if (player.isUnableMove()) {
                if (player.gameMode != GameMode.ADVENTURE) {
                    return
                }
                if (event.from.x != event.to.x || event.from.z != event.to.z) {
                    event.isCancelled = true
                }
            }
        }
    }
}