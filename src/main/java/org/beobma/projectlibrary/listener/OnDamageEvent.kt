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
        val entity = event.entity
        val damage = event.damage
        if (player !is Player) return
        if (!Info.isGaming() && !Info.isStarting()) return

        AbnormalStatusManager().run {
            if (!player.isUnableAttack()) return
            event.isCancelled = true
            return
        }
        if (entity !is Player) return

        val disheveledDamage = (damage / 4).toInt
        
        if (Info.game.playerMainBookShelf[entity]!!.disheveled - disheveledDamage <= 0) {
            AbnormalStatusManager().run {
                if (player.isDisheveled()) {
                    event.damage += disheveledDamage
                }
                else {
                    Info.game.playerMainBookShelf[entity]!!.disheveled()
                }
            }
        }
        else {
            Info.game.playerMainBookShelf[entity]!!.disheveled -= disheveledDamage
        }

        if (entity.health - event.damage <= 0) {
            

        }
        else {
            Info.game.playerMainBookShelf[entity]!!.health = entity.health - event.damage
        }
    }
}