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
            if (player.isUnableAttack()) {
                event.isCancelled = true
                return
            }
        }

        if (entity !is Player) return

        val disheveledDamage = (damage / 4).toInt()

        if (Info.game!!.playerMainBookShelf[entity]!!.disheveled - disheveledDamage <= 0) {
            AbnormalStatusManager().run {
                if (player.isDisheveled()) {
                    event.damage += disheveledDamage
                }
                else {
                    Info.game!!.playerMainBookShelf[entity]!!.disheveled(entity)
                }
            }
        }
        else {
            Info.game!!.playerMainBookShelf[entity]!!.disheveled -= disheveledDamage
        }

        if (entity.health - event.damage <= 0) {
            Info.game!!.playerMainBookShelf[entity]!!.emotion -= 3
            Info.game!!.playerMainBookShelf[player]!!.emotion += 3

            entity.health = 0.0
        }
        else {
            if (event.damage > 3) {
                Info.game!!.playerMainBookShelf[entity]!!.emotion -= 1
                Info.game!!.playerMainBookShelf[player]!!.emotion += 1
            }
            Info.game!!.playerMainBookShelf[entity]!!.health = entity.health - event.damage
        }
    }
}