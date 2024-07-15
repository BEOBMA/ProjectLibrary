package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.game.Game
import org.beobma.projectlibrary.game.GameManager
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.localization.Localization
import org.beobma.projectlibrary.util.Util.getMainBookShelf
import org.beobma.projectlibrary.util.Util.toAbnormalityCard
import org.beobma.projectlibrary.util.Util.toMainBookShelf
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class OnClickItem : Listener {
    @EventHandler
    fun onClickItem(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val clickItem = event.currentItem ?: return
        val game = Info.takeIf { it.isGaming() }?.game ?: return
        val loc = Localization()

        if (clickItem == loc.nullPane) {
            event.isCancelled = true
            return
        }

        if (player.scoreboardTags.contains("rewardChose")) {
            handleRewardChoice(game, player, clickItem, event)
        }

        event.isCancelled = true
    }

    private fun handleRewardChoice(game: Game, player: Player, clickItem: ItemStack, event: InventoryClickEvent) {
        if (clickItem == Localization().nullPane || event.clickedInventory?.type != InventoryType.CHEST) {
            event.isCancelled = true
            return
        }
        val playerMainBookShelf = player.getMainBookShelf() ?: return
        when (clickItem.type) {
            Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE -> playerMainBookShelf.abnormalityCards.add(clickItem.toAbnormalityCard())
            Material.BOOK -> {
                val mainBookShelf = clickItem.toMainBookShelf()
                player.getMainBookShelf()!!.abnormalityCards.forEach {
                    mainBookShelf.abnormalityCards.add(it)
                }
                Info.game!!.playerMainBookShelf[player] = mainBookShelf
            }
            else -> return
        }
        player.scoreboardTags.remove("rewardChose")
        player.inventory.clear()
        player.closeInventory()

        if (game.players.none { it.scoreboardTags.contains("rewardChose") }) {
            if (clickItem.type == Material.BOOK) {
                GameManager().mainBookShelfEnd()
            }
            else {
                GameManager().preparationGame()
            }
        }
    }
}