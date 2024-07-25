package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.localization.Localization
import org.beobma.projectlibrary.manager.AbnormalStatusManager
import org.beobma.projectlibrary.util.Util
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class OnSwapHands : Listener {
    @EventHandler
    fun onSwapHands(event: PlayerSwapHandItemsEvent) {
        val game = Info.game ?: return
        val player = event.player
        if (!Info.isStarting() && !Info.isGaming()) return
        if (player !in game.players) return
        val abnormalStatusManager = AbnormalStatusManager()
        val unableAttackManager = abnormalStatusManager.createUnableAttackManager()

        if (event.offHandItem == Localization().magicMarksmanWeapon) {
            if (player.getCooldown(Material.SPYGLASS) != 0) {
                event.isCancelled = true
                return
            }
            unableAttackManager.run {
                if (player.isUnableAttack()) {
                    event.isCancelled = true
                    return
                }
            }
            player.setCooldown(Material.SPYGLASS, 100)
            val startLocation = player.eyeLocation
            val direction = player.eyeLocation.direction.normalize()

            for (i in 1..50) {
                val particleLocation = startLocation.clone().add(direction.clone().multiply(i))
                player.world.spawnParticle(Particle.END_ROD, particleLocation, 1, 0.0, 0.0, 0.0, 0.0)
            }

            player.world.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10.0f, 0.5f)
            player.world.playSound(player.location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10.0f, 2.0f)
            val target = Util.shootLaser(player)
            if (target is Player) {
                target.damage(8.0, player)
            }
        }
        else if (event.offHandItem == Localization().gunWeapon) {
            if (player.getCooldown(Material.SPYGLASS) != 0) {
                event.isCancelled = true
                return
            }
            unableAttackManager.run {
                if (player.isUnableAttack()) {
                    event.isCancelled = true
                    return
                }
            }
            player.setCooldown(Material.SPYGLASS, 100)
            val startLocation = player.eyeLocation
            val direction = player.eyeLocation.direction.normalize()

            for (i in 1..50) {
                val particleLocation = startLocation.clone().add(direction.clone().multiply(i))
                player.world.spawnParticle(Particle.END_ROD, particleLocation, 1, 0.0, 0.0, 0.0, 0.0)
            }

            player.world.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10.0f, 0.5f)
            player.world.playSound(player.location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10.0f, 2.0f)
            val target = Util.shootLaser(player)
            if (target is Player) {
                target.damage(5.0, player)
            }
        }

        event.isCancelled = true
    }
}