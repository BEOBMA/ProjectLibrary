package org.beobma.projectlibrary.bookshelf

import org.beobma.projectlibrary.info.Info
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class MainBookShelf(
    val name: String,
    val rating: Rating,
    val health: Double,
    val maxHealth: Double,
    var disheveled: Int,
    val maxDisheveled: Int,
    val uniqueAbilities: MutableList<UniqueAbilities>,
    val weapon: ItemStack,
    val disheveledTime: Int = 3
) {

    fun set(player: Player) {
        Info.game!!.playerMainBookShelf[player] = this
        player.maxHealth = this.maxHealth
        player.health = this.health
        player.inventory.clear()
        player.activePotionEffects.forEach { effect ->
            player.removePotionEffect(effect.type)
        }
        player.inventory.setItem(0, this.weapon)
    }


    fun disheveled(player: Player) {
        AbnormalStatusManager().run {
            player.addDisheveled()
        }
        object : BukkitRunnable() {
            override fun run() {
                AbnormalStatusManager().run {
                    player.removeDisheveled()
                }
            }
        }.runTaskLater(ProjectLibrary.instance, (this.disheveledTime * 20).toLong)
    }
}


enum class Rating {
    Supply, Advanced, Limit, Art
}