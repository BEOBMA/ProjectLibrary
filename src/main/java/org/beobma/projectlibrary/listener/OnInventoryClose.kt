package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.game.Game
import org.beobma.projectlibrary.game.GameManager
import org.beobma.projectlibrary.info.Info
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class OnInventoryClose : Listener {
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val game = Info.takeIf { it.isGaming() }?.game ?: return

        if (player !in game.players) return

        when {
            player.scoreboardTags.contains("rewardChose") -> handleRewardChoice(player, game, event)
        }
    }

    private fun handleRewardChoice(player: Player, game: Game, event: InventoryCloseEvent) {
        player.sendMessage("${ChatColor.BOLD}[!] 획득을 포기하셨습니다.")
        player.scoreboardTags.remove("rewardChose")
        player.inventory.clear()

        if (game.players.none { it.scoreboardTags.contains("rewardChose") }) {
            if (event.inventory.contains(Material.BOOK)) {
                GameManager().advanceFloor()
            }
            else {
                GameManager().preparationGame()
            }
        }
    }
}