package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.info.Info
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class OnSwapHands : Listener {
    @EventHandler
    fun onSwapHands(event: PlayerSwapHandItemsEvent) {
        if (Info.isGaming() || Info.isStarting()) {
            event.isCancelled = true
        }
    }
}