package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.abnormalstatus.AbnormalStatusManager
import org.beobma.projectlibrary.info.Info
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class OnDamageEvent : Listener {
    @EventHandler
    fun onPlayerAttack(event: EntityDamageByEntityEvent) {
        val player = event.damager
        val damage = event.damage
        if (player !is Player) return
        if (!Info.isGaming() && !Info.isStarting()) return

        AbnormalStatusManager().run {
            if (!player.isUnableAttack()) return
            event.isCancelled = true
            return
        }

        
    }
}