package org.beobma.projectlibrary.listener

import org.beobma.projectlibrary.abnormalitycard.AbnormalityCardManager
import org.beobma.projectlibrary.abnormalitycard.DefaultAbnormalityCardConverter
import org.beobma.projectlibrary.game.Game
import org.beobma.projectlibrary.manager.GameManager
import org.beobma.projectlibrary.info.Info
import org.beobma.projectlibrary.localization.Localization
import org.beobma.projectlibrary.manager.DefaultFloorHandler
import org.beobma.projectlibrary.manager.DefaultGameService
import org.beobma.projectlibrary.manager.FloorManager
import org.beobma.projectlibrary.util.Util.getMainBookShelf
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

        val playerMainBookShelf = Info.game!!.playerMainBookShelf[player] ?: return
        val converter = DefaultAbnormalityCardConverter()
        val abnormalityCardManager = AbnormalityCardManager(converter)

        when (clickItem.type) {
            Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE -> {
                val item = clickItem.clone()
                val newCard = abnormalityCardManager.toAbnormalityCard(item)
                playerMainBookShelf.abnormalityCards.add(newCard)
            }
            Material.BOOK -> {
                if (playerMainBookShelf.name != clickItem.toMainBookShelf().name) {
                    val item = clickItem.clone()
                    val newMainBookShelf = item.toMainBookShelf()
                    Info.game!!.playerMainBookShelf[player]?.abnormalityCards?.forEach { card ->
                        newMainBookShelf.abnormalityCards.add(card)
                    }
                    Info.game!!.playerMainBookShelf[player] = newMainBookShelf
                    Info.game!!.playerMainBookShelf[player]?.set(player)
                }
            }
            else -> return
        }

        player.scoreboardTags.remove("rewardChose")
        player.inventory.clear()
        player.closeInventory()

        if (game.players.none { it.scoreboardTags.contains("rewardChose") }) {
            if (clickItem.type == Material.BOOK) {
                FloorManager(DefaultFloorHandler()).advanceFloor()
            } else {
                GameManager(DefaultGameService()).preparationGame()
            }
        }
    }

}