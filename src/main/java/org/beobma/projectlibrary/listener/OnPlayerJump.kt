package org.beobma.projectlibrary.listener

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.beobma.projectlibrary.abnormalitycard.AbnormalityCard
import org.beobma.projectlibrary.manager.AbnormalStatusManager
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class OnPlayerJump : Listener {

    @EventHandler
    fun onPlayerJump(event: PlayerJumpEvent) {
        Info.game ?: return
        val abnormalStatusManager = AbnormalStatusManager()
        val moveSpeedManager = abnormalStatusManager.createMoveSpeedManager()

        val player = event.player
        if (!Info.isGaming()) {
            return
        }

        val playerBookShelf = player.getMainBookShelf() ?: return
        val playerBookShelfList = playerBookShelf.abnormalityCards

        if (playerBookShelfList.isNotEmpty()) {
            if (playerBookShelfList.find { it.name == "호기심" } is AbnormalityCard) {
                moveSpeedManager.run {
                    player.moveSpeedPercentage(2, Int.MAX_VALUE, "호기심", true)
                }
            }
        }
    }
}