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
        val player = event.damager as? Player ?: return
        val entity = event.entity as? Player ?: return

        if (!Info.isGaming() && !Info.isStarting()) return

        if (AbnormalStatusManager().isUnableAttack(player)) {
            event.isCancelled = true
            return
        }

        event.damage += damageHandle(player, entity)

        var disheveledDamage = event.damage / 4
        val mainBookShelf = Info.game!!.playerMainBookShelf[entity] ?: return

        if (mainBookShelf.disheveled - disheveledDamage <= 0) {
            if (AbnormalStatusManager().isDisheveled(player)) {
                event.damage += disheveledDamage
            } else {
                mainBookShelf.disheveled(entity)
            }
        } else {
            mainBookShelf.disheveled -= disheveledDamage
        }

        if (entity.health - event.damage <= 0) {
            updateEmotionOnDeath(player, entity)
            entity.health = 0.0
        } else {
            updateEmotionOnDamage(player, entity, event.damage)
            Info.game!!.playerMainBookShelf[entity]!!.health = entity.health - event.damage
        }
    }

    private fun damageHandle(player: Player, entity: Player): Int {
        val bookShelfList = player.getMainBookShelf()!!.abnormalityCards

        if (boomShelfList.isEmpty()) return
        
        bookShelfList.forEach { abnormalityCard ->
            if (abnormalityCard.emotion == EmotionType.Affirmation) {
                when ("${ChatColor.DARK_GREEN}${ChatColor.BOLD}${abnormalityCard.name}") {


                }
            }
            else {
                when ("${ChatColor.DARK_RED}${ChatColor.BOLD}${abnormalityCard.name}") {


                }
            }
        }

    }
    private fun updateEmotionOnDeath(player: Player, entity: Player) {
        val playerBookShelf = Info.game!!.playerMainBookShelf[player] ?: return
        val entityBookShelf = Info.game!!.playerMainBookShelf[entity] ?: return

        playerBookShelf.emotion += 3
        entityBookShelf.emotion -= 3
    }

    private fun updateEmotionOnDamage(player: Player, entity: Player, damage: Double) {
        val playerBookShelf = Info.game!!.playerMainBookShelf[player] ?: return
        val entityBookShelf = Info.game!!.playerMainBookShelf[entity] ?: return

        if (damage > 3) {
            playerBookShelf.emotion += 1
            entityBookShelf.emotion -= 1
        }
    }
}
