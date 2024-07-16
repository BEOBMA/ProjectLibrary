package org.beobma.projectlibrary.listener

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.percentageOf
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class OnPlayerJump : Listener {
    @EventHandler
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player
        if (!Info.isGaming()) {
            return
        }

        val playerBookShelfList = player.getMainBookShelf()!!.abnormalityCards
        val playerBookShelf = player.getMainBookShelf()!!

        if (playerBookShelfList.isNotEmpty()) {
            playerBookShelfList.forEach { abnormalityCard ->
                if (abnormalityCard.name == "호기심") {
                    val original = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.value
                    val final = original + original.percentageOf(2)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute ${player.name} minecraft:generic.movement_speed base set $final")
                }
            }
        }
    }
}